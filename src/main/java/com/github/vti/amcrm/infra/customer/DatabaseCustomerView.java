package com.github.vti.amcrm.infra.customer;

import static com.github.vti.amcrm.db.Tables.CUSTOMER;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.jooq.*;
import org.jooq.impl.DSL;

import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;

public class DatabaseCustomerView implements CustomerView {
    private final DataSource dataSource;
    private final String baseUrl;

    public DatabaseCustomerView(DataSource dataSource, String baseUrl) {
        this.dataSource = dataSource;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<CustomerSummary> load(String id) {
        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Record4<String, String, String, String> record =
                    create.select(
                                    CUSTOMER.ID,
                                    CUSTOMER.NAME,
                                    CUSTOMER.SURNAME,
                                    CUSTOMER.PHOTO_LOCATION)
                            .from(CUSTOMER)
                            .where(CUSTOMER.ID.eq(id))
                            .and(CUSTOMER.DELETED_BY.isNull())
                            .fetchOne();

            if (record == null) {
                return Optional.empty();
            } else {
                CustomerSummary customer =
                        CustomerSummary.builder()
                                .baseUrl(baseUrl)
                                .id(record.getValue(CUSTOMER.ID))
                                .name(record.getValue(CUSTOMER.NAME))
                                .surname(record.getValue(CUSTOMER.SURNAME))
                                .photoLocation(record.getValue(CUSTOMER.PHOTO_LOCATION))
                                .build();
                return Optional.of(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }

    @Override
    public Page<CustomerSummary> find(Pager pager) {
        List<CustomerSummary> customers = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Result<Record4<String, String, String, String>> result =
                    create.select(
                                    CUSTOMER.ID,
                                    CUSTOMER.NAME,
                                    CUSTOMER.SURNAME,
                                    CUSTOMER.PHOTO_LOCATION)
                            .from(CUSTOMER)
                            .where(CUSTOMER.DELETED_BY.isNull())
                            .orderBy(CUSTOMER.CREATED_AT.desc())
                            .limit(pager.getLimit())
                            .offset(pager.getOffset())
                            .fetch();

            for (Record4<String, String, String, String> record : result) {
                CustomerSummary customer =
                        CustomerSummary.builder()
                                .baseUrl(baseUrl)
                                .id(record.getValue(CUSTOMER.ID))
                                .name(record.getValue(CUSTOMER.NAME))
                                .surname(record.getValue(CUSTOMER.SURNAME))
                                .photoLocation(record.getValue(CUSTOMER.PHOTO_LOCATION))
                                .build();

                customers.add(customer);
            }

            return new Page(customers, Pager.nextOf(pager));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }
}
