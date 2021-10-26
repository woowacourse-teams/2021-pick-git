package com.woowacourse.pickgit.unit.post.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
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
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.common.factory.UserFactory;
import com.woowacourse.pickgit.post.application.dto.PostDtoAssembler;
import com.woowacourse.pickgit.post.application.dto.request.SearchPostsRequestDto;
import com.woowacourse.pickgit.post.domain.Post;
import com.woowacourse.pickgit.tag.domain.Tag;
import com.woowacourse.pickgit.unit.ControllerTest;
import com.woowacourse.pickgit.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

class PostFeedControllerTest_searchPosts extends ControllerTest {

    private static final String API_ACCESS_TOKEN = "oauth.access.token";

    private User user;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        user = UserFactory.user();

        post1 = Post.builder()
            .id(1L)
            .author(user)
            .content("content")
            .comments(List.of(new Comment(1L, "content", user, null)))
            .tags(new Tag("tag1"), new Tag("tag3"))
            .githubRepoUrl("github url")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        post2 = Post.builder()
            .id(2L)
            .author(user)
            .content("content")
            .comments(List.of(new Comment(2L, "content", user, null)))
            .tags(new Tag("tag2"), new Tag("tag4"))
            .githubRepoUrl("github url")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @DisplayName("게스트는 tag로 게시물을 검색할 수 있다.")
    @Test
    void search() throws Exception {
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postFeedService.search(any(SearchPostsRequestDto.class), any(Pageable.class)))
            .willReturn(PostDtoAssembler.postResponseDtos(null,  List.of(post1, post2)));

        ResultActions perform = mockMvc.perform(
            get("/api/search/posts")
                .param("type", "tags")
                .param("keyword", "tag1 tag2")
                .param("page", "0")
                .param("limit", "3"));

        perform.andExpect(status().isOk());
        perform.andDo(document("search-tag-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("type").description("검색 타입"),
                parameterWithName("keyword").description("검색 키워드"),
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
                fieldWithPath("[].liked").type(NULL).description("좋아요 여부")
            )
        ));
    }

    @DisplayName("유저는 tag로 게시물을 검색할 수 있다.")
    @Test
    void search2() throws Exception {
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(postFeedService.search(any(SearchPostsRequestDto.class), any(Pageable.class)))
            .willReturn(PostDtoAssembler.postResponseDtos(user, List.of(post1, post2)));

        ResultActions perform = mockMvc.perform(
            get("/api/search/posts")
                .param("type", "tags")
                .param("keyword", "tag1 tag2")
                .param("page", "0")
                .param("limit", "3")
                .header(HttpHeaders.AUTHORIZATION, API_ACCESS_TOKEN));

        perform.andExpect(status().isOk());
        perform.andDo(document("search-tag-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            requestParameters(
                parameterWithName("type").description("검색 타입"),
                parameterWithName("keyword").description("검색 키워드"),
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
        ));
    }
}
