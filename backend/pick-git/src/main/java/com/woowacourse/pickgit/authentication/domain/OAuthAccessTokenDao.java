package com.woowacourse.pickgit.authentication.domain;

import java.util.Optional;

public interface OAuthAccessTokenDao {

    void insert(String token, String oauthAccessToken);

    Optional<String> findByKeyToken(String token);
}
