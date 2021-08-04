package com.woowacourse.pickgit.acceptance.post;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.request_builder.PickGitRequest;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.dto.ApiErrorResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostResponse;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(InfrastructureTestConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class PostAcceptanceTest_searchPost {

    private List<PostResponse> allPostsWithUser;
    private List<PostResponse> allPostsWithGuest;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        List<String[]> tags = List.of(
            new String[]{"tag1", "tag2"},
            new String[]{"tag1"},
            new String[]{"tag1", "tag3"},
            new String[]{"tag2", "tag5"},
            new String[]{"tag4"}
        );
        tags.forEach(this::createPost);

        allPostsWithUser = getAllPostsWithUser(tags.size());
        allPostsWithGuest = getAllPostsWithGuest(tags.size());
    }

    private void createPost(String... tags) {
        int statusCode = PickGitRequest.post("/api/posts")
            .api_posts()
            .withUser()
            .initAllParams()
            .tags(tags)
            .extract().statusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.CREATED.value());
    }

    private List<PostResponse> getAllPostsWithUser(int size) {
        return PickGitRequest.get("/api/posts?page={page}&limit={limit}", 0, size)
            .withUser()
            .extract()
            .as(new TypeRef<>() {
            });
    }

    private List<PostResponse> getAllPostsWithGuest(int size) {
        return PickGitRequest.get("/api/posts?page={page}&limit={limit}", 0, size)
            .withGuest()
            .extract()
            .as(new TypeRef<>() {
            });
    }

    @DisplayName("유저는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void name(String keyword, int page, int limit) {
        ExtractableResponse<Response> extract = PickGitRequest
            .get("/api/search/posts?type=tags&keyword={keyword}&page={page}&limit={limit}",
                keyword, page, limit
            ).withUser()
            .extract();

        assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<PostResponse> actual = extract.as(new TypeRef<>() {
        });

        actual.sort(comparing(PostResponse::getId));
        List<PostResponse> expected = findPostResponseByTags(allPostsWithUser, keyword);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @DisplayName("게스트는 Tag로 게시물을 검색할 수 있다.")
    @ParameterizedTest
    @MethodSource("getPostSearchArguments")
    void name2(String keyword, int page, int limit) {
        ExtractableResponse<Response> extract = PickGitRequest
            .get("/api/search/posts?type=tags&keyword={keyword}&page={page}&limit={limit}",
                keyword, page, limit)
            .withGuest()
            .extract();

        assertThat(extract.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<PostResponse> actual = extract.as(new TypeRef<>() {
        });

        actual.sort(comparing(PostResponse::getId));
        List<PostResponse> expected = findPostResponseByTags(allPostsWithGuest, keyword);

        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private static Stream<Arguments> getPostSearchArguments() {
        return Stream.of(
            Arguments.of("tag1", 0, 5),
            Arguments.of("tag1 tag3", 0, 5),
            Arguments.of("", 0, 5),
            Arguments.of("tag19", 0, 5),
            Arguments.of("tag4", 0, 5),
            Arguments.of("tag4 tag1 tag3", 0, 5),
            Arguments.of("tag1 tag2 tag3", 0, 5),
            Arguments.of("tag1 tag19", 0, 5)
        );
    }

    private List<PostResponse> findPostResponseByTags(List<PostResponse> posts, String keyword) {
        List<String> keywords = List.of(keyword.split(" "));
        return posts.stream()
            .filter(post -> isMatched(keywords, post.getTags()))
            .sorted(comparing(PostResponse::getId))
            .collect(toList());
    }

    private boolean isMatched(List<String> keywords, List<String> tags) {
        return keywords.stream()
            .anyMatch(tags::contains);
    }

    @DisplayName("존재하지 않는 type을 요청하면 예외가 발생한다.")
    @Test
    void name() {
        ApiErrorResponse errorResponse = PickGitRequest
            .get("/api/search/posts?type=invalidType&keyword={keyword}&page={page}&limit={limit}",
                "keyword", 0, 1)
            .withGuest()
            .extract()
            .as(ApiErrorResponse.class);

        assertThat(errorResponse.getErrorCode()).isEqualTo("P0006");
    }
}
