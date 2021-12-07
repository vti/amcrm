package com.github.vti.amcrm;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class Config {
    public static final int DEFAULT_PORT = 4567;
    public static final String DEFAULT_BASE_URL = "http://localhost:" + DEFAULT_PORT;
    public static final StorageProvider DEFAULT_STORAGE_PROVIDER = StorageProvider.MEMORY;

    private URL baseUrl;
    private int port;
    private StorageConfig storage;

    private Config() {}

    private Config(Builder builder) {
        port = builder.port == null ? DEFAULT_PORT : builder.port;
        storage =
                builder.storage == null
                        ? new StorageConfig(DEFAULT_STORAGE_PROVIDER, null)
                        : builder.storage;
        try {
            if (builder.baseUrl == null) {
                baseUrl = new URL(DEFAULT_BASE_URL);

                if (port != DEFAULT_PORT) {
                    baseUrl =
                            new URL(
                                    baseUrl.getProtocol(),
                                    baseUrl.getHost(),
                                    port,
                                    baseUrl.getPath());
                }
            } else {
                baseUrl = builder.baseUrl;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid base url");
        }
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public int getPort() {
        return port;
    }

    public StorageConfig getStorage() {
        return storage;
    }

    @Override
    public String toString() {
        return "Config{" + "baseUrl=" + baseUrl + ", port=" + port + ", storage=" + storage + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private URL baseUrl;
        private Integer port;
        private StorageConfig storage;

        public Builder baseUrl(URL baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder port(Integer port) {
            this.port = port;
            return this;
        }

        public Builder storage(StorageConfig storage) {
            this.storage = Objects.requireNonNull(storage);
            return this;
        }

        public Builder load(File file) throws FileNotFoundException {
            return load(new FileInputStream(file.toString()));
        }

        private Builder load(InputStream inputStream) {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            try {
                JsonSchema schema =
                        factory.getJsonSchema(JsonLoader.fromResource("/schema/config.json"));

                JsonNode jsonNode = objectMapper.readTree(inputStream);

                ProcessingReport report = schema.validate(jsonNode);

                if (!report.isSuccess()) {
                    throw new RuntimeException("Error loading config: invalid schema: " + report);
                }

                Config config = objectMapper.treeToValue(jsonNode, Config.class);

                this.baseUrl = config.getBaseUrl();
                this.port = config.getPort();
                this.storage = config.getStorage();

                return this;
            } catch (IOException | ProcessingException e) {
                throw new RuntimeException("Error loading config", e);
            }
        }

        public Builder loadFromEnv() {
            return loadFromEnv(new EnvReader());
        }

        public Builder loadFromEnv(EnvReader envReader) {
            String baseUrl = envReader.getenv(Env.AMCRM_BASE_URL.toString());
            if (baseUrl != null && !baseUrl.trim().isEmpty()) {
                try {
                    this.baseUrl(new URL(baseUrl));
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Invalid base url");
                }
            }

            String port = envReader.getenv(Env.AMCRM_PORT.toString());
            if (port != null && !port.trim().isEmpty()) {
                this.port(Integer.parseInt(port));
            }

            String storageProvider = envReader.getenv(Env.AMCRM_STORAGE_PROVIDER.toString());
            if (storageProvider != null) {
                if (storageProvider.equals("memory")) {
                    this.storage(new StorageConfig(StorageProvider.MEMORY, null));
                } else if (storageProvider.equals("database")) {
                    String options = envReader.getenv(Env.AMCRM_STORAGE_OPTIONS.toString());

                    if (options != null) {
                        Map<String, String> optionsMap =
                                Arrays.stream(options.split(","))
                                        .map(s -> s.split("="))
                                        .collect(Collectors.toMap(s -> s[0], s -> s[1]));

                        if (optionsMap.containsKey("database")) {
                            this.storage(new StorageConfig(StorageProvider.DATABASE, optionsMap));
                        } else {
                            throw new RuntimeException("Invalid storage options");
                        }
                    } else {
                        throw new RuntimeException("Missing storage options");
                    }
                } else {
                    throw new RuntimeException("Unknown storage provider");
                }
            }

            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    public static class StorageConfig {
        private StorageProvider provider;
        private Map<String, String> options;

        private StorageConfig() {}

        public StorageConfig(StorageProvider provider, Map<String, String> options) {
            this.provider = provider;
            this.options = options;
        }

        public StorageProvider getProvider() {
            return this.provider;
        }

        public Map<String, String> getOptions() {
            return this.options;
        }

        @Override
        public String toString() {
            return "StorageConfig{" + "provider=" + provider + ", options=" + options + '}';
        }
    }

    public enum StorageProvider {
        @JsonProperty("memory")
        MEMORY(),

        @JsonProperty("database")
        DATABASE()
    }

    public enum Env {
        AMCRM_BASE_URL,
        AMCRM_PORT,
        AMCRM_STORAGE_PROVIDER,
        AMCRM_STORAGE_OPTIONS
    }

    public static class EnvReader {
        public String getenv(String key) {
            return System.getenv(key);
        }
    }
}
