package com.woowacourse.pickgit.query.fixture;

import static com.woowacourse.pickgit.query.fixture.TPost.NEOZALPOST;
import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.comment.presentation.dto.response.CommentResponse;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class LoginAndThenAct extends Act {

    private final String token;
    private final TUser tUser;

    public LoginAndThenAct(TUser tUser) {
        this.token = tUser.은로그인을한다();
        this.tUser = tUser;
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

    public ExtractableResponse<Response> 포스트를등록한다(TPost tPost) {
        if (this.tUser.getPosts().containsKey(tPost)) {
            return this.tUser.getPosts().get(tPost);
        }

        ExtractableResponse<Response> response = request(
            token,
            "/api/posts",
            Method.POST,
            tPost
        );

        this.tUser.addPost(tPost, response);

        String location = response.headers().getValue(HttpHeaders.LOCATION);
        String[] split = location.split("/");
        tPost.setId(Long.parseLong(split[split.length-1]));

        return response;
    }

    public ExtractableResponse<Response> 포스트를검색한다(String type, String keyword, HttpStatus status) {
        ExtractableResponse<Response> response = request(
            token,
            String.format(
                "/api/search/posts?type=%s&keyword=%s&page=%s&limit=%s", type, keyword, "0", "10"),
            Method.GET
        );

        assertThat(response.statusCode()).isEqualTo(status.value());
        return response;
    }

    public void 포스트에좋아요를누른다(TPost tpost) {
        if(tpost.getLikes().contains(tUser)) {
            return;
        }

        tpost.addLike(tUser);

        request(
            token,
            String.format("/api/posts/%d/likes", tpost.getId()),
            Method.PUT
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

        if(tPost.getComment().contains(new Pair(tUser, comment))) {
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
}
