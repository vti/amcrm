package com.github.vti.amcrm.domain.session;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;

public class SessionTest {

    @Test
    public void createsNewSession() {
        Session session = TestFactory.newSession(ActorId.of(TestData.getRandomId()));

        assertFalse(session.isExpired());
    }

    @Test
    public void expiresSession() {
        Session session = TestFactory.newExpiredSession(ActorId.of(TestData.getRandomId()));

        assertTrue(session.isExpired());
    }

    @Test
    public void prolongsSession() {
        Session session = TestFactory.newExpiredSession(ActorId.of(TestData.getRandomId()));

        assertTrue(session.isExpired());

        session.prolong(Duration.ofHours(2));

        assertFalse(session.isExpired());
    }
}
