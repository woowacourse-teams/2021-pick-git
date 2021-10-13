package com.woowacourse.pickgit.integration.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostServiceIntegrationTest_likeUsers extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("좋아요가 없는 포스트 조회 시 빈 배열을 반환한다. - 게스트/성공")
    @Test
    void likeUsers_EmptyLikes_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        User author = UserFactory.user("author");
        userRepository.save(author);

        Post savedPost = postRepository.save(PostFactory.post(author));
        Long postId = savedPost.getId();

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse).isEmpty();
    }

    @DisplayName("없는 포스트 조회 500예외가 발생한다. - 게스트/실패")
    @Test
    void likeUsers_InvalidPost_500Exception() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        Long postId = 10L;

        // when then
        assertThatThrownBy(
            () -> postService.likeUsers(authUserForPostRequestDto, postId)
        ).isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002");
    }
}


