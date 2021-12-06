package com.github.vti.amcrm.domain.customer;

import java.util.Objects;
import java.util.Optional;

import com.github.vti.amcrm.domain.Entity;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.event.*;
import com.github.vti.amcrm.domain.user.UserId;

public class Customer extends Entity<Event<CustomerId>> {
    private CustomerId id;
    private Long version;
    private String name;
    private String surname;
    private String photoLocation;
    private UserId createdBy;
    private UserId updatedBy = null;
    private UserId deletedBy = null;

    private Customer(Builder builder) {
        this.id = Objects.requireNonNull(builder.id);
        this.version = Objects.requireNonNull(builder.version);
        this.name = Objects.requireNonNull(builder.name);
        this.surname = Objects.requireNonNull(builder.surname);
        this.photoLocation = builder.photoLocation;
        this.createdBy = Objects.requireNonNull(builder.createdBy);
        this.updatedBy = builder.updatedBy;
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

    public UserId getCreatedBy() {
        return createdBy;
    }

    public UserId getUpdatedBy() {
        return updatedBy;
    }

    public UserId getDeletedBy() {
        return deletedBy;
    }

    public void incrementVersion() {
        this.version++;
    }

    public void changeName(UserId userId, String newName) {
        this.name = newName;
        this.updatedBy = userId;

        this.addEvent(new CustomerNameChanged(this.id, this.updatedBy));
    }

    public void changeSurname(UserId userId, String newSurname) {
        this.surname = newSurname;
        this.updatedBy = userId;

        this.addEvent(new CustomerSurnameChanged(this.id, this.updatedBy));
    }

    public void changePhotoLocation(UserId userId, String photoLocation) {
        this.photoLocation = photoLocation;
        this.updatedBy = userId;

        this.addEvent(new CustomerPhotoChanged(this.id, this.updatedBy));
    }

    public void delete(UserId userId) {
        if (this.isDeleted()) {
            throw new IllegalStateException();
        }

        this.deletedBy = userId;

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
        private UserId createdBy;
        private UserId updatedBy;
        private UserId deletedBy;

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

        public Customer build() {
            return new Customer(this);
        }
    }
}
