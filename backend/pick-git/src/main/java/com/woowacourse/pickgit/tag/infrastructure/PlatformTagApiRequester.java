package com.woowacourse.pickgit.tag.infrastructure;

public interface PlatformTagApiRequester {

    String requestTags(String url, String accessToken);
}
