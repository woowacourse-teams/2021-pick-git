package com.woowacourse.pickgit.user.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.dao.OAuthAccessTokenDao;
import com.woowacourse.pickgit.user.UserFactory;
import com.woowacourse.pickgit.user.domain.User;
import com.woowacourse.pickgit.user.domain.UserRepository;
import com.woowacourse.pickgit.user.presentation.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.UserProfileResponseDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class UserIntegrationTest {

    private static final String NAME = "yjksw";

    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthAccessTokenDao oAuthAccessTokenDao;

    private UserFactory userFactory = new UserFactory();

    private String userAccessToken;

    private String anotherAccessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.save(userFactory.user());
        userRepository.save(userFactory.anotherUser());

        userAccessToken = jwtTokenProvider.createToken(userFactory.user().getName());
        anotherAccessToken = jwtTokenProvider.createToken(userFactory.anotherUser().getName());

        oAuthAccessTokenDao.insert(userAccessToken, "githubToken");
        oAuthAccessTokenDao.insert(anotherAccessToken, "anotherGithubToken");
    }


    @DisplayName("본인의 프로필 조회에 성공한다.")
    @Test
    void getAuthenticatedUserProfile_ValidUser_Success() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/me";
        UserProfileResponseDto expectedResponseDto =
            new UserProfileResponseDto(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter());

        //when
        UserProfileResponseDto actualResponseDto =
            authenticatedGetRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(UserProfileResponseDto.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("본인의 프로필 조회시 없는 유저면 예외를 발생시킨다.")
    @Test
    void getAuthenticatedUserProfile_InvalidUser_ExceptionThrown() {
        //given
        String accessToken = jwtTokenProvider.createToken("invalidUser");
        oAuthAccessTokenDao.insert(accessToken, "githubToken");
        String requestUrl = "/api/profiles/me";

        //when
        //then
//        Response as = authenticatedGetRequest(accessToken, requestUrl, HttpStatus.BAD_REQUEST)
//            .as(Response.class); //TODO 추후 수정

    }

    @DisplayName("로그인 상태에서 타인의 프로필 조회에 성공한다.")
    @Test
    void getUserProfile_ValidLoginUser_Success() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/" + user.getName();
        UserProfileResponseDto expectedResponseDto =
            new UserProfileResponseDto(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter());

        //when
        UserProfileResponseDto actualResponseDto =
            authenticatedGetRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(UserProfileResponseDto.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("비로그인 상태에서 타인의 프로필 조회에 성공한다.")
    @Test
    void getUserProfile_ValidGuestUser_Success() {
        //given
        User user = userFactory.anotherUser();
        String requestUrl = "/api/profiles/" + user.getName();
        UserProfileResponseDto expectedResponseDto =
            new UserProfileResponseDto(user.getName(), user.getImage(), user.getDescription(),
                user.getFollowerCount(), user.getFollowingCount(), user.getPostCount(),
                user.getGithubUrl(), user.getCompany(), user.getLocation(), user.getWebsite(),
                user.getTwitter());

        //when
        UserProfileResponseDto actualResponseDto =
            unauthenticatedGetRequest(requestUrl, HttpStatus.OK)
                .as(UserProfileResponseDto.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    //TODO 없는 유저 조회시 예외발생

    @DisplayName("한 로그인 유저가 다른 유저를 팔로우하는데 성공한다.")
    @Test
    void followUser_ValidUser_Success() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponseDto expectedResponseDto = new FollowResponseDto(1, true);

        //when
        FollowResponseDto actualResponseDto =
            authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(FollowResponseDto.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponseDto);
    }

    @DisplayName("이미 존재하는 팔로우 요청 시 예외가 발생한다.")
    @Test
    void followUser_ExistingFollow_ExceptionThrown() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponseDto followResponse = authenticatedPostRequest(
            userAccessToken, requestUrl, HttpStatus.OK)
            .as(FollowResponseDto.class);

        FollowResponseDto followExpectedResponseDto = new FollowResponseDto(1, true);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponseDto);

        //when
        //then
//        assertThatThrownBy(
//            () -> authenticatedPostRequest(userAccessToken, requestUrl, HttpStatus.BAD_REQUEST))
//            .hasMessage(new DuplicatedFollowException().getMessage()); //TODO 에러 테스트코드
    }

    @DisplayName("한 로그인 유저가 다른 유저를 언팔로우하는데 성공한다.")
    @Test
    void unfollowUser_ValidUser_Success() {
        //given
        User user = userFactory.user();
        String requestUrl = "/api/profiles/" + user.getName() + "/followings";
        FollowResponseDto followResponse = authenticatedPostRequest(
            userAccessToken, requestUrl, HttpStatus.OK)
            .as(FollowResponseDto.class);

        FollowResponseDto followExpectedResponseDto = new FollowResponseDto(1, true);
        FollowResponseDto unfollowExpectedResponseDto = new FollowResponseDto(0, false);

        assertThat(followResponse)
            .usingRecursiveComparison()
            .isEqualTo(followExpectedResponseDto);

        //when
        FollowResponseDto actualResponseDto =
            authenticatedDeleteRequest(userAccessToken, requestUrl, HttpStatus.OK)
                .as(FollowResponseDto.class);

        //then
        assertThat(actualResponseDto)
            .usingRecursiveComparison()
            .isEqualTo(unfollowExpectedResponseDto);
    }

    private ExtractableResponse<Response> authenticatedGetRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> unauthenticatedGetRequest(String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .when().get(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedPostRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().post(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }

    private ExtractableResponse<Response> authenticatedDeleteRequest(String accessToken, String url,
        HttpStatus httpStatus) {
        return RestAssured.given().log().all()
            .auth().oauth2(accessToken)
            .when().delete(url)
            .then().log().all()
            .statusCode(httpStatus.value())
            .extract();
    }
}
