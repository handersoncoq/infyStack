package com.devcommunity.infyStack.aspects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingFactory {
    private static final Logger LOGGER = LogManager.getLogger(LoggingFactory.class);

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    @Pointcut("within(com.devcommunity..*)" +
            " && !execution(* com.devcommunity.infyStack.configs.security.TokenService.*(..))" +
            " && !execution(* com.devcommunity.infyStack.configs.security.TokenAuthenticationFilter.*(..))" +
            " && !execution(* com.devcommunity.infyStack.configs.security.SecurityConfiguration.*(..))" +
            " && !execution(* com.devcommunity.infyStack.configs.security.DelegatedAuthenticationEntryPoint.*(..))" +
            " && !execution(* com.devcommunity.infyStack.aspects.ExceptionResolver.*(..))")
    private static void pcLogAll(){}

    @Pointcut("within(com.devcommunity..*)" +
            " && !execution(* com.devcommunity.infyStack.configs.security.TokenAuthenticationFilter.*(..))" +
            " && !execution(* com.devcommunity.infyStack.configs.security.SecurityConfiguration.*(..))")
    private static void pcLogAllIncludingSecurity(){}

    @Before("pcLogAll()")
    private static void logMethodInvoke(JoinPoint jp){
        String methodArguments = Arrays.toString(jp.getArgs());
        LOGGER.info(ANSI_GREEN + "{}() was invoked." + ANSI_RESET,
                extractClassMethodSignature(jp));
    }

//    @AfterReturning(pointcut = "pcLogAll()", returning = "returnedObject")
//    private static void logMethodReturn(JoinPoint jp, Object returnedObject){
//        LOGGER.info(ANSI_GREEN + "{} returned: {}." + ANSI_RESET,
//                extractClassMethodSignature(jp), returnedObject);
//    }

    @AfterThrowing(pointcut = "pcLogAllIncludingSecurity()", throwing = "exception")
    private static void logMethodThrow(JoinPoint jp, Throwable exception){
        if (!LOGGER.isDebugEnabled()) {
            String throwableName = exception.getClass().getSimpleName();
            LOGGER.warn(ANSI_RED + "{}(\"{}\") thrown in {}()." + ANSI_RESET,
                    throwableName, exception.getMessage(), extractClassMethodSignature(jp));
            LOGGER.warn(ANSI_RED + "{}" + ANSI_RESET,
                    exception.getStackTrace()[0]);
        } else {
            String throwableName = exception.getClass().getName();
            LOGGER.debug(ANSI_YELLOW + "{}(\"{}\") thrown in {}()." + ANSI_RESET,
                    throwableName, exception.getMessage(), extractClassMethodSignature(jp));
            LOGGER.debug(ANSI_YELLOW + "{}" + ANSI_RESET,
                    Arrays.toString(exception.getStackTrace()));
        }
    }

    private static String extractClassMethodSignature(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getDeclaringType().getSimpleName() + "." + methodSignature.getName();
    }
}
