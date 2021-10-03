package com.woowacourse.pickgit.integration;

import com.woowacourse.pickgit.config.DatabaseCleaner;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
public abstract class IntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }
}
