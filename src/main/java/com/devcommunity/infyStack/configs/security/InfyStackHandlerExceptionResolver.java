package com.devcommunity.infyStack.configs.security;

import com.devcommunity.infyStack.exceptions.AuthenticationException;
import com.devcommunity.infyStack.exceptions.AccessDeniedException;
import io.jsonwebtoken.JwtException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class InfyStackHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception exception) {

        try {
            if(exception instanceof AccessDeniedException || exception instanceof org.springframework.security.access.AccessDeniedException){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(new AccessDeniedException().getMessage());
            }
            else if(exception instanceof AuthenticationException ||
                    exception instanceof org.springframework.security.core.AuthenticationException ||
                    exception instanceof JwtException){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new AuthenticationException().getMessage());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(response);
        return modelAndView;
    }
}
