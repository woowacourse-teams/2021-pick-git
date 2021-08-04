package com.woowacourse.pickgit.authentication.infrastructure.dao;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class CollectionOAuthAccessTokenDao implements OAuthAccessTokenDao {

    private final ConcurrentHashMap<String, String> tokenDB = new ConcurrentHashMap<>();

    public void insert(String token, String oauthAccessToken) {
        tokenDB.put(token, oauthAccessToken);
    }

    public Optional<String> findByKeyToken(String token) {
        return Optional.ofNullable(tokenDB.get(token));
    }
}
