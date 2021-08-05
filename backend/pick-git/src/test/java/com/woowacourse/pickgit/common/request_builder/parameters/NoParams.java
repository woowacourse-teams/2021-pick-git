package com.woowacourse.pickgit.common.request_builder.parameters;

import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpMethod;

public class NoParams extends Parameters {

    public NoParams(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }
}
