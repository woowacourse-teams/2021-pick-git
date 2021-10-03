package com.woowacourse.pickgit.query.fixture;

import com.woowacourse.pickgit.user.presentation.dto.response.UserSearchResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.Method;
import java.util.List;
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
        if(this.tUser.getFollowing().contains(tUser)) return;

        request(
            token,
            String.format("/api/profiles/%s/followings?githubFollowing=false", tUser),
            Method.POST,
            HttpStatus.OK
        );

        this.tUser.addFollowing(tUser);
        tUser.addFollower(this.tUser);
    }

    public List<UserSearchResponse> 팔로우를확인한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/followings?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET,
            HttpStatus.OK
        ).as(new TypeRef<>() {
        });
    }

    public List<UserSearchResponse> 팔로워를확인한다(TUser tUser) {
        return request(
            token,
            String.format("/api/profiles/%s/followers?page=%s&limit=%s", tUser.name(), "0", "10"),
            Method.GET,
            HttpStatus.OK
        ).as(new TypeRef<>() {
        });
    }
}
