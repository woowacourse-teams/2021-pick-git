package com.woowacourse.pickgit.unit.comment.presentation;

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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.comment.application.dto.request.CommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.presentation.dto.request.ContentRequest;
import com.woowacourse.pickgit.exception.post.CommentFormatException;
import com.woowacourse.pickgit.unit.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class CommentControllerTest extends ControllerTest {

    @DisplayName("?????? Post??? Comment??? ????????????.")
    @Test
    void addComment_ValidContent_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("kevin", "token");
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
            .id(1L)
            .profileImageUrl("kevin profile image url")
            .authorName(loginUser.getUsername())
            .content("test Comment")
            .liked(false)
            .build();
        ContentRequest commentRequest = new ContentRequest("test Comment");

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        given(commentService.addComment(any(CommentRequestDto.class)))
            .willReturn(commentResponseDto);

        String requestBody = objectMapper.writeValueAsString(commentRequest);
        String responseBody = objectMapper.writeValueAsString(commentResponseDto);

        // when
        ResultActions perform = addCommentApi("/api/posts/{postId}/comments", 1L, requestBody);

        // then
        perform
            .andExpect(status().isOk())
            .andExpect(content().string(responseBody));

        verify(commentService, times(1)).addComment(any(CommentRequestDto.class));

        // documentation
        perform.andDo(document("comment-post",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("????????? id")
            ),
            requestFields(
                fieldWithPath("content").type(STRING).description("?????? ??????")
            ),
            responseFields(
                fieldWithPath("id").type(NUMBER).description("?????? id"),
                fieldWithPath("profileImageUrl").type(STRING).description("?????? ????????? ????????? ??????"),
                fieldWithPath("authorName").type(STRING).description("????????? ??????"),
                fieldWithPath("content").type(STRING).description("?????? ??????"),
                fieldWithPath("liked").type(BOOLEAN).description("????????? ??????")
            )
        ));
    }

    @DisplayName("?????? Post??? ?????? ?????? ????????????. - ??? Comment??? ??????.")
    @Test
    void addComment_InValidContent_ExceptionThrown() throws Exception {
        // given
        ContentRequest commentRequest = new ContentRequest("");

        LoginUser loginUser = new LoginUser("kevin", "token");
        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        given(commentService.addComment(any(CommentRequestDto.class)))
            .willThrow(new CommentFormatException());

        String requestBody = objectMapper.writeValueAsString(commentRequest);

        // when
        ResultActions perform = addCommentApi("/api/posts/{postId}/comments", 1L, requestBody);

        // then
        perform
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errorCode").value("F0001"));

        verify(commentService, never()).addComment(any(CommentRequestDto.class));

        // documentation
        perform.andDo(document("comment-post-emptyContent",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("????????? id")
            ),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("?????? ??????")
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
}
