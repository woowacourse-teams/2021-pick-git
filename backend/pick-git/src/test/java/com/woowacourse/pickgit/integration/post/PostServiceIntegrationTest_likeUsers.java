package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Import(InfrastructureTestConfiguration.class)
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PostServiceIntegrationTest_likeUsers {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("로그인한 사용자는 게시물에 좋아요한 유저 리스트를 조회한다 - 성공")
    @Test
    void likeUsers_LoginUser_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto("author", false);

        User author = UserFactory.user("author");
        userRepository.save(author);

        List<User> likeUsers = UserFactory.mockLikeUsers();
        Post post = PostFactory.post(author);
        likePost(likeUsers, post);
        authorFollowSomeUsers(author, likeUsers);

        Post savedPost = postRepository.save(post);
        Long postId = savedPost.getId();

        List<LikeUsersResponseDto> expectedResponse =
            createLikeUserResponseDtoForLoginUser(likeUsers);

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

    @DisplayName("게스트는 게시물에 좋아요한 유저 리스트를 조회한다 - 성공")
    @Test
    void likeUsers_GuestUser_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        User author = UserFactory.user("author");
        userRepository.save(author);

        List<User> likeUsers = UserFactory.mockLikeUsers();
        Post post = PostFactory.post(author);
        likePost(likeUsers, post);
        authorFollowSomeUsers(author, likeUsers);

        Post savedPost = postRepository.save(post);
        Long postId = savedPost.getId();

        List<LikeUsersResponseDto> expectedResponse =
            createLikeUserResponseForGuest(likeUsers);

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
    }

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

    private void likePost(List<User> likeUsers, Post post) {
        likeUsers.forEach(user -> {
            userRepository.save(user);
            post.like(user);
        });
    }

    private void authorFollowSomeUsers(User author, List<User> likeUsers) {
        for (int i = 0; i < 2; i++) {
            User user = likeUsers.get(i);
            author.follow(user);
        }
    }

    private List<LikeUsersResponseDto> createLikeUserResponseDtoForLoginUser(
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user -> {
                if (isFollowingUser(likeUsers, user)) {
                    return new LikeUsersResponseDto(
                        user.getImage(), user.getName(), true
                    );
                }
                return new LikeUsersResponseDto(
                    user.getImage(), user.getName(), false
                );
            }).collect(toList());
    }

    private boolean isFollowingUser(List<User> likeUsers, User user) {
        return user.getName().equals(likeUsers.get(0).getName())
            || user.getName().equals(likeUsers.get(1).getName());
    }

    private List<LikeUsersResponseDto> createLikeUserResponseForGuest(List<User> likeUsers) {
        return likeUsers.stream()
            .map(
                user -> new LikeUsersResponseDto(user.getImage(), user.getName(), null)
            ).collect(toList());
    }
}


