package com.woowacourse.pickgit.common.request_builder.parameters.post;

import static com.woowacourse.pickgit.common.PickgitHeaders.GITHUB_FOLLOWING;

import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import com.woowacourse.pickgit.common.request_builder.parameters.put.Put_PostUpdateParameters;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpMethod;

public class Post_FollowingsParameters extends Parameters {

    public Post_FollowingsParameters(
        RequestSpecification spec,
        HttpMethod httpMethod,
        String url,
        Object... params
    ) {
        super(spec, httpMethod, url, params);
    }

    public Post_FollowingsParameters githubFollowing(boolean bool) {
        setParam(GITHUB_FOLLOWING, bool);
        return this;
    }
}
