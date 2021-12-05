package com.github.vti.amcrm.api.service.request;

import java.util.Optional;

public class CreateCustomerRequest {
    private String id;
    private String name;
    private String surname;
    private String photoBlob;

    private CreateCustomerRequest() {}

    public CreateCustomerRequest(String id, String name, String surname) {
        this(id, name, surname, null);
    }

    public CreateCustomerRequest(String id, String name, String surname, String photoBlob) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.photoBlob = photoBlob;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Optional<String> getPhotoBlob() {
        return Optional.ofNullable(this.photoBlob);
    }
}
