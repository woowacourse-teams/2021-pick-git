package com.woowacourse.pickgit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class RedisCleaner  {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    public void cleanUpRedis() {
        RedisConnection connection = redisConnectionFactory.getConnection();

        connection.execute("flushall");

        connection.close();
    }
}
