package com.woowacourse.pickgit.authentication.infrastructure.dao;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisOAuthAccessTokenDao implements OAuthAccessTokenDao {

    private final ValueOperations<String, String> opsForValue;
    private final long expirationTimeInMilliSeconds;

    public RedisOAuthAccessTokenDao(
        StringRedisTemplate redisTemplate,
        @Value("${security.jwt.expiration-time}") long expirationTimeInMilliSeconds
    ) {
        this.opsForValue = redisTemplate.opsForValue();
        this.expirationTimeInMilliSeconds = expirationTimeInMilliSeconds;
    }

    @Override
    public void insert(String token, String oauthAccessToken) {
        opsForValue.set(
            token,
            oauthAccessToken,
            expirationTimeInMilliSeconds,
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    public Optional<String> findByKeyToken(String token) {
        return Optional.ofNullable(opsForValue.get(token));
    }
}
