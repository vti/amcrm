package com.github.vti.amcrm.domain;

import java.time.Instant;
import java.util.Optional;

public class Event {
    private final Instant createdAt = Instant.now();
    private final String originId;
    private final ActorId actorId;
    private final Object payload;

    protected Event(String originId, ActorId actorId, Object payload) {
        this.originId = originId;
        this.actorId = actorId;
        this.payload = payload;
    }

    protected Event(String originId, ActorId actorId) {
        this(originId, actorId, null);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getOriginId() {
        return originId;
    }

    public ActorId getActorId() {
        return actorId;
    }

    public Optional<Object> getPayload() {
        return Optional.ofNullable(payload);
    }
}
