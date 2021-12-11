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
import com.github.vti.amcrm.functional.model.UserRequestModel;

@Tag("functional")
public class UserResourceTest {

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
    void createsUser() throws JsonProcessingException {
        UserRequestModel user = new UserRequestModel();
        user.id = TestData.getRandomId();
        user.admin = false;
        user.name = TestData.getRandomName();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(user))
                .when()
                .post(Api.Resource.USERS.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.USER.toString()));
    }

    @Test
    void getsUserDetails() throws JsonProcessingException {
        String userId = TestFunctional.createUser();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .get(TestFunctional.buildPath(Api.Resource.USERS, userId))
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.USER.toString()));
    }

    @Test
    void togglesUserAdminStatus() throws JsonProcessingException {
        String userId = TestFunctional.createUser();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .post(TestFunctional.buildPath(Api.Resource.USERS, userId, "admin"))
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.USER.toString()));
    }

    @Test
    void deletesUser() throws JsonProcessingException {
        String userId = TestFunctional.createUser();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .delete(TestFunctional.buildPath(Api.Resource.USERS, userId))
                .then()
                .statusCode(204);
    }

    @Test
    void listsUsers() throws JsonProcessingException {
        TestFunctional.createUser();
        TestFunctional.createUser();
        TestFunctional.createUser();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .when()
                .get(Api.Resource.USERS.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(TestFunctional.Model.USER_LIST.toString()));
    }

    @Test
    void listsUsersPaginated() throws JsonProcessingException {
        TestFunctional.createUser();
        TestFunctional.createUser();
        TestFunctional.createUser();

        Response response =
                given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                        .when()
                        .queryParam("limit", "2")
                        .get(Api.Resource.USERS.toString())
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .header("Link", containsString("page=2"))
                        .body(
                                matchesJsonSchemaInClasspath(
                                        TestFunctional.Model.USER_LIST.toString()))
                        .extract()
                        .response();

        assertEquals(2, response.jsonPath().getList("$").size());
    }

    @Test
    void returnsValidationError() throws JsonProcessingException {
        UserRequestModel user = new UserRequestModel();
        user.name = TestData.getRandomName();

        given().headers(TestFunctional.getAuthenticatedAdminHeaders())
                .body(DefaultObjectMapper.get().writeValueAsBytes(user))
                .when()
                .post(Api.Resource.USERS.toString())
                .then()
                .statusCode(422)
                .assertThat()
                .body(
                        matchesJsonSchemaInClasspath(
                                TestFunctional.Model.VALIDATION_ERROR.toString()));
    }
}
