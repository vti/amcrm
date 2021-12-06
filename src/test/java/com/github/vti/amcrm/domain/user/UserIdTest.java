package com.github.vti.amcrm.domain.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserIdTest {

    @Test
    void keepsTheValue() {
        UserId customerId = UserId.of("1");

        assertEquals("1", customerId.value());
    }

    @Test
    void equalsByValue() {
        UserId customerId = UserId.of("1");

        assertTrue(customerId.equals(UserId.of("1")));
    }

    @Test
    void notEqualsByValue() {
        UserId customerId = UserId.of("1");

        assertFalse(customerId.equals(UserId.of("2")));
    }
}
