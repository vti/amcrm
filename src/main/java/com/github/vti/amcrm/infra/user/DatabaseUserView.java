package com.github.vti.amcrm.infra.user;

import static com.github.vti.amcrm.db.Tables.USER;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.jooq.*;
import org.jooq.impl.DSL;

import com.github.vti.amcrm.infra.user.dto.UserSummary;

public class DatabaseUserView implements UserView {
    private final DataSource dataSource;

    public DatabaseUserView(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UserSummary> load(String id) {
        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Record3<String, String, Boolean> record =
                    create.select(USER.ID, USER.NAME, USER.IS_ADMIN)
                            .from(USER)
                            .where(USER.ID.eq(id))
                            .and(USER.DELETED_BY.isNull())
                            .fetchOne();

            if (record == null) {
                return Optional.empty();
            } else {
                UserSummary user =
                        UserSummary.builder()
                                .id(record.getValue(USER.ID))
                                .isAdmin(record.getValue(USER.IS_ADMIN))
                                .name(record.getValue(USER.NAME))
                                .build();
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }

    @Override
    public List<UserSummary> find() {
        List<UserSummary> users = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Result<Record3<String, Boolean, String>> result =
                    create.select(USER.ID, USER.IS_ADMIN, USER.NAME)
                            .from(USER)
                            .where(USER.DELETED_BY.isNull())
                            .limit(100)
                            .fetch();

            for (Record3<String, Boolean, String> record : result) {
                UserSummary user =
                        UserSummary.builder()
                                .id(record.getValue(USER.ID))
                                .isAdmin(record.getValue(USER.IS_ADMIN))
                                .name(record.getValue(USER.NAME))
                                .build();

                users.add(user);
            }

            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }
}
