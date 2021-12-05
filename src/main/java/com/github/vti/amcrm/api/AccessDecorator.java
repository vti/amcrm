package com.github.vti.amcrm.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linecorp.armeria.server.annotation.DecoratorFactory;

@DecoratorFactory(AccessDecoratorFactoryFunction.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AccessDecorator {
    Client.Role role() default Client.Role.ANONYMOUS;

    int order() default 0;
}
