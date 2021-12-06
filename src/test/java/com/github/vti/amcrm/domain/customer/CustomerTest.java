package com.github.vti.amcrm.domain.customer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.event.*;

class CustomerTest {

    @Test
    void buildsNewCustomer() {
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        assertEquals("John", customer.getName());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getCreatedBy(), customer.getEvents().get(0).getActorId());
        assertEquals(CustomerCreated.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void changesName() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().name("John").build();

        customer.clearEvents();

        customer.changeName(actorId, "Johnny");

        assertEquals("Johnny", customer.getName());
        assertEquals(actorId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getActorId());
        assertEquals(
                CustomerNameChanged.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void changesSurname() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().surname("Doe").build();

        customer.clearEvents();

        customer.changeSurname(actorId, "Silver");

        assertEquals("Silver", customer.getSurname());
        assertEquals(actorId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getActorId());
        assertEquals(
                CustomerSurnameChanged.class.getSimpleName(),
                customer.getEvents().get(0).getName());
    }

    @Test
    void changesPhotoLocation() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomerBuilder().surname("Doe").build();

        customer.clearEvents();

        customer.changePhotoLocation(actorId, "http://some.url/photo.jpg");

        assertEquals("http://some.url/photo.jpg", customer.getPhotoLocation().get());
        assertEquals(actorId, customer.getUpdatedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getUpdatedBy(), customer.getEvents().get(0).getActorId());
        assertEquals(
                CustomerPhotoChanged.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void delete() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.clearEvents();

        customer.delete(actorId);

        assertEquals(true, customer.isDeleted());
        assertEquals(actorId, customer.getDeletedBy());

        assertEquals(1, customer.getEvents().size());
        assertEquals(customer.getId(), customer.getEvents().get(0).getOriginId());
        assertEquals(customer.getDeletedBy(), customer.getEvents().get(0).getActorId());
        assertEquals(CustomerDeleted.class.getSimpleName(), customer.getEvents().get(0).getName());
    }

    @Test
    void throwsWhenDeletingAlreadyDeleted() {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Customer customer = TestFactory.newCustomer();

        customer.delete(actorId);

        assertThrows(IllegalStateException.class, () -> customer.delete(actorId));
    }

    @Test
    void customerWithSameIdAreEqual() {
        Customer customer1 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();
        Customer customer2 = TestFactory.newCustomerBuilder().id(CustomerId.of("1")).build();

        assertTrue(customer1.equals(customer2));
    }
}
