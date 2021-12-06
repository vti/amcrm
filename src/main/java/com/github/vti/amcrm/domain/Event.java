package com.github.vti.amcrm.domain;

import java.util.Optional;

import com.github.vti.amcrm.domain.user.UserId;

public class Event<T> {
    private final T originId;
    private final UserId userId;
    private final Object payload;

    protected Event(T originId, UserId userId, Object payload) {
        this.originId = originId;
        this.userId = userId;
        this.payload = payload;
    }

    protected Event(T originId, UserId userId) {
        this(originId, userId, null);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public T getOriginId() {
        return originId;
    }

    public UserId getUserId() {
        return userId;
    }

    public Optional<Object> getPayload() {
        return Optional.ofNullable(payload);
    }
}
