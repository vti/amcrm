package com.github.vti.amcrm.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;

public class ClientTest {

    @Test
    void isInRole() {
        assertTrue(Client.admin(TestData.getRandomId()).isInRole(Client.Role.ANONYMOUS));
        assertTrue(Client.admin(TestData.getRandomId()).isInRole(Client.Role.USER));
        assertTrue(Client.admin(TestData.getRandomId()).isInRole(Client.Role.ADMIN));

        assertTrue(Client.user(TestData.getRandomId()).isInRole(Client.Role.ANONYMOUS));
        assertTrue(Client.user(TestData.getRandomId()).isInRole(Client.Role.USER));
        assertFalse(Client.user(TestData.getRandomId()).isInRole(Client.Role.ADMIN));

        assertTrue(Client.anonymous().isInRole(Client.Role.ANONYMOUS));
        assertFalse(Client.anonymous().isInRole(Client.Role.USER));
        assertFalse(Client.anonymous().isInRole(Client.Role.ADMIN));
    }

    @Test
    void testToString() {
        assertNotNull(Client.anonymous().toString());
    }

    @Test
    void testToCompactString() {
        assertNotNull(Client.anonymous().toCompactString());
    }
}
