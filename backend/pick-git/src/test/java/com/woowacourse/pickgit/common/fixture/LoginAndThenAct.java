package com.woowacourse.pickgit.common.fixture;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.portfolio.presentation.dto.request.PortfolioRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.common.fixture.TPost.Pair;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileDescriptionRequest;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class LoginAndThenAct extends Act {

    private final String token;
    private final TUser tUser;
    private final boolean isRead;

    public LoginAndThenAct(TUser tUser, boolean isRead) {
        this.token = tUser.은로그인을한다().getToken();
        this.tUser = tUser;
        this.isRead = isRead;
    }

    public List<ExtractableResponse<Response>> 팔로우를한다(TUser... tUsers) {
        return List.of(tUsers).stream()
            .map(this::팔로우를한다)
            .collect(toList());
    }

    public ExtractableResponse<Response> 팔로우를한다(TUser tUser) {
        if (this.tUser.getFollowing().contains(tUser) && isRead) {
            return null;
        }

        ExtractableResponse<Response> response = request(
            token,
            String.format("/api/profiles/%s/followings?githubFollowing=false", tUser),
            Method.POST
        );

        if (isRead) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            this.tUser.addFollowing(tUser);
            tUser.addFollower(this.tUser);
        }

        return response;
    }

    public List<UserSearchResponse> 팔로우를확인한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/followings?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public List<UserSearchResponse> 팔로워를확인한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/followers?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public ExtractableResponse<Response> 포스트를등록한다(CPost cPost) {
        return 포스트를등록한다(TPost.of(cPost));
    }

    public ExtractableResponse<Response> 포스트를등록한다(TPost tPost) {
        if (isRead) {
            if (this.tUser.getPosts().containsKey(tPost)) {
                return this.tUser.getPosts().get(tPost);
            }
        }

        ExtractableResponse<Response> response = request(
            token,
            "/api/posts",
            Method.POST,
            tPost
        );

        if (response.statusCode() == HttpStatus.CREATED.value()) {
            if (isRead) {
                this.tUser.addPost(tPost, response);
            }
            String location = response.headers().getValue(HttpHeaders.LOCATION);
            String[] split = location.split("/");
            tPost.setId(Long.parseLong(split[split.length - 1]), isRead);
        }

        return response;
    }

    public ExtractableResponse<Response> 포스트를삭제한다(TPost tPost) {
        return request(
            token,
            String.format("/api/posts/%d", tPost.getId(isRead)),
            Method.DELETE
        );
    }

    public ExtractableResponse<Response> 포스트를수정한다(TPost source, CPost target) {
        return 포스트를수정한다(source, TPost.of(target));
    }

    public ExtractableResponse<Response> 포스트를수정한다(TPost source, TPost target) {
        Map<String, Object> values = new HashMap<>();
        values.put("tags", target.getTags());
        values.put("content", target.getContent());

        return request(
            token,
            String.format("api/posts/%d", source.getId(isRead)),
            Method.PUT,
            values
        );
    }

    public ExtractableResponse<Response> 포스트를검색한다(
        String type,
        String keyword,
        HttpStatus status
    ) {
        ExtractableResponse<Response> response = request(
            token,
            String.format(
                "/api/search/posts?type=%s&keyword=%s&page=%s&limit=%s", type, keyword, "0", "10"),
            Method.GET
        );

        assertThat(response.statusCode()).isEqualTo(status.value());
        return response;
    }

    public ExtractableResponse<Response> 포스트를검색한다(TPost tPost, HttpStatus status) {
        ExtractableResponse<Response> response = request(
            String.format("/api/posts?id=%d", tPost.getId(isRead)),
            Method.GET,
            status
        );

        assertThat(response.statusCode()).isEqualTo(status.value());
        return response;
    }

    public ExtractableResponse<Response> 포스트에좋아요를누른다(TPost tPost) {
        if (isRead) {
            if (tPost.getLikes().contains(tUser)) {
                return null;
            }

            tPost.addLike(tUser);
        }

        return request(
            token,
            String.format("/api/posts/%d/likes", tPost.getId(isRead)),
            Method.PUT
        );
    }

    public ExtractableResponse<Response> 포스트에좋아요_취소를_한다(TPost tPost) {
        if (isRead) {
            if (!tPost.getLikes().contains(tUser)) {
                return null;
            }

            tPost.removeLike(tUser);
        }

        return request(
            token,
            String.format("/api/posts/%d/likes", tPost.getId(isRead)),
            Method.DELETE
        );
    }

    public List<LikeUsersResponse> 포스트에좋아요한사용자를조회한다(TPost tPost) {
        return request(
            token,
            String.format("/api/posts/%d/likes", tPost.getId(isRead)),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public ExtractableResponse<Response> 댓글을등록한다(TPost tPost, String comment) {
        String key = tPost.name() + comment;

        if (tPost.getComment().contains(new Pair(tUser, comment)) && isRead) {
            return (ExtractableResponse<Response>) tUser.cache.get(key);
        }

        Map<String, Object> params = new HashMap<>();

        params.put("content", comment);

        tPost.addComment(tUser, comment);

        ExtractableResponse<Response> response = request(
            token,
            String.format("/api/posts/%s/comments", tPost.getId(isRead)),
            Method.POST,
            params
        );

        if(isRead) {
            tUser.cache.put(key, response);
        }
        return response;
    }

    public ExtractableResponse<Response> 댓글을삭제한다(TPost tPost, Long id) {
        return request(
            token,
            String.format("/api/posts/%d/comments/%d", tPost.getId(isRead), id),
            Method.DELETE
        );
    }

    public ExtractableResponse<Response> 댓글을조회한다(TPost tPost, int page, int limit) {
        return request(
            token,
            String.format("/api/posts/%d/comments?page=%d&limit=%d", tPost.getId(isRead), page, limit),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 레포지토리_목록을_가져온다() {
        return request(
            token,
            String.format("/api/github/repositories?page=%d&limit=%d", 0, 50L),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 레포지토리_목록을_키워드로_가져온다(String keyword) {
        return request(
            token,
            String.format(
                "/api/github/search/repositories?keyword=%s&page=%d&limit=%d", keyword, 0, 50L
            ),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 전체_홈피드를_조회한다() {
        return request(
            token,
            "/api/posts?page=0&limit=3",
            Method.GET
        );
    }

    public ExtractableResponse<Response> 팔로잉_홈피드를_조회한다() {
        return request(
            token,
            "/api/posts?page=0&limit=3&type=followings",
            Method.GET
        );
    }

    public ExtractableResponse<Response> 내_피드를_조회한다() {
        return request(
            token,
            "/api/posts/me?page=0&limit=3",
            Method.GET
        );
    }

    public ExtractableResponse<Response> 피드를_조회한다(TUser tUser) {
        return request(
            token,
            String.format("/api/posts/%s?page=0&limit=3", tUser.name()),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 언팔로우를한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/followings?githubUnfollowing=false", tUser),
            Method.DELETE
        );
    }

    public ExtractableResponse<Response> 프로필을_이미지를_수정한다() throws IOException {
        return request(
            token,
            "/api/profiles/me/image",
            Method.PUT,
            new FileInputStream(FileFactory.getTestImage1File()).readAllBytes()
        );
    }

    public ExtractableResponse<Response> 프로필_한줄소개를_수정한다(String description) {
        return request(
            token,
            "/api/profiles/me/description",
            Method.PUT,
            new ProfileDescriptionRequest(description)
        );
    }

    public ExtractableResponse<Response> 유저를_검색한다(String keyword) {
        return request(
            token,
            String.format("/api/search/users?keyword=%s&page=0&limit=5", keyword),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 활동_통계를_조회한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/contributions", tUser),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 자신의_프로필을_조회한다() {
        return request(
            token,
            "/api/profiles/me",
            Method.GET
        );
    }

    public ExtractableResponse<Response> 프로필을_조회한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s", tUser),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 레포지토리의_태그를_추출한다(TRepository tRepository) {
        return request(
            token,
            String.format("/api/github/repositories/%s/tags/languages", tRepository),
            Method.GET
        );
    }

    public ExtractableResponse<Response> 포트폴리오를_조회한다(TUser tUser) {
        String key = "portfolio" + tUser;
        if (isRead && tUser.cache.containsKey(key)) {
            return (ExtractableResponse<Response>) tUser.cache.get(key);
        }

        ExtractableResponse<Response> response = request(
            token,
            String.format("/api/portfolios/%s", tUser),
            Method.GET
        );

        if(isRead) {
            tUser.cache.put(key, response);
        }

        return response;
    }

    public ExtractableResponse<Response> 포트폴리오를_수정한다(PortfolioRequest portfolioRequest) {
        return request(
            token,
            "/api/portfolios",
            Method.PUT,
            portfolioRequest
        );
    }
}
