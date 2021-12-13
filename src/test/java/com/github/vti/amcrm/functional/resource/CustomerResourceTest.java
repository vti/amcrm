package com.github.vti.amcrm.functional.resource;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;

import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.functional.TestFunctional;
import com.github.vti.amcrm.functional.model.CustomerRequestModel;

@Tag("functional")
public class CustomerResourceTest {

    private Api api;

    @TempDir Path tmpDir;

    @BeforeEach
    void setUp() {
        api = TestFunctional.buildApi(tmpDir);

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
    void getsCustomerDetailsAsAdmin() throws JsonProcessingException {
        String customerId = TestFunctional.createCustomer();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .get(TestFunctional.buildPath(Api.Resource.CUSTOMERS, customerId))
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.ADMIN_CUSTOMER.toString()));
    }

    @Test
    void patchesCustomer() throws JsonProcessingException {
        String customerId = TestFunctional.createCustomer();

        CustomerRequestModel customer = new CustomerRequestModel();
        customer.name = TestData.getRandomName();

        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(customer))
                .when()
                .patch(TestFunctional.buildPath(Api.Resource.CUSTOMERS, customerId))
                .then()
                .statusCode(200);
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
    void listsCustomersAsAdmin() throws JsonProcessingException {
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .get(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(
                        matchesJsonSchemaInClasspath(
                                TestFunctional.Model.ADMIN_CUSTOMER_LIST.toString()));
    }

    @Test
    void listsCustomersPaginated() throws JsonProcessingException {
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();
        TestFunctional.createCustomer();

        Response response1 =
                given().headers(TestFunctional.getAuthenticatedUserHeaders())
                        .when()
                        .queryParam("limit", "2")
                        .get(Api.Resource.CUSTOMERS.toString())
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .header("Link", containsString("page=2"))
                        .body(
                                matchesJsonSchemaInClasspath(
                                        TestFunctional.Model.CUSTOMER_LIST.toString()))
                        .extract()
                        .response();

        assertEquals(2, response1.jsonPath().getList("$").size());

        Response response2 =
                given().headers(TestFunctional.getAuthenticatedUserHeaders())
                        .when()
                        .queryParam("limit", "2")
                        .queryParam("page", "2")
                        .get(Api.Resource.CUSTOMERS.toString())
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .header("Link", containsString("page=3"))
                        .body(
                                matchesJsonSchemaInClasspath(
                                        TestFunctional.Model.CUSTOMER_LIST.toString()))
                        .extract()
                        .response();

        assertEquals(1, response2.jsonPath().getList("$").size());
    }

    @Test
    void returnsBadRequestErrorOnNotJson() throws JsonProcessingException {
        given().headers(TestFunctional.getAuthenticatedUserHeaders())
                .body("garbage")
                .when()
                .post(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(400);
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
