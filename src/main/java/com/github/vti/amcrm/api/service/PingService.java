package com.github.vti.amcrm.api.service;

import java.util.HashMap;
import java.util.Map;

import com.linecorp.armeria.server.annotation.ExceptionHandler;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.RequestConverter;
import com.linecorp.armeria.server.annotation.ResponseConverter;

import com.github.vti.amcrm.api.JsonConverter;
import com.github.vti.amcrm.api.exception.ServiceExceptionHandler;
import com.github.vti.amcrm.api.service.request.PingRequest;

@ExceptionHandler(ServiceExceptionHandler.class)
@RequestConverter(JsonConverter.class)
@ResponseConverter(JsonConverter.class)
public class PingService {
    public PingService() {}

    @Post("/")
    public Object ping(PingRequest request) {
        Map<String, Object> map = new HashMap<>();

        map.put("message", request.getMessage());
        map.put("reply", "pong!");

        return map;
    }
}
