package com.github.vti.amcrm.domain;

import java.util.ArrayList;
import java.util.List;

public class Entity<T> {
    private final List<T> events = new ArrayList<>();

    public List<T> getEvents() {
        return this.events;
    }

    public void addEvent(T event) {
        this.events.add(event);
    }

    public void clearEvents() {
        this.events.clear();
    }
}
