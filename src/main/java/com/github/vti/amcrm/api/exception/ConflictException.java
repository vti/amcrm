package com.github.vti.amcrm.api.exception;

import java.util.HashMap;
import java.util.Map;

public class ConflictException extends RuntimeException {
    private final String message;

    public ConflictException(String message) {
        super();

        this.message = message;
    }

    public Map<String, Object> toMap() {
        return new HashMap<String, Object>() {
            {
                put("message", message);
            }
        };
    }
}
