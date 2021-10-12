package com.woowacourse.pickgit.authentication.infrastructure.dao;

import com.woowacourse.pickgit.authentication.domain.OAuthAccessTokenDao;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisOAuthAccessTokenDao implements OAuthAccessTokenDao {

    private final ValueOperations<String, String> opsForValue;

    public RedisOAuthAccessTokenDao(StringRedisTemplate redisTemplate) {
        this.opsForValue = redisTemplate.opsForValue();
    }

    @Override
    public void insert(String token, String oauthAccessToken) {
        opsForValue.set(token, oauthAccessToken);
    }

    @Override
    public Optional<String> findByKeyToken(String token) {
        return Optional.ofNullable(opsForValue.get(token));
    }
}
