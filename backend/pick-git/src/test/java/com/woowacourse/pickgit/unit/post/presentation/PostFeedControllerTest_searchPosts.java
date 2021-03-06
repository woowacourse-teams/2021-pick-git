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

    @DisplayName("???????????? tag??? ???????????? ????????? ??? ??????.")
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
                parameterWithName("type").description("?????? ??????"),
                parameterWithName("keyword").description("?????? ?????????"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("????????? id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("????????? ?????? ??????"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("????????? ??????"),
                fieldWithPath("[].content").type(STRING).description("????????? ??????"),
                fieldWithPath("[].authorName").type(STRING).description("????????? ??????"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("????????? ????????? ??????"),
                fieldWithPath("[].likesCount").type(NUMBER).description("????????? ???"),
                fieldWithPath("[].tags").type(ARRAY).description("?????? ??????"),
                fieldWithPath("[].createdAt").type(STRING).description("??? ?????? ??????"),
                fieldWithPath("[].updatedAt").type(STRING).description("????????? ??? ?????? ??????"),
                fieldWithPath("[].comments").type(ARRAY).description("?????? ??????"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("?????? ?????????"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("?????? ????????? ????????? ??????"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("?????? ????????? ??????"),
                fieldWithPath("[].comments[].content").type(STRING).description("?????? ??????"),
                fieldWithPath("[].comments[].liked").type(BOOLEAN).description("?????? ????????? ??????"),
                fieldWithPath("[].liked").type(NULL).description("????????? ??????")
            )
        ));
    }

    @DisplayName("????????? tag??? ???????????? ????????? ??? ??????.")
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
                parameterWithName("type").description("?????? ??????"),
                parameterWithName("keyword").description("?????? ?????????"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("????????? id"),
                fieldWithPath("[].imageUrls").type(ARRAY).description("????????? ?????? ??????"),
                fieldWithPath("[].githubRepoUrl").type(STRING).description("????????? ??????"),
                fieldWithPath("[].content").type(STRING).description("????????? ??????"),
                fieldWithPath("[].authorName").type(STRING).description("????????? ??????"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("????????? ????????? ??????"),
                fieldWithPath("[].likesCount").type(NUMBER).description("????????? ???"),
                fieldWithPath("[].tags").type(ARRAY).description("?????? ??????"),
                fieldWithPath("[].createdAt").type(STRING).description("??? ?????? ??????"),
                fieldWithPath("[].updatedAt").type(STRING).description("????????? ??? ?????? ??????"),
                fieldWithPath("[].comments").type(ARRAY).description("?????? ??????"),
                fieldWithPath("[].comments[].id").type(NUMBER).description("?????? ?????????"),
                fieldWithPath("[].comments[].profileImageUrl").type(STRING)
                    .description("?????? ????????? ????????? ??????"),
                fieldWithPath("[].comments[].authorName").type(STRING).description("?????? ????????? ??????"),
                fieldWithPath("[].comments[].content").type(STRING).description("?????? ??????"),
                fieldWithPath("[].comments[].liked").type(BOOLEAN).description("?????? ????????? ??????"),
                fieldWithPath("[].liked").type(BOOLEAN).description("????????? ??????")
            )
        ));
    }
}
