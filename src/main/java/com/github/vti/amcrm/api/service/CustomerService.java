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
import com.github.vti.amcrm.api.service.request.CreateCustomerRequest;
import com.github.vti.amcrm.api.service.request.PatchCustomerRequest;
import com.github.vti.amcrm.domain.RepositoryRegistry;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.customer.command.CreateCustomerCommand;
import com.github.vti.amcrm.domain.customer.command.DeleteCustomerCommand;
import com.github.vti.amcrm.domain.customer.command.PatchCustomerCommand;
import com.github.vti.amcrm.domain.customer.exception.CustomerExistsException;
import com.github.vti.amcrm.domain.customer.exception.CustomerNotFoundException;
import com.github.vti.amcrm.domain.user.UserId;
import com.github.vti.amcrm.infra.customer.dto.CustomerSummary;
import com.github.vti.amcrm.infra.photo.Photo;
import com.github.vti.amcrm.infra.photo.PhotoStorage;
import com.github.vti.amcrm.infra.registry.ViewRegistry;

@ExceptionHandler(ServiceExceptionHandler.class)
@RequestConverter(JsonConverter.class)
@ResponseConverter(JsonConverter.class)
@AccessDecorator(role = Client.Role.USER)
public class CustomerService extends BaseService {
    private final RepositoryRegistry repositoryRegistry;
    private final ViewRegistry viewRegistry;
    private final PhotoStorage photoStorage;

    public CustomerService(
            RepositoryRegistry repositoryRegistry,
            ViewRegistry viewRegistry,
            PhotoStorage photoStorage) {
        this.repositoryRegistry = Objects.requireNonNull(repositoryRegistry);
        this.viewRegistry = Objects.requireNonNull(viewRegistry);
        this.photoStorage = photoStorage;
    }

    @Get("")
    public List<CustomerSummary> getCustomerList() {
        List<CustomerSummary> customers = this.viewRegistry.getCustomerView().find();

        return customers;
    }

    @Get("/{id}")
    public CustomerSummary getCustomerSummary(@Param("id") String id) {
        Optional<CustomerSummary> customer = this.viewRegistry.getCustomerView().load(id);

        return customer.orElseThrow(NotFoundException::new);
    }

    @Post("")
    public CustomerSummary createCustomer(CreateCustomerRequest request) {
        Client client = getClient();

        CreateCustomerCommand createCustomerCommand =
                CreateCustomerCommand.builder()
                        .customerRepository(this.repositoryRegistry.getCustomerRepository())
                        .userId(UserId.of(client.getId()))
                        .id(CustomerId.of(request.getId()))
                        .name(request.getName())
                        .surname(request.getSurname())
                        .photoLocation(
                                request.getPhotoBlob().map(p -> processPhoto(p)).orElse(null))
                        .build();

        try {
            createCustomerCommand.execute();

            return this.viewRegistry.getCustomerView().load(request.getId()).get();
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
                        .customerRepository(this.repositoryRegistry.getCustomerRepository())
                        .userId(UserId.of(client.getId()))
                        .id(CustomerId.of(id))
                        .name(request.getName())
                        .surname(request.getSurname())
                        .photoLocation(
                                request.getPhotoBlob().map(p -> processPhoto(p)).orElse(null))
                        .build();

        try {
            patchCustomerCommand.execute();

            return this.viewRegistry.getCustomerView().load(id).get();
        } catch (CustomerNotFoundException e) {
            throw new NotFoundException();
        }
    }

    @Delete("/{id}")
    public HttpResponse deleteCustomer(@Param("id") String id) {
        Client client = getClient();

        DeleteCustomerCommand deleteCustomerCommand =
                DeleteCustomerCommand.builder()
                        .customerRepository(this.repositoryRegistry.getCustomerRepository())
                        .userId(UserId.of(client.getId()))
                        .id(CustomerId.of(id))
                        .build();

        try {
            deleteCustomerCommand.execute();

            return HttpResponse.of(HttpStatus.NO_CONTENT);
        } catch (CustomerNotFoundException e) {
            throw new NotFoundException();
        }
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
