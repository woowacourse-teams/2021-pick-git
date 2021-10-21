package com.woowacourse.pickgit.query.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.query.fixture.TPost.Pair;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public void 팔로우를한다(TUser... tUsers) {
        List.of(tUsers).forEach(this::팔로우를한다);
    }

    public void 팔로우를한다(TUser tUser) {
        if (this.tUser.getFollowing().contains(tUser)) {
            return;
        }

        int statusCode = request(
            token,
            String.format("/api/profiles/%s/followings?githubFollowing=false", tUser),
            Method.POST
        ).statusCode();

        assertThat(statusCode).isEqualTo(HttpStatus.OK.value());

        this.tUser.addFollowing(tUser);
        tUser.addFollower(this.tUser);
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
            this.tUser.addPost(tPost, response);
            String location = response.headers().getValue(HttpHeaders.LOCATION);
            String[] split = location.split("/");
            tPost.setId(Long.parseLong(split[split.length - 1]));
        }

        return response;
    }

    public ExtractableResponse<Response> 포스트를삭제한다(TPost tPost) {
        return request(
            token,
            String.format("/api/posts/%d", tPost.getId()),
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
            String.format("api/posts/%d", source.getId()),
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

    public ExtractableResponse<Response> 포스트에좋아요를누른다(TPost tpost) {
        if (isRead) {
            if (tpost.getLikes().contains(tUser)) {
                return null;
            }

            tpost.addLike(tUser);
        }

        return request(
            token,
            String.format("/api/posts/%d/likes", tpost.getId()),
            Method.PUT
        );
    }

    public ExtractableResponse<Response> 포스트에좋아요_취소를_한다(TPost tpost) {
        if (isRead) {
            if (!tpost.getLikes().contains(tUser)) {
                return null;
            }

            tpost.removeLike(tUser);
        }

        return request(
            token,
            String.format("/api/posts/%d/likes", tpost.getId()),
            Method.DELETE
        );
    }

    public List<LikeUsersResponse> 포스트에좋아요한사용자를조회한다(TPost tPost) {
        return request(
            token,
            String.format("/api/posts/%d/likes", tPost.getId()),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public ExtractableResponse<Response> 댓글을등록한다(TPost tPost, String comment) {
        String key = tPost.name() + comment;

        if (tPost.getComment().contains(new Pair(tUser, comment))) {
            return (ExtractableResponse<Response>) tUser.cache.get(key);
        }

        Map<String, Object> params = new HashMap<>();

        params.put("content", comment);

        tPost.addComment(tUser, comment);

        ExtractableResponse<Response> response = request(
            token,
            String.format("/api/posts/%s/comments", tPost.getId()),
            Method.POST,
            params
        );

        tUser.cache.put(key, response);
        return response;
    }

    public ExtractableResponse<Response> 댓글을삭제한다(TPost tPost, Long id) {
        return request(
            token,
            String.format("/api/posts/%d/comments/%d", tPost.getId(), id),
            Method.DELETE
        );
    }

    public ExtractableResponse<Response> 댓글을조회한다(TPost tPost, int page, int limit) {
        return request(
            token,
            String.format("/api/posts/%d/comments?page=%d&limit=%d", tPost.getId(), page, limit),
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

    public ExtractableResponse<Response> 홈피드를_조회한다() {
        return request(
            token,
            "/api/posts?page=0&limit=3",
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
}
