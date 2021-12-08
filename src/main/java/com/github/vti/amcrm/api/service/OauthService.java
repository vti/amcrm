package com.github.vti.amcrm.api.service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.*;
import com.linecorp.armeria.server.annotation.*;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.api.JsonConverter;
import com.github.vti.amcrm.api.exception.BadRequestException;
import com.github.vti.amcrm.api.exception.ConflictException;
import com.github.vti.amcrm.api.exception.ServiceExceptionHandler;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.infra.registry.RegistryFactory;

@ExceptionHandler(ServiceExceptionHandler.class)
@RequestConverter(JsonConverter.class)
@ResponseConverter(JsonConverter.class)
public class OauthService {
    private static final String GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_ACCESS_TOKEN_URL =
            "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";
    private final Optional<Config.OauthConfig> oauth;
    private final RegistryFactory registryFactory;

    public OauthService(RegistryFactory registryFactory, Optional<Config.OauthConfig> oauth) {
        this.registryFactory = registryFactory;
        this.oauth = oauth;
    }

    @Post("/github")
    public Object github() {
        Config.OauthConfig oauth =
                this.oauth.orElseThrow(
                        () -> new BadRequestException("No oauth enabled & configured"));

        try {
            URL url =
                    new URL(
                            String.format(
                                    "%s?client_id=%s", GITHUB_AUTHORIZE_URL, oauth.getClientId()));

            return new HashMap<String, String>() {
                {
                    put("location", url.toString());
                }
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Oauth error building failed", e);
        }
    }

    @Get("/github/callback")
    public Object callback(@Param("code") String code) {
        Config.OauthConfig oauth =
                this.oauth.orElseThrow(
                        () -> new BadRequestException("No oauth enabled & configured"));

        String accessToken = getAccessToken(oauth.getClientId(), oauth.getClientSecret(), code);
        if (accessToken == null || accessToken.isEmpty()) {
            throw new ConflictException("Authentication failed");
        }

        String name = getUserName(accessToken);

        Optional<User> userOptional =
                registryFactory.getRepositoryRegistry().getUserRepository().loadByName(name);

        if (userOptional.isPresent()) {
            Session session =
                    Session.builder()
                            .id(SessionId.of(UUID.randomUUID().toString()))
                            .actorId(ActorId.of(userOptional.get().getId().value()))
                            .expiresAt(Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)))
                            .build();
            registryFactory.getRepositoryRegistry().getSessionRepository().store(session);

            Map<String, String> sessionInfo = new HashMap<>();
            sessionInfo.put("sessionId", session.getId().value());

            return sessionInfo;
        }

        throw new ConflictException("No user found");
    }

    private String getAccessToken(String clientId, String clientSecret, String code) {
        WebClient webClient = WebClient.of();

        String content =
                String.format(
                        "client_id=%s&client_secret=%s&code=%s", clientId, clientSecret, code);

        AggregatedHttpResponse response =
                webClient.post(GITHUB_ACCESS_TOKEN_URL, content).aggregate().join();

        try {
            Map<String, String> params = parseFormUrlEncoded(response.contentUtf8());

            return params.get("access_token");
        } catch (UnsupportedEncodingException e) {
            throw new ConflictException("Authentication failed");
        }
    }

    private Map<String, String> parseFormUrlEncoded(String content)
            throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();

        String[] pairs = content.split("\\&");
        for (int i = 0; i < pairs.length; i++) {
            String[] fields = pairs[i].split("=");

            if (fields.length == 2) {
                String name = URLDecoder.decode(fields[0], "UTF-8");
                String value = URLDecoder.decode(fields[1], "UTF-8");

                params.put(name, value);
            }
        }

        return params;
    }

    private String getUserName(String accessToken) {
        WebClient webClient = WebClient.of();

        RequestHeaders getJson =
                RequestHeaders.of(
                        HttpMethod.GET,
                        GITHUB_USER_URL,
                        HttpHeaderNames.ACCEPT,
                        "application/json",
                        HttpHeaderNames.AUTHORIZATION,
                        String.format("token %s", accessToken));

        AggregatedHttpResponse response = webClient.execute(getJson).aggregate().join();

        JSONObject userInfo = new JSONObject(response.contentUtf8());

        if (userInfo.has("login")) {
            return userInfo.getString("login");
        }

        throw new ConflictException("Authentication failed");
    }
}
