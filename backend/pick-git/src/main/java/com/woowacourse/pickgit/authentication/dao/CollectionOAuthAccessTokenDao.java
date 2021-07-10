package com.woowacourse.pickgit.authentication.dao;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class CollectionOAuthAccessTokenDao implements OAuthAccessTokenDao {

    private final ConcurrentHashMap<String, String> tokenDb = new ConcurrentHashMap<>();

    public void insert(String token, String oauthAccessToken) {
        tokenDb.put(token, oauthAccessToken);
    }

    public Optional<String> findByKeyToken(String token) {
        return Optional.ofNullable(tokenDb.get(token));
    }
}
