package com.github.vti.amcrm.infra.customer.dto;

public class CustomerSummary {
    private final String baseUrl;
    private final String id;
    private final String name;
    private final String surname;
    private final String photoLocation;
    private final String createdBy;

    public CustomerSummary(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.id = builder.id;
        this.name = builder.name;
        this.surname = builder.surname;
        this.photoLocation = builder.photoLocation;
        this.createdBy = builder.createdBy;
    }

    public static Builder builder() {
        return new Builder();
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

    // TODO: this needs to be properly url escaped when needed
    public String getPhotoLocation() {
        if (photoLocation != null && baseUrl != null && !photoLocation.startsWith("http")) {
            return baseUrl + "/" + photoLocation;
        }

        return photoLocation;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public static class Builder {

        private String id;
        private String name;
        private String surname;
        private String photoLocation;
        private String baseUrl;
        private String createdBy;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder photoLocation(String photoLocation) {
            this.photoLocation = photoLocation;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CustomerSummary build() {
            return new CustomerSummary(this);
        }
    }
}
