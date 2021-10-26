package com.woowacourse.pickgit.unit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.post.application.dto.request.PostDeleteRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.PostUpdateRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.SearchRepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostUpdateResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.PostUpdateRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import com.woowacourse.pickgit.post.presentation.dto.response.PostUpdateResponse;
import com.woowacourse.pickgit.unit.ControllerTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class PostControllerTest extends ControllerTest {

    private static final String ACCESS_TOKEN = "testToken";

    @DisplayName("게시물을 작성할 수 있다. - 사용자")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postService.write(any(PostRequestDto.class)))
            .willReturn(1L);

        // when
        ResultActions perform = mockMvc.perform(multipart("/api/posts")
            .file("images", "testImage1".getBytes())
            .file("images", "testImage2".getBytes())
            .param(PickGit.GITHUB_REPO_URL, "https://github.com/bperhaps")
            .param(PickGit.CONTENT, "content")
            .param(PickGit.TAGS, new String[]{"tag1", "tag2"})
            .header(HttpHeaders.AUTHORIZATION, loginUser.getAccessToken()));

        // then
        perform.andExpect(status().isCreated());

        //documentation
        perform.andDo(document("posts-post-user",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            requestPartBody("images"),
            responseHeaders(
                headerWithName(HttpHeaders.LOCATION).description("게시물 주소")
            ))
        );
    }

    @DisplayName("게시물을 작성할 수 없다. - 게스트")
    @Test
    void write_GuestUser_Fail() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(false);

        // when
        ResultActions perform = mockMvc.perform(multipart("/api/posts")
            .file("images", "testImage1".getBytes())
            .file("images", "testImage2".getBytes())
            .param(PickGit.GITHUB_REPO_URL, "https://github.com/bperhaps")
            .param(PickGit.CONTENT, "content")
            .param(PickGit.TAGS, new String[]{"tag1", "tag2"}));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0001"));

        //documentation
        perform.andDo(document("posts-post-guest",
            getDocumentRequest(),
            getDocumentResponse(),
            requestPartBody("images"),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(null);
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void userRepositories_LoginUser_Success() throws Exception {
        // given
        List<RepositoryResponseDto> repositoryResponseDtos = List.of(
            new RepositoryResponseDto("https://github.com/jipark3/pick", "pick"),
            new RepositoryResponseDto("https://github.com/jipark3/git", "git")
        );

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new LoginUser("testUser", ACCESS_TOKEN));
        given(postService.userRepositories(any(RepositoryRequestDto.class)))
            .willReturn(repositoryResponseDtos);

        String repositories = objectMapper
            .writeValueAsString(repositoryResponseDtos);

        // when
        ResultActions perform = mockMvc.perform(get("/api/github/repositories")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
            .queryParam("page", "0")
            .queryParam("limit", "50"));

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().string(repositories));

        verify(postService, times(1))
            .userRepositories(any(RepositoryRequestDto.class));

        //documentation
        perform.andDo(document("post-repositories-loggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            responseFields(
                fieldWithPath("[].html_url").type(STRING).description("레포지토리 주소"),
                fieldWithPath("[].name").type(STRING).description("레포지토리 이름")
            )
        ));
    }

    @DisplayName("사용자는 Repository 목록을 검색할 수 있다. - 성공")
    @Test
    void userSearchedRepositories_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);

        List<RepositoryResponseDto> repositoryResponseDtos = List.of(
            new RepositoryResponseDto("pick", "https://github.com/jipark3/pick"),
            new RepositoryResponseDto("pick-git", "https://github.com/jipark3/pick-git")
        );
        String repositories = objectMapper.writeValueAsString(repositoryResponseDtos);

        given(postService.searchUserRepositories(any(SearchRepositoryRequestDto.class)))
            .willReturn(repositoryResponseDtos);

        // then
        ResultActions perform = mockMvc
            .perform(get("/api/github/search/repositories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                .param("keyword", "pick")
                .param("page", "1")
                .param("limit", "2")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(repositories));

        //documentation
        perform.andDo(document("post-searchRepositories-loggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            requestParameters(
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].name").type(STRING).description("레포지토리 이름"),
                fieldWithPath("[].html_url").type(STRING).description("레포지토리 주소")
            )
        ));
    }

    @DisplayName("로그인 한 사용자는 게시물을 좋아요 할 수 있다. - 성공")
    @Test
    void likePost_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");
        LikeResponseDto likeResponseDto = new LikeResponseDto(1, true);
        Long postId = 1L;

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        given(postService.like(any(AppUser.class), anyLong()))
            .willReturn(likeResponseDto);

        String likeResponse =
            objectMapper.writeValueAsString(
                new LikeResponse(likeResponseDto.getLikesCount(), likeResponseDto.getLiked())
            );

        // when
        ResultActions perform =
            mockMvc.perform(put("/api/posts/{postId}/likes", postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().string(likeResponse));

        // documentation
        perform.andDo(document("post-likePost-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + ACCESS_TOKEN)
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("likesCount").type(NUMBER).description("게시물 좋아요 개수"),
                fieldWithPath("liked").type(BOOLEAN).description("게시물 좋아요 여부")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(ACCESS_TOKEN);
        verify(oAuthService, times(1))
            .findRequestUserByToken(ACCESS_TOKEN);
        verify(postService, times(1))
            .like(loginUser, postId);
    }

    @DisplayName("로그인 한 사용자는 게시물을 좋아요 취소 할 수 있다. - 성공")
    @Test
    void unlikePost_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");
        LikeResponseDto likeResponseDto = new LikeResponseDto(0, false);
        Long postId = 1L;

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postService.unlike(any(AppUser.class), anyLong()))
            .willReturn(likeResponseDto);

        String likeResponse =
            objectMapper.writeValueAsString(
                new LikeResponse(likeResponseDto.getLikesCount(), likeResponseDto.getLiked())
            );

        // when
        ResultActions perform =
            mockMvc.perform(delete("/api/posts/{postId}/likes", postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().string(likeResponse));

        // documentation
        perform.andDo(document("post-unlikePost-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + ACCESS_TOKEN)
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("likesCount").type(NUMBER).description("게시물 좋아요 개수"),
                fieldWithPath("liked").type(BOOLEAN).description("게시물 좋아요 여부")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(ACCESS_TOKEN);
        verify(oAuthService, times(1))
            .findRequestUserByToken(ACCESS_TOKEN);
        verify(postService, times(1))
            .unlike(loginUser, postId);
    }

    @DisplayName("사용자는 좋아요한 게시물을 중복 좋아요 추가 할 수 없다. - 실패")
    @Test
    void likePost_DuplicatedLike_ExceptionThrown() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");
        Long postId = 1L;

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postService.like(any(AppUser.class), anyLong()))
            .willThrow(new DuplicatedLikeException());

        // when
        ResultActions perform =
            mockMvc.perform(put("/api/posts/{postId}/likes", postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("P0003"));

        // documentation
        perform.andDo(document("post-likePost-duplicatedLike",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + ACCESS_TOKEN)
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(ACCESS_TOKEN);
        verify(oAuthService, times(1))
            .findRequestUserByToken(ACCESS_TOKEN);
        verify(postService, times(1))
            .like(loginUser, postId);
    }

    @DisplayName("사용자는 좋아요 하지 않은 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlikePost_unlikePost_ExceptionThrown() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");
        Long postId = 1L;

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);
        given(postService.unlike(any(AppUser.class), anyLong()))
            .willThrow(new CannotUnlikeException());

        // when
        ResultActions perform =
            mockMvc.perform(delete("/api/posts/{postId}/likes", postId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("P0004"));

        // documentation
        perform.andDo(document("post-unlikePost-unlikedPost",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer " + ACCESS_TOKEN)
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(ACCESS_TOKEN);
        verify(oAuthService, times(1))
            .findRequestUserByToken(ACCESS_TOKEN);
        verify(postService, times(1))
            .unlike(loginUser, postId);
    }

    @DisplayName("게스트는 게시물을 좋아요 할 수 없다. - 실패")
    @Test
    void like_GuestUser_ExceptionThrown() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(false);

        // when
        ResultActions perform =
            mockMvc.perform(put("/api/posts/{postId}/likes", 1L));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0001"));

        // documentation
        perform.andDo(document("post-likePost-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(any());
    }

    @DisplayName("게스트는 게시물을 좋아요 취소 할 수 없다. - 실패")
    @Test
    void unlike_GuestUser_ExceptionThrown() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(false);

        // when
        ResultActions perform =
            mockMvc.perform(delete("/api/posts/{postId}/likes", 1L));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0001"));

        // documentation
        perform.andDo(document("post-unlikePost-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));

        verify(oAuthService, times(1))
            .validateToken(any());
    }

    private static class PickGit {

        public static final String GITHUB_REPO_URL = "githubRepoUrl";
        public static final String CONTENT = "content";
        public static final String TAGS = "tags";
    }

    @DisplayName("사용자는 게시물을 수정한다.")
    @Test
    void update_LoginUser_Success() throws Exception {
        // given
        LoginUser user = new LoginUser("testUser", "Bearer testToken");

        PostUpdateResponseDto updateResponseDto = PostUpdateResponseDto.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        given(postService.update(any(PostUpdateRequestDto.class)))
            .willReturn(updateResponseDto);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();
        PostUpdateResponse updateResponse = PostUpdateResponse.builder()
            .tags(List.of("java", "spring"))
            .content("hello")
            .build();

        String requestBody = objectMapper.writeValueAsString(updateRequest);
        String responseBody = objectMapper.writeValueAsString(updateResponse);

        // when
        ResultActions perform = mockMvc.perform(put("/api/posts/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

        // then
        perform
            .andExpect(status().isCreated())
            .andExpect(content().string(responseBody));

        verify(postService, times(1))
            .update(any(PostUpdateRequestDto.class));

        perform.andDo(document("post-update",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("게시물 id")
            ),
            requestFields(
                fieldWithPath("tags").type(ARRAY).description("수정할 태그들"),
                fieldWithPath("content").type(STRING).description("수정할 내용")
            ),
            responseFields(
                fieldWithPath("tags").type(ARRAY).description("수정된 태그들"),
                fieldWithPath("content").type(STRING).description("수정된 내용")
            )
        ));
    }

    @DisplayName("유효하지 않은 내용(null)으로 게시물을 수정할 수 없다. - 400 예외")
    @Test
    void update_NullContent_400Exception() throws Exception {
        // given
        LoginUser user = new LoginUser("testUser", "Bearer testToken");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content(null)
            .build();

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // when
        ResultActions perform = mockMvc.perform(put("/api/posts/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("F0001"));

        verify(postService, never())
            .update(any(PostUpdateRequestDto.class));

        perform.andDo(document("post-update-nullContent",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("게시물 id")
            ),
            requestFields(
                fieldWithPath("tags").type(ARRAY).description("수정할 태그들"),
                fieldWithPath("content").type(NULL).description("수정할 내용(null)")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    @DisplayName("유효하지 않은 내용(500자 초)으로 게시물을 수정할 수 없다. - 400 예외")
    @Test
    void update_Over500Content_400Exception() throws Exception {
        // given
        LoginUser user = new LoginUser("testUser", "Bearer testToken");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
            .tags(List.of("java", "spring"))
            .content("a".repeat(501))
            .build();

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // when
        ResultActions perform = mockMvc.perform(put("/api/posts/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("F0004"));

        verify(postService, never())
            .update(any(PostUpdateRequestDto.class));

        perform.andDo(document("post-update-Over500Content",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("게시물 id")
            ),
            requestFields(
                fieldWithPath("tags").type(ARRAY).description("수정할 태그들"),
                fieldWithPath("content").type(STRING).description("수정할 내용(500자 초과)")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    @DisplayName("사용자는 게시물을 삭제한다.")
    @Test
    void delete_LoginUser_Success() throws Exception {
        // given
        LoginUser user = new LoginUser("testUser", "Bearer testToken");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        willDoNothing()
            .given(postService)
            .delete(any(PostDeleteRequestDto.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/posts/{postId}", 1L)
            .header(HttpHeaders.AUTHORIZATION, "Bearer testToken"));

        // then
        perform
            .andExpect(status().isNoContent());

        verify(postService, times(1))
            .delete(any(PostDeleteRequestDto.class));

        perform.andDo(document("post-delete",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("게시물 id")
            )
        ));
    }
}
