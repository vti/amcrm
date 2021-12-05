package com.github.vti.amcrm.api;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.common.AggregatedHttpRequest;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.RequestConverterFunction;
import com.linecorp.armeria.server.annotation.ResponseConverterFunction;
import com.networknt.schema.*;

import com.github.vti.amcrm.api.exception.JsonSchemaValidationException;
import com.github.vti.amcrm.util.CamelCase;

public class JsonConverter implements RequestConverterFunction, ResponseConverterFunction {

    private static final Logger log = LogManager.getLogger(JsonConverter.class);
    private static final Map<String, JsonSchema> schemaCache = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = DefaultObjectMapper.get();

    @Override
    public Object convertRequest(
            ServiceRequestContext ctx,
            AggregatedHttpRequest request,
            Class<?> expectedResultType,
            ParameterizedType parameterizedType) {

        String schemaPath =
                String.format(
                        "schema/api/%s.json",
                        CamelCase.camelToSnake(expectedResultType.getSimpleName()));

        JsonSchema schema = getJsonSchema(schemaPath);

        try {
            JsonNode json = objectMapper.readTree(request.content().toInputStream());

            Set<ValidationMessage> validationResult = schema.validate(json);

            if (validationResult.isEmpty()) {
                return objectMapper.treeToValue(json, expectedResultType);
            }

            throw new JsonSchemaValidationException(validationResult);
        } catch (IOException e) {
            log.error("Schema validation failed", e);

            throw new RuntimeException("Schema validation failed", e);
        }
    }

    @Override
    public HttpResponse convertResponse(
            ServiceRequestContext ctx, ResponseHeaders headers, Object result, HttpHeaders trailers)
            throws Exception {

        if (result instanceof HttpResponse) {
            return ResponseConverterFunction.fallthrough();
        }

        ObjectMapper objectMapper = DefaultObjectMapper.get();
        String asJson = objectMapper.writeValueAsString(result);

        return HttpResponse.of(HttpStatus.OK, MediaType.JSON_UTF_8, asJson);
    }

    private JsonSchema getJsonSchema(String schemaPath) {
        return schemaCache.computeIfAbsent(
                schemaPath,
                path -> {
                    final InputStream schemaStream =
                            getClass().getClassLoader().getResourceAsStream(path);
                    final SchemaValidatorsConfig config = new SchemaValidatorsConfig();

                    JsonSchemaFactory schemaFactory =
                            JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

                    try {
                        return schemaFactory.getSchema(schemaStream, config);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                String.format(
                                        "An error occurred while loading JSON Schema: %s", path),
                                e);
                    }
                });
    }
}
