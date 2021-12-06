package com.github.vti.amcrm.infra.customer;

import java.util.List;
import java.util.Optional;

import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;

public interface CustomerView {
    Optional<CustomerSummary> load(String id);

    List<CustomerSummary> find();
}
