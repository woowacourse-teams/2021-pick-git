package com.woowacourse.pickgit.unit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.unit.ControllerTest;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserForUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class UserSearchControllerTest extends ControllerTest {

    @DisplayName("????????? - ?????? ???????????? ????????? ????????? ?????? ????????? ????????? ??? ??????. (???????????? ?????? boolean)")
    @Test
    void searchUser_LoginUser_Success() throws Exception {
        // given
        String searchKeyword = "bing";
        List<UserSearchResponseDto> userSearchRespons = List.of(
            new UserSearchResponseDto("image1", "bingbingdola", true),
            new UserSearchResponseDto("image2", "bing", false),
            new UserSearchResponseDto("image3", "bbbbbing", false)
        );

        // mock
        given(oAuthService.validateToken("token")).willReturn(true);
        given(oAuthService.findRequestUserByToken("token"))
            .willReturn(new LoginUser("pick-git", "token"));
        given(userService.searchUser(
            any(AuthUserForUserRequestDto.class),
            anyString(),
            any(Pageable.class))
        ).willReturn(userSearchRespons);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/search/users")
                    .param("keyword", searchKeyword)
                    .param("page", "0")
                    .param("limit", "5")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"));

        // then
        perform
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl",
                contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username",
                containsInRelativeOrder("bingbingdola", "bing", "bbbbbing")))
            .andExpect(jsonPath("$[*].following",
                containsInRelativeOrder(true, false, false)));

        verify(oAuthService, times(1)).validateToken("token");
        verify(oAuthService, times(1))
            .findRequestUserByToken("token");
        verify(userService, times(1))
            .searchUser(any(AuthUserForUserRequestDto.class), anyString(), any(Pageable.class));

        // restdocs
        perform.andDo(document("search-user-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("?????? ?????????"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("?????? ????????? url"),
                fieldWithPath("[].username").type(STRING).description("?????? ??????"),
                fieldWithPath("[].following").type(BOOLEAN).description("???????????? ????????? ?????? ????????? ??????")
            )
        ));
    }

    @DisplayName("??? ????????? - ?????? ???????????? ????????? ????????? ?????? ????????? ????????? ??? ??????. (????????? ?????? null)")
    @Test
    void searchUser_GuestUser_Success() throws Exception {
        // given
        String searchKeyword = "bing";
        List<UserSearchResponseDto> userSearchRespons = List.of(
            new UserSearchResponseDto("image1", "bingbingdola", null),
            new UserSearchResponseDto("image2", "bing", null),
            new UserSearchResponseDto("image3", "bbbbbing", null)
        );

        // mock
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new GuestUser());
        given(userService
            .searchUser(any(AuthUserForUserRequestDto.class), anyString(), any(Pageable.class)))
            .willReturn(userSearchRespons);

        // when
        ResultActions perform = mockMvc
            .perform(
                get("/api/search/users")
                    .param("keyword", searchKeyword)
                    .param("page", "0")
                    .param("limit", "5")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.ALL));

        // then
        perform
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].imageUrl",
                contains("image1", "image2", "image3")))
            .andExpect(jsonPath("$[*].username",
                containsInRelativeOrder("bingbingdola", "bing", "bbbbbing")))
            .andExpect(jsonPath("$[*].following",
                containsInRelativeOrder(nullValue(), nullValue(), nullValue()
                )));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(userService, times(1))
            .searchUser(any(AuthUserForUserRequestDto.class), anyString(), any(Pageable.class));

        // restdocs
        perform.andDo(document("search-user-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("?????? ?????????"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("?????? ????????? url"),
                fieldWithPath("[].username").type(STRING).description("?????? ??????"),
                fieldWithPath("[].following").type(NULL).description("??? ???????????? ????????? ?????? ????????? ??????")
            )
        ));
    }
}
