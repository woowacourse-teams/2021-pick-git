package com.woowacourse.pickgit.unit.comment.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.comment.application.CommentService;
import com.woowacourse.pickgit.comment.application.dto.request.QueryCommentRequestDto;
import com.woowacourse.pickgit.comment.application.dto.response.CommentResponseDto;
import com.woowacourse.pickgit.comment.domain.Comment;
import com.woowacourse.pickgit.comment.presentation.CommentController;
import com.woowacourse.pickgit.common.factory.UserFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@WebMvcTest(CommentController.class)
public class CommentControllerTest_queryComments {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private OAuthService oAuthService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("사용자는 특정 Post의 Comment를 요청할 수 있다.")
    @Test
    void queryComments_UserCanRequestCommentsOfSpecificPost() throws Exception {
        given(oAuthService.validateToken(anyString())).willReturn(true);
        given(oAuthService.findRequestUserByToken(anyString()))
            .willReturn(new LoginUser("testUser", "token"));
        given(commentService.queryComments(any(QueryCommentRequestDto.class)))
            .willReturn(createCommentResponseDtos(true));

        ResultActions perform = mockMvc.perform(
            get("/api/posts/{postId}/comments?page={page}&limit={limit}", 1L, 0, 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer Test"));

        perform
            .andExpect(status().isOk())
            .andExpect(
                content().string(objectMapper.writeValueAsString(createCommentResponseDtos(true))));

        verify(commentService, times(1))
            .queryComments(any(QueryCommentRequestDto.class));

        createDocument("comment-queryComments-loginMember", true, perform);
    }

    @DisplayName("게스트는 특정 Post의 Comment를 요청할 수 있다.")
    @Test
    void guestComments_UserCanRequestCommentsOfSpecificPost() throws Exception {
        given(oAuthService.validateToken(anyString())).willReturn(true);
        given(oAuthService.findRequestUserByToken(null))
            .willReturn(new LoginUser("testUser", "token"));
        given(commentService.queryComments(any(QueryCommentRequestDto.class)))
            .willReturn(createCommentResponseDtos(null));

        ResultActions perform = mockMvc.perform(
            get("/api/posts/{postId}/comments?page={page}&limit={limit}", 1L, 0, 1)
        );

        perform
            .andExpect(status().isOk())
            .andExpect(
                content().string(objectMapper.writeValueAsString(createCommentResponseDtos(null))));

        verify(commentService, times(1))
            .queryComments(any(QueryCommentRequestDto.class));

        createDocument("comment-queryComments-guest", null, perform);
    }

    private void createDocument(String documentName, Boolean liked, ResultActions perform)
        throws Exception {
        perform.andDo(document(documentName,
            getDocumentRequest(),
            getDocumentResponse(),
            pathParameters(
                parameterWithName("postId").description("포스트 id")
            ),
            requestParameters(
                parameterWithName("page").description("페이지"),
                parameterWithName("limit").description("가지고 올 페이지 수")
            ),
            responseFields(
                fieldWithPath("[].id").type(NUMBER).description("댓글 id"),
                fieldWithPath("[].profileImageUrl").type(STRING).description("댓글 작성자 프로필 사진"),
                fieldWithPath("[].authorName").type(STRING).description("작성자 이름"),
                fieldWithPath("[].content").type(STRING).description("댓글 내용"),
                fieldWithPath("[].liked").type(liked).description("좋아요 여부")
            )
        ));
    }

    private List<CommentResponseDto> createCommentResponseDtos(Boolean liked) {
        return createComments().stream()
            .map(comment -> createCommentResponseDto(comment, liked))
            .collect(toList());
    }

    private List<Comment> createComments() {
        return List.of(
            new Comment(1L, "testContent1", UserFactory.user()),
            new Comment(2L, "testContent2", UserFactory.user())
        );
    }

    private CommentResponseDto createCommentResponseDto(Comment comment, Boolean liked) {
        return CommentResponseDto.builder()
            .id(comment.getId())
            .profileImageUrl(comment.getProfileImageUrl())
            .authorName(comment.getAuthorName())
            .content(comment.getContent())
            .liked(liked)
            .build();
    }
}
