package com.github.vti.amcrm.api.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.annotation.*;

import com.github.vti.amcrm.api.AccessDecorator;
import com.github.vti.amcrm.api.Client;
import com.github.vti.amcrm.api.JsonConverter;
import com.github.vti.amcrm.api.exception.ConflictException;
import com.github.vti.amcrm.api.exception.NotFoundException;
import com.github.vti.amcrm.api.exception.ServiceExceptionHandler;
import com.github.vti.amcrm.api.service.request.CreateUserRequest;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.domain.user.command.CreateUserCommand;
import com.github.vti.amcrm.domain.user.command.DeleteUserCommand;
import com.github.vti.amcrm.domain.user.command.ToggleUserAdminStatusCommand;
import com.github.vti.amcrm.domain.user.exception.UserExistsException;
import com.github.vti.amcrm.domain.user.exception.UserNotFoundException;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
import com.github.vti.amcrm.infra.registry.ViewRegistry;
import com.github.vti.amcrm.infra.user.dto.UserSummary;

@ExceptionHandler(ServiceExceptionHandler.class)
@RequestConverter(JsonConverter.class)
@ResponseConverter(JsonConverter.class)
@AccessDecorator(role = Client.Role.ADMIN)
public class UserService extends BaseService {
    private final RepositoryRegistry repositoryRegistry;
    private final ViewRegistry viewRegistry;

    public UserService(RepositoryRegistry repositoryRegistry, ViewRegistry viewRegistry) {
        this.repositoryRegistry = Objects.requireNonNull(repositoryRegistry);
        this.viewRegistry = Objects.requireNonNull(viewRegistry);
    }

    @Get("")
    public List<UserSummary> getUserList(
            @Param("limit") Optional<Integer> limit, @Param("offset") Optional<Integer> offset) {
        Pager pager = new Pager(limit, offset);

        Page<UserSummary> page = this.viewRegistry.getUserView().find(pager);

        return page.getItems();
    }

    @Get("/{id}")
    public UserSummary getUserSummary(@Param("id") String id) {
        Optional<UserSummary> user = this.viewRegistry.getUserView().load(id);

        return user.orElseThrow(NotFoundException::new);
    }

    @Post("")
    public UserSummary createUser(CreateUserRequest request) {
        Client client = getClient();

        CreateUserCommand createUserCommand =
                CreateUserCommand.builder()
                        .userRepository(this.repositoryRegistry.getUserRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(UserId.of(request.getId()))
                        .name(request.getName())
                        .build();

        try {
            createUserCommand.execute();

            return this.viewRegistry.getUserView().load(request.getId()).get();
        } catch (UserExistsException e) {
            throw new ConflictException("User already exists");
        }
    }

    @Post("/{id}/admin")
    public UserSummary toggleUserAdminStatus(@Param("id") String id) {
        Client client = getClient();

        ToggleUserAdminStatusCommand toggleUserAdminStatusCommand =
                ToggleUserAdminStatusCommand.builder()
                        .userRepository(this.repositoryRegistry.getUserRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(UserId.of(id))
                        .build();

        try {
            toggleUserAdminStatusCommand.execute();

            return this.viewRegistry.getUserView().load(id).get();
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @Delete("/{id}")
    public HttpResponse deleteUser(@Param("id") String id) {
        Client client = getClient();

        DeleteUserCommand deleteUserCommand =
                DeleteUserCommand.builder()
                        .userRepository(this.repositoryRegistry.getUserRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(UserId.of(id))
                        .build();

        try {
            deleteUserCommand.execute();

            return HttpResponse.of(HttpStatus.NO_CONTENT);
        } catch (UserNotFoundException e) {
            throw new NotFoundException();
        }
    }
}
