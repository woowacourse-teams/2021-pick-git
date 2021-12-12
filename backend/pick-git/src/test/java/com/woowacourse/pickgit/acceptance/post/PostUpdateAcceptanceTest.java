package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.fixture.CPost;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostUpdateAcceptanceTest extends AcceptanceTest {

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
}
