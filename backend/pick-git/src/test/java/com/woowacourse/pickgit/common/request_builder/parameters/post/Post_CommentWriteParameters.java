package com.woowacourse.pickgit.common.request_builder.parameters.post;

import static com.woowacourse.pickgit.common.PickgitHeaders.CONTENT;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpMethod;

public class Post_CommentWriteParameters extends Parameters {

    public Post_CommentWriteParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public Post_CommentWriteParameters initAllParams() {
        content("test comment");
        return this;
    }

    public Post_CommentWriteParameters content(String content) {
        setParam(CONTENT, content);
        return this;
    }
}
