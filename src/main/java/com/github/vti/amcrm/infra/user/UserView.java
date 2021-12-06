package com.github.vti.amcrm.infra.user;

import java.util.List;
import java.util.Optional;

import com.github.vti.amcrm.infra.user.dto.UserSummary;

public interface UserView {
    Optional<UserSummary> load(String id);

    List<UserSummary> find();
}
