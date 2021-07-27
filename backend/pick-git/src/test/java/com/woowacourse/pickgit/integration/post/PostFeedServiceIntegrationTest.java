package com.woowacourse.pickgit.integration.post;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.post.application.PostFeedService;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@Import(InfrastructureTestConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
public class PostFeedServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostFeedService postFeedService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("저장된 게시물 중 3, 4번째 글을 최신날짜순으로 가져온다.")
    @Test
    void readHomeFeed_Success() {
        //given
        createMockPosts();

        userRepository.save(UserFactory.user("kevin"));
        HomeFeedRequestDto homeFeedRequestDto = HomeFeedRequestDto.builder()
            .requestUserName("kevin")
            .isGuest(false)
            .page(1L)
            .limit(2L)
            .build();

        // when
        List<PostResponseDto> postResponseDtos = postFeedService.homeFeed(homeFeedRequestDto);

        //then
        List<String> postNames = postResponseDtos.stream()
            .map(PostResponseDto::getAuthorName)
            .collect(toList());

        List<String> repoNames = extractGithubRepoUrls(postResponseDtos);

        assertThat(postResponseDtos).hasSize(2);
        assertThat(postNames).containsExactly("dani", "ginger");
        assertThat(repoNames).containsExactly("java-racingcar", "jwp-chess");
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
                Long postId = postService.write(newPost).getId();

                CommentRequestDto commentRequestDto =
                    new CommentRequestDto(user.getName(), "test comment" + index, postId);
                postService.addComment(commentRequestDto);
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
            .page(0L)
            .limit((long) postRequestDtos.size())
            .build();

        List<PostResponseDto> postResponseDtos = postFeedService.myFeed(homeFeedRequestDto);
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
            .page(0L)
            .limit((long) postRequestDtos.size())
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
            .page(0L)
            .limit((long) postRequestDtos.size())
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
            .map(PostResponseDto::getIsLiked)
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
