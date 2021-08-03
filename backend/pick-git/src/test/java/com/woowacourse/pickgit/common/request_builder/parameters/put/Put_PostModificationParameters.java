package com.woowacourse.pickgit.common.request_builder.parameters.put;

import static com.woowacourse.pickgit.common.PickgitHeaders.CONTENT;
import static com.woowacourse.pickgit.common.PickgitHeaders.TAGS;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.springframework.http.HttpMethod;

public class Put_PostModificationParameters extends Parameters {

    public Put_PostModificationParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public Put_PostModificationParameters tags(String... tags) {
        return tags(List.of(tags));
    }

    public Put_PostModificationParameters tags(List<String> tags) {
        setParam(TAGS, tags);
        return this;
    }

    public Put_PostModificationParameters content(String content) {
        setParam(CONTENT, content);
        return this;
    }
}
