package com.github.vti.amcrm.api.exception;

import com.linecorp.armeria.common.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException() {
        super("Not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
