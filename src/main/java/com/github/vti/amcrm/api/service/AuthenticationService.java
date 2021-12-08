package com.github.vti.amcrm.api.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;

import io.netty.util.AttributeKey;

import com.github.vti.amcrm.api.Authentication;
import com.github.vti.amcrm.api.Client;
import com.github.vti.amcrm.infra.registry.RegistryFactory;

public class AuthenticationService extends SimpleDecoratingHttpService {
    private static final Logger log = LogManager.getLogger(AuthenticationService.class);

    private final RegistryFactory registryFactory;

    public AuthenticationService(HttpService delegate, RegistryFactory registryFactory) {
        super(delegate);

        this.registryFactory = registryFactory;
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        String header = req.headers().get("Authorization");

        Client client =
                new Authentication(
                                registryFactory.getRepositoryRegistry().getSessionRepository(),
                                registryFactory.getRepositoryRegistry().getUserRepository())
                        .authenticate(header);

        log.info("Authenticated user: {}", client);

        ctx.setAttr(AttributeKey.valueOf("client"), client);

        ThreadContext.put("client", client.toCompactString());

        return unwrap().serve(ctx, req);
    }
}
