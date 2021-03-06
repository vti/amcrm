package com.github.vti.amcrm.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linecorp.armeria.common.JacksonObjectMapperProvider;

public class DefaultObjectMapper implements JacksonObjectMapperProvider {
    private static final ObjectMapper objectMapper = buildObjectMapper();

    public static ObjectMapper get() {
        return objectMapper;
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return objectMapper;
    }

    public ObjectMapper newObjectMapper() {
        return buildObjectMapper();
    }
}
