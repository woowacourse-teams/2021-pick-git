package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class PostFactory {

    private PostFactory() {
    }

    public static List<PostRequestDto> mockPostRequestDtos() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        PostRequestDto fixture1 = MockPostRequestDto.builder()
            .token("a")
            .userName("sean")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags("Java", "Python", "C++")
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = MockPostRequestDto.builder()
            .token("a")
            .userName("ginger")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags("Javascirpt", "C", "HTML")
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = MockPostRequestDto.builder()
            .token("a")
            .userName("dani")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags("Go", "Objective-C")
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = MockPostRequestDto.builder()
            .token("a")
            .userName("coda")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags("Java", "CSS", "HTML")
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = MockPostRequestDto.builder()
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

        PostRequestDto fixture1 = MockPostRequestDto.builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags("Java", "Python", "C++")
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = MockPostRequestDto.builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags("Javascirpt", "C", "HTML")
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = MockPostRequestDto.builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags("Go", "Objective-C")
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = MockPostRequestDto.builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags("Java", "CSS", "HTML")
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = MockPostRequestDto.builder()
            .token("a")
            .userName("kevin")
            .images(images)
            .githubRepoUrl("jpa-learning-test")
            .tags("Java", "CSS", "HTML")
            .content("jpa is so fun!")
            .build();

        return List.of(fixture1, fixture2, fixture3, fixture4, fixture5);
    }

    public static List<PostResponseDto> mockPostResponseDtos() {
        CommentResponse commentFixture1 = MockCommentResponse.builder()
            .id(1L)
            .authorName("commentAuthorName1")
            .content("commentContent1")
            .isLiked(false)
            .build();

        CommentResponse commentFixture2 = MockCommentResponse.builder()
            .id(2L)
            .authorName("commentAuthorName2")
            .content("commentContent2")
            .isLiked(false)
            .build();

        PostResponseDto fixture1 = MockPostResponseDto.builder()
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
