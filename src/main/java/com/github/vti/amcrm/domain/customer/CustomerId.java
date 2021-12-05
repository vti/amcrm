package com.github.vti.amcrm.domain.customer;

import java.util.Objects;

public final class CustomerId {
    private final String id;

    private CustomerId(String id) {
        this.id = id;
    }

    public static CustomerId of(String id) {
        return new CustomerId(id);
    }

    public String value() {
        return this.id;
    }

    @Override
    public String toString() {
        return "CustomerId{" + "id=" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
