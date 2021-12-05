package com.github.vti.amcrm.api.service.request;

import java.util.Optional;

public class PatchCustomerRequest {
    private String name;
    private String surname;
    private String photoBlob;

    private PatchCustomerRequest() {}

    public PatchCustomerRequest(String name, String surname) {
        this(name, surname, null);
    }

    public PatchCustomerRequest(String name, String surname, String photoBlob) {
        this.name = name;
        this.surname = surname;
        this.photoBlob = photoBlob;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Optional<String> getPhotoBlob() {
        return Optional.ofNullable(photoBlob);
    }
}
