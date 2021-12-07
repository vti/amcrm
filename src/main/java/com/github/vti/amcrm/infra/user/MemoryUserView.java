package com.github.vti.amcrm.infra.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

public class MemoryUserView implements UserView {
    private final Map<UserId, User> storage;

    public MemoryUserView(Map<UserId, User> storage, String baseUrl) {
        this.storage = storage;
    }

    @Override
    public Optional<UserSummary> load(String id) {
        User user = storage.get(UserId.of(id));

        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(
                UserSummary.builder()
                        .id(user.getId().value())
                        .isAdmin(user.isAdmin())
                        .name(user.getName())
                        .build());
    }

    @Override
    public Page<UserSummary> find(Pager pager) {
        List<UserSummary> users =
                storage.values().stream()
                        .map(
                                user ->
                                        UserSummary.builder()
                                                .id(user.getId().value())
                                                .isAdmin(user.isAdmin())
                                                .name(user.getName())
                                                .build())
                        .collect(Collectors.toList());

        if (users.size() > 0) {
            users = users.subList(0, Math.min(users.size(), pager.getLimit()));
        }

        return new Page(users, new Pager(pager.getLimit()));
    }
}
