package com.github.vti.amcrm.domain.customer.command;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;
import com.github.vti.amcrm.infra.customer.MemoryCustomerRepository;

class PatchCustomerCommandTest {
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        Map<CustomerId, Customer> storage = new HashMap<>();
        customerRepository = new MemoryCustomerRepository(storage);
    }

    @Test
    public void patchesExistingCustomer()
            throws CustomerExistsException, CustomerNotFoundException {
        CustomerId customerId = createCustomer();
        ActorId actorId = ActorId.of(TestData.getRandomId());

        PatchCustomerCommand command =
                PatchCustomerCommand.builder()
                        .customerRepository(customerRepository)
                        .actorId(actorId)
                        .id(customerId)
                        .name("Johnny")
                        .build();

        command.execute();

        Customer customer = customerRepository.load(customerId).get();

        assertEquals("Johnny", customer.getName());
    }

    @Test
    public void throwsOnUnknownCustomer()
            throws CustomerExistsException, CustomerNotFoundException {
        ActorId actorId = ActorId.of(TestData.getRandomId());

        PatchCustomerCommand command =
                PatchCustomerCommand.builder()
                        .customerRepository(customerRepository)
                        .actorId(actorId)
                        .id(CustomerId.of(TestData.getRandomId()))
                        .name(TestData.getRandomName())
                        .build();

        assertThrows(CustomerNotFoundException.class, () -> command.execute());
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
