package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import com.woowacourse.pickgit.common.fixture.CPost;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

class PostAcceptanceTest extends AcceptanceTest {

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

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void userRepositories_LoginUser_Success() {
        // given
        List<RepositoryResponseDto> response =
            NEOZAL.은로그인을하고().레포지토리_목록을_가져온다().as(new TypeRef<>() {
        });

        assertThat(response).hasSize(2);
    }

    @DisplayName("토큰이 유효하지 않은 경우 Repository 목록을 가져오는데 예외가 발생한다. - 500 예외")
    @Test
    void userRepositories_InvalidAccessToken_500Exception() {
        int statusCode = GUEST.는().비정상토큰으로_레포지토리목록을_가져온다().statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그인 사용자는 게시물을 좋아요 할 수 있다. - 성공")
    @Test
    void likePost_LoginUser_Success() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        LikeResponse likeResponse =
            MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST).as(LikeResponse.class);

        assertThat(likeResponse.getLikesCount()).isOne();
        assertThat(likeResponse.getLiked()).isTrue();
    }

    @DisplayName("로그인 사용자는 게시물을 좋아요 취소 할 수 있다. - 성공")
    @Test
    void unlikePost_LoginUser_Success() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);
        LikeResponse response =
            MARK.은로그인을하고().포스트에좋아요_취소를_한다(NEOZALPOST).as(LikeResponse.class);

        assertThat(response.getLikesCount()).isZero();
        assertThat(response.getLiked()).isFalse();
    }

    @DisplayName("게스트는 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void likePost_GuestUser_401ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = GUEST.는().포스트에좋아요를누른다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("게스트는 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlikePost_GuestUser_401ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = GUEST.는().포스트에좋아요_취소를_한다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

    @DisplayName("로그인 사용자는 이미 좋아요한 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void likePost_DuplicatedLike_400ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse =
            MARK.은로그인을하고().포스트에좋아요를누른다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("P0003");
    }

    @DisplayName("로그인 사용자는 좋아요 하지 않은 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlikePost_cannotUnlike_400ExceptionThrown() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ExtractableResponse<Response> extractableResponse = MARK.은로그인을하고().포스트에좋아요_취소를_한다(NEOZALPOST);

        int statusCode = extractableResponse.statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST.value());

        ApiErrorResponse response = extractableResponse.as(ApiErrorResponse.class);
        assertThat(response.getErrorCode()).isEqualTo("P0004");
    }

    @DisplayName("사용자는 게시물을 수정한다.")
    @Test
    void update_LoginUser_Success() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        PostUpdateResponse response = NEOZAL.은로그인을하고().포스트를수정한다(NEOZALPOST, KEVINPOST)
            .as(PostUpdateResponse.class);

        assertThat(response.getContent()).isEqualTo(KEVINPOST.getContent());
        assertThat(response.getTags()).containsExactlyInAnyOrderElementsOf(KEVINPOST.getTags());
    }

    @DisplayName("유효하지 않은 내용(null)의 게시물은 수정할 수 없다. - 400 예외")
    @Test
    void update_NullContent_400Exception() {
        CPost post = CPost.builder()
            .content(null)
            .build();

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ApiErrorResponse response = NEOZAL.은로그인을하고().포스트를수정한다(NEOZALPOST, post)
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("F0001");
    }

    @DisplayName("유효하지 않은 내용(500자 초과)의 게시물은 수정할 수 없다. - 400 예외")
    @Test
    void update_Over500Content_400Exception() {
        CPost post = CPost.builder()
            .content("a".repeat(501))
            .build();

        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ApiErrorResponse response = NEOZAL.은로그인을하고().포스트를수정한다(NEOZALPOST, post)
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("F0004");
    }

    @DisplayName("유효하지 않은 토큰으로 게시물을 수정할 수 없다. - 401 예외")
    @Test
    void update_InvalidToken_401Exception() {
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        ApiErrorResponse response = GUEST.는().비정상토큰으로_게시물을_수정한다(NEOZALPOST, KEVINPOST)
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0001");
    }

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
