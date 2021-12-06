package com.github.vti.amcrm.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DatabaseUtils {

    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static Instant toInstant(LocalDateTime datetime) {
        if (datetime == null) {
            return null;
        }

        return datetime.toInstant(ZoneOffset.UTC);
    }
}
