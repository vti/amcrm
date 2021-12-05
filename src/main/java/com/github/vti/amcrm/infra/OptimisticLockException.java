package com.github.vti.amcrm.infra;

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException() {
        super();
    }

    public OptimisticLockException(Long currentVersion) {
        super(String.format("Version mismatch: version=%s", currentVersion));
    }
}
