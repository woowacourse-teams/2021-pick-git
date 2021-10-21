package com.woowacourse.pickgit.acceptance;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.config.DatabaseConfigurator;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    protected final TokenDto 로그인_되어있음(String name) {
        TokenDto response = 로그인_요청(name)
            .as(TokenDto.class);

        assertThat(response.getToken()).isNotBlank();

        return response;
    }

    protected final ExtractableResponse<Response> 로그인_요청(String name) {
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code={code}", name)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    private void clearDataBase() {
        databaseConfigurator.clear();
    }

    protected void toRead() {
        databaseConfigurator.toRead();
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
}
