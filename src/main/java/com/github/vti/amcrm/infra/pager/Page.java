package com.github.vti.amcrm.infra.pager;

import java.util.List;

public class Page<T> {
    private final List<T> items;
    private final Pager pager;

    public Page(List<T> items, Pager pager) {
        this.items = items;
        this.pager = pager;
    }

    public List<T> getItems() {
        return items;
    }

    public Pager getPager() {
        return pager;
    }
}
