package com.github.vti.amcrm.infra.registry;

import javax.sql.DataSource;

import com.github.vti.amcrm.infra.customer.CustomerView;
import com.github.vti.amcrm.infra.customer.DatabaseCustomerView;

public class DatabaseViewRegistry implements ViewRegistry {
    private final DataSource dataSource;
    private final String baseUrl;

    public DatabaseViewRegistry(DataSource dataSource, String baseUrl) {
        this.dataSource = dataSource;
        this.baseUrl = baseUrl;
    }

    @Override
    public CustomerView getCustomerView() {
        return new DatabaseCustomerView(dataSource, baseUrl);
    }
}
