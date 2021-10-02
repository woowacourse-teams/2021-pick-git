package com.woowacourse.pickgit.config.redis;

import com.woowacourse.pickgit.exception.redis.InvalidExecuteProcessCommandException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

@Slf4j
@Profile(value = {"test", "local"})
@Configuration
public class EmbeddedRedisServerConfiguration {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String PASSWORD = "test";
    private static final String BIN_SH = "/bin/sh";
    private static final String BIN_SH_OPTION = "-c";
    private static final String COMMAND = "netstat -nat | grep LISTEN|grep %d";

    @Value("${security.redis.port}")
    private int port;

    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration =
            new RedisStandaloneConfiguration(LOCAL_HOST, port);
        configuration.setPassword(PASSWORD);
        return new LettuceConnectionFactory(configuration);
    }

    @PostConstruct
    public void redisServer() {
        int redisPort = findRedisPort();
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (!Objects.isNull(redisServer) && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    private int findRedisPort() {
        if (isRedisRunning()) {
            return findAvailablePort();
        }
        return port;
    }

    private boolean isRedisRunning() {
        return isRunning(executeGrepProcessCommand(port));
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input =
            new BufferedReader(new InputStreamReader(process.getInputStream()))
        ) {

            while (Objects.isNull(line = input.readLine())) {
                pidInfo.append(line);
            }

        } catch (Exception e) {
            log.error("Embedded Redis Server Error: {}", e);
        }

        return !pidInfo.toString().isEmpty();
    }

    private Process executeGrepProcessCommand(int port) {
        try {
            String command = String.format(COMMAND, port);
            String[] shell = {BIN_SH, BIN_SH_OPTION, command};
            return Runtime.getRuntime().exec(shell);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new InvalidExecuteProcessCommandException();
    }

    private int findAvailablePort() {

        for (int tempPort = 10000; tempPort <= 65535; tempPort++) {
            Process process = executeGrepProcessCommand(tempPort);
            if (!isRunning(process)) {
                return tempPort;
            }
        }

        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
    }
}
