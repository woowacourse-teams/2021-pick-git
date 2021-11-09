package com.woowacourse.pickgit.unit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.infrastructure.requester.GithubRepositoryApiRequester;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

class GithubRepositoryApiRequesterTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String VALID_RESPONSE = "["
        + "{\"name\": \"binghe-hi\", \"html_url\": \"https://github.com/jipark3/binghe-hi\"},"
        + "{\"name\": \"doms-react\", \"html_url\": \"https://github.com/jipark3/doms-react\"}"
        + "]";
    private static final String INVALID_RESPONSE = "{"
        + "\"message\": \"Bad credentials\","
        + "\"documentation_url\": \"https://docs.github.com/rest\""
        + "}";

    private GithubRepositoryApiRequester githubRepositoryApiRequester;

    @BeforeEach
    void setUp() {
        setUpGithubRepositoryApiRequester((requestEntity, responseType) -> {
                HttpHeaders headers = requestEntity.getHeaders();

                List<String> authorizations = Optional.ofNullable(
                    headers.get(HttpHeaders.AUTHORIZATION)
                ).orElse(List.of());

                boolean hasAccessToken = authorizations.stream()
                    .anyMatch(authorization -> authorization.contains(ACCESS_TOKEN));

                if (!hasAccessToken) {
                    return ResponseEntity.ok(
                        responseType.cast(INVALID_RESPONSE)
                    );
                }

                return ResponseEntity.ok(
                    responseType.cast(VALID_RESPONSE)
                );
            }
        );
    }

    @DisplayName("깃허브 레포지토리를 요청하면, 정상적인 반환값을 얻는다.")
    @Test
    void request_requestGithubRepositoryOfUser_thenGetRightResponse() {
        //when
        String request = githubRepositoryApiRequester.request(ACCESS_TOKEN, "githubUrl");

        //then
        assertThat(request).isEqualTo(VALID_RESPONSE);
    }

    @DisplayName("잘못된 토큰으로 깃허브 레포지토리 요청을 하면, Bad Credential 예외가 발생한다.")
    @Test
    void request_requestGithubRepositoryOfUserWithBadBearerToken_thenGetBadCredential() {
        //when
        String request = githubRepositoryApiRequester.request("Bearer invalidToken", "githubUrl");

        //then
        assertThat(request).isEqualTo(INVALID_RESPONSE);
    }

    @DisplayName("토큰 없이 깃허브 레포지토리 요청을 하면, Bad Credential 예외가 발생한다.")
    @Test
    void request_requestGithubRepositoryWithoutBearerToken_thenGetBadCredential() {
        //when
        String request = githubRepositoryApiRequester.request(null, "githubUrl");

        //then
        assertThat(request).isEqualTo(INVALID_RESPONSE);
    }

    private void setUpGithubRepositoryApiRequester(
        RestClientExchangeFunctionalInterface functionalInterface
    ) {
        githubRepositoryApiRequester = new GithubRepositoryApiRequester(
            new StubRestClient() {
                @Override
                public <T> ResponseEntity<T> exchange(
                    RequestEntity<?> requestEntity, Class<T> responseType
                ) throws RestClientException {
                    ResponseEntity<Object> exchange =
                        functionalInterface.exchange(requestEntity, responseType);

                    return new ResponseEntity<>(
                        responseType.cast(exchange.getBody()),
                        exchange.getHeaders(),
                        exchange.getStatusCode()
                    );
                }
            }
        );
    }

    private interface RestClientExchangeFunctionalInterface {

        ResponseEntity<Object> exchange(
            RequestEntity<?> requestEntity, Class<? extends Object> responseType
        );
    }
}
