package com.github.vti.amcrm.functional.resource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.functional.TestFunctional;
import com.github.vti.amcrm.functional.model.CustomerRequestModel;

@Tag("functional")
public class CustomerResourceTest {

    private Api api;

    @BeforeEach
    void setUp() throws Exception {
        api = TestFunctional.buildApi();

        api.start();
    }

    @AfterEach
    void tearDown() {
        api.stop();
    }

    @Test
    void createsCustomer() throws JsonProcessingException {
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.id = TestData.getRandomId();
        customer.name = TestData.getRandomName();
        customer.surname = TestData.getRandomSurname();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(customer))
                .when()
                .post(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.CUSTOMER.toString()));
    }

    @Test
    void getsCustomerDetails() throws JsonProcessingException {
        String customerId = TestFunctional.createCustomer();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .when()
                .get(TestFunctional.buildPath(Api.Resource.CUSTOMERS, customerId))
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.CUSTOMER.toString()));
    }

    @Test
    void deletesCustomer() throws JsonProcessingException {
        String customerId = TestFunctional.createCustomer();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .when()
                .delete(TestFunctional.buildPath(Api.Resource.CUSTOMERS, customerId))
                .then()
                .statusCode(204);
    }

    @Test
    void listsCustomers() throws JsonProcessingException {
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .when()
                .get(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.CUSTOMER_LIST.toString()));
    }

    @Test
    void returnsValidationError() throws JsonProcessingException {
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.name = TestData.getRandomName();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(customer))
                .when()
                .post(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(422)
                .assertThat()
                .body(
                        matchesJsonSchemaInClasspath(
                                TestFunctional.Model.VALIDATION_ERROR.toString()));
    }
}
