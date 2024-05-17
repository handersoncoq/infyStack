package com.devcommunity.infyStack.annotations;

import com.devcommunity.infyStack.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserAccess {

    UserRole[] value() default {};
}
