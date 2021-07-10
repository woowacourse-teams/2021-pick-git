package com.woowacourse.pickgit.authentication.dao;

import java.util.Optional;

public interface OAuthAccessTokenDao {

    void insert(String token, String oauthAccessToken);

    Optional<String> findByKeyToken(String token);
}
