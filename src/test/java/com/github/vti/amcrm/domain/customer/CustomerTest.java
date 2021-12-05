package com.github.vti.amcrm.domain.customer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.user.UserId;

class CustomerTest {

    @Test
    void builder() {
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        assertEquals("John", customer.getName());
    }

    @Test
    void changesName() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        customer.changeName(userId, "Johnny");

        assertEquals("Johnny", customer.getName());
        assertEquals(userId, customer.getUpdatedBy());
    }

    @Test
    void changesSurname() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().surname("Doe").build();

        customer.changeSurname(userId, "Silver");

        assertEquals("Silver", customer.getSurname());
        assertEquals(userId, customer.getUpdatedBy());
    }

    @Test
    void delete() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.delete(userId);

        assertEquals(true, customer.isDeleted());
        assertEquals(userId, customer.getDeletedBy());
    }

    @Test
    void throwsWhenDeletingAlreadyDeleted() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.delete(userId);

        assertThrows(IllegalStateException.class, () -> customer.delete(userId));
    }

    @Test
    void customerWithSameIdAreEqual() {
        Customer customer1 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();
        Customer customer2 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();

        assertTrue(customer1.equals(customer2));
    }
}
