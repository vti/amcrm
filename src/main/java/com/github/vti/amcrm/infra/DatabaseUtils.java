package com.github.vti.amcrm.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.github.vti.amcrm.domain.ActorId;

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

    public static ActorId toActorId(String value) {
        if (value == null) {
            return null;
        }

        return ActorId.of(value);
    }
}
