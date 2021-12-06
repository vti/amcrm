package com.github.vti.amcrm.infra.customer;

import static com.github.vti.amcrm.db.Tables.CUSTOMER;
import static com.github.vti.amcrm.db.Tables.EVENT;
import static com.github.vti.amcrm.infra.DatabaseUtils.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.CustomerRepository;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.infra.OptimisticLockException;

public class DatabaseCustomerRepository implements CustomerRepository {
    private final DataSource dataSource;

    public DatabaseCustomerRepository(DataSource dataSource) {
        Objects.requireNonNull(dataSource);

        this.dataSource = dataSource;
    }

    @Override
    public Optional<Customer> load(CustomerId id) {
        Objects.requireNonNull(id);

        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Record11<
                            String,
                            Long,
                            String,
                            String,
                            String,
                            LocalDateTime,
                            String,
                            LocalDateTime,
                            String,
                            LocalDateTime,
                            String>
                    record =
                            create.select(
                                            CUSTOMER.ID,
                                            CUSTOMER.VERSION,
                                            CUSTOMER.NAME,
                                            CUSTOMER.SURNAME,
                                            CUSTOMER.PHOTO_LOCATION,
                                            CUSTOMER.CREATED_AT,
                                            CUSTOMER.CREATED_BY,
                                            CUSTOMER.UPDATED_AT,
                                            CUSTOMER.UPDATED_BY,
                                            CUSTOMER.DELETED_AT,
                                            CUSTOMER.DELETED_BY)
                                    .from(CUSTOMER)
                                    .where(CUSTOMER.ID.eq(id.value()))
                                    .fetchOne();

            if (record == null) {
                return Optional.empty();
            } else {
                Customer customer =
                        Customer.builder()
                                .id(CustomerId.of(record.getValue(CUSTOMER.ID)))
                                .version(record.getValue(CUSTOMER.VERSION))
                                .name(record.getValue(CUSTOMER.NAME))
                                .surname(record.getValue(CUSTOMER.SURNAME))
                                .photoLocation(record.getValue(CUSTOMER.PHOTO_LOCATION))
                                .createdAt(toInstant(record.getValue(CUSTOMER.CREATED_AT)))
                                .createdBy(toActorId(record.getValue(CUSTOMER.CREATED_BY)))
                                .updatedAt(toInstant(record.getValue(CUSTOMER.UPDATED_AT)))
                                .updatedBy(toActorId(record.getValue(CUSTOMER.UPDATED_BY)))
                                .deletedAt(toInstant(record.getValue(CUSTOMER.DELETED_AT)))
                                .deletedBy(toActorId(record.getValue(CUSTOMER.DELETED_BY)))
                                .build();

                return Optional.of(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }

    @Override
    public void store(Customer customer) throws CustomerExistsException {
        try (Connection connection = this.dataSource.getConnection()) {
            connection.setAutoCommit(false);

            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            if (customer.getVersion() == 0) {
                try {
                    create.insertInto(
                                    CUSTOMER,
                                    CUSTOMER.ID,
                                    CUSTOMER.VERSION,
                                    CUSTOMER.NAME,
                                    CUSTOMER.SURNAME,
                                    CUSTOMER.PHOTO_LOCATION,
                                    CUSTOMER.CREATED_AT,
                                    CUSTOMER.CREATED_BY,
                                    CUSTOMER.UPDATED_AT,
                                    CUSTOMER.UPDATED_BY,
                                    CUSTOMER.DELETED_AT,
                                    CUSTOMER.DELETED_BY)
                            .values(
                                    customer.getId().value(),
                                    customer.getVersion() + 1L,
                                    customer.getName(),
                                    customer.getSurname(),
                                    customer.getPhotoLocation().orElse(null),
                                    toLocalDateTime(customer.getCreatedAt()),
                                    customer.getCreatedBy().value(),
                                    toLocalDateTime(customer.getUpdatedAt()),
                                    Optional.ofNullable(customer.getUpdatedBy())
                                            .map(v -> v.value())
                                            .orElse(null),
                                    toLocalDateTime(customer.getDeletedAt()),
                                    Optional.ofNullable(customer.getDeletedBy())
                                            .map(v -> v.value())
                                            .orElse(null))
                            .execute();
                } catch (DataAccessException e) {
                    if (e.getMessage().contains("UNIQUE constraint failed")) {
                        throw new CustomerExistsException();
                    } else {
                        throw e;
                    }
                }
            } else {
                int rowsUpdated =
                        create.update(CUSTOMER)
                                .set(CUSTOMER.ID, customer.getId().value())
                                .set(CUSTOMER.VERSION, customer.getVersion() + 1L)
                                .set(CUSTOMER.NAME, customer.getName())
                                .set(CUSTOMER.SURNAME, customer.getSurname())
                                .set(
                                        CUSTOMER.PHOTO_LOCATION,
                                        customer.getPhotoLocation().orElse(null))
                                .set(CUSTOMER.CREATED_AT, toLocalDateTime(customer.getCreatedAt()))
                                .set(CUSTOMER.CREATED_BY, customer.getCreatedBy().value())
                                .set(CUSTOMER.UPDATED_AT, toLocalDateTime(customer.getCreatedAt()))
                                .set(
                                        CUSTOMER.UPDATED_BY,
                                        Optional.ofNullable(customer.getUpdatedBy())
                                                .map(v -> v.value())
                                                .orElse(null))
                                .set(CUSTOMER.DELETED_AT, toLocalDateTime(customer.getCreatedAt()))
                                .set(
                                        CUSTOMER.DELETED_BY,
                                        Optional.ofNullable(customer.getDeletedBy())
                                                .map(v -> v.value())
                                                .orElse(null))
                                .where(CUSTOMER.ID.eq(customer.getId().value()))
                                .and(CUSTOMER.VERSION.eq(customer.getVersion()))
                                .execute();

                if (rowsUpdated == 0) {
                    throw new OptimisticLockException(customer.getVersion());
                }
            }

            storeEvents(connection, customer.getEvents());

            connection.commit();

            customer.incrementVersion();
            customer.clearEvents();
        } catch (SQLException e) {
            throw new RuntimeException("Error storing", e);
        }
    }

    public static void storeEvents(Connection connection, List<Event<CustomerId>> events) {
        DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

        for (Event<CustomerId> event : events) {
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
                                                    return String.valueOf(
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
