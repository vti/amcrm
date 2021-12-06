package com.github.vti.amcrm.domain.user.event;

import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.UserId;

public class UserCreated extends Event<UserId> {
    public UserCreated(UserId originId, UserId userId) {
        super(originId, userId);
    }
}