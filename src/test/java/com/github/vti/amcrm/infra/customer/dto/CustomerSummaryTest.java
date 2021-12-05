package com.github.vti.amcrm.infra.customer.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CustomerSummaryTest {

    @Test
    public void returnsPrefixedPhotoLocationWhenNotAbsolute() {
        CustomerSummary customer =
                CustomerSummary.builder()
                        .baseUrl("http://localhost:8080")
                        .photoLocation("customer/123.jpg")
                        .build();

        assertEquals("http://localhost:8080/customer/123.jpg", customer.getPhotoLocation());
    }

    @Test
    public void returnsPhotoLocationWhenAbsolute() {
        CustomerSummary customer =
                CustomerSummary.builder()
                        .baseUrl("http://localhost:8080")
                        .photoLocation("http://other.url/customer/123.jpg")
                        .build();

        assertEquals("http://other.url/customer/123.jpg", customer.getPhotoLocation());
    }
}
