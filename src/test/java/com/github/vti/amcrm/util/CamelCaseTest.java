package com.github.vti.amcrm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CamelCaseTest {

    @Test
    void givenCamelCaseConvertsToSnakeCase() {
        assertEquals("foo-bar", CamelCase.camelToSnake("FooBar"));
    }
}
