package com.woowacourse.pickgit.acceptance.post;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.MARKPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.common.fixture.TPost;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PostSearchAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트를등록한다(MARKPOST);
        KEVIN.은로그인을하고().포스트를등록한다(KEVINPOST);
    }

    @DisplayName("유저는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void userCanFindPostViaTags(String keyword) {
        List<PostResponse> posts = NEOZAL.은로그인을하고().포스트를검색한다("tags", keyword, OK)
            .as(new TypeRef<>() {
            });

        List<String> contents = TPost.searchByTagAndGetContent(keyword);

        assertThat(posts)
            .extracting("content", String.class)
            .containsExactly(contents.toArray(String[]::new));
    }

    @DisplayName("게스트는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void guestCanFindPostViaTags(String keyword) {
        List<PostResponse> posts = GUEST.는().포스트를검색한다("tags", keyword, OK)
            .as(new TypeRef<>() {
            });

        List<String> contents = TPost.searchByTagAndGetContent(keyword);

        assertThat(posts)
            .extracting("content", String.class)
            .containsExactly(contents.toArray(String[]::new));
    }

    @DisplayName("존재하지 않는 type을 요청하면 예외가 발생한다.")
    @Test
    void userCanFindPostViaTags() {
        ApiErrorResponse response = NEOZAL.은로그인을하고().포스트를검색한다(
            "invalid", "java", BAD_REQUEST)
            .as(ApiErrorResponse.class);

        assertThat(response.getErrorCode()).isEqualTo("P0006");
    }

    @DisplayName("유저는 postId로 단일 포스트 검색을 할 수 있다.")
    @Test
    void userSearchPostById() {
        PostResponse response = NEOZAL.은로그인을하고().포스트를검색한다(NEOZALPOST, OK).as(PostResponse.class);

        assertThat(response.getId()).isEqualTo(NEOZALPOST.getId(true));
        assertThat(response.getContent()).isEqualTo(NEOZALPOST.getContent());
    }

    @DisplayName("게스트는 postId로 단일 포스트 검색을 할 수 있다.")
    @Test
    void guestSearchPostById() {
        PostResponse response = GUEST.는().포스트를검색한다(NEOZALPOST, OK).as(PostResponse.class);

        assertThat(response.getId()).isEqualTo(NEOZALPOST.getId(true));
        assertThat(response.getContent()).isEqualTo(NEOZALPOST.getContent());
    }
}
