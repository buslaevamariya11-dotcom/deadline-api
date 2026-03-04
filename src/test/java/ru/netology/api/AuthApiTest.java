package ru.netology.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.db.DbUtils;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthApiTest {

    @BeforeEach
    void clearAuth() {
        DbUtils.clearAuthCodes();
    }

    @Test
    void shouldLoginAndGetToken() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 9999;

        String loginBody =
                "{ \"login\": \"vasya\", \"password\": \"qwerty123\" }";

        given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .post("/api/auth")
                .then()
                .statusCode(200);

        String code = DbUtils.getAuthCode("vasya");

        String verifyBody =
                "{ \"login\": \"vasya\", \"code\": \"" + code + "\" }";

        String token =
                given()
                        .contentType(ContentType.JSON)
                        .body(verifyBody)
                        .post("/api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");

        assertNotNull(token);
    }
}