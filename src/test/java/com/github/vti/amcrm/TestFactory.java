package com.github.vti.amcrm;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import com.github.vti.amcrm.api.service.request.CreateCustomerRequest;
import com.github.vti.amcrm.api.service.request.CreateUserRequest;
import com.github.vti.amcrm.domain.ActorId;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.session.Session;
import com.github.vti.amcrm.domain.session.SessionId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;

public class TestFactory {
    public static User.Builder newUserBuilder() {
        return User.builder()
                .id(UserId.of(TestData.getRandomId()))
                .name(TestData.getRandomName())
                .createdBy(ActorId.of(TestData.getRandomId()));
    }

    public static User newUser() {
        return newUserBuilder().build();
    }

    public static Customer.Builder newCustomerBuilder() {
        return Customer.builder()
                .id(CustomerId.of(TestData.getRandomId()))
                .name(TestData.getRandomName())
                .surname(TestData.getRandomSurname())
                .createdBy(ActorId.of(TestData.getRandomId()));
    }

    public static CreateUserRequest newCreateUserRequest() {
        return new CreateUserRequest(TestData.getRandomId(), false, TestData.getRandomName());
    }

    public static Customer newCustomer() {
        return newCustomerBuilder().build();
    }

    public static CreateCustomerRequest newCreateCustomerRequest() {
        return new CreateCustomerRequest(
                TestData.getRandomId(), TestData.getRandomName(), TestData.getRandomSurname());
    }

    public static CreateCustomerRequest newCreateCustomerRequestWithInvalidPhoto() {
        return new CreateCustomerRequest(
                TestData.getRandomId(),
                TestData.getRandomName(),
                TestData.getRandomSurname(),
                "foobar");
    }

    public static CreateCustomerRequest newCreateCustomerRequestWithPhoto() throws Exception {
        String photo = TestFileUtils.readFileBase64(TestData.getPhotoFile());

        return new CreateCustomerRequest(
                TestData.getRandomId(),
                TestData.getRandomName(),
                TestData.getRandomSurname(),
                photo);
    }

    public static Session newSession(ActorId actorId) {
        return Session.builder()
                .id(SessionId.of(TestData.getRandomId()))
                .actorId(actorId)
                .expiresAt(Instant.now().plusSeconds(TimeUnit.HOURS.toSeconds(1)))
                .build();
    }

    public static Session newExpiredSession(ActorId actorId) {
        return Session.builder()
                .id(SessionId.of(TestData.getRandomId()))
                .actorId(actorId)
                .expiresAt(Instant.now().minusSeconds(TimeUnit.HOURS.toSeconds(1)))
                .build();
    }
}
