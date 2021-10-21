package com.woowacourse.pickgit.query.acceptance.user;

import static com.woowacourse.pickgit.query.fixture.TUser.DANI;
import static com.woowacourse.pickgit.query.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.query.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.query.fixture.TUser.KODA;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static com.woowacourse.pickgit.query.fixture.TUser.모든유저;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.query.fixture.TUser;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.response.UserProfileResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserAcceptanceTest_Query extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        모든유저().로그인을한다();

        NEOZAL.은로그인을하고().팔로우를한다(KODA, DANI);
        KODA.은로그인을하고().팔로우를한다(NEOZAL, MARK, DANI);
        MARK.은로그인을하고().팔로우를한다(KODA, DANI);
        DANI.은로그인을하고().팔로우를한다(KEVIN);
    }

    @DisplayName("로그인된 사용자는 자신의 프로필을 조회할 수 있다.")
    @Test
    void getAuthenticatedUserProfile_LoginUser_Success() {
        UserProfileResponse response = NEOZAL.은로그인을하고().자신의_프로필을_조회한다()
            .as(UserProfileResponse.class);
        assertThat(response.getName()).isEqualTo(NEOZAL.name());
    }

    @DisplayName("유효하지 않은 토큰을 지닌 사용자는 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithInvalidToken_401Exception() {
        ExtractableResponse<Response> extractableResponse = GUEST.는().비정상토큰으로_자신의_프로필을_조회한다();
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("토큰이 없는 사용자는 자신의 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUserWithoutToken_401Exception() {
        ExtractableResponse<Response> extractableResponse = GUEST.는().자신의_프로필을_조회한다();
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인된 사용자는 팔로우한 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsFollowing_Success() {
        MARK.은로그인을한다();
        NEOZAL.은로그인을하고().팔로우를한다(MARK);
        UserProfileResponse response = NEOZAL.은로그인을하고().프로필을_조회한다(MARK)
            .as(UserProfileResponse.class);

        assertThat(response.getName()).isEqualTo(MARK.name());
    }

    @DisplayName("로그인된 사용자는 팔로우하지 않은 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUserIsNotFollowing_Success() {
        UserProfileResponse response = NEOZAL.은로그인을하고().프로필을_조회한다(MARK)
            .as(UserProfileResponse.class);

        assertThat(response.getName()).isEqualTo(MARK.name());
    }

    @DisplayName("로그인된 사용자는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_LoginUser_400Exception() {
        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().프로필을_조회한다(GUEST);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("게스트 유저는 다른 유저의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_GuestUser_Success() {
        //given
        MARK.은로그인을한다();

        UserProfileResponse response = GUEST.는().프로필을_조회한다(MARK)
            .as(UserProfileResponse.class);

        assertThat(response.getName()).isEqualTo(MARK.name());
    }

    @DisplayName("게스트 유저는 존재하지 않는 유저 이름으로 프로필을 조회할 수 없다. - 400 예외")
    @Test
    void getUserProfile_GuestUser_400Exception() {
        ExtractableResponse<Response> extractableResponse = GUEST.는().프로필을_조회한다(GUEST);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. 단, 자기 자신은 검색되지 않는다.(팔로잉 여부 true/false)")
    @Test
    void searchUser_LoginUser_Success() {
        List<UserSearchResponseDto> response = KEVIN.은로그인을하고().유저를_검색한다("K")
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(2)
            .extracting("username", "following")
            .containsExactlyInAnyOrder(
                tuple(MARK.name(), false),
                tuple(KODA.name(), false)
            );
    }

    @DisplayName("비 로그인 - 저장된 유저중 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() {
        List<UserSearchResponseDto> response = GUEST.는().유저를_검색한다("K")
            .as(new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(3)
            .extracting("username", "following")
            .containsExactlyInAnyOrder(
                tuple(MARK.name(), null),
                tuple(KEVIN.name(), null),
                tuple(KODA.name(), null)
            );
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
