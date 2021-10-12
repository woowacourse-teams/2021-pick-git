package com.woowacourse.pickgit.config.redis;

import com.woowacourse.pickgit.exception.redis.EmbeddedRedisServerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

@Slf4j
@Profile({"test", "local"})
@Configuration
public class EmbeddedRedisConfiguration {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String BIN_SH = "/bin/sh";
    private static final String BIN_SH_OPTION = "-c";
    private static final String COMMAND = "netstat -nat | grep LISTEN|grep %d";
    private static final int START_PORT = 10000;
    private static final int END_PORT = 65535;

    @Value("${security.redis.port}")
    private int port;

    private RedisServer redisServer;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(LOCAL_HOST, port);
    }

    @PostConstruct
    public void redisServer() throws IOException {
        int redisPort = isRedisRunning() ? findAvailablePort() : port;
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }

    private boolean isRedisRunning() throws IOException {
        return isRunning(executeGrepProcessCommand(port));
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

        } catch (Exception e) {
            log.error("Embedded Redis Server Error: {}", e);
        }

        return !pidInfo.toString().isEmpty();
    }

    private Process executeGrepProcessCommand(int port) throws IOException {
        String command = String.format(COMMAND, port);
        String[] shell = {BIN_SH, BIN_SH_OPTION, command};
        return Runtime.getRuntime().exec(shell);
    }

    private int findAvailablePort() throws IOException {
        for (int tempPort = START_PORT; tempPort <= END_PORT; tempPort++) {
            Process process = executeGrepProcessCommand(tempPort);
            if (!isRunning(process)) {
                return tempPort;
            }
        }
        throw new EmbeddedRedisServerException();
    }
}
