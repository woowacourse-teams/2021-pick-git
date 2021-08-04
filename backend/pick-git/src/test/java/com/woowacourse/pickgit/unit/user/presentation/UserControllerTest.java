package com.woowacourse.pickgit.unit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.presentation.UserController;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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

    @DisplayName("로그인 되어있을 때")
    @Nested
    class Describe_UnderLoginCondition {

        @DisplayName("사용자는 내 프로필을 조회할 수 있다.")
        @Test
        void getAuthenticatedUserProfile_LoginUser_Success() throws Exception {
            // given
            UserProfileResponseDto responseDto = UserFactory.mockLoginUserProfileResponseDto();

            given(oAuthService.validateToken("testToken"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("testToken"))
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

            verify(oAuthService, times(1))
                .validateToken("testToken");
            verify(oAuthService, times(1))
                .findRequestUserByToken("testToken");
            verify(userService, times(1))
                .getMyUserProfile(any(AuthUserRequestDto.class));

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
                    fieldWithPath("following").type(NULL).description("팔로잉 여부")
                )
            ));
        }

        @DisplayName("사용자는 타인의 프로필을 조회할 수 있다.")
        @Test
        void getUserProfile_LoginUser_Success() throws Exception {
            // given
            UserProfileResponseDto responseDto =
                UserFactory.mockLoginUserProfileIsFollowingResponseDto();

            given(oAuthService.validateToken("testToken"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("testToken"))
                .willReturn(new LoginUser("loginUser", "Bearer testToken"));
            given(userService.getUserProfile(any(AuthUserRequestDto.class), eq("testUser2")))
                .willReturn(responseDto);

            // when
            ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}", "testUser2")
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

            verify(oAuthService, times(1))
                .validateToken("testToken");
            verify(oAuthService, times(1))
                .findRequestUserByToken("testToken");
            verify(userService, times(1))
                .getUserProfile(any(AuthUserRequestDto.class), eq("testUser2"));

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

        @DisplayName("사용자는 팔로우를 할 수 있다.")
        @Test
        void followUser_LoginUser_Success() throws Exception {
            // given
            FollowResponseDto responseDto = new FollowResponseDto(1, true);

            given(oAuthService.validateToken("testToken"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("testToken"))
                .willReturn(new LoginUser("loginUser", "Bearer testToken"));
            given(userService.followUser(any(AuthUserRequestDto.class), eq("testUser")))
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

            verify(oAuthService, times(1))
                .validateToken("testToken");
            verify(oAuthService, times(1))
                .findRequestUserByToken("testToken");
            verify(userService, times(1))
                .followUser(any(AuthUserRequestDto.class), eq("testUser"));

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

        @DisplayName("사용자는 언팔로우를 할 수 있다.")
        @Test
        void unfollowUser_LoginUser_Success() throws Exception {
            // given
            FollowResponseDto followResponseDto = new FollowResponseDto(1, false);

            given(oAuthService.validateToken("testToken"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("testToken"))
                .willReturn(new LoginUser("loginUser", "Bearer testToken"));
            given(userService.unfollowUser(any(AuthUserRequestDto.class), eq("testUser")))
                .willReturn(followResponseDto);

            // when
            ResultActions perform = mockMvc
                .perform(RestDocumentationRequestBuilders
                    .delete("/api/profiles/{userName}/followings", "testUser")
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

            verify(oAuthService, times(1))
                .validateToken("testToken");
            verify(oAuthService, times(1))
                .findRequestUserByToken("testToken");
            verify(userService, times(1))
                .unfollowUser(any(AuthUserRequestDto.class), eq("testUser"));

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

        @DisplayName("사용자는 자신의 프로필을 수정할 수 있다.")
        @Test
        void editUserProfile_LoginUserWithImageAndDescription_Success() throws Exception {
            // given
            AppUser loginUser = new LoginUser("testUser", "token");
            String description = "updated description";
            MockMultipartFile image = FileFactory.getTestImage1();
            ProfileEditResponseDto responseDto = ProfileEditResponseDto.builder()
                .imageUrl(image.getOriginalFilename())
                .description(description)
                .build();

            // mock
            given(oAuthService.validateToken("token"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("token"))
                .willReturn(loginUser);
            given(userService
                .editProfile(any(AuthUserRequestDto.class), any(ProfileEditRequestDto.class)))
                .willReturn(responseDto);

            // when
            ResultActions perform = mockMvc.perform(multipart("/api/profiles/me")
                .file(image)
                .param("description", description)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
            );

            // then
            perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("imageUrl").value(image.getOriginalFilename()))
                .andExpect(jsonPath("description").value(description));

            verify(oAuthService, times(1)).validateToken("token");
            verify(oAuthService, times(1)).findRequestUserByToken("token");
            verify(userService, times(1))
                .editProfile(any(AuthUserRequestDto.class), any(ProfileEditRequestDto.class));

            // restdocs
            perform.andDo(document("edit-profile",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
                ),
                requestPartBody("images"),
                responseFields(
                    fieldWithPath("imageUrl").type(STRING).description("변경된 프로필 이미지 url"),
                    fieldWithPath("description").type(STRING).description("변경된 한 줄 소개")
                ))
            );
        }
    }

    @DisplayName("비로그인 상태일 때")
    @Nested
    class Describe_UnderGuestCondition {

        @DisplayName("게스트는 내 프로필을 조회할 수 없다. - 401 예외")
        @Test
        void getAuthenticatedUserProfile_LoginUser_401Exception() throws Exception {
            // given
            given(oAuthService.validateToken(any()))
                .willReturn(false);

            // when
            ResultActions perform = mockMvc.perform(get("/api/profiles/me")
                .header(HttpHeaders.AUTHORIZATION, "Bad testToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

            // then
            verify(oAuthService, times(1))
                .validateToken(any());

            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("A0001"));

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

        @DisplayName("게스트는 타인의 프로필을 조회할 수 있다.")
        @Test
        void getUserProfile_GuestUser_Success() throws Exception {
            // given
            UserProfileResponseDto responseDto = UserFactory.mockGuestUserProfileResponseDto();

            given(oAuthService.findRequestUserByToken(any()))
                .willCallRealMethod();
            given(userService.getUserProfile(any(AuthUserRequestDto.class), eq("testUser")))
                .willReturn(responseDto);

            // when
            ResultActions perform = mockMvc.perform(get("/api/profiles/{userName}", "testUser")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

            // then
            String body = perform
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

            verify(oAuthService, times(1))
                .findRequestUserByToken(any());
            verify(userService, times(1))
                .getUserProfile(any(AuthUserRequestDto.class), eq("testUser"));

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

        @DisplayName("게스트는 팔로우를 할 수 없다. - 401 예외")
        @Test
        void followUser_GuestUser_401Exception() throws Exception {
            // given
            given(oAuthService.validateToken(any()))
                .willReturn(false);

            // when
            ResultActions perform = mockMvc
                .perform(post("/api/profiles/{userName}/followings", "testUser")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL));

            // then
            verify(oAuthService, times(1))
                .validateToken(any());

            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("A0001"));

            perform.andDo(document("following-unLoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userName").description("다른 사용자 이름")
                ),
                responseFields(
                    fieldWithPath("errorCode").description("에러 코드")
                )
            ));
        }

        @DisplayName("게스트는 언팔로우를 할 수 없다. - 401 예외")
        @Test
        void unfollowUser_GuestUser_401Exception() throws Exception {
            // given
            given(oAuthService.validateToken(any()))
                .willReturn(false);

            // when
            ResultActions perform = mockMvc
                .perform(RestDocumentationRequestBuilders
                    .delete("/api/profiles/{userName}/followings", "testUser")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL));

            // then
            verify(oAuthService, times(1))
                .validateToken(any());

            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("A0001"));

            perform.andDo(document("unfollowing-unLoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("userName").description("다른 사용자 이름")
                ),
                responseFields(
                    fieldWithPath("errorCode").description("에러 코드")
                )
            ));
        }

        @DisplayName("게스트는 프로필을 수정할 수 없다.")
        @Test
        void editUserProfile_GuestUser_Fail() throws Exception {
            // given
            MockMultipartFile image = FileFactory.getTestImage1();

            // mock
            given(oAuthService.validateToken(any()))
                .willReturn(false);

            // when
            ResultActions perform = mockMvc.perform(multipart("/api/profiles/me")
                .file(image)
                .param("description", "updated description")
            );

            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("A0001"));

            verify(oAuthService, times(1)).validateToken(any());
        }
    }

    @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
    @Test
    void getContributions_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "testAccessToken");

        ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

        given(oAuthService.validateToken("testAccessToken"))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken("testAccessToken"))
            .willReturn(loginUser);
        given(userService.calculateContributions(any(ContributionRequestDto.class)))
            .willReturn(responseDto);

        // when
        ResultActions perform = mockMvc
            .perform(get("/api/profiles/{username}/contributions", "testUser")
                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL));

        // then
        String body = perform
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body).isEqualTo(objectMapper.writeValueAsString(responseDto));

        verify(oAuthService, times(1))
            .validateToken("testAccessToken");
        verify(oAuthService, times(1))
            .findRequestUserByToken("testAccessToken");
        verify(userService, times(1))
            .calculateContributions(any(ContributionRequestDto.class));

        perform.andDo(document("contributions-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer testAccessToken")
            ),
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
}
