package com.woowacourse.pickgit.unit.post.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.common.mockapi.MockRepositoryApiRequester;
import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.post.domain.util.PlatformRepositorySearchExtractor;
import com.woowacourse.pickgit.post.domain.util.dto.RepositoryNameAndUrl;
import com.woowacourse.pickgit.post.infrastructure.extractor.GithubRepositorySearchExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

public class GithubRepositorySearchExtractorTest {

    private static final String ACCESS_TOKEN = "oauth.access.token";
    private static final String USERNAME = "jipark3";
    private static final String KEYWORD = "woowa";
    private static final int PAGE = 0;
    private static final int LIMIT = 2;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PlatformRepositorySearchExtractor platformRepositorySearchExtractor;

    @BeforeEach
    void setUp() {
        this.platformRepositorySearchExtractor = new GithubRepositorySearchExtractor(
                objectMapper,
                new MockRepositoryApiRequester(),
            "https://api.github.com"
            );
    }

    @DisplayName("Github 레포지토리를 검색 키워드와 함께 요청하면 해당 레포지토리 리스트를 반환한다. - 성공")
    @Test
    void extract_RepoSearch_Success() {
        // given
        List<RepositoryNameAndUrl> expectedResponse =
            List.of(
                new RepositoryNameAndUrl("woowa-binghe-hi", "https://github.com/jipark3/woowa-binghe-hi"),
                new RepositoryNameAndUrl("woowa-doms-react", "https://github.com/jipark3/woowa-doms-react")
            );

        // when
        List<RepositoryNameAndUrl> response = platformRepositorySearchExtractor.extract(
            ACCESS_TOKEN,
            USERNAME,
            KEYWORD,
            PageRequest.of(PAGE, LIMIT)
        );

        // then
        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    @DisplayName("유효하지 않은 토큰 일 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidToken_500Exception() {
        // given when
        String invalidToken = "Invalid Token";

        // then
        assertThatThrownBy(() ->
            platformRepositorySearchExtractor.extract(
                invalidToken, USERNAME, KEYWORD, PageRequest.of(PAGE, LIMIT)
            )
        ).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", "V0001"
            );

    }

    @DisplayName("유효하지 않은 유저 일 경우 예외가 발생한다. - 500 예외")
    @Test
    void extract_InvalidUser_500Exception() {
        // then
        assertThatThrownBy(() ->
            platformRepositorySearchExtractor.extract(
                ACCESS_TOKEN, "invalid", KEYWORD, PageRequest.of(PAGE, LIMIT)
            )
        ).isInstanceOf(PlatformHttpErrorException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", "V0001"
            );

    }
}
