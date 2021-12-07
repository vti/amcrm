package com.github.vti.amcrm.infra.pager;

import java.util.Optional;

public class Pager {
    public static int DEFAULT_LIMIT = 100;
    public static int MAX_LIMIT = 500;

    private final int limit;
    private final int offset;

    public Pager() {
        this(DEFAULT_LIMIT);
    }

    public Pager(int limit) {
        this(limit, 0);
    }

    public Pager(Optional<Integer> limit, Optional<Integer> offset) {
        this(limit.orElse(DEFAULT_LIMIT), offset.orElse(0));
    }

    public Pager(int limit, int offset) {
        this.limit = Math.min(limit, MAX_LIMIT);
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "Pager{" + "limit=" + limit + ", offset=" + offset + '}';
    }
}
