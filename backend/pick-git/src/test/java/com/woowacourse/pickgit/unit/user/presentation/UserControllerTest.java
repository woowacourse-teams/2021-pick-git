package com.woowacourse.pickgit.unit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.UserController;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private UserService userService;

    @DisplayName("사용자는 내 프로필을 조회할 수 있다.")
    @Test
    void getAuthenticatedUserProfile_LoginUser_Success() throws Exception {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("loginUser", "testToken"));
        given(userService.getMyUserProfile(any(AuthUserRequestDto.class)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(get("/api/profiles/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        perform.andDo(document("profilesMe",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("imageUrl").type(STRING).description("프로필 이미지 url"),
                fieldWithPath("description").type(STRING).description("한줄 소개"),
                fieldWithPath("followerCount").type(NUMBER).description("팔로워 수"),
                fieldWithPath("followingCount").type(NUMBER).description("팔로잉 수"),
                fieldWithPath("postCount").type(NUMBER).description("게시물 수"),
                fieldWithPath("githubUrl").type(STRING).description("깃허브 url"),
                fieldWithPath("company").type(STRING).description("회사"),
                fieldWithPath("location").type(STRING).description("위치"),
                fieldWithPath("website").type(STRING).description("웹 사이트"),
                fieldWithPath("twitter").type(STRING).description("트위터"),
                fieldWithPath("following").type(BOOLEAN).description("팔로잉 여부")
            )
        ));
    }

    @DisplayName("게스트는 내 프로필을 조회할 수 없다. - 401 예외")
    @Test
    void getAuthenticatedUserProfile_LoginUser_401Exception() throws Exception {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();
        given(userService.getMyUserProfile(any(AuthUserRequestDto.class)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(get("/api/profiles/me")
            .header(HttpHeaders.AUTHORIZATION, "Bad testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0002"));

        perform.andDo(document("profilesMe",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bad token")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    @DisplayName("사용자는 타인의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_LoginUser_Success() throws Exception {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("loginUser", "Bearer testToken"));
        given(userService.getUserProfile(any(AppUser.class), anyString()))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}}", "testUser")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        perform.andDo(document("profiles-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("imageUrl").type(STRING).description("프로필 이미지 url"),
                fieldWithPath("description").type(STRING).description("한줄 소개"),
                fieldWithPath("followerCount").type(NUMBER).description("팔로워 수"),
                fieldWithPath("followingCount").type(NUMBER).description("팔로잉 수"),
                fieldWithPath("postCount").type(NUMBER).description("게시물 수"),
                fieldWithPath("githubUrl").type(STRING).description("깃허브 url"),
                fieldWithPath("company").type(STRING).description("회사"),
                fieldWithPath("location").type(STRING).description("위치"),
                fieldWithPath("website").type(STRING).description("웹 사이트"),
                fieldWithPath("twitter").type(STRING).description("트위터"),
                fieldWithPath("following").type(BOOLEAN).description("팔로잉 여부")
            )
        ));
    }

    @DisplayName("게스트는 타인의 프로필을 조회할 수 있다.")
    @Test
    void getUserProfile_GuestUser_Success() throws Exception {
        // given
        UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();
        given(userService.getUserProfile(any(AppUser.class), anyString()))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}}", "testUser")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        perform.andDo(document("profiles-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("imageUrl").type(STRING).description("프로필 이미지 url"),
                fieldWithPath("description").type(STRING).description("한줄 소개"),
                fieldWithPath("followerCount").type(NUMBER).description("팔로워 수"),
                fieldWithPath("followingCount").type(NUMBER).description("팔로잉 수"),
                fieldWithPath("postCount").type(NUMBER).description("게시물 수"),
                fieldWithPath("githubUrl").type(STRING).description("깃허브 url"),
                fieldWithPath("company").type(STRING).description("회사"),
                fieldWithPath("location").type(STRING).description("위치"),
                fieldWithPath("website").type(STRING).description("웹 사이트"),
                fieldWithPath("twitter").type(STRING).description("트위터"),
                fieldWithPath("following").type(NULL).description("팔로잉 여부")
            )
        ));
    }

    @DisplayName("누구든지 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_Anyone_Success() throws Exception {
        // given
        ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

        given(userService.calculateContributions(anyString()))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc
            .perform(get("/api/profiles/{username}/contributions", "testUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        perform.andDo(document("contributions-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("username").description("사용자 이름")
            ),
            responseFields(
                fieldWithPath("starsCount").description("스타 개수"),
                fieldWithPath("commitsCount").description("커밋 개수"),
                fieldWithPath("prsCount").description("PR 개수"),
                fieldWithPath("issuesCount").description("이슈 개수"),
                fieldWithPath("reposCount").description("퍼블릭 레포지토리 개수")
            )
        ));
    }

    @DisplayName("사용자는 팔로우를 할 수 있다.")
    @Test
    void followUser_LoginUser_Success() throws Exception {
        // given
        FollowResponseDto responseDto = new FollowResponseDto(1, true);

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("loginUser", "Bearer testToken"));
        given(userService.followUser(any(AuthUserRequestDto.class), anyString()))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc
            .perform(post("/api/profiles/{userName}/followings", "testUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        perform.andDo(document("following-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("followerCount").description("팔로워 수"),
                fieldWithPath("following").description("팔로잉 여부")
            )
        ));
    }

    @DisplayName("게스트는 팔로우를 할 수 없다. - 401 예외")
    @Test
    void followUser_GuestUser_401Exception() throws Exception {
        // given
        FollowResponseDto responseDto = new FollowResponseDto(1, true);

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();
        given(userService.followUser(any(AuthUserRequestDto.class), anyString()))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc
            .perform(post("/api/profiles/{userName}/followings", "testUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0002"));

        perform.andDo(document("following-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").description("A0002")
            )
        ));
    }

    @DisplayName("사용자는 언팔로우를 할 수 있다.")
    @Test
    void unfollowUser_LoginUser_Success() throws Exception {
        // given
        FollowResponseDto followResponseDto = new FollowResponseDto(1, false);

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("loginUser", "Bearer testToken"));
        given(userService.followUser(any(AuthUserRequestDto.class), anyString()))
            .willReturn(followResponseDto);

        // when
        ResultActions perform = mockMvc
            .perform(post("/api/profiles/{userName}/followings", "testUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(followResponseDto));

        perform.andDo(document("unfollowing-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("followerCount").description("팔로워 수"),
                fieldWithPath("following").description("팔로잉 여부")
            )
        ));
    }

    @DisplayName("게스트는 언팔로우를 할 수 없다. - 401 예외")
    @Test
    void unfollowUser_GuestUser_401Exception() throws Exception {
        // given
        FollowResponseDto followResponseDto = new FollowResponseDto(1, false);

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();
        given(userService.followUser(any(AuthUserRequestDto.class), anyString()))
            .willReturn(followResponseDto);

        // when
        ResultActions perform = mockMvc
            .perform(post("/api/profiles/{userName}/followings", "testUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0002"));

        perform.andDo(document("unfollowing-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").description("A0002")
            )
        ));
    }

    @DisplayName("로그인 - 검색 키워드와 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉한 여부 boolean")
    @Test
    void searchUser_LoginUser_Success() throws Exception {
        // given
        String searchKeyword = "bing";
        List<UserSearchResponseDto> userSearchRespons = List.of(
            new UserSearchResponseDto("image1", "bingbingdola", true),
            new UserSearchResponseDto("image2", "bing", false),
            new UserSearchResponseDto("image3", "bbbbbing", false)
        );

        // mock
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new LoginUser("pick-git", "token"));
        given(userService.searchUser(any(LoginUser.class), any(UserSearchRequestDto.class)))
            .willReturn(userSearchRespons);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/search")
                    .param("keyword", searchKeyword)
                    .param("page", "0")
                    .param("limit", "5")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL));

        // then
        perform
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users[*].imageUrl",
                contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$.users[*].username",
                containsInRelativeOrder("bingbingdola", "bing", "bbbbbing")))
            .andExpect(jsonPath("$.users[*].following",
                containsInRelativeOrder(true, false, false)));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(userService, times(1))
            .searchUser(any(AppUser.class), any(UserSearchRequestDto.class));

        // restdocs
        perform.andDo(document("search-user-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("users[].imageUrl").type(STRING).description("유저 이미지 url"),
                fieldWithPath("users[].username").type(STRING).description("유저 이름"),
                fieldWithPath("users[].following").type(BOOLEAN).description("로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("비 로그인 - 검색 키워드와 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
    @Test
    void searchUser_GuestUser_Success() throws Exception {
        // given
        String searchKeyword = "bing";
        List<UserSearchResponseDto> userSearchRespons = List.of(
            new UserSearchResponseDto("image1", "bingbingdola", null),
            new UserSearchResponseDto("image2", "bing", null),
            new UserSearchResponseDto("image3", "bbbbbing", null)
        );

        // mock
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(userService.searchUser(any(GuestUser.class), any(UserSearchRequestDto.class)))
            .willReturn(userSearchRespons);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/search")
                    .param("keyword", searchKeyword)
                    .param("page", "0")
                    .param("limit", "5")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        perform
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.users[*].imageUrl",
                contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$.users[*].username",
                containsInRelativeOrder("bingbingdola", "bing", "bbbbbing")))
            .andExpect(jsonPath("$.users[*].following",
                containsInRelativeOrder(nullValue(), nullValue(), nullValue()
            )));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(userService, times(1))
            .searchUser(any(AppUser.class), any(UserSearchRequestDto.class));

        // restdocs
        perform.andDo(document("search-user-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("users[].imageUrl").type(STRING).description("유저 이미지 url"),
                fieldWithPath("users[].username").type(STRING).description("유저 이름"),
                fieldWithPath("users[].following").type(NULL).description("비 로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }
}
