package com.github.vti.amcrm.infra.user;

import java.util.Optional;

import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

public interface UserView {
    Optional<UserSummary> load(String id);

    Page<UserSummary> find(Pager page);
}
