package com.github.vti.amcrm.infra.pager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PagerTest {

    @Test
    public void defaults() {
        Pager pager = new Pager();

        assertEquals(Pager.DEFAULT_LIMIT, pager.getLimit());
        assertEquals(1, pager.getPage());
        assertEquals(0, pager.getOffset());
    }

    @Test
    public void defaultsWhenEmpty() {
        Pager pager = new Pager(Optional.empty(), Optional.empty());

        assertEquals(Pager.DEFAULT_LIMIT, pager.getLimit());
        assertEquals(1, pager.getPage());
        assertEquals(0, pager.getOffset());
    }

    @Test
    public void buildsNextPager() {
        Pager pager = new Pager(10, 1);

        Pager nextPager = Pager.nextOf(pager);

        assertEquals(pager.getLimit(), nextPager.getLimit());
        assertEquals(pager.getPage() + 1, nextPager.getPage());
        assertEquals(pager.getOffset() + pager.getLimit(), nextPager.getOffset());
    }

    @Test
    public void buildsPrevPager() {
        Pager pager = new Pager(10, 2);

        Pager nextPager = Pager.prevOf(pager).get();

        assertEquals(pager.getLimit(), nextPager.getLimit());
        assertEquals(pager.getPage() - 1, nextPager.getPage());
        assertEquals(pager.getOffset() - pager.getLimit(), nextPager.getOffset());
    }

    @Test
    public void noPrevPagerWhenFirstPage() {
        Pager pager = new Pager(10, 1);

        assertFalse(Pager.prevOf(pager).isPresent());
    }

    @ParameterizedTest
    @MethodSource("paginationValues")
    public void performsPagination(int page, int expectedOffset) {
        Pager pager = new Pager(200, page);

        assertEquals(200, pager.getLimit());
        assertEquals(page, pager.getPage());
        assertEquals(expectedOffset, pager.getOffset());
    }

    private static Stream<Arguments> paginationValues() {
        return Stream.of(Arguments.of(1, 0), Arguments.of(2, 200), Arguments.of(10, 1800));
    }

    @Test
    public void defaultsWhenMaxLimit() {
        Pager pager = new Pager(10000);

        assertEquals(Pager.MAX_LIMIT, pager.getLimit());
    }

    @Test
    public void defaultsWhenInvalidPage() {
        Pager pager = new Pager(100, -10);

        assertEquals(1, pager.getPage());
    }
}
