package com.woowacourse.pickgit.acceptance;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;

import com.woowacourse.pickgit.common.fixture.TContact;
import com.woowacourse.pickgit.common.fixture.TProject;
import com.woowacourse.pickgit.common.fixture.TSection;
import com.woowacourse.pickgit.config.DatabaseConfigurator;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import io.restassured.RestAssured;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public abstract class AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    private DatabaseConfigurator databaseConfigurator;

    @BeforeEach
    void init() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        clearDataBase();
        databaseConfigurator.toWrite();
    }

    private void clearDataBase() {
        databaseConfigurator.clear();
    }

    protected void toRead() {
        databaseConfigurator.toRead();
    }

    protected void toWrite() {
        databaseConfigurator.toWrite();
    }

    protected static Stream<Arguments> getPostSearchArguments() {
        return Stream.of(
            Arguments.of("java"),
            Arguments.of("java post", 0, 5),
            Arguments.of("", 0, 5),
            Arguments.of("c++", 0, 5),
            Arguments.of("html", 0, 5),
            Arguments.of("java c++ spring", 0, 5)
        );
    }

    protected static Stream<Arguments> getPortfolioUpdateArguments() {
        return Stream.of(
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST)),
                List.of(TSection.createRandom())
            ),
            Arguments.of(
                List.of(TContact.createRandom(), TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST)),
                List.of(TSection.createRandom())
            ),
            Arguments.of(
                List.of(TContact.createRandom(), TContact.createRandom(), TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST)),
                List.of(TSection.createRandom(), TSection.createRandom())
            )
        );
    }

    protected static Stream<Arguments> getPortfolioUpdateDuplicateProjectsArguments() {
        return Stream.of(
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST), TProject.of(NEOZALPOST)),
                List.of(TSection.createRandom())
            ),
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST), TProject.of(NEOZALPOST), TProject.of(NEOZALPOST)),
                List.of(TSection.createRandom())
            )
        );
    }

    protected static Stream<Arguments> getPortfolioUpdateInvalidDateProjectsArguments() {
        return Stream.of(
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.invalidDateOf(NEOZALPOST)),
                List.of(TSection.createRandom())
            )
        );
    }

    protected static Stream<Arguments> getPortfolioUpdateDuplicateSectionsArguments() {
        return Stream.of(
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST)),
                List.of(TSection.of(), TSection.of())
            ),
            Arguments.of(
                List.of(TContact.createRandom()),
                List.of(TProject.of(NEOZALPOST)),
                List.of(TSection.of(), TSection.of(), TSection.of())
            )
        );
    }

    protected static Stream<Arguments> getParametersForQueryComments() {
        return Stream.of(
            Arguments.of(10, 1, 3, 3),
            Arguments.of(10, 2, 3, 3),
            Arguments.of(10, 3, 3, 1),
            Arguments.of(10, 4, 3, 0)
        );
    }
}
