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

import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
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
    public Page<UserSummary> find(Pager pager) {
        List<UserSummary> users = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection()) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);

            Result<Record3<String, Boolean, String>> result =
                    create.select(USER.ID, USER.IS_ADMIN, USER.NAME)
                            .from(USER)
                            .where(USER.DELETED_BY.isNull())
                            .orderBy(USER.CREATED_AT.desc())
                            .limit(pager.getLimit())
                            .offset(pager.getOffset())
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

            Pager newPager = new Pager(pager.getLimit(), pager.getOffset() + pager.getLimit());

            return new Page(users, newPager);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching", e);
        }
    }
}
