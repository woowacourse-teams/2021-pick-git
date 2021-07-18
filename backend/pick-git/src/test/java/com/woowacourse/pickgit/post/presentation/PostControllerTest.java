package com.woowacourse.pickgit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.FileFactory;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.post.PostTestConfiguration;
import com.woowacourse.pickgit.post.application.PostFactory;
import com.woowacourse.pickgit.post.application.PostService;
import com.woowacourse.pickgit.post.application.dto.CommentResponse;
import com.woowacourse.pickgit.post.application.dto.request.PostRequestDto;
import com.woowacourse.pickgit.post.application.dto.request.RepositoryRequestDto;
import com.woowacourse.pickgit.post.application.dto.response.PostImageUrlResponseDto;
import com.woowacourse.pickgit.post.application.dto.response.RepositoriesResponseDto;
import com.woowacourse.pickgit.post.domain.dto.RepositoryResponseDto;
import com.woowacourse.pickgit.post.presentation.dto.request.CommentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.post.presentation.dto.request.HomeFeedRequest;
import java.util.List;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@AutoConfigureRestDocs
@Import({PostTestConfiguration.class})
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

    private LoginUser user;
    private List<MultipartFile> images;
    private String githubRepoUrl;
    private String[] tags;
    private String postContent;

    @BeforeEach
    void setUp() {
        user = new LoginUser(USERNAME, ACCESS_TOKEN);
        images = List.of(
            FileFactory.getTestImage1(),
            FileFactory.getTestImage2()
        );
        githubRepoUrl = "https://github.com/woowacourse-teams/2021-pick-git/";
        tags = new String[]{"java", "spring"};
        postContent = "pickgit";
    }


    @DisplayName("게시물을 작성할 수 있다. - 사용자")
    @Test
    void write_LoginUser_Success() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        given(postService.write(any(PostRequestDto.class)))
            .willReturn(new PostImageUrlResponseDto(1L));

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("githubRepoUrl", githubRepoUrl);
        multiValueMap.add("content", postContent);

        // then
        ResultActions perform = mockMvc.perform(multipart("/api/posts")
            .file(new MockMultipartFile("images", "testImage1.jpg",
                ContentType.IMAGE_JPEG.getMimeType(), "testimage1Binary".getBytes()))
            .file(new MockMultipartFile("images", "testImage2.jpg",
                ContentType.IMAGE_JPEG.getMimeType(), "testimage2Binary".getBytes()))
            .params(multiValueMap)
            .param("tags", this.tags)
            .header(HttpHeaders.AUTHORIZATION, user.getAccessToken()))
            .andExpect(status().isCreated());

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
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willCallRealMethod();

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("githubRepoUrl", githubRepoUrl);
        multiValueMap.add("content", postContent);

        // then
        ResultActions perform = mockMvc.perform(multipart("/api/posts")
            .file(new MockMultipartFile("images", "testImage1.jpg",
                ContentType.IMAGE_JPEG.getMimeType(), "testimage1Binary".getBytes()))
            .file(new MockMultipartFile("images", "testImage2.jpg",
                ContentType.IMAGE_JPEG.getMimeType(), "testimage2Binary".getBytes()))

            .params(multiValueMap)
            .param("tags", tags)
            .header(HttpHeaders.AUTHORIZATION, "Bad AccessToken"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0002"));

        perform.andDo(document("posts-post-guest",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bad Bearer token")
            ),
            requestPartBody("images"),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }

    @DisplayName("특정 Post에 댓글을 추가한다.")
    @Test
    void addComment_ValidContent_Success() throws Exception {
        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);

        String url = "/api/posts/{postId}/comments";
        CommentResponse commentResponseDto =
            new CommentResponse(1L, "kevin", "test comment", false);

        String requestBody = objectMapper.writeValueAsString(new ContentRequest("test"));
        String responseBody = objectMapper.writeValueAsString(commentResponseDto);
        given(postService.addComment(any(CommentRequest.class)))
            .willReturn(commentResponseDto);

        ResultActions perform = addCommentApi(url, requestBody)
            .andExpect(status().isOk())
            .andExpect(content().string(responseBody));

        verify(postService, times(1)).addComment(any(CommentRequest.class));

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
                fieldWithPath("authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("content").type(STRING).description("댓글 내용"),
                fieldWithPath("isLiked").type(BOOLEAN).description("좋아요 여부")
            )
        ));
    }

    @DisplayName("특정 Post에 댓글 등록 실패한다. - 빈 댓글인 경우.")
    @Test
    void addComment_InValidContent_ExceptionThrown() throws Exception {
        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);

        String url = "/api/posts/{postId}/comments";
        String requestBody = objectMapper.writeValueAsString("");
        given(postService.addComment(any(CommentRequest.class)))
            .willThrow(new CommentFormatException());

        ResultActions perform = addCommentApi(url, requestBody)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("F0001"));
        verify(postService, never()).addComment(any(CommentRequest.class));

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

    private ResultActions addCommentApi(String url, String requestBody) throws Exception {
        return mockMvc.perform(post(url, 1)
            .header("Authorization", "Bearer test")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody));
    }

    @DisplayName("사용자는 Repository 목록을 가져올 수 있다.")
    @Test
    void showRepositories_LoginUser_Success() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);

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
        given(postService.readHomeFeed(any(HomeFeedRequest.class)))
            .willReturn(PostFactory.mockPostResponseDtos());

        ResultActions perform = mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("limit", "3"))
            .andExpect(status().isOk());

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
        given(oAuthService.validateToken(any()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(user);
        given(postService.readHomeFeed(any(HomeFeedRequest.class)))
            .willReturn(PostFactory.mockPostResponseDtos());

        ResultActions perform = mockMvc.perform(get("/api/posts")
            .param("page", "0")
            .param("limit", "3")
            .header(HttpHeaders.AUTHORIZATION, API_ACCESS_TOKEN))
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
                fieldWithPath("[].comments[].authorName").type(STRING).description("댓글 작성자 이름"),
                fieldWithPath("[].comments[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].comments[].isLiked").type(BOOLEAN).description("댓글 좋아요 여부"),
                fieldWithPath("[].isLiked").type(BOOLEAN).description("좋아요 여부")
            )
            )
        );
    }
}
