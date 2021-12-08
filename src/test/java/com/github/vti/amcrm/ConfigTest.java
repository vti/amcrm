package com.github.vti.amcrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ConfigTest {

    @Test
    void defaults() {
        Config config = Config.builder().build();

        assertEquals(Config.DEFAULT_BASE_URL, config.getBaseUrl().toString());
        assertEquals(Config.DEFAULT_PORT, config.getPort());
        assertEquals(Config.DEFAULT_STORAGE_PROVIDER, config.getStorage().getProvider());
    }

    @Test
    void defaultsWhenEnvEmpty() {
        Config config = Config.builder().loadFromEnv().build();

        assertEquals(Config.DEFAULT_BASE_URL, config.getBaseUrl().toString());
        assertEquals(Config.DEFAULT_PORT, config.getPort());
        assertEquals(Config.DEFAULT_STORAGE_PROVIDER, config.getStorage().getProvider());
    }

    @Test
    void fromEnv() {
        Config.EnvReader envReader = spy(new Config.EnvReader());
        when(envReader.getenv(Config.Env.AMCRM_BASE_URL.toString())).thenReturn("http://other.url");
        when(envReader.getenv(Config.Env.AMCRM_PORT.toString())).thenReturn("1234");
        when(envReader.getenv(Config.Env.AMCRM_STORAGE_PROVIDER.toString())).thenReturn("database");
        when(envReader.getenv(Config.Env.AMCRM_STORAGE_OPTIONS.toString()))
                .thenReturn("database=db.db");
        when(envReader.getenv(Config.Env.AMCRM_OAUTH_CLIENT_ID.toString()))
                .thenReturn("1234567890");
        when(envReader.getenv(Config.Env.AMCRM_OAUTH_CLIENT_SECRET.toString()))
                .thenReturn("12039d6dd9a7e27622301e935b6eefc78846802e");

        Config config = Config.builder().loadFromEnv(envReader).build();

        assertEquals("http://other.url", config.getBaseUrl().toString());
        assertEquals(1234, config.getPort());
        assertEquals(Config.StorageProvider.DATABASE, config.getStorage().getProvider());

        Map<String, String> options = config.getStorage().getOptions();
        assertEquals("db.db", options.get("database"));

        assertTrue(config.getOauth().isPresent());
        assertEquals("1234567890", config.getOauth().get().getClientId());
        assertEquals(
                "12039d6dd9a7e27622301e935b6eefc78846802e",
                config.getOauth().get().getClientSecret());
    }

    @Test
    void loadsFromFile() throws FileNotFoundException {
        Config config = Config.builder().load(TestData.getConfigFile()).build();

        assertEquals("http://other.url", config.getBaseUrl().toString());
        assertEquals(1234, config.getPort());
        assertEquals(Config.StorageProvider.DATABASE, config.getStorage().getProvider());

        Map<String, String> options = config.getStorage().getOptions();
        assertEquals("db.db", options.get("database"));

        assertTrue(config.getOauth().isPresent());
        assertEquals("1234567890", config.getOauth().get().getClientId());
        assertEquals(
                "12039d6dd9a7e27622301e935b6eefc78846802e",
                config.getOauth().get().getClientSecret());
    }

    @Test
    void manualConfiguration() {
        Map<String, String> options =
                new HashMap<String, String>() {
                    {
                        put("database", "db.db");
                    }
                };
        Config config =
                Config.builder()
                        .port(1234)
                        .storage(new Config.StorageConfig(Config.StorageProvider.DATABASE, options))
                        .build();

        assertEquals(1234, config.getPort());
        assertEquals(Config.StorageProvider.DATABASE, config.getStorage().getProvider());

        assertEquals("db.db", config.getStorage().getOptions().get("database"));
    }

    @Test
    void overwrites() throws FileNotFoundException {
        Config config =
                Config.builder()
                        .load(TestData.getConfigFile())
                        .port(4321)
                        .storage(new Config.StorageConfig(Config.StorageProvider.MEMORY, null))
                        .build();

        assertEquals(4321, config.getPort());
        assertEquals(Config.StorageProvider.MEMORY, config.getStorage().getProvider());
    }

    @Test
    void updateDefaultBaseUrlPortWhenNotBaseUrlSpecified() {
        Config config = Config.builder().port(4321).build();

        assertEquals("http://localhost:4321", config.getBaseUrl().toString());
    }
}
