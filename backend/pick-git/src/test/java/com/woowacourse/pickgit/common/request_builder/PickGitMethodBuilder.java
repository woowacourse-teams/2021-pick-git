package com.woowacourse.pickgit.common.request_builder;

import com.woowacourse.pickgit.common.request_builder.parameters.post.PostWriteParameters;
import org.springframework.http.HttpMethod;

public class PickGitMethodBuilder {
    private final HttpMethod httpMethod;
    private final String url;
    private final Object[] params;

    public PickGitMethodBuilder(
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.params = params;
    }

    public LoginBuilder<PostWriteParameters> Post_write() {
        return new LoginBuilder<>(PostWriteParameters.class, httpMethod, url, params);
    }
}
