package com.github.vti.amcrm.api.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.infra.TestDatabase;
import com.github.vti.amcrm.infra.registry.DatabaseRepositoryRegistry;

class OauthServiceTest {
    private OauthService service;
    private final String baseUrl = "http://localhost:4567";

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = TestDatabase.setupDatabase(tmpDir);
        RepositoryRegistry repositoryRegistry = new DatabaseRepositoryRegistry(dataSource);

        service =
                new OauthService(
                        Optional.of(new Config.OauthConfig("1234567890", "secret")),
                        repositoryRegistry);
    }

    @Test
    void returnsAuthorizationUrl() {
        Map<String, String> map = service.github();

        assertNotNull(map.get("location"));
    }
}
