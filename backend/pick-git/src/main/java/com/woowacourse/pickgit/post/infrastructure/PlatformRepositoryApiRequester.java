package com.woowacourse.pickgit.post.infrastructure;

public interface PlatformRepositoryApiRequester {

    String request(String token, String url);
}
