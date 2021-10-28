package com.woowacourse.pickgit.common.fixture;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import org.springframework.http.MediaType;

public abstract class Act {

    protected ExtractableResponse<Response> request(
        String accessToken,
        String url,
        Method method
    ) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().request(method, url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String url,
        Method method
    ) {
        return RestAssured.given().log().all()
            .when().request(method, url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String accessToken,
        String url,
        Method method,
        TPost tPost
    ) {
        RequestSpecification spec = given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(tPost.params());

        for (File image : tPost.images()) {
            spec = spec.multiPart("images", image);
        }

        return spec.when()
            .request(method, url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String url,
        Method method,
        TPost tPost
    ) {
        RequestSpecification spec = given().log().all()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .formParams(tPost.params());

        for (File image : tPost.images()) {
            spec = spec.multiPart("images", image);
        }

        return spec.when()
            .request(method, url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String accessToken,
        String url,
        Method method,
        Object params
    ) {
        return given().log().all()
            .auth().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .request(method, url)
            .then().log().all()
            .extract();
    }

    protected ExtractableResponse<Response> request(
        String url,
        Method method,
        Object params
    ) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .request(method, url)
            .then().log().all()
            .extract();
    }
}
