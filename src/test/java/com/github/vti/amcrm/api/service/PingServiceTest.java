package com.github.vti.amcrm.api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.vti.amcrm.api.service.request.PingRequest;

@Tag("integration")
public class PingServiceTest {

    private PingService service;

    @BeforeEach
    void setUp() {
        service = new PingService();
    }

    @Test
    void testService() {
        Object res = service.ping(new PingRequest());

        assertNotNull(res);
    }
}
