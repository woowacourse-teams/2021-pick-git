package com.woowacourse.pickgit.common.request_builder.parameters.put;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpMethod;

public class Put_ProfileImageUpdateParameters extends Parameters {

    public Put_ProfileImageUpdateParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
        spec.contentType(ContentType.TEXT_PLAIN.getMimeType());
    }

    public Put_ProfileImageUpdateParameters body(File file) {
        setBody(file);
        return this;
    }
}
