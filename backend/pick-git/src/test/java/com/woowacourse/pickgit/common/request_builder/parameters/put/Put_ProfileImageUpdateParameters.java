package com.woowacourse.pickgit.common.request_builder.parameters.put;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import com.woowacourse.pickgit.common.request_builder.parameters.post.Post_PostWriteParameters;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.nio.file.Files;
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

    public Put_ProfileImageUpdateParameters initAllParams() {
        body(FileFactory.getTestImage1File());
        return this;
    }

    public Put_ProfileImageUpdateParameters body(File file) {
        setBody(file);
        return this;
    }
}
