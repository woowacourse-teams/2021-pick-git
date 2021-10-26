package com.woowacourse.pickgit.unit.comment.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.comment.application.dto.request.CommentDeleteRequestDto;
import com.woowacourse.pickgit.unit.ControllerTest;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

public class CommentControllerTest_delete extends ControllerTest {

    @DisplayName("사용자는 댓글을 삭제한다.")
    @Test
    void delete_LoginUser_Success() throws Exception {
        // given
        LoginUser loginUser = new LoginUser("dani", "oauth.access.token");

        given(oAuthService.validateToken(anyString()))
            .willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(loginUser);
        willDoNothing()
            .given(commentService)
            .delete(any(CommentDeleteRequestDto.class));

        // when
        ResultActions perform = mockMvc
            .perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L)
                .header(HttpHeaders.AUTHORIZATION, "Bearer testToken"));

        // then
        perform
            .andExpect(status().isNoContent());

        verify(commentService, times(1))
            .delete(any(CommentDeleteRequestDto.class));

        perform.andDo(document("comment-delete-loggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestHeaders(
                headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token")
            ),
            pathParameters(
                parameterWithName("postId").description("게시물 id"),
                parameterWithName("commentId").description("댓글 id")
            )
        ));
    }

    @DisplayName("게스트는 댓글을 삭제할 수 없다. - 401 예외")
    @Test
    void delete_GuestUser_401Exception() throws Exception {
        // given
        given(oAuthService.validateToken(any()))
            .willReturn(false);

        // when
        ResultActions perform = mockMvc
            .perform(delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L));

        // then
        perform
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("errorCode").value("A0001"));

        perform.andDo(document("comment-delete-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            responseFields(
                fieldWithPath("errorCode").type(STRING).description("에러 코드")
            )
        ));
    }
}
