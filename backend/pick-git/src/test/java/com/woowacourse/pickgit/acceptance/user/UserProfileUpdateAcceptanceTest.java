package com.woowacourse.pickgit.acceptance.user;

import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileDescriptionResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.ProfileImageEditResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class UserProfileUpdateAcceptanceTest extends AcceptanceTest {

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
