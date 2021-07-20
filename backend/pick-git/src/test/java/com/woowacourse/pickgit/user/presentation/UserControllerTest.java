package com.woowacourse.pickgit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.user.UserFactory;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserServiceDto;
import com.woowacourse.pickgit.user.application.dto.FollowServiceDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileServiceDto;
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
import org.springframework.test.web.servlet.MvcResult;
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
    private UserService userService;

    @MockBean
    private OAuthService oAuthService;

    @DisplayName("인증된 사용자의 프로필을 가져온다.")
    @Test
    void getAuthenticatedUserProfile() throws Exception {
        UserProfileServiceDto userProfileServiceDto = new UserFactory()
            .mockLoginUserProfileServiceDto();

        given(userService.getMyUserProfile(any(AuthUserServiceDto.class)))
            .willReturn(userProfileServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(get("/api/profiles/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(userProfileServiceDto));

        perform.andDo(document("profilesMe",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("baerer token")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("image").type(STRING).description("프로필 이미지 url"),
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

    @DisplayName("다른 사용자의 프로필을 가져온다. - 로그인")
    @Test
    void getUserProfile_loggedIn() throws Exception {
        UserProfileServiceDto userProfileServiceDto = new UserFactory()
            .mockLoginUserProfileServiceDto();

        given(userService.getUserProfile(any(AppUser.class), anyString()))
            .willReturn(userProfileServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}}", "testUser")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(userProfileServiceDto));

        perform.andDo(document("profiles-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("baerer token")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("image").type(STRING).description("프로필 이미지 url"),
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

    @DisplayName("다른 사용자의 프로필을 가져온다. - 비 로그인")
    @Test
    void getUserProfile_unLoggedIn() throws Exception {
        UserProfileServiceDto userProfileServiceDto = new UserFactory()
            .mockUnLoginUserProfileServiceDto();

        given(userService.getUserProfile(any(AppUser.class), anyString()))
            .willReturn(userProfileServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}}", "testUser")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(userProfileServiceDto));

        perform.andDo(document("profiles-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("name").type(STRING).description("사용자 이름"),
                fieldWithPath("image").type(STRING).description("프로필 이미지 url"),
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

    @DisplayName("팔로잉을 한다. - 로그인")
    @Test
    void followUser() throws Exception {
        FollowServiceDto followServiceDto = new FollowServiceDto(1, true);

        given(userService.followUser(any(AuthUserServiceDto.class), anyString()))
            .willReturn(followServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(post("/api/profiles/{userName}/followings", "testUser")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(followServiceDto));

        perform.andDo(document("following-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
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

    @DisplayName("언팔로잉일 한다. - 로그인")
    @Test
    void unfollowUser() throws Exception {
        FollowServiceDto followServiceDto = new FollowServiceDto(1, false);

        given(userService.followUser(any(AuthUserServiceDto.class), anyString()))
            .willReturn(followServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(post("/api/profiles/{userName}/followings", "testUser")
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(followServiceDto));

        perform.andDo(document("unfollowing-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
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

    @DisplayName("팔로잉을 한다. - 비 로그인")
    @Test
    void followUser_unLogin() throws Exception {
        FollowServiceDto followServiceDto = new FollowServiceDto(1, true);

        given(userService.followUser(any(AuthUserServiceDto.class), anyString()))
            .willReturn(followServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(post("/api/profiles/{userName}/followings", "testUser")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        MvcResult mvcResult = perform.andReturn();
        String body = mvcResult.getResponse().getContentAsString();

        perform.andExpect(jsonPath("errorCode").value("A0001"));

        perform.andDo(document("following-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").description("A0001")
            )
        ));
    }

    @DisplayName("언팔로잉일 한다. - 비 로그인")
    @Test
    void unfollowUser_unLogin() throws Exception {
        FollowServiceDto followServiceDto = new FollowServiceDto(1, false);

        given(userService.followUser(any(AuthUserServiceDto.class), anyString()))
            .willReturn(followServiceDto);
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("test", "test"));

        ResultActions perform = mockMvc.perform(post("/api/profiles/{userName}/followings", "testUser")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.ALL));

        perform.andExpect(jsonPath("errorCode").value("A0001"));

        perform.andDo(document("unfollowing-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("userName").description("다른 사용자 이름")
            ),
            responseFields(
                fieldWithPath("errorCode").description("A0001")
            )
        ));
    }
}