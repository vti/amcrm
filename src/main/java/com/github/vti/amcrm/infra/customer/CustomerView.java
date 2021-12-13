package com.github.vti.amcrm.infra.customer;

import java.util.Optional;

import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;

public interface CustomerView {
    Optional<CustomerSummary> load(String id);

    Optional<CustomerSummary> loadForAdmin(String id);

    Page<CustomerSummary> find(Pager pager);

    Page<CustomerSummary> findForAdmin(Pager pager);
}
