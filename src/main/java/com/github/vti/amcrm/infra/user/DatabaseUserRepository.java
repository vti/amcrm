package com.github.vti.amcrm.infra.user;

import static com.github.vti.amcrm.db.Tables.*;
import static com.github.vti.amcrm.infra.DatabaseUtils.toActorId;
import static com.github.vti.amcrm.infra.DatabaseUtils.toLocalDateTime;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.UserRepository;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.infra.OptimisticLockException;

public class DatabaseUserRepository implements UserRepository {
    private final DataSource dataSource;

    public DatabaseUserRepository(DataSource dataSource) {
        Objects.requireNonNull(dataSource);

        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> load(UserId id) {
        Objects.requireNonNull(id);

        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Record10<
                            String,
                            Long,
                            Boolean,
                            String,
                            LocalDateTime,
                            String,
                            LocalDateTime,
                            String,
                            LocalDateTime,
                            String>
                    record =
                            create.select(
                                            USER.ID,
                                            USER.VERSION,
                                            USER.IS_ADMIN,
                                            USER.NAME,
                                            USER.CREATED_AT,
                                            USER.CREATED_BY,
                                            USER.UPDATED_AT,
                                            USER.UPDATED_BY,
                                            USER.DELETED_AT,
                                            USER.DELETED_BY)
                                    .from(USER)
                                    .where(USER.ID.eq(id.value()))
                                    .fetchOne();

            if (record == null) {
                return Optional.empty();
            } else {
                User user =
                        User.builder()
                                .id(UserId.of(record.getValue(USER.ID)))
                                .version(record.getValue(USER.VERSION))
                                .admin(record.getValue(USER.IS_ADMIN))
                                .name(record.getValue(USER.NAME))
                                .createdBy(toActorId(record.getValue(USER.CREATED_BY)))
                                .updatedBy(toActorId(record.getValue(USER.UPDATED_BY)))
                                .deletedBy(toActorId(record.getValue(USER.DELETED_BY)))
                                .build();

                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }

    @Override
    public void store(User user) throws UserExistsException {
        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            if (user.getVersion() == 0) {
                try {
                    create.insertInto(
                                    USER,
                                    USER.ID,
                                    USER.VERSION,
                                    USER.IS_ADMIN,
                                    USER.NAME,
                                    USER.CREATED_AT,
                                    USER.CREATED_BY,
                                    USER.UPDATED_AT,
                                    USER.UPDATED_BY,
                                    USER.DELETED_AT,
                                    USER.DELETED_BY)
                            .values(
                                    user.getId().value(),
                                    user.getVersion() + 1L,
                                    user.isAdmin(),
                                    user.getName(),
                                    toLocalDateTime(user.getCreatedAt()),
                                    user.getCreatedBy().value(),
                                    toLocalDateTime(user.getUpdatedAt()),
                                    Optional.ofNullable(user.getUpdatedBy())
                                            .map(ActorId::value)
                                            .orElse(null),
                                    toLocalDateTime(user.getDeletedAt()),
                                    Optional.ofNullable(user.getDeletedBy())
                                            .map(ActorId::value)
                                            .orElse(null))
                            .execute();
                } catch (DataAccessException e) {
                    if (e.getMessage().contains("UNIQUE constraint failed")) {
                        throw new UserExistsException();
                    } else {
                        throw e;
                    }
                }
            } else {
                int rowsUpdated =
                        create.update(USER)
                                .set(USER.ID, user.getId().value())
                                .set(USER.VERSION, user.getVersion() + 1L)
                                .set(USER.IS_ADMIN, user.isAdmin())
                                .set(USER.NAME, user.getName())
                                .set(USER.CREATED_AT, toLocalDateTime(user.getCreatedAt()))
                                .set(USER.CREATED_BY, user.getCreatedBy().value())
                                .set(USER.UPDATED_AT, toLocalDateTime(user.getUpdatedAt()))
                                .set(
                                        USER.UPDATED_BY,
                                        Optional.ofNullable(user.getUpdatedBy())
                                                .map(ActorId::value)
                                                .orElse(null))
                                .set(USER.DELETED_AT, toLocalDateTime(user.getDeletedAt()))
                                .set(
                                        USER.DELETED_BY,
                                        Optional.ofNullable(user.getDeletedBy())
                                                .map(ActorId::value)
                                                .orElse(null))
                                .where(USER.ID.eq(user.getId().value()))
                                .and(USER.VERSION.eq(user.getVersion()))
                                .execute();

                if (rowsUpdated == 0) {
                    throw new OptimisticLockException(user.getVersion());
                }
            }

            storeEvents(connection, user.getEvents());

            connection.commit();

            user.incrementVersion();
            user.clearEvents();
        } catch (SQLException e) {
            throw new RuntimeException("Error storing", e);
        }
    }

    @Override
    public boolean isEmpty() {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);

            Record1<String> record = create.select(USER.ID).from(USER).fetchOne();

            if (record == null) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Empty check failed", e);
        }
    }

    private void storeEvents(Connection connection, List<Event<UserId>> events) {
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

        for (Event<UserId> event : events) {
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
                            event.getOriginId().value(),
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
