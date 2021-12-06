package com.github.vti.amcrm.infra;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.sqlite.SQLiteDataSource;

public class TestDatabase {
    public static DataSource setupDatabase(Path tmpDir) {
        SQLiteDataSource dataSource = new SQLiteDataSource();

        System.getProperties().setProperty("org.jooq.no-logo", "true");

        dataSource.setUrl(String.format("jdbc:sqlite:%s", Paths.get(tmpDir.toString(), "db.db")));

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            InputStream inputStream =
                    TestDatabase.class.getClassLoader().getResourceAsStream("db.sql");
            for (String sql : readSchema(inputStream)) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Database setup failed", e);
        }

        return dataSource;
    }

    private static List<String> readSchema(InputStream inputStream) {
        StringBuilder contentBuilder = new StringBuilder();

        String sql =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

        return Arrays.asList(sql.split(";"));
    }
}
