package com.github.vti.amcrm.infra.session;

import static com.github.vti.amcrm.db.Tables.SESSION;
import static com.github.vti.amcrm.infra.DatabaseUtils.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.*;
import org.jooq.impl.DSL;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.session.SessionRepository;

public class DatabaseSessionRepository implements SessionRepository {
    private static final Logger log = LogManager.getLogger(DatabaseSessionRepository.class);

    private final DataSource dataSource;

    public DatabaseSessionRepository(DataSource dataSource) {
        Objects.requireNonNull(dataSource);

        this.dataSource = dataSource;
    }

    @Override
    public Optional<Session> load(SessionId id) {
        Objects.requireNonNull(id);

        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Record4<String, String, LocalDateTime, LocalDateTime> record =
                    create.select(
                                    SESSION.ID,
                                    SESSION.USER_ID,
                                    SESSION.CREATED_AT,
                                    SESSION.EXPIRES_AT)
                            .from(SESSION)
                            .where(SESSION.ID.eq(id.value()))
                            .fetchOne();

            if (record == null) {
                return Optional.empty();
            } else {
                Session session =
                        Session.builder()
                                .id(SessionId.of(record.getValue(SESSION.ID)))
                                .actorId(ActorId.of(record.getValue(SESSION.USER_ID)))
                                .createdAt(toInstant(record.getValue(SESSION.CREATED_AT)))
                                .expiresAt(toInstant(record.getValue(SESSION.EXPIRES_AT)))
                                .build();

                return Optional.of(session);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }

    @Override
    public void store(Session session) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            if (!exists(session.getId())) {
                create.insertInto(
                                SESSION,
                                SESSION.ID,
                                SESSION.USER_ID,
                                SESSION.CREATED_AT,
                                SESSION.EXPIRES_AT)
                        .values(
                                session.getId().value(),
                                session.getActorId().value(),
                                toLocalDateTime(session.getCreatedAt()),
                                toLocalDateTime(session.getExpiresAt()))
                        .execute();
            } else {
                create.update(SESSION)
                        .set(SESSION.ID, session.getId().value())
                        .set(SESSION.USER_ID, session.getActorId().value())
                        .set(SESSION.CREATED_AT, toLocalDateTime(session.getCreatedAt()))
                        .set(SESSION.EXPIRES_AT, toLocalDateTime(session.getExpiresAt()))
                        .where(SESSION.ID.eq(session.getId().value()))
                        .execute();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error storing", e);
        }
    }

    private boolean exists(SessionId id) {
        try (Connection connection = dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.POSTGRES);
            Record1<String> record =
                    create.select(SESSION.ID)
                            .from(SESSION)
                            .where(SESSION.ID.eq(id.value()))
                            .fetchOne();

            if (record == null) {
                return false;
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Exists check failed", e);
        }
    }
}
