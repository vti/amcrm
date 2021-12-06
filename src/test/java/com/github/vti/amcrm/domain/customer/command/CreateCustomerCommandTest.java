package com.github.vti.amcrm.domain.customer.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.infra.customer.MemoryCustomerRepository;

public class CreateCustomerCommandTest {

    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        Map<CustomerId, Customer> storage = new HashMap<>();
        customerRepository = new MemoryCustomerRepository(storage);
    }

    @Test
    public void createsCustomer() throws CustomerExistsException {
        CustomerId customerId = createCustomer();

        Optional<Customer> customer = customerRepository.load(customerId);

        assertTrue(customer.isPresent());
    }

    public CustomerId createCustomer() throws CustomerExistsException {
        CustomerId customerId = CustomerId.of(TestData.getRandomId());
        ActorId actorId = ActorId.of(TestData.getRandomId());

        CreateCustomerCommand command =
                CreateCustomerCommand.builder()
                        .customerRepository(customerRepository)
                        .actorId(actorId)
                        .id(customerId)
                        .name(TestData.getRandomName())
                        .surname(TestData.getRandomSurname())
                        .build();

        command.execute();

        return customerId;
    }
}
