package com.woowacourse.pickgit.acceptance.user;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserContributionAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
    }

    @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_LoginUser_Success() {
        ContributionResponseDto response = NEOZAL.은로그인을하고().활동_통계를_조회한다(MARK)
            .as(ContributionResponseDto.class);

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(ContributionResponseDto.builder()
                .starsCount(11)
                .commitsCount(48)
                .prsCount(48)
                .issuesCount(48)
                .reposCount(48)
                .build());
    }

    @DisplayName("유효하지 않은 토큰으로 활동 통계를 조회할 수 없다. - 401 예외")
    @Test
    void getContributions_invalidToken_401Exception() {
        ApiErrorResponse response = GUEST.는().비정상토큰으로_활통통계를_조회한다(NEOZAL)
            .as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("유효하지 않은 유저 이름으로 활동 통계를 조회할 수 없다. - 400 예외")
    @Test
    void getContributions_invalidUsername_400Exception() {
        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().활동_통계를_조회한다(GUEST);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }
}
