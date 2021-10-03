package com.woowacourse.pickgit.query.fixture;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.authentication.application.dto.OAuthProfileResponse;
import com.woowacourse.pickgit.authentication.application.dto.TokenDto;
import com.woowacourse.pickgit.user.domain.profile.BasicProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public enum TUser {
    NEOZAL,
    MARK,
    KEVIN,
    DANI,
    KODA;

    private String token;
    private BasicProfile basicProfile;

    TUser() { }

    public String 은로그인을한다() {
        if(this.token == null) {
            this.token = requestLogin(name());
        }
        return this.token;
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

    public static void 모든유저는로그인을한다() {
        List.of(values()).forEach(TUser::은로그인을한다);
    }

    public static Act 모든유저() {
        return new Act(values());
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

    public static class Act {

        private final List<TUser> users;
        private final List<TUser> except;

        public Act(TUser... users) {
            this.users = List.of(users);
            this.except = new ArrayList<>();
        }

        public List<TUser> 가져온다() {
            return users.stream()
                .filter(user -> !except.contains(user))
                .collect(toList());
        }

        public List<String> Accesstoken을가져온다() {
            return users.stream()
                .filter(user -> !except.contains(user))
                .map(TUser::은로그인을한다)
                .collect(toList());
        }

        public Act 이유저는제외하고(TUser... tUsers) {
            except.addAll(List.of(tUsers));
            return this;
        }
    }
}
