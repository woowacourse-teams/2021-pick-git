package com.woowacourse.pickgit.query.fixture;

import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
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
}
