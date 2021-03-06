package com.github.vti.amcrm.domain.customer.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;

public class CreateCustomerCommand {
    private final CustomerRepository customerRepository;
    private final ActorId actorId;
    private final CustomerId id;
    private final String name;
    private final String surname;
    private final String photoLocation;

    private CreateCustomerCommand(Builder builder) {
        this.customerRepository =
                Objects.requireNonNull(builder.customerRepository, "customerRepository");
        this.actorId = Objects.requireNonNull(builder.actorId, "actorId");
        this.id = Objects.requireNonNull(builder.id, "id");
        this.name = Objects.requireNonNull(builder.name, "name");
        this.surname = Objects.requireNonNull(builder.surname, "surname");
        this.photoLocation = builder.photoLocation;
    }

    public void execute() throws CustomerExistsException {
        Customer customer =
                Customer.builder()
                        .id(this.id)
                        .name(this.name)
                        .surname(this.surname)
                        .photoLocation(this.photoLocation)
                        .createdBy(this.actorId)
                        .build();

        this.customerRepository.store(customer);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CustomerRepository customerRepository;
        private ActorId actorId;
        private CustomerId id;
        private String name;
        private String surname;
        private String photoLocation;

        public Builder customerRepository(CustomerRepository customerRepository) {
            this.customerRepository = customerRepository;
            return this;
        }

        public Builder actorId(ActorId actorId) {
            this.actorId = actorId;
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

        public CreateCustomerCommand build() {
            return new CreateCustomerCommand(this);
        }
    }
}
