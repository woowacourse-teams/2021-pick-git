package com.woowacourse.pickgit.common.request_builder.parameters.put;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpMethod;

public class Put_LikesAddParameters extends Parameters {

    public Put_LikesAddParameters(RequestSpecification spec,
        HttpMethod httpMethod, String url, Object... params) {
        super(spec, httpMethod, url, params);
    }
}
