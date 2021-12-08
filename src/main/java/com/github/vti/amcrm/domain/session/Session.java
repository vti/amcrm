package com.github.vti.amcrm.domain.session;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import com.github.vti.amcrm.domain.ActorId;

public class Session {
    private SessionId id;
    private ActorId actorId;
    private Instant createdAt;
    private Instant expiresAt;

    public Session(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.actorId = Objects.requireNonNull(builder.actorId, "actorId");
        this.createdAt = Objects.requireNonNull(builder.createdAt, "createdAt");
        this.expiresAt = Objects.requireNonNull(builder.expiresAt, "expiresAt");
    }

    public SessionId getId() {
        return this.id;
    }

    public ActorId getActorId() {
        return this.actorId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }

    public void prolong(Duration duration) {
        expiresAt = Instant.now().plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Session)) return false;

        Session other = (Session) o;

        return getId().equals(other.getId());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "Session{" + "id=" + id + ", actorId=" + actorId + ", expiresAt=" + expiresAt + '}';
    }

    public static class Builder {
        private SessionId id;
        private ActorId actorId;
        private Instant createdAt = Instant.now();
        private Instant expiresAt;

        public Builder id(SessionId id) {
            this.id = id;

            return this;
        }

        public Builder actorId(ActorId actorId) {
            this.actorId = actorId;

            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;

            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;

            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}
