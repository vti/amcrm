package com.github.vti.amcrm.domain.session;

import java.util.Objects;

public final class SessionId {
    private final String id;

    private SessionId(String id) {
        this.id = id;
    }

    public static SessionId of(String id) {
        return new SessionId(id);
    }

    public String value() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SessionId{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionId that = (SessionId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
