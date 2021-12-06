package com.github.vti.amcrm.domain.customer.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;

public class DeleteCustomerCommand {
    private final CustomerRepository customerRepository;
    private final ActorId actorId;
    private final CustomerId id;

    private DeleteCustomerCommand(Builder builder) {
        this.customerRepository = Objects.requireNonNull(builder.customerRepository);
        this.actorId = Objects.requireNonNull(builder.actorId);
        this.id = Objects.requireNonNull(builder.id);
    }

    public void execute() throws CustomerNotFoundException {
        Customer customer =
                this.customerRepository.load(this.id).orElseThrow(CustomerNotFoundException::new);

        if (customer.isDeleted()) {
            throw new CustomerNotFoundException();
        }

        customer.delete(this.actorId);

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
        private ActorId actorId;
        private CustomerId id;

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

        public DeleteCustomerCommand build() {
            return new DeleteCustomerCommand(this);
        }
    }
}
