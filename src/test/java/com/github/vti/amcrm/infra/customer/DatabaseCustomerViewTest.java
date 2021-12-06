package com.github.vti.amcrm.infra.customer;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.infra.TestDatabase;
import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;

public class DatabaseCustomerViewTest {
    private DataSource dataSource;
    private CustomerRepository customerRepository;
    private DatabaseCustomerView customerView;
    private final String baseUrl = "http://localhost:4567";

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDatabase.setupDatabase(tmpDir);
        customerRepository = new DatabaseCustomerRepository(dataSource);
        customerView = new DatabaseCustomerView(dataSource, baseUrl);
    }

    @Test
    void returnsEmptyDetailsResult() {
        assertEquals(false, customerView.load(TestData.getRandomId()).isPresent());
    }

    @Test
    void returnsDetails() {
        Customer customer = createCustomer();

        CustomerSummary details = customerView.load(customer.getId().value()).get();

        assertEquals(customer.getId().value(), details.getId());
        assertEquals(customer.getName(), details.getName());
        assertEquals(customer.getSurname(), details.getSurname());
    }

    @Test
    void doesNotReturnDeleted() throws Exception {
        Customer customer = createCustomer();

        customer.delete(ActorId.of(TestData.getRandomId()));

        customerRepository.store(customer);

        assertFalse(customerView.load(customer.getId().value()).isPresent());
    }

    @Test
    void returnsEmptySummaryList() {
        assertEquals(0, customerView.find().size());
    }

    @Test
    void returnsSummaryList() {
        createCustomer();

        assertEquals(1, customerView.find().size());
    }

    private Customer createCustomer() {
        Customer customer = TestFactory.newCustomer();

        try {
            customerRepository.store(customer);
        } catch (CustomerExistsException e) {
            throw new RuntimeException(e);
        }

        return customer;
    }
}
