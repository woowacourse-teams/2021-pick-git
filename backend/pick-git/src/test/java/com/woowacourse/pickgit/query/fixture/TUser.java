package com.woowacourse.pickgit.query.fixture;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public enum TUser {
    NEOZAL,
    MARK,
    KEVIN,
    DANI,
    KODA,
    GUEST;

    private String token;
    private final List<TUser> following;
    private final List<TUser> follower;

    TUser() {
        this.follower = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public String accessToken() {
        return token;
    }

    public static OAuthProfileResponse oAuthProfileResponse(String name) {
        return new OAuthProfileResponse(
            name,
            "https://github.com/testImage.jpg",
            "testDescription",
            "https://github.com/" + name,
            "testCompany",
            "testLocation",
            "testWebsite",
            "testTwitter"
        );
    }

    void addFollowing(TUser tUser) {
        this.following.add(tUser);
    }

    void addFollower(TUser tUser) {
        this.follower.add(tUser);
    }

    public String 은로그인을한다() {
        if (this.token == null) {
            this.token = requestLogin(name());
        }
        return this.token;
    }

    public LoginAndThenAct 은로그인을하고() {
        return new LoginAndThenAct(this);
    }

    public UnLoginAndThenAct 는() {
        return new UnLoginAndThenAct();
    }

    public static AllUserAct 모든유저() {
        return new AllUserAct(List.of(values()).stream()
            .filter(user -> GUEST != user)
            .collect(toList())
        );
    }

    List<TUser> getFollowing() {
        return following;
    }

    private static String requestLogin(String code) {
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code={code}", code)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(TokenDto.class)
            .getToken();
    }
}
