package com.woowacourse.pickgit.integration;

import com.woowacourse.pickgit.config.DatabaseConfigurator;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

@Import(InfrastructureTestConfiguration.class)
@Transactional
@SpringBootTest
@ActiveProfiles({"test"})
public abstract class IntegrationTest {

    @Autowired
    private DatabaseConfigurator databaseConfigurator;

    @AfterEach
    void tearDown() {
        databaseConfigurator.clear();
        databaseConfigurator.toWrite();
    }
}
