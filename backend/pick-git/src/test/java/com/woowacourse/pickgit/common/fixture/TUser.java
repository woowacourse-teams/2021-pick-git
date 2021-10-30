package com.woowacourse.pickgit.common.fixture;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import com.woowacourse.pickgit.user.domain.profile.GithubProfile;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public enum TUser {
    NEOZAL,
    MARK,
    KEVIN,
    DANI,
    KODA,
    GUEST;

    private TokenDto token;
    private final List<TUser> following;
    private final List<TUser> follower;
    private final Map<TPost, ExtractableResponse<Response>> posts;
    private final OAuthProfileResponse oAuthProfileResponse;
    protected final Map<String, Object> cache;

    private static boolean isRead = false;

    TUser() {
        this.follower = new ArrayList<>();
        this.following = new ArrayList<>();
        this.posts = new HashMap<>();
        this.cache = new HashMap<>();
        this.oAuthProfileResponse = new OAuthProfileResponse(
            name(),
            "https://github.com/testImage.jpg",
            "testDescription",
            "https://github.com/" + name(),
            "testCompany",
            "testLocation",
            "testWebsite",
            "testTwitter"
        );
    }

    public static void toWrite() {
        isRead = false;
    }

    public static void toRead() {
        isRead = true;
    }

    public User toEntity() {
        return new User(
            new BasicProfile(
                name(),
                oAuthProfileResponse.getImage(),
                oAuthProfileResponse.getDescription()
            ),
            new GithubProfile(
                oAuthProfileResponse.getGithubUrl(),
                oAuthProfileResponse.getCompany(),
                oAuthProfileResponse.getLocation(),
                oAuthProfileResponse.getWebsite(),
                oAuthProfileResponse.getTwitter()
            )
        );
    }

    public String accessToken() {
        return token.getToken();
    }

    public static OAuthProfileResponse oAuthProfileResponse(String name) {
        return Arrays.stream(values())
            .filter(user -> user.name().equals(name))
            .findAny()
            .map(user -> user.oAuthProfileResponse)
            .orElseGet(() -> new OAuthProfileResponse(
                name,
                "https://github.com/testImage.jpg",
                "testDescription",
                "https://github.com/" + name,
                "testCompany",
                "testLocation",
                "testWebsite",
                "testTwitter"
            ));
    }

    void addFollowing(TUser tUser) {
        this.following.add(tUser);
    }

    void addFollower(TUser tUser) {
        this.follower.add(tUser);
    }

    void addPost(TPost tPost, ExtractableResponse<Response> response) {
        this.posts.put(tPost, response);
    }

    public TokenDto 은로그인을한다() {
        if(isRead) {
            if (this.token != null) {
                return this.token;
            }
            this.token = requestLogin(name());
            return this.token;
        }
        return requestLogin(name());
    }

    public LoginAndThenAct 은로그인을하고() {
        return new LoginAndThenAct(
            this,
            isRead
        );
    }

    public UnLoginAndThenAct 는() {
        return new UnLoginAndThenAct(isRead);
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

    public Map<TPost, ExtractableResponse<Response>> getPosts() {
        return posts;
    }

    private static TokenDto requestLogin(String code) {
        return given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/api/afterlogin?code={code}", code)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .as(TokenDto.class);
    }
}
