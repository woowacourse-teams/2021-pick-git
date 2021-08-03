package com.woowacourse.pickgit.common.request_builder;

import com.woowacourse.pickgit.common.request_builder.method_builder.PickGitPostMethodBuilder;
import com.woowacourse.pickgit.common.request_builder.method_builder.PickGitPutMethodBuilder;
import com.woowacourse.pickgit.common.request_builder.parameters.NoParams;
import org.springframework.http.HttpMethod;

public class PickGitRequest {

    public static PickGitPostMethodBuilder post(String url, Object... params) {
        return new PickGitPostMethodBuilder(url, params);
    }

    public static LoginBuilder<NoParams> get(String url, Object... params) {
        return new LoginBuilder<>(NoParams.class, HttpMethod.GET, url, params);
    }

    public static PickGitPutMethodBuilder put(String url, Object... params) {
        return new PickGitPutMethodBuilder(url, params);
    }

    public static LoginBuilder<NoParams> delete(String url, Object... params) {
        return new LoginBuilder<NoParams>(NoParams.class, HttpMethod.DELETE, url, params);
    }
}
