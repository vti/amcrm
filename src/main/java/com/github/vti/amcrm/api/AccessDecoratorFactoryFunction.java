package com.github.vti.amcrm.api;

import java.util.function.Function;

import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction;

import com.github.vti.amcrm.api.service.AccessService;

public final class AccessDecoratorFactoryFunction
        implements DecoratorFactoryFunction<AccessDecorator> {

    @Override
    public Function<? super HttpService, ? extends HttpService> newDecorator(
            AccessDecorator parameter) {
        return (delegate) -> {
            return new AccessService(delegate, parameter.role());
        };
    }
}
