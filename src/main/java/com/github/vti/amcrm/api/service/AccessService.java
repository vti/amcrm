package com.github.vti.amcrm.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;

import io.netty.util.AttributeKey;

import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.Client;

public class AccessService extends SimpleDecoratingHttpService {
    private static final Logger log = LogManager.getLogger(Api.class);
    private final Client.Role role;

    public AccessService(HttpService delegate, Client.Role role) {
        super(delegate);

        this.role = role;
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        Client client = ctx.attr(AttributeKey.valueOf("client"));

        if (client == null) {
            log.warn("Client not authenticated");
            return HttpResponse.of(HttpStatus.UNAUTHORIZED);
        }

        if (!client.isInRole(this.role)) {
            log.warn("Client not authorized");
            return HttpResponse.of(HttpStatus.UNAUTHORIZED);
        }

        log.info("Client authorized: {}", client);

        return unwrap().serve(ctx, req);
    }
}
