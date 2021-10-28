package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.integration.IntegrationTest;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.post.domain.repository.PostRepository;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.repository.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.UserAssembler;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

class PostFeedServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostFeedService postFeedService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DisplayName("비로그인 홈피드 - 전체 게시물 중 3, 4번째 글을 최신순으로 가져온다. (좋아요 여부 null)")
    @Test
    void readHomeFeed_Guest_LatestPosts() {
        // given
        createMockPosts();
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName(null)
            .isGuest(true)
            .pageable(PageRequest.of(1, 2))
            .build();

        // when
        List<PostResponseDto> postResponseDtos = postFeedService.allHomeFeed(homeFeedRequestDto);

        // then
        assertThat(postResponseDtos)
            .extracting("authorName", "githubRepoUrl", "liked")
            .containsExactly(
                tuple("dani", "java-racingcar", null),
                tuple("ginger", "jwp-chess", null)
            );
    }

    @DisplayName("로그인 홈피드 - 내가 팔로잉하는 사람들과 내 글을 최신순으로 가져온다. (좋아요 여부 true/false)")
    @Test
    void readHomeFeed_Login_FollowingsLatestPosts() {
        // given
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("kevin")
            .isGuest(false)
            .pageable(PageRequest.of(0, 10))
            .build();

        AuthUserForUserRequestDto authDto =
            UserAssembler.authUserForUserRequestDto(new LoginUser("kevin", "token"));

        User requester = userRepository.save(UserFactory.user("kevin"));
        List<User> mockUsers = userRepository.saveAll(UserFactory.mockSearchUsers());

        postRepository.save(PostFactory.mockPostBy(requester));
        List<Post> mockPostsBy = postRepository.saveAll(PostFactory.mockPostsBy(mockUsers));

        for (User mockUser : mockUsers) {
            userService.followUser(new FollowRequestDto(authDto, mockUser.getName(), false));
        }
        for (int i = 0; i < mockPostsBy.size(); i += 2) {
            postService.like(new LoginUser("kevin", "token"), mockPostsBy.get(i).getId());
        }

        // when
        List<PostResponseDto> postResponseDtos = postFeedService.allHomeFeed(homeFeedRequestDto);

        // then
        assertThat(postResponseDtos)
            .extracting("authorName", "githubRepoUrl", "liked")
            .containsExactly(
                tuple("bingbing", "url4", true),
                tuple("bbbbinghe", "url3", false),
                tuple("jinbinghe", "url2", true),
                tuple("bing", "url1", false),
                tuple("binghe", "url0", true),
                tuple("kevin", "mock-url", false)
            );
    }

    private void createMockPosts() {
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestDtos();
        List<User> users = postRequestDtos.stream()
            .map(PostRequestDto::getUsername)
            .map(UserFactory::user)
            .collect(toList());

        IntStream.range(0, users.size())
            .forEach(index -> {
                User user = users.get(index);
                PostRequestDto newPost = postRequestDtos.get(index);

                userRepository.save(user);
                Long postId = postService.write(newPost);

                CommentRequestDto commentRequestDto =
                    new CommentRequestDto(user.getName(), "test comment" + index, postId);
                commentService.addComment(commentRequestDto);
            });
    }

    @DisplayName("내 피드 게시물들만 조회한다.")
    @Test
    void readMyFeed_Success() {
        //given
        User savedUser = userRepository.save(UserFactory.user("kevin"));
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        //when
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName(savedUser.getName())
            .isGuest(false)
            .pageable(PageRequest.of(0, postRequestDtos.size()))
            .build();

        List<PostResponseDto> postResponseDtos = postFeedService.userFeed(homeFeedRequestDto, "kevin");
        List<String> repoNames = postResponseDtos.stream()
            .map(PostResponseDto::getGithubRepoUrl)
            .collect(toList());

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
    }

    @DisplayName("로그인 사용자가 다른 사용자의 피드 게시물을 조회한다.")
    @Test
    void readUserFeed_LoginUser_Success() {
        //given
        User neozal = userRepository.save(UserFactory.user("neozal"));
        User kevin = userRepository.save(UserFactory.user("kevin"));

        List<PostRequestDto> postRequestDtos =
            PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName(neozal.getName())
            .isGuest(false)
            .pageable(PageRequest.of(0, postRequestDtos.size()))
            .build();

        //when
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, kevin.getName());

        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).containsAll(extractLikes(postResponseDtos));
    }

    @DisplayName("비로그인 사용자가 다른 사용자의 피드 게시물을 조회한다.")
    @Test
    void readUserFeed_GuestUser_Success() {
        User savedUser = userRepository.save(UserFactory.user("kevin"));

        //given
        List<PostRequestDto> postRequestDtos = PostFactory.mockPostRequestForAssertingMyFeed();
        postRequestDtos.forEach(postService::write);

        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .isGuest(true)
            .pageable(PageRequest.of(0, postRequestDtos.size()))
            .build();

        //when
        List<PostResponseDto> postResponseDtos =
            postFeedService.userFeed(homeFeedRequestDto, savedUser.getName());
        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);
        List<Boolean> likes = extractLikes(postResponseDtos);

        //then
        assertThat(postResponseDtos).hasSize(postRequestDtos.size());
        assertThat(repoNames).containsAll(extractGithubRepoUrls(postRequestDtos));
        assertThat(likes).allMatch(Objects::isNull);
    }

    private List<Boolean> extractLikes(List<PostResponseDto> postResponseDtos) {
        return postResponseDtos.stream()
            .map(PostResponseDto::getLiked)
            .collect(toList());
    }

    private List<String> extractGithubRepoUrls(List<?> dtos) {
        Objects.requireNonNull(dtos);

        return dtos.stream()
            .map(dto -> {
                if (dto instanceof PostResponseDto) {
                    return ((PostResponseDto) dto).getGithubRepoUrl();
                }

                if (dto instanceof PostRequestDto) {
                    return ((PostRequestDto) dto).getGithubRepoUrl();
                }

                throw new IllegalArgumentException();
            }).collect(toList());
    }

}
