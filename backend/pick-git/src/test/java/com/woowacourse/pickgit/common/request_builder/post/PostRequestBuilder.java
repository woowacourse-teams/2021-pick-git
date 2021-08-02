package com.woowacourse.pickgit.common.request_builder.post;

import com.woowacourse.pickgit.common.request_builder.LoginBuilder;
import com.woowacourse.pickgit.common.request_builder.RequestBuilder;
import com.woowacourse.pickgit.common.request_builder.ServiceBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class PostRequestBuilder implements RequestBuilder {

    public LoginBuilder<Write> write() {
        return new LoginBuilder<>(Write.class);
    }

    public static class Write implements ServiceBuilder {

        private RequestSpecification spec;

        public Write(RequestSpecification spec) {
            this.spec = spec;
        }

        @Override
        public RequestSpecification getSpec() {
            return spec;
        }

        @Override
        public ExtractableResponse<Response> responseExtract(RequestSpecification spec) {
            return spec.when()
                .post("/api/posts")
                .then().log().all()
                .extract();
        }
    }
}