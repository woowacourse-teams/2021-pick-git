package com.woowacourse.pickgit.common.request_builder;

import com.woowacourse.pickgit.common.request_builder.post.PostRequestBuilder;
import io.restassured.RestAssured;
import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;

public class PickGitRequest {

    public static PostRequestBuilder post() {
        return new PostRequestBuilder();
    }

    @Test
    void name() {
        ExtractableResponse<Response> extract = RestAssured.given().log().all()
            .auth().form("bperhaps", "#@thsalstjd1A", new FormAuthConfig("/session", "login_field", "password"))
            .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36")
            .when().log().all()
            .post("https://github.com/login?client_id=7775859b314b6084eff0&redirect_url=http://dev.pickgit.p-e.kr/auth")
            .then().log().all()
            .extract();

        System.out.println(extract);
    }

}
