package com.github.vti.amcrm.domain.customer.event;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.CustomerId;

public class CustomerNameChanged extends Event {
    public CustomerNameChanged(CustomerId customerId, ActorId actorId) {
        super(customerId.value(), actorId);
    }
}
