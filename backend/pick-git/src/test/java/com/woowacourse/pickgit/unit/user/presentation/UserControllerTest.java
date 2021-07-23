package com.woowacourse.pickgit.unit.user.presentation;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.UserController;
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
}
