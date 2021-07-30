package com.woowacourse.pickgit.unit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.AppUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.FileFactory;
import com.woowacourse.pickgit.common.factory.PostFactory;
import com.woowacourse.pickgit.config.InfrastructureTestConfiguration;
import com.woowacourse.pickgit.exception.post.CannotUnlikeException;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.exception.post.DuplicatedLikeException;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.LikeResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.PostController;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import com.woowacourse.pickgit.post.presentation.dto.response.LikeResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@Import(InfrastructureTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
@ActiveProfiles("test")
class PostControllerTest {

    private static final String USERNAME = "jipark3";
    private static final String ACCESS_TOKEN = "pickgit";
    private static final String API_ACCESS_TOKEN = "oauth.access.token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private OAuthService oAuthService;


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
            .willReturn(new PostImageUrlResponseDto(1L));

        // when
        ResultActions perform = mockMvc.perform(multipart("/api/posts")
            .file(FileFactory.getTestImage1())
            .file(FileFactory.getTestImage2())
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
            .file(FileFactory.getTestImage1())
            .file(FileFactory.getTestImage2())
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

    @DisplayName("특정 Post에 Comment을 추가한다.")
    @Test
    void addComment_ValidContent_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("kevin", "token");
        CommentResponse commentResponse = CommentResponse.builder()
            .id(1L)
            .profileImageUrl("kevin profile image url")
            .authorName(loginUser.getUsername())
            .content("test Comment")
            .isLiked(false)
            .build();
        ContentRequest commentRequest = new ContentRequest("test Comment");

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        given(postService.addComment(any(CommentRequest.class)))
            .willReturn(commentResponse);

        String requestBody = objectMapper.writeValueAsString(commentRequest);
        String responseBody = objectMapper.writeValueAsString(commentResponse);

        // when
        ResultActions perform = addCommentApi("/api/posts/{postId}/comments", 1L, requestBody);

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().string(responseBody));

        verify(postService, times(1)).addComment(any(CommentRequest.class));

        // documentation
        perform.andDo(document("comment-post",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            requestFields(
                fieldWithPath("content").type(STRING).description("댓글 내용")
            ),
            responseFields(
                fieldWithPath("id").type(NUMBER).description("댓글 id"),
                fieldWithPath("profileImageUrl").type(STRING).description("댓글 작성자 프로필 사진"),
                fieldWithPath("authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("content").type(STRING).description("댓글 내용"),
                fieldWithPath("isLiked").type(BOOLEAN).description("좋아요 여부")
            )
        ));
    }

    @DisplayName("특정 Post에 댓글 등록 실패한다. - 빈 Comment인 경우.")
    @Test
    void addComment_InValidContent_ExceptionThrown() throws Exception {
        // given
        ContentRequest commentRequest = new ContentRequest("");

        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        given(postService.addComment(any(CommentRequest.class)))
            .willThrow(new CommentFormatException());

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        // when
        ResultActions perform = addCommentApi("/api/posts/{postId}/comments", 1L, requestBody);

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("F0001"));

        verify(postService, never()).addComment(any(CommentRequest.class));

        // documentation
        perform.andDo(document("comment-post-emptyContent",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    private ResultActions addCommentApi(String url, Long postId, String requestBody)
        throws Exception {
        return mockMvc.perform(post(url, postId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody));
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("testUser", "at");

        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(loginUser);

        RepositoriesResponseDto responseDto = new RepositoriesResponseDto(List.of(
            new RepositoryResponseDto("pick", "https://github.com/jipark3/pick"),
            new RepositoryResponseDto("git", "https://github.com/jipark3/git")
        ));
        String repositories = objectMapper.writeValueAsString(responseDto.getRepositories());

        given(postService.showRepositories(any(RepositoryRequestDto.class)))
            .willReturn(responseDto);

        // then
        ResultActions perform = mockMvc
            .perform(get("/api/github/${userName}/repositories", USERNAME)
                .header(HttpHeaders.AUTHORIZATION, API_ACCESS_TOKEN))
            .andExpect(status().isOk())
            .andExpect(content().string(repositories));

        //documentation
        perform.andDo(document("repositories-loggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("userName").description("유저 이름")
            ),
            responseFields(
                fieldWithPath("[].name").type(STRING).description("레포지토리 이름"),
                fieldWithPath("[].html_url").type(STRING).description("레포지토리 주소")
            )
        ));
    }

    @DisplayName("비로그인 유저는 홈피드를 조회할 수 있다.")
    @Test
    void readHomeFeed_GuestUser_Success() throws Exception {
        // given
        given(postService.readHomeFeed(any(HomeFeedRequest.class)))
            .willReturn(PostFactory.mockPostResponseDtos());

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
                fieldWithPath("[].comments[].isLiked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].isLiked").type(BOOLEAN).description("좋아요 여부")
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
        given(postService.readHomeFeed(any(HomeFeedRequest.class)))
            .willReturn(PostFactory.mockPostResponseDtos());

        // when
        ResultActions perform = mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("limit", "3")
            .header(HttpHeaders.AUTHORIZATION, API_ACCESS_TOKEN));

        // then
        perform
            .andExpect(status().isOk());

        perform.andDo(document("post-homefeed-LoggedIn",
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
                fieldWithPath("[].comments[].isLiked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].isLiked").type(BOOLEAN).description("좋아요 여부")
            )
            )
        );
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
                new LikeResponse(likeResponseDto.getLikeCount(), likeResponseDto.isLiked())
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
                fieldWithPath("likeCount").type(NUMBER).description("게시물 좋아요 개수"),
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
                new LikeResponse(likeResponseDto.getLikeCount(), likeResponseDto.isLiked())
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
                fieldWithPath("likeCount").type(NUMBER).description("게시물 좋아요 개수"),
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
        public static final String IMAGES = "images";
    }
}
