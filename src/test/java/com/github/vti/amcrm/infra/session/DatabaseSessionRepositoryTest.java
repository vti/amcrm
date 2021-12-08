package com.github.vti.amcrm.infra.session;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.session.SessionRepository;
import com.github.vti.amcrm.infra.TestDatabase;

public class DatabaseSessionRepositoryTest {

    private DataSource dataSource;
    private SessionRepository sessionRepository;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = TestDatabase.setupDatabase(tmpDir);
        sessionRepository = new DatabaseSessionRepository(dataSource);
    }

    @Test
    void returnsEmptyOnUnknownId() {
        assertEquals(
                false, sessionRepository.load(SessionId.of(TestData.getRandomId())).isPresent());
    }

    @Test
    void loadsSession() throws Exception {
        Session session = TestFactory.newSession(ActorId.of(TestData.getRandomId()));

        sessionRepository.store(session);

        Optional<Session> sessionOptional = sessionRepository.load(session.getId());

        assertEquals(true, sessionOptional.isPresent());
        assertFalse(session.isExpired());
    }

    @Test
    void storesSession() throws Exception {
        Session session = TestFactory.newSession(ActorId.of(TestData.getRandomId()));

        sessionRepository.store(session);

        Session loadedSession = sessionRepository.load(session.getId()).get();

        assertEquals(session.getId(), loadedSession.getId());
        assertFalse(session.isExpired());
    }

    @Test
    void storesUpdatedSession() throws Exception {
        ActorId actorId = ActorId.of(TestData.getRandomId());
        Session session = TestFactory.newExpiredSession(ActorId.of(TestData.getRandomId()));

        sessionRepository.store(session);

        session.prolong(Duration.ofHours(1));

        sessionRepository.store(session);

        Optional<Session> sessionOptional = sessionRepository.load(session.getId());

        assertFalse(sessionOptional.get().isExpired());
    }
}
