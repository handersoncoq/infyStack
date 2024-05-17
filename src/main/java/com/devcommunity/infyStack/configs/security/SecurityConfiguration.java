package com.devcommunity.infyStack.configs.security;

import com.devcommunity.infyStack.annotations.PublicAccess;
import com.devcommunity.infyStack.annotations.UserAccess;
import com.devcommunity.infyStack.auth.AuthController;
import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.services.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.AnnotationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${infystack.client.url}")
    private String CLIENT_URL;
    private final TokenAuthenticationFilter jwtAuthFilter;
    private final UserService userService;
    private final InfyStackAccessDeniedHandler accessDeniedHandler;

    @Qualifier("delegatedAuthenticationEntryPoint")
    private final AuthenticationEntryPoint authEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        var securityConfigure = httpSecurity
                .csrf().disable()
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests()
                .requestMatchers(SecurityConfiguration::requestAuthorizeWithoutToken).permitAll();
        for(UserRole role : UserRole.values()){
            securityConfigure.requestMatchers(generateMatcherOfMethodsForUserRole(role)).hasAuthority(role.name());
        }
        return securityConfigure
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authEntryPoint)
                .and().build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(CLIENT_URL);
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "PATCH"));
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService::getByEmail;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    // Helper Methods
    // ----------------------------------------------------------------------------------------------
    private static Set<Class> findAllClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static Set<Method> findAllMethodsInControllers(){
        Set<Method> result = new HashSet<>();
        Set<Class> controllers = findAllClasses("com.devcommunity.infyStack.controllers");
        controllers.add(AuthController.class);
        for(Class controller : controllers) {
            result.addAll(Arrays.stream(controller.getMethods())
                    .filter(method -> !method.getDeclaringClass().equals(Object.class))
                    .collect(Collectors.toSet()));
        }
        return result;
    }

    private static Set<String> methodsToAuthorizeWithoutToken;
    private static Set<String> getMethodsToAuthorizeWithoutToken(){
        if(methodsToAuthorizeWithoutToken == null) {
            methodsToAuthorizeWithoutToken = new HashSet<>();
            for (Method method : findAllMethodsInControllers()) {
                PublicAccess annotation = method.getAnnotation(PublicAccess.class);
                if (annotation != null) methodsToAuthorizeWithoutToken.add(getPathFromMappingAnnotations(method));
            }
        }
        return methodsToAuthorizeWithoutToken;
    }

    private static boolean requestAuthorizeWithoutToken(HttpServletRequest request){
        return getMethodsToAuthorizeWithoutToken().contains(request.getServletPath());
    }

    private static Map<UserRole, Set<String>> methodsThatRequireUserRoles;
    private static Map<UserRole, Set<String>> getMethodsThatRequireUserRoles(){
        if(methodsThatRequireUserRoles == null) {
            methodsThatRequireUserRoles = new EnumMap<>(UserRole.class);
            for (UserRole role : UserRole.values()) {
                methodsThatRequireUserRoles.put(role, new HashSet<>());
            }
            for (Method method : findAllMethodsInControllers()) {
                UserAccess annotation = method.getAnnotation(UserAccess.class);
                if (annotation != null) addPathToRoles(annotation, getPathFromMappingAnnotations(method));
            }
        }
        return methodsThatRequireUserRoles;
    }

    private static String getPathFromMappingAnnotations(Method method){
        List<String> paths = new ArrayList<>();
        RequestMapping requestMappingAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        if(requestMappingAnnotation == null)
            throw new AnnotationException("Controller " + method.getDeclaringClass() + " is missing the @RequestMapping annotation.");
        if(requestMappingAnnotation.value().length == 0)
            throw new AnnotationException("Controller " + method.getDeclaringClass() + "'s @RequestMapping annotation is missing its value.");
        String classPath = requestMappingAnnotation.value()[0];

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) paths.add(classPath + postMapping.value()[0]);
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) paths.add(classPath + putMapping.value()[0]);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) paths.add(classPath + getMapping.value()[0]);
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) paths.add(classPath + deleteMapping.value()[0]);
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) paths.add(classPath + patchMapping.value()[0]);

        if(paths.isEmpty()) throw new AnnotationException("Method " + method.getName() + " is missing a REST Mapping.");
        if(paths.size() > 1) throw new AnnotationException("Method " + method.getName() + " has too many REST Mappings.");
        return paths.get(0);
    }

    private static void addPathToRoles(Annotation annotation, String path){
        for(UserRole role : ((UserAccess) annotation).value()){
            if (!methodsThatRequireUserRoles.containsKey(role))
                methodsThatRequireUserRoles.put(role, new HashSet<>());
            methodsThatRequireUserRoles.get(role).add(path);
        }
    }

    private static RequestMatcher generateMatcherOfMethodsForUserRole(UserRole role){
        return request -> getMethodsThatRequireUserRoles().get(role).contains(request.getServletPath());
    }
}
