package com.github.vti.amcrm.domain;

import java.util.Objects;

public class ActorId {
    private final String id;

    private ActorId(String id) {
        this.id = id;
    }

    public static ActorId of(String id) {
        Objects.requireNonNull(id);

        return new ActorId(id);
    }

    public String value() {
        return id;
    }

    @Override
    public String toString() {
        return "ActorId{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActorId that = (ActorId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
