package com.github.vti.amcrm.infra.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;
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
    public List<UserSummary> find() {
        return storage.values().stream()
                .map(
                        user ->
                                UserSummary.builder()
                                        .id(user.getId().value())
                                        .isAdmin(user.isAdmin())
                                        .name(user.getName())
                                        .build())
                .collect(Collectors.toList());
    }
}
