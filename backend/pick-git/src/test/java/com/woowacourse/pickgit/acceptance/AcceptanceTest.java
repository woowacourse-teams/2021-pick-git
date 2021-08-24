package com.woowacourse.pickgit.acceptance;

import com.woowacourse.pickgit.common.request_builder.PickGitRequest;
import com.woowacourse.pickgit.config.DatabaseCleaner;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void init() {
        setPort();
    }

    @AfterEach
    void tearDown() {
        clearDataBase();
    }

    private void setPort() {
        RestAssured.port = port;
    }

    private void clearDataBase() {
        databaseCleaner.execute();
    }
}
