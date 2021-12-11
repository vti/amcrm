package com.github.vti.amcrm.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.infra.pager.Pager;

public class LinkHeaderTest {

    @Test
    public void buildsHeader() {
        LinkHeader header = new LinkHeader("http://example.com", new Pager(10, 2));

        assertEquals(
                ""
                        + "<http://example.com?limit=10&page=1>; rel=\"prev\", "
                        + "<http://example.com?limit=10&page=3>; rel=\"next\"",
                header.toString());
    }

    @Test
    public void noPrev() {
        LinkHeader header = new LinkHeader("http://example.com", new Pager(10, 1));

        assertEquals("" + "<http://example.com?limit=10&page=2>; rel=\"next\"", header.toString());
    }

    @Test
    public void uriWithQuery() {
        LinkHeader header = new LinkHeader("http://example.com?foo=bar", new Pager(10, 2));

        assertEquals(
                ""
                        + "<http://example.com?foo=bar&limit=10&page=1>; rel=\"prev\", "
                        + "<http://example.com?foo=bar&limit=10&page=3>; rel=\"next\"",
                header.toString());
    }

    @Test
    public void uriWithQueryOverwrite() {
        LinkHeader header =
                new LinkHeader("http://example.com?foo=bar&limit=40&page=10", new Pager(10, 2));

        assertEquals(
                ""
                        + "<http://example.com?foo=bar&limit=10&page=1>; rel=\"prev\", "
                        + "<http://example.com?foo=bar&limit=10&page=3>; rel=\"next\"",
                header.toString());
    }
}
