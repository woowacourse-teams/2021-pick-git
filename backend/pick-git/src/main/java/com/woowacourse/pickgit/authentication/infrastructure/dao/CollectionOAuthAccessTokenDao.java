package com.woowacourse.pickgit.authentication.infrastructure.dao;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionOAuthAccessTokenDao implements OAuthAccessTokenDao {

    private final ConcurrentHashMap<String, String> tokenDB;

    public CollectionOAuthAccessTokenDao() {
        this(new ConcurrentHashMap<>());
    }

    private CollectionOAuthAccessTokenDao(ConcurrentHashMap<String, String> tokenDB) {
        this.tokenDB = tokenDB;
    }

    public void insert(String token, String oauthAccessToken) {
        tokenDB.put(token, oauthAccessToken);
    }

    public Optional<String> findByKeyToken(String token) {
        return Optional.ofNullable(tokenDB.get(token));
    }
}
