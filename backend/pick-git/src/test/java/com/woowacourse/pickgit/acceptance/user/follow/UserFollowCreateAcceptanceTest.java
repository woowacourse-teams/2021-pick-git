package com.woowacourse.pickgit.acceptance.user.follow;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.FollowResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserFollowCreateAcceptanceTest extends AcceptanceTest {

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
}
