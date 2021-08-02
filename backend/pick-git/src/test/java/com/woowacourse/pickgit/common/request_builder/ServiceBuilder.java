package com.woowacourse.pickgit.common.request_builder;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public interface ServiceBuilder {

    RequestSpecification getSpec();
    ExtractableResponse<Response> responseExtract(RequestSpecification spec);
}
