package com.github.vti.amcrm.domain.customer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.customer.event.*;
import com.github.vti.amcrm.domain.user.UserId;

class CustomerTest {

    @Test
    void buildsNewCustomer() {
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        assertEquals("John", customer.getName());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getCreatedBy(), customer.getEvents().get(0).getUserId());
        assertEquals(CustomerCreated.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void changesName() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        customer.clearEvents();

        customer.changeName(userId, "Johnny");

        assertEquals("Johnny", customer.getName());
        assertEquals(userId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getUserId());
        assertEquals(
                CustomerNameChanged.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void changesSurname() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().surname("Doe").build();

        customer.clearEvents();

        customer.changeSurname(userId, "Silver");

        assertEquals("Silver", customer.getSurname());
        assertEquals(userId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getUserId());
        assertEquals(
                CustomerSurnameChanged.class.getSimpleName(),
                customer.getEvents().get(0).getName());
    }

    @Test
    void changesPhotoLocation() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().surname("Doe").build();

        customer.clearEvents();

        customer.changePhotoLocation(userId, "http://some.url/photo.jpg");

        assertEquals("http://some.url/photo.jpg", customer.getPhotoLocation().get());
        assertEquals(userId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getUserId());
        assertEquals(
                CustomerPhotoChanged.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void delete() {
        UserId userId = UserId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.clearEvents();

        customer.delete(userId);

        assertEquals(true, customer.isDeleted());
        assertEquals(userId, customer.getDeletedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getDeletedBy(), customer.getEvents().get(0).getUserId());
        assertEquals(CustomerDeleted.class.getSimpleName(), customer.getEvents().get(0).getName());
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
