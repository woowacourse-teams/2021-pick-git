package com.woowacourse.pickgit.common.request_builder;

import org.springframework.http.HttpMethod;

public class PickGitRequest {

    public static PickGitMethodBuilder post(String url, Object... params) {
        return new PickGitMethodBuilder(HttpMethod.POST, url, params);
    }

    public static PickGitMethodBuilder get(String url, Object... params) {
        return new PickGitMethodBuilder(HttpMethod.GET, url, params);
    }

    public static PickGitMethodBuilder put(String url, Object... params) {
        return new PickGitMethodBuilder(HttpMethod.PUT, url, params);
    }

    public static PickGitMethodBuilder delete(String url, Object... params) {
        return new PickGitMethodBuilder(HttpMethod.DELETE, url, params);
    }
}
