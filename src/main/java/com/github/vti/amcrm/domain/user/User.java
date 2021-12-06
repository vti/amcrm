package com.github.vti.amcrm.domain.user;

import java.util.Objects;

import com.github.vti.amcrm.domain.Entity;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.event.*;

public class User extends Entity<Event<UserId>> {
    private UserId id;
    private Long version;
    private String name;
    private Boolean isAdmin;
    private UserId createdBy;
    private UserId updatedBy = null;
    private UserId deletedBy = null;

    private User(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.version = Objects.requireNonNull(builder.version, "version");
        this.isAdmin = Objects.requireNonNull(builder.isAdmin, "isAdmin");
        this.name = Objects.requireNonNull(builder.name, "name");
        this.createdBy = Objects.requireNonNull(builder.createdBy, "createdBy");
        this.updatedBy = builder.updatedBy;
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
        return isAdmin;
    }

    public String getName() {
        return name;
    }

    public UserId getCreatedBy() {
        return createdBy;
    }

    public UserId getUpdatedBy() {
        return updatedBy;
    }

    public UserId getDeletedBy() {
        return deletedBy;
    }

    public void toggleAdminStatus(UserId userId) {
        this.isAdmin = !this.isAdmin;

        this.updatedBy = userId;

        this.addEvent(new UserAdminStatusToggled(this.id, this.updatedBy));
    }

    public void incrementVersion() {
        this.version++;
    }

    public void delete(UserId userId) {
        if (this.isDeleted()) {
            throw new IllegalStateException();
        }

        this.deletedBy = userId;

        this.addEvent(new UserDeleted(this.id, this.deletedBy));
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isDeleted() {
        return this.deletedBy == null ? false : true;
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
        private Boolean isAdmin = false;
        private String name;
        private UserId createdBy;
        private UserId updatedBy;
        private UserId deletedBy;

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder isAdmin(Boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder createdBy(UserId createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(UserId updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder deletedBy(UserId deletedBy) {
            this.deletedBy = deletedBy;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
