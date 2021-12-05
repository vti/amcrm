package com.github.vti.amcrm.domain.customer.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;
import com.github.vti.amcrm.domain.user.UserId;

public class PatchCustomerCommand {
    private CustomerRepository customerRepository;
    private UserId userId;
    private CustomerId id;
    private String name;
    private String surname;
    private String photoLocation;

    private PatchCustomerCommand(Builder builder) {
        this.customerRepository = Objects.requireNonNull(builder.customerRepository);
        this.userId = Objects.requireNonNull(builder.userId);
        this.id = Objects.requireNonNull(builder.id);
        this.name = builder.name;
        this.surname = builder.surname;
        this.photoLocation = builder.photoLocation;
    }

    public void execute() throws CustomerNotFoundException {
        Customer customer =
                this.customerRepository.load(this.id).orElseThrow(CustomerNotFoundException::new);

        if (customer.isDeleted()) {
            throw new CustomerNotFoundException();
        }

        if (this.name != null) {
            customer.changeName(this.userId, this.name);
        }

        if (this.surname != null) {
            customer.changeSurname(this.userId, this.surname);
        }

        if (this.photoLocation != null) {
            customer.changePhotoLocation(this.userId, this.photoLocation);
        }

        try {
            this.customerRepository.store(customer);
        } catch (CustomerExistsException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CustomerRepository customerRepository;
        private UserId userId;
        private CustomerId id;
        private String name;
        private String surname;
        private String photoLocation;

        public Builder customerRepository(CustomerRepository customerRepository) {
            this.customerRepository = customerRepository;
            return this;
        }

        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        public Builder id(CustomerId id) {
            this.id = id;
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

        public PatchCustomerCommand build() {
            return new PatchCustomerCommand(this);
        }
    }
}
