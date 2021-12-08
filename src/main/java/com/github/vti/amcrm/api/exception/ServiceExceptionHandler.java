package com.github.vti.amcrm.api.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;

import com.github.vti.amcrm.api.DefaultObjectMapper;

public class ServiceExceptionHandler implements ExceptionHandlerFunction {
    private static final Logger log = LogManager.getLogger(ServiceExceptionHandler.class);

    @Override
    public HttpResponse handleException(
            ServiceRequestContext ctx, HttpRequest req, Throwable cause) {
        if (cause instanceof JsonSchemaValidationException) {
            JsonSchemaValidationException eCause = (JsonSchemaValidationException) cause;

            try {
                String asJson = DefaultObjectMapper.get().writeValueAsString(eCause.toMap());

                return HttpResponse.of(
                        HttpStatus.UNPROCESSABLE_ENTITY, MediaType.JSON_UTF_8, asJson);
            } catch (JsonProcessingException e) {
                log.error("Json serialization failed", e);

                throw new RuntimeException(e);
            }
        } else if (cause instanceof NotFoundException) {
            NotFoundException eCause = (NotFoundException) cause;

            try {
                String asJson = DefaultObjectMapper.get().writeValueAsString(eCause.toMap());

                return HttpResponse.of(HttpStatus.NOT_FOUND, MediaType.JSON_UTF_8, asJson);
            } catch (JsonProcessingException e) {
                log.error("Json serialization failed", e);

                throw new RuntimeException(e);
            }
        } else if (cause instanceof ConflictException) {
            ConflictException eCause = (ConflictException) cause;

            try {
                String asJson = DefaultObjectMapper.get().writeValueAsString(eCause.toMap());

                return HttpResponse.of(HttpStatus.CONFLICT, MediaType.JSON_UTF_8, asJson);
            } catch (JsonProcessingException e) {
                log.error("Json serialization failed", e);

                throw new RuntimeException(e);
            }
        }

        log.error("Unhandled exception", cause);

        return ExceptionHandlerFunction.fallthrough();
    }
}
