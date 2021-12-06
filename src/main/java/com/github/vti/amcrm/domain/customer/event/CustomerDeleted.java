package com.github.vti.amcrm.domain.customer.event;

import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.user.UserId;

public class CustomerDeleted extends Event<CustomerId> {
    public CustomerDeleted(CustomerId originId, UserId userId) {
        super(originId, userId);
    }
}
