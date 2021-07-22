package com.woowacourse.pickgit.unit.tag.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.exception.platform.PlatformHttpErrorException;
import com.woowacourse.pickgit.tag.infrastructure.GithubTagApiRequester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GithubTagApiRequesterTest {

    private static final String GITHUB_TAG_API_FORMAT
        = "https://api.github.com/repos/%s/%s/languages";

    private GithubTagApiRequester requester;

    @BeforeEach
    void setUp() {
        requester = new GithubTagApiRequester();
    }

    @DisplayName("유효하지 않은 토큰이면 예외가 발생한다.")
    @Test
    void requestTags_InvalidAccessToken_ExceptionThrown() {
        // given
        String url = String.format(GITHUB_TAG_API_FORMAT, "binghe819", "TIL");
        String accessToken = "invalid access token";

        // when, then
        assertThatThrownBy(() -> requester.requestTags(url, accessToken))
            .isInstanceOf(PlatformHttpErrorException.class)
            .extracting("errorCode")
            .isEqualTo("V0001");
    }
}
