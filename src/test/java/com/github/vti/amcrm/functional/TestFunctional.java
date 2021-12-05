package com.github.vti.amcrm.functional;

import static io.restassured.RestAssured.given;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.RestAssured;

import com.github.vti.amcrm.Config;
import com.github.vti.amcrm.TestData;
import com.github.vti.amcrm.api.Api;
import com.github.vti.amcrm.api.DefaultObjectMapper;
import com.github.vti.amcrm.functional.model.CustomerRequestModel;

public class TestFunctional {
    public static int findOpenPort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No free port available");
        }
    }

    public static Api buildApi() {
        Config config = Config.builder().port(TestFunctional.findOpenPort()).build();

        String baseUrl = config.getBaseUrl().toString();

        RestAssured.baseURI = baseUrl;

        return new Api(config);
    }

    public static Map<String, String> getAuthenticatedUserHeaders() {
        return new HashMap<String, String>() {
            {
                put("Authorization", "user");
            }
        };
    }

    public static String createCustomer() throws JsonProcessingException {
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.id = TestData.getRandomId();
        customer.name = TestData.getRandomName();
        customer.surname = TestData.getRandomSurname();

        given().headers(getAuthenticatedUserHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(customer))
                .when()
                .post(Api.Resource.CUSTOMERS.toString())
                .then()
                .statusCode(200);

        return customer.id;
    }

    public static String buildPath(Api.Resource resource, String... parts) {
        return String.join(
                "/", resource.toString(), Arrays.stream(parts).collect(Collectors.joining("/")));
    }

    public enum Model {
        CUSTOMER("schema/api/model/customer.json"),
        CUSTOMER_LIST("schema/api/model/customer-list.json"),
        VALIDATION_ERROR("schema/api/model/validation-error.json");

        private String value;

        Model(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
