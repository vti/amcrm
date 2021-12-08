package com.github.vti.amcrm.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.TestFactory;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionRepository;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.MemoryStorage;
import com.github.vti.amcrm.infra.registry.MemoryRepositoryRegistry;

public class AuthenticationTest {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MemoryStorage storage = new MemoryStorage();
        RepositoryRegistry repositoryRegistry = new MemoryRepositoryRegistry(storage);
        sessionRepository = repositoryRegistry.getSessionRepository();
        userRepository = repositoryRegistry.getUserRepository();
        authentication = new Authentication(sessionRepository, userRepository);
    }

    @Test
    public void authenticatesAdmin() throws UserExistsException {
        User user = TestFactory.newUserBuilder().admin(true).build();
        userRepository.store(user);

        Session session = TestFactory.newSession(ActorId.of(user.getId().value()));
        sessionRepository.store(session);

        assertTrue(authentication.authenticate(session.getId().value()).isAdmin());
    }

    @Test
    public void authenticatesUser() throws UserExistsException {
        User user = TestFactory.newUserBuilder().build();
        userRepository.store(user);

        Session session = TestFactory.newSession(ActorId.of(user.getId().value()));
        sessionRepository.store(session);

        assertTrue(authentication.authenticate(session.getId().value()).isUser());
    }

    @Test
    public void anonymousOnExpiredSession() throws UserExistsException {
        User user = TestFactory.newUserBuilder().build();
        userRepository.store(user);

        Session session = TestFactory.newExpiredSession(ActorId.of(user.getId().value()));
        sessionRepository.store(session);

        assertTrue(authentication.authenticate(session.getId().value()).isAnonymous());
    }

    @Test
    public void anonymousOnUnknownUser() throws UserExistsException {
        Session session = TestFactory.newSession(ActorId.of(TestData.getRandomId()));
        sessionRepository.store(session);

        assertTrue(authentication.authenticate(session.getId().value()).isAnonymous());
    }

    @Test
    public void anonymousOnInvalidToken() {
        assertTrue(authentication.authenticate(null).isAnonymous());

        assertTrue(authentication.authenticate("garbage").isAnonymous());
    }
}
