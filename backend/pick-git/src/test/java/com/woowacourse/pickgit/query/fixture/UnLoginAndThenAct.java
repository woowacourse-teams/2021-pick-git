package com.woowacourse.pickgit.query.fixture;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class UnLoginAndThenAct extends Act {

    public List<UserSearchResponse> 팔로잉를확인한다(TUser tUser) {
        return request(
            String.format("/api/profiles/%s/followings?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public List<UserSearchResponse> 팔로워를확인한다(TUser tUser) {
        return request(
            String.format("/api/profiles/%s/followers?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public ExtractableResponse<Response> 포스트를검색한다(
        String type,
        String keyword,
        HttpStatus status
    ) {
        ExtractableResponse<Response> response = request(
            String.format(
                "/api/search/posts?type=%s&keyword=%s&page=%s&limit=%s", type, keyword, "0", "10"),
            Method.GET
        );

        assertThat(response.statusCode()).isEqualTo(status.value());
        return response;
    }

    public List<LikeUsersResponse> 포스트에좋아요한사용자를조회한다(TPost tPost) {
        return request(
            String.format("/api/posts/%d/likes", tPost.getId()),
            Method.GET
        ).as(new TypeRef<>() {
        });
    }

    public void 포스트에좋아요한사용자를조회한다(Long id, HttpStatus status) {
        ExtractableResponse<Response> response = request(
            String.format("/api/posts/%d/likes", id),
            Method.GET
        );

        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    public ExtractableResponse<Response> 댓글을등록한다(TPost tPost, String comment) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", comment);

        return request(
            String.format("/api/posts/%s/comments", tPost.getId()),
            Method.POST,
            params
        );
    }

    public ExtractableResponse<Response> 댓글을삭제한다(TPost tPost, Long id) {
        return request(
            String.format("/api/posts/%d/comments/%d", tPost.getId(), id),
            Method.DELETE
        );
    }

    public ExtractableResponse<Response> 댓글을조회한다(TPost tPost, int page, int limit) {
        return request(
            String.format("/api/posts/%d/comments?page=%d&limit=%d", tPost.getId(), page, limit),
            Method.GET
        );
    }
}
