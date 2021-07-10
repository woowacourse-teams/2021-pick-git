package com.woowacourse.pickgit.authentication.dao;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class OAuthAccessTokenDao {

    private final ConcurrentHashMap<String, String> tokenDb = new ConcurrentHashMap<>();

    public void insert(String token, String oauthAccessToken) {
        tokenDb.putIfAbsent(token, oauthAccessToken);
    }

    public void delete(String token) {
        tokenDb.remove(token);
    }

    public boolean isExistsByToken(String token) {
        return tokenDb.containsKey(token);
    }
}
