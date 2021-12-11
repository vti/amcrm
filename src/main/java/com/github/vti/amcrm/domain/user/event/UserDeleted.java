package com.github.vti.amcrm.domain.user.event;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.UserId;

public class UserDeleted extends Event {
    public UserDeleted(UserId userId, ActorId actorId) {
        super(userId.value(), actorId);
    }
}
