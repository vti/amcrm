package com.github.vti.amcrm.api;

public class Client {
    private final Role role;
    private final String id;

    public Client(Role role, String id) {
        this.role = role;
        this.id = id;
    }

    public static Client anonymous() {
        return new Client(Role.ANONYMOUS, null);
    }

    public static Client user(String id) {
        return new Client(Role.USER, id);
    }

    public static Client admin(String id) {
        return new Client(Role.ADMIN, id);
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAnonymous() {
        return this.role == Role.ANONYMOUS;
    }

    public boolean isUser() {
        return this.role == Role.USER;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isInRole(Client.Role role) {
        if (this.isAdmin()) {
            return true;
        }

        if (this.isUser() && (role == Role.USER || role == Role.ANONYMOUS)) {
            return true;
        }

        return this.isAnonymous() && role == Role.ANONYMOUS;
    }

    @Override
    public String toString() {
        return "Client{" + "role=" + role + ", id='" + id + '\'' + '}';
    }

    public String toCompactString() {
        if (isAnonymous()) {
            return String.format("%", role);
        }

        return String.format("%s/%s", role, id);
    }

    public enum Role {
        ANONYMOUS,
        USER,
        ADMIN
    }
}
