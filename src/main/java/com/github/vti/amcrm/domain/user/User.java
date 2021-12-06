package com.github.vti.amcrm.domain.user;

import java.time.Instant;
import java.util.Objects;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Entity;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.event.*;

public class User extends Entity<Event<UserId>> {
    private final UserId id;
    private Long version;
    private final String name;
    private Boolean admin;
    private final Instant createdAt;
    private final ActorId createdBy;
    private Instant updatedAt = null;
    private ActorId updatedBy = null;
    private Instant deletedAt = null;
    private ActorId deletedBy = null;

    private User(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.version = Objects.requireNonNull(builder.version, "version");
        this.admin = Objects.requireNonNull(builder.admin, "isAdmin");
        this.name = Objects.requireNonNull(builder.name, "name");
        this.createdAt = Objects.requireNonNull(builder.createdAt, "createdAt");
        this.createdBy = Objects.requireNonNull(builder.createdBy, "createdBy");
        this.updatedAt = builder.updatedAt;
        this.updatedBy = builder.updatedBy;
        this.deletedAt = builder.deletedAt;
        this.deletedBy = builder.deletedBy;

        if (this.version == 0L) {
            this.addEvent(new UserCreated(this.id, this.createdBy));
        }
    }

    public UserId getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ActorId getCreatedBy() {
        return createdBy;
    }

    public Instant getUpdatedAt() {
        return createdAt;
    }

    public ActorId getUpdatedBy() {
        return updatedBy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public ActorId getDeletedBy() {
        return deletedBy;
    }

    public void toggleAdminStatus(ActorId actorId) {
        this.admin = !this.admin;

        this.updatedAt = Instant.now();
        this.updatedBy = actorId;

        this.addEvent(new UserAdminStatusToggled(this.id, this.updatedBy));
    }

    public void incrementVersion() {
        this.version++;
    }

    public void delete(ActorId actorId) {
        if (this.isDeleted()) {
            throw new IllegalStateException();
        }

        this.deletedBy = actorId;

        this.addEvent(new UserDeleted(this.id, this.deletedBy));
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isDeleted() {
        return this.deletedBy != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private UserId id;
        private Long version = 0L;
        private Boolean admin = false;
        private String name;
        private Instant createdAt = Instant.now();
        private ActorId createdBy;
        private Instant updatedAt;
        private ActorId updatedBy;
        private Instant deletedAt;
        private ActorId deletedBy;

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder admin(Boolean admin) {
            this.admin = admin;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder createdBy(ActorId createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder updatedBy(ActorId updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder deletedAt(Instant deletedAt) {
            this.deletedAt = deletedAt;
            return this;
        }

        public Builder deletedBy(ActorId deletedBy) {
            this.deletedBy = deletedBy;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
