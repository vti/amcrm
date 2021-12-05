package com.github.vti.amcrm.domain.user;

import java.util.Objects;

public class UserId {
    private final String id;

    private UserId(String id) {
        this.id = id;
    }

    public static UserId of(String id) {
        Objects.requireNonNull(id);

        return new UserId(id);
    }

    public String value() {
        return this.id;
    }

    @Override
    public String toString() {
        return "UserId{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId that = (UserId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
