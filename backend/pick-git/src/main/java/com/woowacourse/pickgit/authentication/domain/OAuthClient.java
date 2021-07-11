package com.woowacourse.pickgit.authentication.domain;

public interface OAuthClient {

    String getLoginUrl();

    String getAccessToken(String code);
}
