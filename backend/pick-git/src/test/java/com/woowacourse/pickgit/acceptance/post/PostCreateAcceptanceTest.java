package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.fixture.CPost;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class PostCreateAcceptanceTest extends AcceptanceTest {

    @DisplayName("사용자는 게시글을 등록한다.")
    @Test
    void write_LoginUser_Success() {
        int statusCode = NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST).statusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("사용자는 태그 없이 게시글을 작성할 수 있다.")
    @Test
    void write_LoginUserWithNoneTags_Success() {
        CPost post = CPost.builder()
            .tags(List.of())
            .build();

        int statusCode = NEOZAL.은로그인을하고().포스트를등록한다(post).statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("잘못된 태그 이름을 가진 게시글을 작성할 수 없다.")
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "  ", "abcdeabcdeabcdeabcdea"})
    void write_LoginUserWithInvalidTags_Fail(String tagName) {
        CPost post = CPost.builder()
            .tags(List.of("Java", "JavaScript", tagName))
            .build();

        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().포스트를등록한다(post);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("F0003");
    }

    @DisplayName("사용자는 중복된 태그를 가진 게시글을 작성할 수 없다.")
    @Test
    void write_LoginUserWithDuplicatedTags_Fail() {

        CPost post = CPost.builder()
            .tags(List.of("Java", "JavaScript", "Java"))
            .build();

        ExtractableResponse<Response> extractableResponse = NEOZAL.은로그인을하고().포스트를등록한다(post);

        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("P0001");
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 유효하지 않은 토큰이 있는 경우 (Authorization header O)")
    @Test
    void write_GuestUserWithToken_Fail() {
        int statusCode = GUEST.는().유효하지_않은_토큰으로_포스트를등록한다(NEOZALPOST).statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("게스트는 게시글을 등록할 수 없다. - 토큰이 없는 경우 (Authorization header X)")
    @Test
    void write_GuestUserWithoutToken_Fail() {
        int statusCode = GUEST.는().포스트를등록한다(NEOZALPOST).statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
