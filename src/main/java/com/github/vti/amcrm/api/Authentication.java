package com.github.vti.amcrm.api;

import java.time.Duration;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.session.SessionRepository;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;

public class Authentication {
    private static final Logger log = LogManager.getLogger(Authentication.class);

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public Authentication(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    public Client authenticate(String token) {
        if (token == null || token.isEmpty()) {
            log.info("No token available");
            return anonymous();
        }

        Optional<Session> optionalSession = sessionRepository.load(SessionId.of(token));

        if (!optionalSession.isPresent()) {
            log.info("No session found");

            return anonymous();
        }

        Session session = optionalSession.get();

        if (session.isExpired()) {
            log.info("Session expired");

            return anonymous();
        }

        Optional<User> optionalUser = userRepository.load(UserId.of(session.getActorId().value()));

        if (!optionalUser.isPresent()) {
            log.info("Session found, but not user");

            return anonymous();
        }

        User user = optionalUser.get();

        session.prolong(Duration.ofHours(1));
        log.info("Extended session: {}", session.getExpiresAt());

        if (user.isAdmin()) {
            log.info("Admin session found");

            return Client.admin(user.getId().value());
        } else {
            log.info("User session found");

            return Client.user(user.getId().value());
        }
    }

    public Client anonymous() {
        return Client.anonymous();
    }
}
