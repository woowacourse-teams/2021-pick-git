package com.woowacourse.pickgit.common.request_builder.parameters.put;

import static com.woowacourse.pickgit.common.PickgitHeaders.CONTENT;
import static com.woowacourse.pickgit.common.PickgitHeaders.TAGS;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.springframework.http.HttpMethod;

public class Put_PostUpdateParameters extends Parameters {

    public Put_PostUpdateParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public Put_PostUpdateParameters initAllParams() {
        tags("updateTag1", "updateTag2");
        content("update content");
        return this;
    }

    public Put_PostUpdateParameters tags(String... tags) {
        return tags(List.of(tags));
    }

    public Put_PostUpdateParameters tags(List<String> tags) {
        setParam(TAGS, tags);
        return this;
    }

    public Put_PostUpdateParameters content(String content) {
        setParam(CONTENT, content);
        return this;
    }
}
