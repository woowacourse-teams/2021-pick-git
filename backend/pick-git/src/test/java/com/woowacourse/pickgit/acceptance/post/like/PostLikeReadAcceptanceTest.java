package com.woowacourse.pickgit.acceptance.post.like;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.MARKPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.DANI;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.KODA;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostLikeReadAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트를등록한다(MARKPOST);
        KEVIN.은로그인을하고().포스트를등록한다(KEVINPOST);

        MARK.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
        KODA.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        DANI.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
    }

    @DisplayName("특정 게시물을 좋아요한 계정 리스트를 조회할 수 있다 - 로그인/성공")
    @Test
    void searchLikeUsers_LoginUser_Success() {
        List<LikeUsersResponse> ListUsersForMarkPost = NEOZAL.은로그인을하고().포스트에좋아요한사용자를조회한다(MARKPOST);

        assertThat(ListUsersForMarkPost)
            .extracting("username")
            .containsExactly(MARK.name(), KODA.name());
    }

    @DisplayName("특정 게시물을 좋아요한 계정 리스트를 조회할 수 있다 - 비 로그인/성공")
    @Test
    void searchLikeUsers_GuestUser_Success() {
        List<LikeUsersResponse> ListUsersForMarkPost = GUEST.는().포스트에좋아요한사용자를조회한다(MARKPOST);

        assertThat(ListUsersForMarkPost)
            .extracting("username")
            .containsExactly(MARK.name(), KODA.name());
    }

    @DisplayName("좋아요가 없는 게시물을 조회하면 빈 배열을 반환한다. - 비 로그인/성공")
    @Test
    void searchLikeUsers_EmptyLikes_Success() {
        List<LikeUsersResponse> ListUsersForNeozalPost = NEOZAL.은로그인을하고().포스트에좋아요한사용자를조회한다(NEOZALPOST);
        assertThat(ListUsersForNeozalPost).isEmpty();
    }

    @DisplayName("존재하지 않은 포스트를 조회하면 500예외가 발생한다. - 비 로그인/실패")
    @Test
    void searchLikeUsers_InvalidPostId_500Exception() {
        GUEST.는().포스트에좋아요한사용자를조회한다(99999L, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
