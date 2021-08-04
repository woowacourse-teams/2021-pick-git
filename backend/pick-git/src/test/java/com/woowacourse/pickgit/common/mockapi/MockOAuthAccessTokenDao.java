package com.woowacourse.pickgit.common.mockapi;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import java.util.Optional;

public class MockOAuthAccessTokenDao implements OAuthAccessTokenDao {

    @Override
    public void insert(String token, String oauthAccessToken) {
    }

    @Override
    public Optional<String> findByKeyToken(String token) {
        return Optional.of("oauth.access.token");
    }
}
