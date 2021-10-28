package com.woowacourse.pickgit.acceptance.comment;

import static com.woowacourse.pickgit.common.fixture.TPost.KEVINPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.MARKPOST;
import static com.woowacourse.pickgit.common.fixture.TPost.NEOZALPOST;
import static com.woowacourse.pickgit.common.fixture.TUser.GUEST;
import static com.woowacourse.pickgit.common.fixture.TUser.KEVIN;
import static com.woowacourse.pickgit.common.fixture.TUser.MARK;
import static com.woowacourse.pickgit.common.fixture.TUser.NEOZAL;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.acceptance.AcceptanceTest;
import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CommentReadAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUp() {
        toRead();
        NEOZAL.은로그인을하고().포스트를등록한다(NEOZALPOST);
        MARK.은로그인을하고().포스트를등록한다(MARKPOST);
        KEVIN.은로그인을하고().포스트를등록한다(KEVINPOST);
    }

    @DisplayName("사용자는 PostId로 댓글을 가져올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    void queryComments_UserCanRequestCommentsOfSpecificPost_Success(
        int commentSize,
        int page,
        int limit,
        int expected
    ) {
        for (int i = 0; i < commentSize; i++) {
            MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, "comment" + i);
        }

        List<CommentResponse> actual = NEOZAL.은로그인을하고().댓글을조회한다(NEOZALPOST, page, limit)
            .as(new TypeRef<>() {
            });

        assertThat(actual).hasSize(expected);
    }

    @DisplayName("게스트는 PostId로 댓글을 가져올 수 있다.")
    @ParameterizedTest
    @MethodSource("getParametersForQueryComments")
    void queryComments_GuestCanRequestCommentsOfSpecificPost_Success(int commentSize, int page,
                                                                     int limit, int expected) {
        for (int i = 0; i < commentSize; i++) {
            MARK.은로그인을하고().댓글을등록한다(NEOZALPOST, "comment" + i);
        }

        List<CommentResponse> actual = GUEST.는().댓글을조회한다(NEOZALPOST, page, limit)
            .as(new TypeRef<>() {
            });

        assertThat(actual).hasSize(expected);
    }
}
