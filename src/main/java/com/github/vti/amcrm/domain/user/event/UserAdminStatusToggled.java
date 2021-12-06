package com.github.vti.amcrm.domain.user.event;

import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.Event;
import com.github.vti.amcrm.domain.user.UserId;

public class UserAdminStatusToggled extends Event<UserId> {
    public UserAdminStatusToggled(UserId originId, ActorId userId) {
        super(originId, userId);
    }
}
