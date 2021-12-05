package com.github.vti.amcrm.domain.customer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CustomerIdTest {

    @Test
    void keepsTheValue() {
        CustomerId customerId = CustomerId.of("1");

        assertEquals("1", customerId.value());
    }

    @Test
    void equalsByValue() {
        CustomerId customerId = CustomerId.of("1");

        assertTrue(customerId.equals(CustomerId.of("1")));
    }

    @Test
    void notEqualsByValue() {
        CustomerId customerId = CustomerId.of("1");

        assertFalse(customerId.equals(CustomerId.of("2")));
    }
}
