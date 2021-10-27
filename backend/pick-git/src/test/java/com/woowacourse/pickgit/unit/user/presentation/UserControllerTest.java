package com.woowacourse.pickgit.unit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.unit.ControllerTest;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.FollowRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.ProfileImageEditRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.ContributionResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.FollowResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.ProfileImageEditResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserProfileResponseDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ContributionRequestDto;
import com.woowacourse.pickgit.user.presentation.dto.request.ProfileDescriptionRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

class UserControllerTest extends ControllerTest {

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
            given(userService.getMyUserProfile(any(AuthUserForUserRequestDto.class)))
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
                .getMyUserProfile(any(AuthUserForUserRequestDto.class));

            perform.andDo(document("profiles-me",
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
            given(userService.getUserProfile(any(AuthUserForUserRequestDto.class), eq("testUser2")))
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
                .getUserProfile(any(AuthUserForUserRequestDto.class), eq("testUser2"));

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
            given(userService.followUser(any(FollowRequestDto.class)))
                .willReturn(responseDto);

            // when
            ResultActions perform = mockMvc
                .perform(
                    post("/api/profiles/{userName}/followings?githubFollowing={githubFollowing}",
                        "testUser", false)
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
                .followUser(any(FollowRequestDto.class));

            perform.andDo(document("following-LoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                ),
                requestParameters(
                    parameterWithName("githubFollowing").description("깃헙 자동 팔로우 여부")
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
            given(userService.unfollowUser(any(FollowRequestDto.class)))
                .willReturn(followResponseDto);

            // when
            ResultActions perform = mockMvc
                .perform(RestDocumentationRequestBuilders
                    .delete(
                        "/api/profiles/{userName}/followings?githubUnfollowing={githubUnfollowing}",
                        "testUser", false)
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
                .unfollowUser(any(FollowRequestDto.class));

            perform.andDo(document("unfollowing-LoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
                ),
                requestParameters(
                    parameterWithName("githubUnfollowing").description("깃헙 자동 언팔로우 여부")
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

        @DisplayName("사용자는 활동 통계를 조회할 수 있다.")
        @Test
        void getContributions_LoginUser_Success() throws Exception {
            // given
            LoginUser loginUser = new LoginUser("testUser", "oauth.access.token");

            ContributionResponseDto responseDto = UserFactory.mockContributionResponseDto();

            given(oAuthService.validateToken("test.access.token"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("test.access.token"))
                .willReturn(loginUser);
            given(userService.calculateContributions(any(ContributionRequestDto.class)))
                .willReturn(responseDto);

            // when
            ResultActions perform = mockMvc
                .perform(get("/api/profiles/{username}/contributions", "testUser")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer test.access.token")
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
                .validateToken("test.access.token");
            verify(oAuthService, times(1))
                .findRequestUserByToken("test.access.token");
            verify(userService, times(1))
                .calculateContributions(any(ContributionRequestDto.class));

            perform.andDo(document("profiles-contributions-LoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer accessToken")
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

        @DisplayName("자신의 프로필 이미지를 수정할 수 있다.")
        @Test
        void editUserProfileImage_LoginUserWithImage_Success() throws Exception {
            // given
            AppUser loginUser = new LoginUser("testUser", "token");
            File file = FileFactory.getTestImage1File();

            // mock
            given(oAuthService.validateToken("token"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("token"))
                .willReturn(loginUser);
            given(userService
                .editProfileImage(any(AuthUserForUserRequestDto.class),
                    any(ProfileImageEditRequestDto.class)))
                .willReturn(new ProfileImageEditResponseDto(file.getName()));

            // when
            ResultActions perform = mockMvc.perform(
                put("/api/profiles/me/image")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .content(new FileInputStream(file).readAllBytes())
            );
            // then
            perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("imageUrl").value(file.getName()));

            verify(oAuthService, times(1)).validateToken("token");
            verify(oAuthService, times(1)).findRequestUserByToken("token");
            verify(userService, times(1))
                .editProfileImage(any(AuthUserForUserRequestDto.class),
                    any(ProfileImageEditRequestDto.class));
        }

        @DisplayName("자신의 프로필 한 줄 소개를 수정할 수 있다.")
        @Test
        void editUserProfileDescription_LoginUserWithDescription_Success()
            throws Exception {
            // given
            AppUser loginUser = new LoginUser("testUser", "token");
            String description = "updated description";

            // mock
            given(oAuthService.validateToken("token"))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken("token"))
                .willReturn(loginUser);
            given(userService
                .editProfileDescription(any(AuthUserForUserRequestDto.class), anyString()))
                .willReturn(description);

            // when
            ResultActions perform = mockMvc.perform(
                put("/api/profiles/me/description")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(
                        objectMapper.writeValueAsString(new ProfileDescriptionRequest(description)))
            );

            // then
            perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("description").value(description));

            verify(oAuthService, times(1)).validateToken("token");
            verify(oAuthService, times(1)).findRequestUserByToken("token");
            verify(userService, times(1))
                .editProfileDescription(any(AuthUserForUserRequestDto.class), anyString());

            // restdocs
            perform.andDo(document("profiles-edit-description",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
                ),
                requestFields(
                    fieldWithPath("description").type(STRING).description("프로필 한 줄 소개")
                ),
                responseFields(
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

            perform.andDo(document("profiles-me-unLoggedIn",
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
            given(userService.getUserProfile(any(AuthUserForUserRequestDto.class), eq("testUser")))
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
                .getUserProfile(any(AuthUserForUserRequestDto.class), eq("testUser"));

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

        @DisplayName("게스트는 활동 통계를 조회할 수 없다. - 401 예외")
        @Test
        void getContributions_GuestUser_401Exception() throws Exception {
            // given
            GuestUser guestUser = new GuestUser();

            given(oAuthService.validateToken(null))
                .willReturn(true);
            given(oAuthService.findRequestUserByToken(null))
                .willReturn(guestUser);

            // when
            ResultActions perform = mockMvc
                .perform(get("/api/profiles/{username}/contributions", "testUser")
                    .header(HttpHeaders.AUTHORIZATION, Optional.empty())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL));

            // then
            perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("errorCode").value("A0002"));

            verify(oAuthService, times(1))
                .validateToken(null);
            verify(oAuthService, times(1))
                .findRequestUserByToken(null);

            perform.andDo(document("profiles-contributions-unLoggedIn",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description(Optional.empty())
                ),
                responseFields(
                    fieldWithPath("errorCode").type(STRING).description("에러 코드")
                )
            ));
        }
    }

    @DisplayName("로그인 - 특정 유저의 팔로잉 목록을 조회한다.")
    @Test
    void searchFollowings_LoginUser_Success() throws Exception {
        // given
        List<UserSearchResponseDto> userSearchResponseDtos = List.of(
            new UserSearchResponseDto("image1", "kevin", true),
            new UserSearchResponseDto("image2", "koda", false),
            new UserSearchResponseDto("image3", "source", null)
        );
        given(oAuthService.validateToken("token"))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken("token"))
            .willReturn(new LoginUser("source", "token"));
        given(userService.searchFollowings(
            any(AuthUserForUserRequestDto.class),
            anyString(),
            any(Pageable.class)
        )).willReturn(userSearchResponseDtos);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/profiles/{username}/followings", "mark")
                .param("page", "0")
                .param("limit", "3")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl", contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username", contains("kevin", "koda", "source")))
            .andExpect(jsonPath("$[*].following", contains(true, false, null)));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1)).findRequestUserByToken("token");
        verify(userService, times(1))
            .searchFollowings(
                any(AuthUserForUserRequestDto.class),
                anyString(),
                any(Pageable.class)
            );

        // restdocs
        resultActions.andDo(document("search-followings-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("username").description("팔로잉 목록 조회 대상 유저 이름")
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("팔로잉 유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("팔로잉 유저 이름"),
                fieldWithPath("[].following").type(BOOLEAN).optional()
                    .description("로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("비로그인 - 특정 유저의 팔로잉 목록을 조회한다.")
    @Test
    void searchFollowings_GuestUser_Success() throws Exception {
        // given
        List<UserSearchResponseDto> userSearchResponseDtos = List.of(
            new UserSearchResponseDto("image1", "kevin", null),
            new UserSearchResponseDto("image2", "koda", null),
            new UserSearchResponseDto("image3", "source", null)
        );
        given(oAuthService.findRequestUserByToken(null))
            .willReturn(new GuestUser());
        given(userService.searchFollowings(
            any(AuthUserForUserRequestDto.class),
            anyString(),
            any(Pageable.class)
        )).willReturn(userSearchResponseDtos);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/profiles/{username}/followings", "mark")
                .param("page", "0")
                .param("limit", "3")
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl", contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username", contains("kevin", "koda", "source")))
            .andExpect(jsonPath("$[*].following", contains(nullValue(), nullValue(), nullValue())));

        verify(oAuthService, times(1)).findRequestUserByToken(null);
        verify(userService, times(1))
            .searchFollowings(
                any(AuthUserForUserRequestDto.class),
                anyString(),
                any(Pageable.class)
            );

        // restdocs
        resultActions.andDo(document("search-followings-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("username").description("팔로잉 목록 조회 대상 유저 이름")
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("팔로잉 유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("팔로잉 유저 이름"),
                fieldWithPath("[].following").type(NULL).description("비로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("로그인 - 특정 유저의 팔로워 목록을 조회한다.")
    @Test
    void searchFollowers_LoginUser_Success() throws Exception {
        // given
        List<UserSearchResponseDto> userSearchResponseDtos = List.of(
            new UserSearchResponseDto("image1", "kevin", true),
            new UserSearchResponseDto("image2", "koda", false),
            new UserSearchResponseDto("image3", "source", null)
        );
        given(oAuthService.validateToken("token"))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken("token"))
            .willReturn(new LoginUser("source", "token"));
        given(userService.searchFollowers(
            any(AuthUserForUserRequestDto.class),
            anyString(),
            any(Pageable.class)
        )).willReturn(userSearchResponseDtos);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/profiles/{username}/followers", "mark")
                .param("page", "0")
                .param("limit", "3")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl", contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username", contains("kevin", "koda", "source")))
            .andExpect(jsonPath("$[*].following", contains(true, false, null)));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1)).findRequestUserByToken("token");
        verify(userService, times(1))
            .searchFollowers(
                any(AuthUserForUserRequestDto.class),
                anyString(),
                any(Pageable.class)
            );

        // restdocs
        resultActions.andDo(document("search-followers-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("username").description("팔로워 목록 조회 대상 유저 이름")
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("팔로워 유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("팔로워 유저 이름"),
                fieldWithPath("[].following").type(BOOLEAN).optional()
                    .description("로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("비로그인 - 특정 유저의 팔로워 목록을 조회한다.")
    @Test
    void searchFollowers_GuestUser_Success() throws Exception {
        // given
        List<UserSearchResponseDto> userSearchResponseDtos = List.of(
            new UserSearchResponseDto("image1", "kevin", null),
            new UserSearchResponseDto("image2", "koda", null),
            new UserSearchResponseDto("image3", "source", null)
        );
        given(oAuthService.findRequestUserByToken(null))
            .willReturn(new GuestUser());
        given(userService.searchFollowers(
            any(AuthUserForUserRequestDto.class),
            anyString(),
            any(Pageable.class)
        )).willReturn(userSearchResponseDtos);

        // when
        ResultActions resultActions = mockMvc.perform(
            get("/api/profiles/{username}/followers", "mark")
                .param("page", "0")
                .param("limit", "3")
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl", contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username", contains("kevin", "koda", "source")))
            .andExpect(jsonPath("$[*].following", contains(nullValue(), nullValue(), nullValue())));

        verify(oAuthService, times(1)).findRequestUserByToken(null);
        verify(userService, times(1))
            .searchFollowers(
                any(AuthUserForUserRequestDto.class),
                anyString(),
                any(Pageable.class)
            );

        // restdocs
        resultActions.andDo(document("search-followers-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("username").description("팔로워 목록 조회 대상 유저 이름")
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("팔로워 유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("팔로워 유저 이름"),
                fieldWithPath("[].following").type(NULL).description("비로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }
}
