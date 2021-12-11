package com.github.vti.amcrm.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.github.vti.amcrm.infra.pager.Pager;

public class LinkHeader {
    private final URI base;
    private final Pager pager;

    public LinkHeader(String base, Pager pager) {
        Objects.requireNonNull(base, "baseUrl");
        Objects.requireNonNull(pager, "pager");

        try {
            this.base = new URI(base);
            this.pager = pager;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid base URI", e);
        }
    }

    private String buildUrl(Pager pager) {
        String appendQuery = String.format("limit=%s&page=%s", pager.getLimit(), pager.getPage());

        String newQuery = base.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery = newQuery.replaceAll("(?:\\?|\\&)?limit=.*?(?:\\&|$)", "");
            newQuery = newQuery.replaceAll("(?:\\?|\\&)?page=.*?(?:\\&|$)", "");
            newQuery += "&" + appendQuery;
        }

        try {
            return new URI(
                            base.getScheme(),
                            base.getAuthority(),
                            base.getPath(),
                            newQuery,
                            base.getFragment())
                    .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI", e);
        }
    }

    @Override
    public String toString() {
        if (Pager.prevOf(pager).isPresent()) {
            Pager prevPager = Pager.prevOf(pager).get();
            Pager nextPager = Pager.nextOf(pager);

            return String.format(
                    "<%s>; rel=\"prev\", <%s>; rel=\"next\"",
                    buildUrl(prevPager), buildUrl(nextPager));
        } else {
            Pager nextPager = Pager.nextOf(pager);

            return String.format("<%s>; rel=\"next\"", buildUrl(nextPager));
        }
    }
}
