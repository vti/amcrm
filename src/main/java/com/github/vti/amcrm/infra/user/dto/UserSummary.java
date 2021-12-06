package com.github.vti.amcrm.infra.user.dto;

public class UserSummary {
    private final String id;
    private final Boolean admin;
    private final String name;

    public UserSummary(Builder builder) {
        this.id = builder.id;
        this.admin = builder.admin;
        this.name = builder.name;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {

        private String id;
        private Boolean admin;
        private String name;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder isAdmin(Boolean isAdmin) {
            this.admin = isAdmin;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public UserSummary build() {
            return new UserSummary(this);
        }
    }
}
