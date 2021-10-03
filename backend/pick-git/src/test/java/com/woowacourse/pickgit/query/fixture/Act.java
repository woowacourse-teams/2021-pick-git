package com.woowacourse.pickgit.query.fixture;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;

public abstract class Act {

    protected ExtractableResponse<Response> request(
        String accessToken,
        String url,
        Method method,
        HttpStatus httpStatus
    ) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().request(method, url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String url,
        Method method,
        HttpStatus httpStatus
    ) {
        return RestAssured.given().log().all()
            .when().request(method, url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }
}
