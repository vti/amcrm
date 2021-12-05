package com.github.vti.amcrm.api.exception;

import java.util.HashMap;
import java.util.Map;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> response = new HashMap<>();

        response.put("message", "Not found");
        return response;
    }
}
