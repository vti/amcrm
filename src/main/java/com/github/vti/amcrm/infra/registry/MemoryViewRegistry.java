package com.github.vti.amcrm.infra.registry;

import com.github.vti.amcrm.infra.MemoryStorage;
import com.github.vti.amcrm.infra.customer.CustomerView;
import com.github.vti.amcrm.infra.customer.MemoryCustomerView;
import com.github.vti.amcrm.infra.user.MemoryUserView;
import com.github.vti.amcrm.infra.user.UserView;

public class MemoryViewRegistry implements ViewRegistry {
    private final MemoryStorage storage;
    private final String baseUrl;

    public MemoryViewRegistry(MemoryStorage storage, String baseUrl) {
        this.storage = storage;
        this.baseUrl = baseUrl;
    }

    @Override
    public CustomerView getCustomerView() {
        return new MemoryCustomerView(storage.getCustomerStorage(), baseUrl);
    }

    @Override
    public UserView getUserView() {
        return new MemoryUserView(storage.getUserStorage(), baseUrl);
    }
}
