package com.github.vti.amcrm.api.exception;

import com.linecorp.armeria.common.HttpStatus;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}
