package com.github.vti.amcrm.api.service.request;

public class CreateUserRequest {
    private String id;
    private Boolean admin;
    private String name;

    private CreateUserRequest() {}

    public CreateUserRequest(String id, Boolean admin, String name) {
        this.id = id;
        this.admin = admin;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public String getName() {
        return name;
    }
}
