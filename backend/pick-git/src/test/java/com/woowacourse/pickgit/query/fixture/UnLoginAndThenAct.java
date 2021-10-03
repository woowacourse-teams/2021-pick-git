package com.woowacourse.pickgit.query.fixture;

import com.woowacourse.pickgit.post.presentation.dto.response.LikeUsersResponse;
import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.HttpStatus;

public class UnLoginAndThenAct extends Act {
    public List<UserSearchResponse> 팔로잉를확인한다(TUser tUser) {
        return request(
            String.format("/api/profiles/%s/followings?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET,
            HttpStatus.OK
        ).as(new TypeRef<>() {
        });
    }

    public List<UserSearchResponse> 팔로워를확인한다(TUser tUser) {
        return request(
            String.format("/api/profiles/%s/followers?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET,
            HttpStatus.OK
        ).as(new TypeRef<>() {
        });
    }

    public ExtractableResponse<Response> 포스트를검색한다(String type, String keyword, HttpStatus status) {
        return request(
            String.format(
                "/api/search/posts?type=%s&keyword=%s&page=%s&limit=%s", type, keyword, "0", "10"),
            Method.GET,
            status
        );
    }

    public List<LikeUsersResponse> 포스트에좋아요한사용자를조회한다(TPost tPost) {
        return request(
            String.format("/api/posts/%d/likes", tPost.getId()),
            Method.GET,
            HttpStatus.OK
        ).as(new TypeRef<>() {
        });
    }

    public void 포스트에좋아요한사용자를조회한다(Long id, HttpStatus status) {
        request(
            String.format("/api/posts/%d/likes", id),
            Method.GET,
            status
        );
    }
}
