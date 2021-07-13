package com.woowacourse.pickgit.tag.infrastructure;

public interface PlatformApiRequester {

    String requestTags(String url, String accessToken);
}
