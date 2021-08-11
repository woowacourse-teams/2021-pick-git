package com.woowacourse.pickgit.common.request_builder.parameters.put;

import static com.woowacourse.pickgit.common.PickgitHeaders.DESCRIPTION;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpMethod;

public class Put_ProfileContentUpdateParameters extends Parameters {

    public Put_ProfileContentUpdateParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
        spec.contentType(ContentType.TEXT_PLAIN.getMimeType());
    }

    public Put_ProfileContentUpdateParameters initAllParams() {
        description("test description");
        return this;
    }

    public Put_ProfileContentUpdateParameters description(String content) {
        setParam(DESCRIPTION, content);
        return this;
    }
}
