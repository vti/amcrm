package com.github.vti.amcrm.api.service;

import java.util.Objects;
import java.util.Optional;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.server.annotation.*;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.api.AccessDecorator;
import com.github.vti.amcrm.api.Client;
import com.github.vti.amcrm.api.JsonConverter;
import com.github.vti.amcrm.api.LinkHeader;
import com.github.vti.amcrm.api.exception.ConflictException;
import com.github.vti.amcrm.api.exception.NotFoundException;
import com.github.vti.amcrm.api.exception.ServiceExceptionHandler;
import com.github.vti.amcrm.api.service.request.CreateCustomerRequest;
import com.github.vti.amcrm.api.service.request.PatchCustomerRequest;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.command.CreateCustomerCommand;
import com.github.vti.amcrm.domain.customer.command.DeleteCustomerCommand;
import com.github.vti.amcrm.domain.customer.command.PatchCustomerCommand;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;
import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.pager.Page;
import com.github.vti.amcrm.infra.pager.Pager;
import com.github.vti.amcrm.infra.photo.Photo;
import com.github.vti.amcrm.infra.photo.PhotoStorage;
import com.github.vti.amcrm.infra.registry.ViewRegistry;

@ExceptionHandler(ServiceExceptionHandler.class)
@RequestConverter(JsonConverter.class)
@ResponseConverter(JsonConverter.class)
@AccessDecorator(role = Client.Role.USER)
public class CustomerService extends BaseService {
    private final Config config;
    private final RepositoryRegistry repositoryRegistry;
    private final ViewRegistry viewRegistry;
    private final PhotoStorage photoStorage;

    public CustomerService(
            Config config,
            RepositoryRegistry repositoryRegistry,
            ViewRegistry viewRegistry,
            PhotoStorage photoStorage) {
        this.config = config;
        this.repositoryRegistry = Objects.requireNonNull(repositoryRegistry);
        this.viewRegistry = Objects.requireNonNull(viewRegistry);
        this.photoStorage = photoStorage;
    }

    @Get("")
    public HttpResponse getCustomerList(
            @Param("limit") Optional<Integer> limit, @Param("page") Optional<Integer> pageNum) {
        Pager pager = new Pager(limit, pageNum);

        Page<CustomerSummary> page =
                getClient().isUser()
                        ? viewRegistry.getCustomerView().find(pager)
                        : viewRegistry.getCustomerView().findForAdmin(pager);

        String linkHeader = new LinkHeader(config.getBaseUrl().toString(), pager).toString();

        return HttpResponse.ofJson(
                ResponseHeaders.of(HttpStatus.OK, "Link", linkHeader), page.getItems());
    }

    @Get("/{id}")
    public CustomerSummary getCustomerSummary(@Param("id") String id) {
        Optional<CustomerSummary> customer = loadForClient(id);

        return customer.orElseThrow(NotFoundException::new);
    }

    @Post("")
    public CustomerSummary createCustomer(CreateCustomerRequest request) {
        Client client = getClient();

        CreateCustomerCommand createCustomerCommand =
                CreateCustomerCommand.builder()
                        .customerRepository(repositoryRegistry.getCustomerRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(CustomerId.of(request.getId()))
                        .name(request.getName())
                        .surname(request.getSurname())
                        .photoLocation(request.getPhotoBlob().map(this::processPhoto).orElse(null))
                        .build();

        try {
            createCustomerCommand.execute();

            return loadForClient(request.getId()).orElseThrow(RuntimeException::new);
        } catch (CustomerExistsException e) {
            throw new ConflictException("Customer already exists");
        }
    }

    @Patch("/{id}")
    public CustomerSummary patchCustomer(@Param("id") String id, PatchCustomerRequest request) {
        Client client = getClient();

        // TODO: old photo is not removed nor replaced
        PatchCustomerCommand patchCustomerCommand =
                PatchCustomerCommand.builder()
                        .customerRepository(repositoryRegistry.getCustomerRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(CustomerId.of(id))
                        .name(request.getName())
                        .surname(request.getSurname())
                        .photoLocation(request.getPhotoBlob().map(this::processPhoto).orElse(null))
                        .build();

        try {
            patchCustomerCommand.execute();

            return loadForClient(id).orElseThrow(RuntimeException::new);
        } catch (CustomerNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @Delete("/{id}")
    public HttpResponse deleteCustomer(@Param("id") String id) {
        Client client = getClient();

        DeleteCustomerCommand deleteCustomerCommand =
                DeleteCustomerCommand.builder()
                        .customerRepository(repositoryRegistry.getCustomerRepository())
                        .actorId(ActorId.of(client.getId()))
                        .id(CustomerId.of(id))
                        .build();

        try {
            deleteCustomerCommand.execute();

            return HttpResponse.of(HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            throw new NotFoundException();
        }
    }

    private Optional<CustomerSummary> loadForClient(String id) {
        return getClient().isUser()
                ? viewRegistry.getCustomerView().load(id)
                : viewRegistry.getCustomerView().loadForAdmin(id);
    }

    private String processPhoto(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            // TODO: make configurable
            Photo photo = Photo.load(data).resize(32, 32);

            return photoStorage.store(photo);
        } catch (Exception e) {
            throw new ConflictException("Invalid photo");
        }
    }
}
