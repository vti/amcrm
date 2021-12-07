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

import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.Client;

public class AuthenticationService extends SimpleDecoratingHttpService {
    private static final Logger log = LogManager.getLogger(Api.class);

    public AuthenticationService(HttpService delegate) {
        super(delegate);
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        String authorization = req.headers().get("Authorization");

        Client client = Client.anonymous();
        if (authorization != null && !authorization.isEmpty()) {
            if (authorization.equals("user")) {
                client = Client.user("2");
            } else if (authorization.equals("admin")) {
                client = Client.admin("1");
            }
        }

        ctx.setAttr(AttributeKey.valueOf("client"), client);

        log.info("Authenticating user: {}", client);

        ThreadContext.put("client", client.toCompactString());

        return unwrap().serve(ctx, req);
    }
}
