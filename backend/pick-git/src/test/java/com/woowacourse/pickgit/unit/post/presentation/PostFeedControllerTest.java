package com.woowacourse.pickgit.unit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.post.application.dto.request.HomeFeedRequestDto;
import com.woowacourse.pickgit.unit.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

class PostFeedControllerTest extends ControllerTest {

    private static final String API_ACCESS_TOKEN = "oauth.access.token";

    @DisplayName("로그인 유저는 자신의 홈피드를 조회할 수 있다.")
    @Test
    void readMyHomeFeed_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postFeedService.userFeed(any(HomeFeedRequestDto.class), anyString()))
            .willReturn(PostFactory.mockPostResponseDtosForLogin());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/me")
            .param("page", "0")
            .param("limit", "3")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk());

        perform.andDo(document("post-myfeed-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + API_ACCESS_TOKEN)
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("게시물 id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("이미지 주소 목록"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("깃허브 주소"),
                fieldWithPath("[].content").type(STRING).description("게시물 내용"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("프로필 이미지 주소"),
                fieldWithPath("[].likesCount").type(NUMBER).description("좋아요 수"),
                fieldWithPath("[].tags").type(ARRAY).description("태그 목록"),
                fieldWithPath("[].createdAt").type(STRING).description("글 작성 시간"),
                fieldWithPath("[].updatedAt").type(STRING).description("마지막 글 수정 시간"),
                fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("댓글 아이디"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].liked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].liked").type(BOOLEAN).description("좋아요 여부")
            )
            )
        );
    }

    @DisplayName("로그인 유저는 특정 유저의 홈피드를 조회할 수 있다.")
    @Test
    void readUserHomeFeed_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postFeedService.userFeed(any(HomeFeedRequestDto.class), anyString()))
            .willReturn(PostFactory.mockPostResponseDtosForLogin());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/{username}", "pickgit-user")
            .param("page", "0")
            .param("limit", "3")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk());

        perform.andDo(document("post-userfeed-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + API_ACCESS_TOKEN)
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("게시물 id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("이미지 주소 목록"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("깃허브 주소"),
                fieldWithPath("[].content").type(STRING).description("게시물 내용"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("프로필 이미지 주소"),
                fieldWithPath("[].likesCount").type(NUMBER).description("좋아요 수"),
                fieldWithPath("[].tags").type(ARRAY).description("태그 목록"),
                fieldWithPath("[].createdAt").type(STRING).description("글 작성 시간"),
                fieldWithPath("[].updatedAt").type(STRING).description("마지막 글 수정 시간"),
                fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("댓글 아이디"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].liked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].liked").type(BOOLEAN).description("좋아요 여부")
            )
            )
        );
    }

    @DisplayName("비로그인 유저는 특정 유저의 홈피드를 조회할 수 있다.")
    @Test
    void readUserHomeFeed_GuestUser_Success() throws Exception {
        // given
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postFeedService.userFeed(any(HomeFeedRequestDto.class), anyString()))
            .willReturn(PostFactory.mockPostResponseDtosForGuest());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts/{username}", "pickgit-user")
            .param("page", "0")
            .param("limit", "3"));

        // then
        perform
            .andExpect(status().isOk());

        perform.andDo(document("post-userfeed-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("게시물 id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("이미지 주소 목록"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("깃허브 주소"),
                fieldWithPath("[].content").type(STRING).description("게시물 내용"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("프로필 이미지 주소"),
                fieldWithPath("[].likesCount").type(NUMBER).description("좋아요 수"),
                fieldWithPath("[].tags").type(ARRAY).description("태그 목록"),
                fieldWithPath("[].createdAt").type(STRING).description("글 작성 시간"),
                fieldWithPath("[].updatedAt").type(STRING).description("마지막 글 수정 시간"),
                fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("댓글 아이디"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].liked").type(NULL).description("댓글 좋아요 여부"),
                fieldWithPath("[].liked").type(NULL).description("좋아요 여부")
            )
            )
        );
    }


    @DisplayName("비로그인 유저는 홈피드를 조회할 수 있다.")
    @Test
    void readHomeFeed_GuestUser_Success() throws Exception {
        // given
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postFeedService.allHomeFeed(any(HomeFeedRequestDto.class)))
            .willReturn(PostFactory.mockPostResponseDtosForGuest());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("limit", "3"));

        // then
        perform.andExpect(status().isOk());

        // documentation
        perform.andDo(document("post-homefeed-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("게시물 id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("이미지 주소 목록"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("깃허브 주소"),
                fieldWithPath("[].content").type(STRING).description("게시물 내용"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("프로필 이미지 주소"),
                fieldWithPath("[].likesCount").type(NUMBER).description("좋아요 수"),
                fieldWithPath("[].tags").type(ARRAY).description("태그 목록"),
                fieldWithPath("[].createdAt").type(STRING).description("글 작성 시간"),
                fieldWithPath("[].updatedAt").type(STRING).description("마지막 글 수정 시간"),
                fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("댓글 아이디"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].liked").type(NULL).description("댓글 좋아요 여부"),
                fieldWithPath("[].liked").type(NULL).description("좋아요 여부")
            )
            )
        );
    }


    @DisplayName("로그인 유저는 홈피드를 조회할 수 있다.")
    @Test
    void readHomeFeed_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postFeedService.allHomeFeed(any(HomeFeedRequestDto.class)))
            .willReturn(PostFactory.mockPostResponseDtosForLogin());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("limit", "3")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + API_ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk());

        perform.andDo(document("post-homefeed-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + API_ACCESS_TOKEN)
            ),
            requestParameters(
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("게시물 id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("이미지 주소 목록"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("깃허브 주소"),
                fieldWithPath("[].content").type(STRING).description("게시물 내용"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("프로필 이미지 주소"),
                fieldWithPath("[].likesCount").type(NUMBER).description("좋아요 수"),
                fieldWithPath("[].tags").type(ARRAY).description("태그 목록"),
                fieldWithPath("[].createdAt").type(STRING).description("글 작성 시간"),
                fieldWithPath("[].updatedAt").type(STRING).description("마지막 글 수정 시간"),
                fieldWithPath("[].comments").type(ARRAY).description("댓글 목록"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("댓글 아이디"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].liked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].liked").type(BOOLEAN).description("좋아요 여부")
            )
            )
        );
    }
}
