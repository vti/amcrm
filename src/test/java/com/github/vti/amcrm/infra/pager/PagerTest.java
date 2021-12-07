package com.github.vti.amcrm.infra.pager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class PagerTest {

    @Test
    public void defaults() {
        Pager pager = new Pager();

        assertEquals(Pager.DEFAULT_LIMIT, pager.getLimit());
        assertEquals(0, pager.getOffset());
    }

    @Test
    public void defaultsWhenEmpty() {
        Pager pager = new Pager(Optional.empty(), Optional.empty());

        assertEquals(Pager.DEFAULT_LIMIT, pager.getLimit());
        assertEquals(0, pager.getOffset());
    }

    @Test
    public void acceptsParameters() {
        Pager pager = new Pager(200, 10);

        assertEquals(200, pager.getLimit());
        assertEquals(10, pager.getOffset());
    }

    @Test
    public void defaultsWhenMaxLimit() {
        Pager pager = new Pager(10000);

        assertEquals(Pager.MAX_LIMIT, pager.getLimit());
    }
}
