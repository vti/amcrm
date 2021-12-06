package com.github.vti.amcrm.domain.customer;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Entity;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.event.*;

public class Customer extends Entity<Event<CustomerId>> {
    private CustomerId id;
    private Long version;
    private String name;
    private String surname;
    private String photoLocation;
    private Instant createdAt;
    private ActorId createdBy;
    private Instant updatedAt = null;
    private ActorId updatedBy = null;
    private Instant deletedAt = null;
    private ActorId deletedBy = null;

    private Customer(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id");
        this.version = Objects.requireNonNull(builder.version, "version");
        this.name = Objects.requireNonNull(builder.name, "name");
        this.surname = Objects.requireNonNull(builder.surname, "surname");
        this.photoLocation = builder.photoLocation;
        this.createdAt = Objects.requireNonNull(builder.createdAt, "createdAt");
        this.createdBy = Objects.requireNonNull(builder.createdBy, "createdBy");
        this.updatedAt = builder.updatedAt;
        this.updatedBy = builder.updatedBy;
        this.deletedAt = builder.deletedAt;
        this.deletedBy = builder.deletedBy;

        if (this.version == 0L) {
            this.addEvent(new CustomerCreated(this.id, this.createdBy));
        }
    }

    public CustomerId getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Optional<String> getPhotoLocation() {
        return Optional.ofNullable(photoLocation);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ActorId getCreatedBy() {
        return createdBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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

    public void incrementVersion() {
        this.version++;
    }

    public void changeName(ActorId actorId, String newName) {
        this.name = newName;
        this.updatedAt = Instant.now();
        this.updatedBy = actorId;

        this.addEvent(new CustomerNameChanged(this.id, this.updatedBy));
    }

    public void changeSurname(ActorId actorId, String newSurname) {
        this.surname = newSurname;
        this.updatedAt = Instant.now();
        this.updatedBy = actorId;

        this.addEvent(new CustomerSurnameChanged(this.id, this.updatedBy));
    }

    public void changePhotoLocation(ActorId actorId, String photoLocation) {
        this.photoLocation = photoLocation;
        this.updatedAt = Instant.now();
        this.updatedBy = actorId;

        this.addEvent(new CustomerPhotoChanged(this.id, this.updatedBy));
    }

    public void delete(ActorId actorId) {
        if (this.isDeleted()) {
            throw new IllegalStateException();
        }

        this.deletedAt = Instant.now();
        this.deletedBy = actorId;

        this.addEvent(new CustomerDeleted(this.id, this.deletedBy));
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
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Builder {
        private CustomerId id;
        private Long version = 0L;
        private String name;
        private String surname;
        private String photoLocation;
        private Instant createdAt = Instant.now();
        private ActorId createdBy;
        private Instant updatedAt;
        private ActorId updatedBy;
        private Instant deletedAt;
        private ActorId deletedBy;

        public Builder id(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder photoLocation(String photoLocation) {
            this.photoLocation = photoLocation;
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

        public Customer build() {
            return new Customer(this);
        }
    }
}
