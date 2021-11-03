package com.woowacourse.pickgit.unit.tag.infrastructure;

import static com.woowacourse.pickgit.common.fixture.TRepository.OTHER;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.mockapi.MockGithubTagRequester;
import com.woowacourse.pickgit.tag.domain.PlatformTagExtractor;
import com.woowacourse.pickgit.tag.infrastructure.GithubTagExtractor;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GithubTagExtractorTest {

    private static final String TESTER_ACCESS_TOKEN = "oauth.access.token";
    private static final String USER_NAME = "jipark3";
    private static final String REPOSITORY_NAME = OTHER.name();

    private PlatformTagExtractor platformTagExtractor;

    @BeforeEach
    void setUp() {
        platformTagExtractor = new GithubTagExtractor(
            new MockGithubTagRequester(null),
            "https://api.github.com"
        );
    }

    @DisplayName("명시된 User의 Repository에 기술된 Language Tags(Other 제외)를 추출한다.")
    @Test
    void extractTags_ValidRepository_ExtractionSuccess() {
        List<String> tags = platformTagExtractor
            .extractTags(TESTER_ACCESS_TOKEN, USER_NAME, REPOSITORY_NAME);

        List<String> expected = OTHER.getTags().stream()
            .filter(t -> !t.equals("Other"))
            .collect(toList());

        assertThat(tags).containsExactlyInAnyOrderElementsOf(expected);
    }
}
