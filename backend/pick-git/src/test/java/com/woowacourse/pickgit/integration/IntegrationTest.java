package com.woowacourse.pickgit.integration;

import com.woowacourse.pickgit.config.DatabaseConfigurator;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@Transactional
@SpringBootTest
@ActiveProfiles({"test"})
public abstract class IntegrationTest {

    @Autowired
    private DatabaseConfigurator databaseConfigurator;

    @BeforeEach
    void setUp() {
        databaseConfigurator.toWrite();
        databaseConfigurator.clear();
    }

    @AfterEach
    void tearDown() {
        databaseConfigurator.clear();
        databaseConfigurator.toWrite();
    }
}
