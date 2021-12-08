package com.github.vti.amcrm.infra.session;

import java.util.Map;
import java.util.Optional;

import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.session.SessionRepository;

public class MemorySessionRepository implements SessionRepository {
    private final Map<SessionId, Session> storage;

    public MemorySessionRepository(Map<SessionId, Session> storage) {
        this.storage = storage;
    }

    @Override
    public Optional<Session> load(SessionId id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void store(Session session) {
        storage.put(session.getId(), session);
    }
}
