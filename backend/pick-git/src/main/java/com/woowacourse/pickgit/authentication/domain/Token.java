package com.woowacourse.pickgit.authentication.domain;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("token")
public class Token {

    @Id
    private String id;
    private String refreshToken;
    private String oauthToken;

    public Token() {
    }

    public Token(String id, String refreshToken, String oauthToken) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.oauthToken = oauthToken;
    }

    public String getId() {
        return id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token = (Token) o;
        return Objects.equals(getId(), token.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
