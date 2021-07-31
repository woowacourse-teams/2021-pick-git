package com.woowacourse.pickgit.unit.user.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
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

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.user.GuestUser;
import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.user.application.UserService;
import com.woowacourse.pickgit.user.application.dto.request.AuthUserRequestDto;
import com.woowacourse.pickgit.user.application.dto.request.UserSearchRequestDto;
import com.woowacourse.pickgit.user.application.dto.response.UserSearchResponseDto;
import com.woowacourse.pickgit.user.presentation.UserSearchController;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureRestDocs
@WebMvcTest(UserSearchController.class)
@ActiveProfiles("test")
public class UserSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private UserService userService;

    @DisplayName("로그인 - 검색 키워드와 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉한 여부 boolean)")
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
        given(oAuthService.findRequestUserByToken(any()))
            .willReturn(new LoginUser("pick-git", "token"));
        given(userService.searchUser(any(AuthUserRequestDto.class), any(UserSearchRequestDto.class)))
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
                containsInRelativeOrder(true, false, false)));
        verify(oAuthService, times(1))
            .findRequestUserByToken(any());
        verify(userService, times(1))
            .searchUser(any(AuthUserRequestDto.class), any(UserSearchRequestDto.class));

        // restdocs
        perform.andDo(document("search-user-LoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("유저 이름"),
                fieldWithPath("[].following").type(BOOLEAN).description("로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }

    @DisplayName("비 로그인 - 검색 키워드와 유사한 이름을 가진 유저를 검색할 수 있다. (팔로잉 필드 null)")
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
        given(userService.searchUser(any(AuthUserRequestDto.class), any(UserSearchRequestDto.class)))
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
            .searchUser(any(AuthUserRequestDto.class), any(UserSearchRequestDto.class));

        // restdocs
        perform.andDo(document("search-user-unLoggedIn",
            getDocumentRequest(),
            getDocumentResponse(),
            requestParameters(
                parameterWithName("keyword").description("검색 키워드"),
                parameterWithName("page").description("page"),
                parameterWithName("limit").description("limit")
            ),
            responseFields(
                fieldWithPath("[].imageUrl").type(STRING).description("유저 이미지 url"),
                fieldWithPath("[].username").type(STRING).description("유저 이름"),
                fieldWithPath("[].following").type(NULL).description("비 로그인시 검색된 유저 팔로잉 여부")
            )
        ));
    }
}
