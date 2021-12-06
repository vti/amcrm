package com.github.vti.amcrm.infra.user;

import java.util.List;
import java.util.Optional;

import com.github.vti.amcrm.infra.user.dto.UserSummary;

public interface UserView {
    public abstract Optional<UserSummary> load(String id);

    public abstract List<UserSummary> find();
}
