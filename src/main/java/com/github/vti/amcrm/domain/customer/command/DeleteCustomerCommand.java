package com.github.vti.amcrm.domain.customer.command;

import java.util.Objects;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;
import com.github.vti.amcrm.domain.user.UserId;

public class DeleteCustomerCommand {
    private CustomerRepository customerRepository;
    private UserId userId;
    private CustomerId id;

    private DeleteCustomerCommand(Builder builder) {
        this.customerRepository = Objects.requireNonNull(builder.customerRepository);
        this.userId = Objects.requireNonNull(builder.userId);
        this.id = Objects.requireNonNull(builder.id);
    }

    public void execute() throws CustomerNotFoundException {
        Customer customer =
                this.customerRepository.load(this.id).orElseThrow(CustomerNotFoundException::new);

        if (customer.isDeleted()) {
            throw new CustomerNotFoundException();
        }

        customer.delete(this.userId);

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

        public DeleteCustomerCommand build() {
            return new DeleteCustomerCommand(this);
        }
    }
}
