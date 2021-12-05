package com.github.vti.amcrm.api.service;

import java.util.Objects;

import com.linecorp.armeria.server.ServiceRequestContext;

import io.netty.util.AttributeKey;

import com.github.vti.amcrm.api.Client;

public class BaseService {
    public Client getClient() {
        ServiceRequestContext ctx = ServiceRequestContext.current();

        Objects.requireNonNull(ctx, "ctx");

        Client client = ctx.attr(AttributeKey.valueOf("client"));

        if (client == null) {
            throw new RuntimeException("No client info available");
        }

        return client;
    }
}
