package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.PostRequest;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

public class PostFactory {

    private PostFactory() {
    }

    public static List<PostRequestDto> mockPostRequestDtos() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        PostRequestDto fixture1 = MockPostRequestDto.Builder()
            .token("a")
            .userName("sean")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags("Java", "Python", "C++")
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = MockPostRequestDto.Builder()
            .token("a")
            .userName("ginger")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags("Javascirpt", "C", "HTML")
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = MockPostRequestDto.Builder()
            .token("a")
            .userName("dani")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags("Go", "Objective-C")
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = MockPostRequestDto.Builder()
            .token("a")
            .userName("coda")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags("Java", "CSS", "HTML")
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = MockPostRequestDto.Builder()
            .token("a")
            .userName("dave")
            .images(images)
            .githubRepoUrl("jpa-learning-test")
            .tags("Java", "CSS", "HTML")
            .content("jpa is so fun!")
            .build();

        return List.of(fixture1, fixture2, fixture3, fixture4, fixture5);
    }

    public static List<PostRequestDto> mockPostRequestForAssertingMyFeed() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        PostRequestDto fixture1 = MockPostRequestDto.Builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags("Java", "Python", "C++")
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = MockPostRequestDto.Builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags("Javascirpt", "C", "HTML")
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = MockPostRequestDto.Builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags("Go", "Objective-C")
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = MockPostRequestDto.Builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags("Java", "CSS", "HTML")
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = MockPostRequestDto.Builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("jpa-learning-test")
            .tags("Java", "CSS", "HTML")
            .content("jpa is so fun!")
            .build();

        return List.of(fixture1, fixture2, fixture3, fixture4, fixture5);
    }

    public static List<User> mockUsers() {
        return List.of(
            new User(
                new BasicProfile("sean", "a.jpg", "a"),
                new GithubProfile("github1.com", "a", "a", "a", "a")
            ),
            new User(
                new BasicProfile("ginger", "a.jpg", "a"),
                new GithubProfile("github2.com", "a", "a", "a", "a")
            ),
            new User(
                new BasicProfile("dani", "a.jpg", "a"),
                new GithubProfile("dani.com", "a", "a", "a", "a")
            ),
            new User(
                new BasicProfile("coda", "a.jpg", "a"),
                new GithubProfile("coda.com", "a", "a", "a", "a")
            ),
            new User(
                new BasicProfile("dave", "a.jpg", "a"),
                new GithubProfile("dave.com", "a", "a", "a", "a")
            )
        );
    }

    public static List<User> mockUsers2() {
        return List.of(
            new User(
                new BasicProfile("ala", "a.jpg", "a"),
                new GithubProfile("github1.com", "a", "a", "a", "a")
            ),
            new User(
                new BasicProfile("dave", "a.jpg", "a"),
                new GithubProfile("github2.com", "a", "a", "a", "a")
            )
        );
    }

    private static long autoIncrement = 1;

    public static List<PostResponseDto> mockPostResponseDtos() {
        CommentResponse commentFixture1 = MockCommentResponse.Builder()
            .id(1L)
            .authorName("commentAuthorName1")
            .content("commentContent1")
            .isLiked(false)
            .build();

        CommentResponse commentFixture2 = MockCommentResponse.Builder()
            .id(2L)
            .authorName("commentAuthorName2")
            .content("commentContent2")
            .isLiked(false)
            .build();

        PostResponseDto fixture1 = MockPostResponseDto.Builder()
            .id(1L)
            .imageUrls("image1Url", "image2Url")
            .githubRepoUrl("githubRepoUrl")
            .content("content")
            .authorName("authorName")
            .profileImageUrl("profileImageUrl")
            .likesCount(1)
            .tags("tag1", "tag2")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .comments(commentFixture1, commentFixture2)
            .isLiked(false)
            .build();

        return List.of(fixture1);
    }
}
