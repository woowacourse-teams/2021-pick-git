package com.woowacourse.pickgit.acceptance.user;

import static com.woowacourse.pickgit.query.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.query.fixture.TUser.MARK;
import static com.woowacourse.pickgit.query.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;
import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileDescriptionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileImageEditResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserAcceptanceTest extends AcceptanceTest {

    @DisplayName("로그인되지 않은 사용자는 target 유저를 팔로우 할 수 없다.")
    @Test
    void followUser_NotLogin_Failure() {
        ApiErrorResponse response = GUEST.는().팔로우를한다(NEOZAL).as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("팔로우중이지 않다면 source 유저는 target 유저를 팔로우할 수 있다.")
    @Test
    void followUser_SourceToTarget_Success() {
        MARK.은로그인을한다();
        FollowResponse response = NEOZAL.은로그인을하고().팔로우를한다(MARK).as(FollowResponse.class);

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(new FollowResponse(1, true));
    }

    @DisplayName("source 유저와 target 유저가 동일하면 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SameSourceToSameTarget_400Exception() {
        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().팔로우를한다(NEOZAL);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToInvalidTarget_400Exception() {
        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().팔로우를한다(GUEST);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 이미 팔로우한 유저를 팔로우할 수 없다. - 400 예외")
    @Test
    void followUser_SourceToExistingTarget_400Exception() {
        MARK.은로그인을한다();
        NEOZAL.은로그인을하고().팔로우를한다(MARK);

        ApiErrorResponse response = NEOZAL.은로그인을하고().팔로우를한다(MARK).as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0002");
    }

    @DisplayName("로그인되지 않은 사용자는 target 유저를 언팔로우 할 수 없다.")
    @Test
    void unfollowUser_NotLogin_Failure() {
        NEOZAL.은로그인을한다();
        ExtractableResponse<Response> extractableResponse = GUEST.는().언팔로우를한다(NEOZAL);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("source 유저는 target 유저를 언팔로우할 수 있다.")
    @Test
    void unfollowUser_SourceToTarget_Success() {
        MARK.은로그인을한다();
        NEOZAL.은로그인을하고().팔로우를한다(MARK);
        FollowResponse response = NEOZAL.은로그인을하고().언팔로우를한다(MARK).as(FollowResponse.class);

        assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(new FollowResponse(0, false));
    }

    @DisplayName("source 유저와 target 유저가 동일하면 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_SameSourceToSameTarget_400Exception() {
        // when
        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().언팔로우를한다(NEOZAL);
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0004");
    }

    @DisplayName("source 유저는 존재하지 않는 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_SourceToInvalidTarget_400Exception() {
        ApiErrorResponse response = NEOZAL.은로그인을하고().언팔로우를한다(GUEST).as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0001");
    }

    @DisplayName("source 유저는 팔로우하지 않는 유저를 언팔로우할 수 없다. - 400 예외")
    @Test
    void unfollowUser_NotExistingFollow_ExceptionThrown() {
        MARK.은로그인을한다();
        ApiErrorResponse response = NEOZAL.은로그인을하고().언팔로우를한다(MARK).as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("U0003");
    }

    @DisplayName("로그인 사용자는 자신의 프로필 이미지를 수정할 수 있다.")
    @Test
    void editProfileImage_LoginUser_Success() throws IOException {
        ProfileImageEditResponse response = NEOZAL.은로그인을하고().프로필을_이미지를_수정한다()
            .as(ProfileImageEditResponse.class);

        assertThat(response.getImageUrl()).isNotBlank();
    }

    @DisplayName("게스트는 프로필 이미지를 수정할 수 없다.")
    @Test
    void editProfileImage_GuestUser_Fail() throws IOException {
        ExtractableResponse<Response> extractableResponse = GUEST.는().프로필_이미지를_수정한다();
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 사용자는 자신의 한 줄 소개를 수정할 수 있다.")
    @Test
    void editProfileDescription_LoginUser_Success() {
        ProfileDescriptionResponse response = NEOZAL.은로그인을하고().프로필_한줄소개를_수정한다("변경된 프로필")
            .as(ProfileDescriptionResponse.class);

        assertThat(response.getDescription()).isEqualTo("변경된 프로필");
    }

    @DisplayName("게스트는 자신의 한 줄 소개를 수정할 수 없다.")
    @Test
    void editProfileDescription_GuestUser_Fail() {
        ApiErrorResponse response = GUEST.는().프로필_한줄소개를_수정한다("변경된 프로필")
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }
}
