package com.woowacourse.pickgit.unit.post.application;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.exception.post.PostNotFoundException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.like.Like;
import com.woowacourse.pickgit.post.domain.like.Likes;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest_likeUsers {

    private static final String IMAGE_URL = "https://github.com/testImage.jpg";

    @InjectMocks
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @DisplayName("로그인한 사용자는 게시물에 좋아요한 유저 리스트를 조회한다 - 성공")
    @Test
    void likeUsers_LoginUser_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto("author", false);

        Long postId = 1L;
        User author = UserFactory.user("author");

        List<User> likeUsers = UserFactory.mockLikeUsersWithId();
        followSomeUsers(author, likeUsers);

        Likes likes = new Likes(List.of(
            new Like(PostFactory.post(), likeUsers.get(0)),
            new Like(PostFactory.post(), likeUsers.get(1)),
            new Like(PostFactory.post(), likeUsers.get(2)),
            new Like(PostFactory.post(), likeUsers.get(3)),
            new Like(PostFactory.post(), likeUsers.get(4))
        ));

        Post post = PostFactory.likedPost(author, likes);

        given(postRepository.findPostWithLikeUsers(postId))
            .willReturn(Optional.of(post));
        given(userRepository.findByBasicProfile_Name("author"))
            .willReturn(Optional.of(author));

        List<LikeUsersResponseDto> expectedResponse =
            createLikeUserResponseDtoForLoginUser(post.getLikeUsers());

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);

        verify(postRepository, times(1))
            .findPostWithLikeUsers(postId);
        verify(userRepository, times(1))
            .findByBasicProfile_Name("author");
    }

    private void followSomeUsers(User author, List<User> likeUsers) {
        author.follow(likeUsers.get(0));
        author.follow(likeUsers.get(1));
    }

    @DisplayName("게스트는 게시물에 좋아요한 유저 리스트를 조회한다 - 성공")
    @Test
    void likeUsers_GuestUser_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        Long postId = 1L;
        User author = UserFactory.user("author");

        Likes likes = new Likes(List.of(
            new Like(PostFactory.post(), UserFactory.user("user1", IMAGE_URL)),
            new Like(PostFactory.post(), UserFactory.user("user2", IMAGE_URL)),
            new Like(PostFactory.post(), UserFactory.user("user3", IMAGE_URL))
        ));

        Post post = PostFactory.likedPost(author, likes);

        given(postRepository.findPostWithLikeUsers(postId))
            .willReturn(Optional.of(post));

        List<LikeUsersResponseDto> expectedResponse =
            createLikeUserResponseForGuest(post.getLikeUsers());

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);

        verify(postRepository, times(1))
            .findPostWithLikeUsers(postId);
        verify(userRepository, never())
            .findByBasicProfile_Name("author");
    }

    @DisplayName("좋아요가 없는 포스트 조회 시 빈 배열을 반환한다. - 게스트/성공")
    @Test
    void likeUsers_EmptyLikes_Success() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        Long postId = 1L;
        Post post = PostFactory.post(UserFactory.user("author"));

        given(postRepository.findPostWithLikeUsers(postId))
            .willReturn(Optional.of(post));

        // when
        List<LikeUsersResponseDto> actualResponse = postService
            .likeUsers(authUserForPostRequestDto, postId);

        // then
        assertThat(actualResponse).isEmpty();

        verify(postRepository, times(1))
            .findPostWithLikeUsers(postId);
        verify(userRepository, never())
            .findByBasicProfile_Name("author");
    }

    @DisplayName("없는 포스트 조회 500예외가 발생한다. - 게스트/실패")
    @Test
    void likeUsers_InvalidPost_500Exception() {
        // given
        AuthUserForPostRequestDto authUserForPostRequestDto =
            new AuthUserForPostRequestDto(null, true);

        Long postId = 1L;

        given(postRepository.findPostWithLikeUsers(postId))
            .willReturn(Optional.empty());

        // when then
        assertThatThrownBy(
            () -> postService.likeUsers(authUserForPostRequestDto, postId)
        ).isInstanceOf(PostNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "P0002");

        verify(postRepository, times(1))
            .findPostWithLikeUsers(postId);
        verify(userRepository, never())
            .findByBasicProfile_Name("author");
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
