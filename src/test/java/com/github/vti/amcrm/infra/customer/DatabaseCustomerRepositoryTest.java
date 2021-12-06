package com.github.vti.amcrm.infra.customer;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.infra.OptimisticLockException;
import com.github.vti.amcrm.infra.TestDatabase;

class DatabaseCustomerRepositoryTest {

    private DataSource dataSource;
    private CustomerRepository customerRepository;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDatabase.setupDatabase(tmpDir);
        customerRepository = new DatabaseCustomerRepository(dataSource);
    }

    @Test
    void returnsEmptyOnUnknownId() {
        assertEquals(
                false, customerRepository.load(CustomerId.of(TestData.getRandomId())).isPresent());
    }

    @Test
    void loadsCustomer() throws Exception {
        Customer customer = TestFactory.newCustomer();

        customerRepository.store(customer);

        Optional<Customer> customerOptional = customerRepository.load(customer.getId());

        assertEquals(true, customerOptional.isPresent());
        assertEquals(customer.getName(), customerOptional.get().getName());
    }

    @Test
    void storesCustomer() throws Exception {
        Customer customer = TestFactory.newCustomer();

        customerRepository.store(customer);

        Customer loadedCustomer = customerRepository.load(customer.getId()).get();

        assertEquals(customer.getId(), loadedCustomer.getId());
        assertEquals(1L, loadedCustomer.getVersion());
        assertEquals(customer.getName(), loadedCustomer.getName());
        assertEquals(customer.getSurname(), loadedCustomer.getSurname());
    }

    @Test
    void storingClearsAllEvents() throws Exception {
        Customer customer = TestFactory.newCustomer();

        assertTrue(customer.getEvents().size() != 0);

        customerRepository.store(customer);

        assertEquals(0, customer.getEvents().size());
    }

    @Test
    void storesUpdatedCustomer() throws Exception {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customerRepository.store(customer);

        customer.changeName(actorId, "Johnny");

        customerRepository.store(customer);

        Optional<Customer> customerOptional = customerRepository.load(customer.getId());

        assertEquals("Johnny", customerOptional.get().getName());
        assertTrue(customerOptional.get().getUpdatedBy().equals(actorId));
    }

    @Test
    void throwsWhenCustomerAlreadyExists() throws Exception {
        Customer customer1 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();
        Customer customer2 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();

        customerRepository.store(customer1);

        assertThrows(
                CustomerExistsException.class,
                () -> {
                    customerRepository.store(customer2);
                });
    }

    @Test
    void optimisticLockingPreventsCorruptionDuringUpdate() throws Exception {
        Customer customer = TestFactory.newCustomer();

        assertEquals(0, customer.getVersion());

        customerRepository.store(customer);

        assertEquals(1, customer.getVersion());

        Customer customer1 = customerRepository.load(customer.getId()).get();
        Customer customer2 = customerRepository.load(customer.getId()).get();

        assertEquals(1, customer1.getVersion());
        assertEquals(1, customer2.getVersion());

        customerRepository.store(customer1);

        assertEquals(2, customer1.getVersion());
        assertEquals(1, customer2.getVersion());

        assertThrows(
                OptimisticLockException.class,
                () -> {
                    customerRepository.store(customer2);
                });
    }

    @Test
    void softDeletes() throws Exception {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.delete(actorId);

        customerRepository.store(customer);

        Customer loadedCustomer = customerRepository.load(customer.getId()).get();

        assertTrue(loadedCustomer.isDeleted());
        assertEquals(actorId, loadedCustomer.getDeletedBy());
    }
}
