package com.woowacourse.pickgit.unit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BOOLEAN;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.application.dto.request.AuthUserForPostRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeUsersResponseDto;
import com.woowacourse.pickgit.unit.ControllerTest;
import com.woowacourse.pickgit.user.domain.User;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class PostControllerTest_likeUsers extends ControllerTest {

    @DisplayName("게시물의 좋아요 리스트를 조회할 수 있다. - 로그인/성공")
    @Test
    void searchLikeUsers_LoginUser_Success() throws Exception {
        String token = "token";
        Long postId = 1L;
        List<User> likeUsers = UserFactory.mockLikeUsersWithId();

        List<LikeUsersResponseDto> likeUsersResponseDto =
            createLikeUserResponseDtoForLoginUser(likeUsers);

        // mock
        given(oAuthService.validateToken(token)).willReturn(true);
        given(oAuthService.findRequestUserByToken(token))
            .willReturn(new LoginUser("author", "token"));
        given(postService.likeUsers(any(AuthUserForPostRequestDto.class), anyLong()))
            .willReturn(likeUsersResponseDto);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/posts/{postId}/likes", postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            );

        // then
        String body = perform
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body)
            .isEqualTo(objectMapper.writeValueAsString(likeUsersResponseDto));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1))
            .findRequestUserByToken("token");
        verify(postService, times(1))
            .likeUsers(any(AuthUserForPostRequestDto.class), anyLong());

        // restdocs
        perform.andDo(document("post-likeUsers-loggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + token)
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("유저 이미지 URL"),
                fieldWithPath("[].username").type(STRING).description("유저 이름"),
                fieldWithPath("[].following").type(BOOLEAN).description("로그인 유저의 타겟 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("게시물의 좋아요 리스트를 조회할 수 있다. - 비 로그인/성공")
    @Test
    void searchLikeUsers_GuestUser_Success() throws Exception {
        Long postId = 1L;
        List<User> likeUsers = UserFactory.mockLikeUsersWithId();

        List<LikeUsersResponseDto> likeUsersResponseDto =
            createLikeUserResponseForGuest(likeUsers);

        // mock
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postService.likeUsers(any(AuthUserForPostRequestDto.class), anyLong()))
            .willReturn(likeUsersResponseDto);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/posts/{postId}/likes", postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)
            );

        // then
        String body = perform
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body)
            .isEqualTo(objectMapper.writeValueAsString(likeUsersResponseDto));

        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(postService, times(1))
            .likeUsers(any(AuthUserForPostRequestDto.class), anyLong());

        // restdocs
        perform.andDo(document("post-likeUsers-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("유저 이미지 URL"),
                fieldWithPath("[].username").type(STRING).description("유저 이름"),
                fieldWithPath("[].following").type(BOOLEAN).description("로그인 유저의 타겟 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("좋아요가 하나도 없는 게시물을 조회하면 빈 배열을 반환한다. - 비 로그인/성공")
    @Test
    void searchLikeUsers_EmptyLikes_Success() throws Exception {
        Long postId = 1L;
        List<LikeUsersResponseDto> likeUsersResponseDto = List.of();

        // mock
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postService.likeUsers(any(AuthUserForPostRequestDto.class), anyLong()))
            .willReturn(likeUsersResponseDto);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/posts/{postId}/likes", postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL)
            );

        // then
        String body = perform
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        assertThat(body)
            .isEqualTo(objectMapper.writeValueAsString(likeUsersResponseDto));

        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(postService, times(1))
            .likeUsers(any(AuthUserForPostRequestDto.class), anyLong());

        // restdocs
        perform.andDo(document("post-likeUsers-emptyLikes",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("[]").type(LIST).description("빈 배열")
            )
        ));
    }

    private List<LikeUsersResponseDto> createLikeUserResponseDtoForLoginUser(
        List<User> likeUsers
    ) {
        return likeUsers.stream()
            .map(user -> {
                if (isFollowingUser(likeUsers, user)) {
                    return new LikeUsersResponseDto(
                        user.getImage(), user.getName(), true
                    );
                }
                return new LikeUsersResponseDto(
                    user.getImage(), user.getName(), false
                );
            }).collect(toList());
    }

    private boolean isFollowingUser(List<User> likeUsers, User user) {
        return user.getName().equals(likeUsers.get(0).getName())
            || user.getName().equals(likeUsers.get(1).getName());
    }

    private List<LikeUsersResponseDto> createLikeUserResponseForGuest(List<User> likeUsers) {
        return likeUsers.stream()
            .map(
                user -> new LikeUsersResponseDto(user.getImage(), user.getName(), null)
            ).collect(toList());
    }
}
