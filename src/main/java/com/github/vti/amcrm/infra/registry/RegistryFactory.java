package com.github.vti.amcrm.infra.registry;

import javax.sql.DataSource;

import org.sqlite.SQLiteDataSource;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.infra.MemoryStorage;

public class RegistryFactory {
    DataSource dataSource = null;
    MemoryStorage memoryStorage = null;
    String baseUrl = null;

    public RegistryFactory(Config.StorageConfig storageConfig, String baseUrl) {
        if (storageConfig.getProvider() == Config.StorageProvider.DATABASE) {
            dataSource = getDataSource(storageConfig);
        } else {
            memoryStorage = getMemoryStorage(storageConfig);
        }

        this.baseUrl = baseUrl;
    }

    public RepositoryRegistry getRepositoryRegistry() {
        if (dataSource != null) {
            return new DatabaseRepositoryRegistry(dataSource);
        }

        return new MemoryRepositoryRegistry(memoryStorage);
    }

    public ViewRegistry getViewRegistry() {
        if (dataSource != null) {
            return new DatabaseViewRegistry(dataSource, baseUrl);
        }

        return new MemoryViewRegistry(memoryStorage, baseUrl);
    }

    private SQLiteDataSource getDataSource(Config.StorageConfig storageConfig) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(
                String.format("jdbc:sqlite:%s", storageConfig.getOptions().get("database")));
        return dataSource;
    }

    private MemoryStorage getMemoryStorage(Config.StorageConfig storageConfig) {
        return new MemoryStorage();
    }
}
