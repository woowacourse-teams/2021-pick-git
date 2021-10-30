package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.MARKPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.DANI;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.KODA;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static com.woowacourse.pickgit.common.fixture.TUser.모든유저;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PostReadAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        모든유저().로그인을한다();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트를등록한다(MARKPOST);
        KEVIN.은로그인을하고().포스트를등록한다(KEVINPOST);

        MARK.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        MARK.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
        KODA.은로그인을하고().포스트에좋아요를누른다(MARKPOST);
        DANI.은로그인을하고().포스트에좋아요를누른다(KEVINPOST);
    }

    @DisplayName("로그인일때 전체 홈 피드를 조회한다. - 게시글 좋아요 여부 true/false")
    @Test
    void allHomeFeed_LoginUser_Success() {
        List<PostResponseDto> response = NEOZAL.은로그인을하고().전체_홈피드를_조회한다().as(new TypeRef<>() {
        });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(false, false, false);
    }

    @DisplayName("로그인일때 팔로잉 홈 피드를 조회한다. - 게시글 좋아요 여부 true/false")
    @Test
    void followingHomeFeed_LoginUser_Success() {
        List<PostResponseDto> response = NEOZAL.은로그인을하고().팔로잉_홈피드를_조회한다().as(new TypeRef<>() {
        });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비 로그인이어도 홈 피드 조회가 가능하다. - 게시물 좋아요 여부는 항상 null")
    @Test
    void allHomeFeed_GuestUser_Success() {
        List<PostResponseDto> response = GUEST.는().전체_홈피드를_조회한다().as(new TypeRef<>() {
        });

        assertThat(response)
            .hasSize(3)
            .extracting("liked")
            .containsExactly(null, null, null);
    }

    @DisplayName("게스트는 팔로우 피드 조회가 불가능하다.")
    @Test
    void followingHomeFeed_GuestUser_Success() {
        ApiErrorResponse response = GUEST.는().팔로잉_홈피드를_조회한다().as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("A0002");
    }

    @DisplayName("로그인 상태에서 내 피드 조회가 가능하다.")
    @Test
    void readMyFeed_LoginUser_Success() {
        List<PostResponseDto> response = NEOZAL.은로그인을하고().내_피드를_조회한다().as(
            new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비로그인 상태에서는 내 피드 조회가 불가능하다.")
    @Test
    void readMyFeed_GuestUser_Success() {
        int statusCode = GUEST.는().내_피드를_조회한다().statusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_LoginUser_Success() {
        List<PostResponseDto> response = NEOZAL.은로그인을하고().피드를_조회한다(MARK).as(
            new TypeRef<>() {
            });

        assertThat(response)
            .hasSize(1)
            .extracting("liked")
            .containsExactly(false);
    }

    @DisplayName("비 로그인 상태에서 다른 유저 피드 조회가 가능하다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        List<PostResponseDto> response = GUEST.는().피드를_조회한다(MARK).as(
            new TypeRef<>() {
            });

        assertThat(response).hasSize(1);
    }
}
