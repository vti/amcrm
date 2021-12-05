package com.github.vti.amcrm.api.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.networknt.schema.ValidationMessage;

public class JsonSchemaValidationException extends RuntimeException {
    private final Set<ValidationMessage> validationMessages;

    public JsonSchemaValidationException(Set<ValidationMessage> validationMessages) {
        super();

        this.validationMessages = validationMessages;
    }

    public List<String> getMessages() {
        return this.validationMessages.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.toList());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("message", "Validation failed");
        map.put("details", this.getMessages());

        return map;
    }
}
