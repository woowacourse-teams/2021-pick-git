package com.woowacourse.pickgit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.time.Duration;
import java.util.ArrayList;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("test")
public class RedisCachingTestConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    private final ObjectMapper objectMapper;

    public RedisCachingTestConfiguration(
        RedisConnectionFactory redisConnectionFactory,
        ObjectMapper objectMapper
    ) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
    }

    @Bean(name = "cacheManager")
    public CacheManager redisCacheManager() {
        CollectionType listType =
            objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, PostResponseDto.class);

        RedisCacheConfiguration redisCachingConfiguration = RedisCacheConfiguration
            .defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new Jackson2JsonRedisSerializer<>(
                        listType
                    )
                )
            )
            .entryTtl(Duration.ofMinutes(30));

        return RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory)
            .cacheDefaults(redisCachingConfiguration)
            .build();
    }
}
