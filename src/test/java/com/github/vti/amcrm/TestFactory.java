package com.github.vti.amcrm;

import com.github.vti.amcrm.api.service.request.CreateCustomerRequest;
import com.github.vti.amcrm.domain.customer.Customer;
import com.github.vti.amcrm.domain.customer.CustomerId;
import com.github.vti.amcrm.domain.user.User;
import com.github.vti.amcrm.domain.user.UserId;

public class TestFactory {
    public static User.Builder newUserBuilder() {
        return User.builder()
                .id(UserId.of(TestData.getRandomId()))
                .name(TestData.getRandomName())
                .createdBy(UserId.of(TestData.getRandomId()));
    }

    public static User newUser() {
        return newUserBuilder().build();
    }

    public static Customer.Builder newCustomerBuilder() {
        return Customer.builder()
                .id(CustomerId.of(TestData.getRandomId()))
                .name(TestData.getRandomName())
                .surname(TestData.getRandomSurname())
                .createdBy(UserId.of(TestData.getRandomId()));
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
}
