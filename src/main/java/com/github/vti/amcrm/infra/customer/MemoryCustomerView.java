package com.github.vti.amcrm.infra.customer;

import java.util.*;
import java.util.stream.Collectors;

import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;

public class MemoryCustomerView implements CustomerView {
    private final Map<CustomerId, Customer> storage;
    private final String baseUrl;

    public MemoryCustomerView(Map<CustomerId, Customer> storage, String baseUrl) {
        this.storage = storage;
        this.baseUrl = baseUrl;
    }

    @Override
    public Optional<CustomerSummary> load(String id) {
        Customer customer = storage.get(CustomerId.of(id));

        if (customer == null) {
            return Optional.empty();
        }

        return Optional.of(
                CustomerSummary.builder()
                        .baseUrl(baseUrl)
                        .id(customer.getId().value())
                        .name(customer.getName())
                        .surname(customer.getSurname())
                        .photoLocation(customer.getPhotoLocation().orElse(null))
                        .build());
    }

    @Override
    public Page<CustomerSummary> find(Pager pager) {
        List<CustomerSummary> customers =
                storage.values().stream()
                        .map(
                                customer ->
                                        CustomerSummary.builder()
                                                .baseUrl(baseUrl)
                                                .id(customer.getId().value())
                                                .name(customer.getName())
                                                .surname(customer.getSurname())
                                                .photoLocation(
                                                        customer.getPhotoLocation().orElse(null))
                                                .build())
                        .collect(Collectors.toList());

        if (customers.size() > 0) {
            customers =
                    customers.subList(
                            pager.getOffset(),
                            Math.min(customers.size(), pager.getOffset() + pager.getLimit()));
        }

        return new Page(customers, Pager.nextOf(pager));
    }
}
