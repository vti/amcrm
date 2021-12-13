package com.github.vti.amcrm.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DefaultObjectMapperTest {

    @Test
    public void doesNotIncludeNulls() throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("foo", null);
        map.put("bar", "baz");

        String json = DefaultObjectMapper.get().writeValueAsString(map);

        assertEquals("{\"bar\":\"baz\"}", json);
    }

    @Test
    public void doesNotIncludeEmptyOptionals() throws JsonProcessingException {
        Map<String, Optional<String>> map = new HashMap<>();
        map.put("foo", Optional.empty());
        map.put("bar", Optional.of("baz"));

        String json = DefaultObjectMapper.get().writeValueAsString(map);

        assertEquals("{\"bar\":\"baz\"}", json);
    }
}
