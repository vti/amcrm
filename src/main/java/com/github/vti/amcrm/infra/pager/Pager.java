package com.github.vti.amcrm.infra.pager;

import java.util.Objects;
import java.util.Optional;

public class Pager {
    public static int DEFAULT_LIMIT = 100;
    public static int MAX_LIMIT = 500;
    public static int DEFAULT_PAGE = 1;

    private final int limit;
    private final int page;

    public Pager() {
        this(DEFAULT_LIMIT, DEFAULT_PAGE);
    }

    public Pager(int limit) {
        this(limit, DEFAULT_PAGE);
    }

    public Pager(Optional<Integer> limit, Optional<Integer> page) {
        this(limit.orElse(DEFAULT_LIMIT), page.orElse(DEFAULT_PAGE));
    }

    public Pager(int limit, int page) {
        this.limit = Math.min(limit, MAX_LIMIT);
        this.page = page > 0 ? page : DEFAULT_PAGE;
    }

    public static Pager nextOf(Pager pager) {
        Objects.requireNonNull(pager, "pager");

        return new Pager(pager.getLimit(), pager.getPage() + 1);
    }

    public static Optional<Pager> prevOf(Pager pager) {
        Objects.requireNonNull(pager, "pager");

        return Optional.ofNullable(
                pager.getPage() > 1 ? new Pager(pager.getLimit(), pager.getPage() - 1) : null);
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    public int getOffset() {
        return (page - 1) * limit;
    }

    @Override
    public String toString() {
        return "Pager{"
                + "limit="
                + getLimit()
                + ", page="
                + getPage()
                + ", offset="
                + getOffset()
                + '}';
    }
}
