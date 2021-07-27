package com.woowacourse.pickgit.common.factory;

import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class PostFactory {

    private PostFactory() {
    }

    public static class Builder {

    }

    public static List<PostRequestDto> mockPostRequestDtos() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        PostRequestDto fixture1 = PostRequestDto.builder()
            .token("a")
            .username("sean")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags(List.of("Java", "Python", "C++"))
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = PostRequestDto.builder()
            .token("a")
            .username("ginger")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags(List.of("Javascirpt", "C", "HTML"))
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = PostRequestDto.builder()
            .token("a")
            .username("dani")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags(List.of("Go", "Objective-C"))
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = PostRequestDto.builder()
            .token("a")
            .username("coda")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags(List.of("Java", "CSS", "HTML"))
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = PostRequestDto.builder()
            .token("a")
            .username("dave")
            .images(images)
            .githubRepoUrl("jpa-learning-test")
            .tags(List.of("Java", "CSS", "HTML"))
            .content("jpa is so fun!")
            .build();

        return List.of(fixture1, fixture2, fixture3, fixture4, fixture5);
    }

    public static List<PostRequestDto> mockPostRequestForAssertingMyFeed() {
        List<MultipartFile> images =
            List.of(FileFactory.getTestImage1(), FileFactory.getTestImage2());

        PostRequestDto fixture1 = PostRequestDto.builder()
            .token("a")
            .username("kevin")
            .images(images)
            .githubRepoUrl("atdd-subway-fare")
            .tags(List.of("Java", "Python", "C++"))
            .content("woowacourse mission")
            .build();

        PostRequestDto fixture2 = PostRequestDto.builder()
            .token("a")
            .username("kevin")
            .images(images)
            .githubRepoUrl("jwp-chess")
            .tags(List.of("Javascirpt", "C", "HTML"))
            .content("it's so easy!")
            .build();

        PostRequestDto fixture3 = PostRequestDto.builder()
            .token("a")
            .username("kevin")
            .images(images)
            .githubRepoUrl("java-racingcar")
            .tags(List.of("Go", "Objective-C"))
            .content("I love TDD")
            .build();

        PostRequestDto fixture4 = PostRequestDto.builder()
            .token("a")
            .username("kevin")
            .images(images)
            .githubRepoUrl("junit-test")
            .tags(List.of("Java", "CSS", "HTML"))
            .content("hi there!")
            .build();

        PostRequestDto fixture5 = PostRequestDto.builder()
            .token("a")
            .username("kevin")
            .images(images)
            .githubRepoUrl("jpa-learning-test")
            .tags(List.of("Java", "CSS", "HTML"))
            .content("jpa is so fun!")
            .build();

        return List.of(fixture1, fixture2, fixture3, fixture4, fixture5);
    }

    public static List<PostResponseDto> mockPostResponseDtos() {
        CommentResponse commentFixture1 = CommentResponse.builder()
            .id(1L)
            .profileImageUrl("commentAuthorProfileImageUrl")
            .authorName("commentAuthorName1")
            .content("commentContent1")
            .isLiked(false)
            .build();

        CommentResponse commentFixture2 = CommentResponse.builder()
            .id(2L)
            .profileImageUrl("commentAuthorProfileImageUrl")
            .authorName("commentAuthorName2")
            .content("commentContent2")
            .isLiked(false)
            .build();

        PostResponseDto fixture1 = PostResponseDto.builder()
            .id(1L)
            .imageUrls(List.of("image1Url", "image2Url"))
            .githubRepoUrl("githubRepoUrl")
            .content("content")
            .authorName("authorName")
            .profileImageUrl("profileImageUrl")
            .likesCount(1)
            .tags(List.of("tag1", "tag2"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .comments(List.of(commentFixture1, commentFixture2))
            .isLiked(false)
            .build();

        return List.of(fixture1);
    }
}
