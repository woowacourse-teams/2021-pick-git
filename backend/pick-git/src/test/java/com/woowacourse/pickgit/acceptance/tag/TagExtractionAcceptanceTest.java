package com.woowacourse.pickgit.acceptance.tag;

import static com.woowacourse.pickgit.common.fixture.TRepository.PICK_GIT;
import static com.woowacourse.pickgit.common.fixture.TRepository.UNKNOWN;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class TagExtractionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
    }

    @DisplayName("특정 User의 Repository에 기술된 언어 태그들을 추출한다.")
    @Test
    void extractLanguageTags_ValidRepository_ExtractionSuccess() {
        List<String> response = NEOZAL.은로그인을하고().레포지토리의_태그를_추출한다(PICK_GIT)
            .as(new TypeRef<>() {
            });

        assertThat(response).containsExactlyInAnyOrderElementsOf(PICK_GIT.getTags());
    }

    @DisplayName("유효하지 않은 레포지토리 태그 추출 요청시 500 예외 메시지가 반환된다.")
    @Test
    void extractLanguageTags_InvalidRepository_ExceptionThrown() {
        ApiErrorResponse response = NEOZAL.은로그인을하고().레포지토리의_태그를_추출한다(UNKNOWN)
            .as(ApiErrorResponse.class);
        // then
        assertThat(response.getErrorCode()).isEqualTo("V0001");
    }

    @DisplayName("유효하지 않은 AccessToken으로 태그 추출 요청시 401 예외가 발생한다.")
    @Test
    void extractLanguageTags_InvalidAccessToken_ExceptionThrown() {
        ExtractableResponse<Response> extractableResponse =
            GUEST.는().비정상토큰으로_레포지토리의_태그를_추출한다(PICK_GIT);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("토큰을 포함하지 않고 태그 추출 요청시 401 예외가 발생한다.")
    @Test
    void extractLanguageTags_EmptyToken_ExceptionThrown() {
        ExtractableResponse<Response> extractableResponse =
            GUEST.는().레포지토리의_태그를_추출한다(PICK_GIT);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }
}
