package com.woowacourse.pickgit.integration.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostFeedServiceIntegrationTest_SearchById extends IntegrationTest {

    @Autowired
    private PostFeedService postFeedService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인 사용자는 모든 게시물을 ID를 통해 조회할 수 있다. (좋아요 여부 true)")
    @Test
    void searchById_LoginUser_true() {
        // given
        Post savedPost = createMockPost();
        User savedUser = userRepository.save(UserFactory.user("neozal"));

        // when
        postService.like(new LoginUser(savedUser.getName(), ""), savedPost.getId());
        SearchPostRequestDto searchPostRequestDto
            = new SearchPostRequestDto(savedPost.getId(), savedUser.getName(), false);
        PostResponseDto postResponseDto = postFeedService.searchById(searchPostRequestDto);

        // then
        PostResponseDto actual = PostDtoAssembler.assembleFrom(savedUser, savedPost);

        assertThat(postResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(actual);
        assertThat(postResponseDto.getLiked()).isTrue();
    }

    @DisplayName("로그인 사용자는 모든 게시물을 ID를 통해 조회할 수 있다. (좋아요 여부 false)")
    @Test
    void searchById_LoginUser_false() {
        // given
        Post savedPost = createMockPost();
        User savedUser = userRepository.save(UserFactory.user("neozal"));

        // when
        SearchPostRequestDto searchPostRequestDto
            = new SearchPostRequestDto(savedPost.getId(), savedUser.getName(), false);
        PostResponseDto postResponseDto = postFeedService.searchById(searchPostRequestDto);

        // then
        PostResponseDto actual = PostDtoAssembler.assembleFrom(savedUser, savedPost);

        assertThat(postResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(actual);
        assertThat(postResponseDto.getLiked()).isFalse();
    }

    @DisplayName("비로그인 사용자는 모든 게시물을 ID를 통해 조회할 수 있다. (좋아요 여부 null)")
    @Test
    void searchById_GuestUser_null() {
        // given
        Post savedPost = createMockPost();

        // when
        SearchPostRequestDto searchPostRequestDto
            = new SearchPostRequestDto(savedPost.getId(), new GuestUser());
        PostResponseDto postResponseDto = postFeedService.searchById(searchPostRequestDto);

        // then
        PostResponseDto actual = PostDtoAssembler.assembleFrom(null, savedPost);

        assertThat(postResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(actual);
    }

    private Post createMockPost() {
        User savedUser = userRepository.save(UserFactory.user("mark"));
        Post post = PostFactory.mockPostBy(savedUser);

        return postRepository.save(post);
    }
}
