package com.woowacourse.pickgit.config.redis;

import com.woowacourse.pickgit.exception.redis.EmbeddedRedisServerException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import redis.embedded.RedisServer;

@Slf4j
@Profile(value = {"test", "local"})
@Configuration
public class EmbeddedRedisConfiguration {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final String BIN_SH = "/bin/sh";
    private static final String BIN_SH_OPTION = "-c";
    private static final String COMMAND = "netstat -nat | grep LISTEN | grep %d";
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
        if (isArmMac()) {
            redisServer = new RedisServer(Objects.requireNonNull(getRedisFileForArcMac()),
                redisPort);
        }
        if (!isArmMac()) {
            redisServer = new RedisServer(redisPort);
        }
        redisServer.start();
    }

    private boolean isArmMac() {
        return Objects.equals(System.getProperty("os.arch"), "aarch64") &&
            Objects.equals(System.getProperty("os.name"), "Mac OS X");
    }

    private File getRedisFileForArcMac() {
        try {
            return new ClassPathResource("binary/redis/redis-server-6.2.5-mac-arm64").getFile();
        } catch (Exception e) {
            throw new EmbeddedRedisServerException();
        }
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

        try (BufferedReader input = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {

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
