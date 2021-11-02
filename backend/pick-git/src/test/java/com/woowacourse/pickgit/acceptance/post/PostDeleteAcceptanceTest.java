package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostDeleteAcceptanceTest {

    @DisplayName("사용자는 게시물을 삭제한다.")
    @Test
    void delete_LoginUser_Success() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        int statusCode = NEOZAL.은로그인을하고().포스트를삭제한다(NEOZALPOST).statusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("유효하지 않은 토큰으로 게시물을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_invalidToken_401Exception() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse =
            GUEST.는().비정상토큰으로_게시물을_삭제한다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }
}
