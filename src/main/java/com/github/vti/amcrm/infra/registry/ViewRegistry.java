package com.github.vti.amcrm.infra.registry;

import com.github.vti.amcrm.infra.customer.CustomerView;
import com.github.vti.amcrm.infra.user.UserView;

public interface ViewRegistry {
    public CustomerView getCustomerView();

    public UserView getUserView();
}
