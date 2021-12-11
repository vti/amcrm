package com.github.vti.amcrm.infra;

import static com.github.vti.amcrm.db.Tables.EVENT;

import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;

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

    public static void storeEvents(Connection connection, List<Event> events) {
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

        for (Event event : events) {
            create.insertInto(
                            EVENT,
                            EVENT.CREATED_AT,
                            EVENT.NAME,
                            EVENT.ORIGIN_ID,
                            EVENT.USER_ID,
                            EVENT.PAYLOAD)
                    .values(
                            toLocalDateTime(event.getCreatedAt()),
                            event.getName(),
                            event.getOriginId(),
                            event.getActorId().value(),
                            event.getPayload()
                                    .map(
                                            p -> {
                                                try {
                                                    return Arrays.toString(
                                                            DefaultObjectMapper.get()
                                                                    .writeValueAsBytes(p));
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(
                                                            "Event payload serialization failed",
                                                            e);
                                                }
                                            })
                                    .orElse(null))
                    .execute();
        }
    }
}
