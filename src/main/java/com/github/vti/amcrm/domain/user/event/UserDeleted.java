package com.github.vti.amcrm.domain.user.event;

import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.UserId;

public class UserDeleted extends Event<UserId> {
    public UserDeleted(UserId originId, UserId userId) {
        super(originId, userId);
    }
}
