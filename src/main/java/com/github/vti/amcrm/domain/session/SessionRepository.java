package com.github.vti.amcrm.domain.session;

import java.util.Optional;

public interface SessionRepository {
    Optional<Session> load(SessionId id);

    void store(Session session);
}
