package com.github.vti.amcrm.domain.customer.event;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.customer.CustomerId;

public class CustomerSurnameChanged extends Event<CustomerId> {
    public CustomerSurnameChanged(CustomerId originId, ActorId actorId) {
        super(originId, actorId);
    }
}
