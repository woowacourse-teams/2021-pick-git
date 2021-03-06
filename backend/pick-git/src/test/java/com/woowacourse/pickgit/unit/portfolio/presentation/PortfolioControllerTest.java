package com.woowacourse.pickgit.unit.portfolio.presentation;

import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentRequest;
import static com.woowacourse.pickgit.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.woowacourse.pickgit.authentication.domain.user.LoginUser;
import com.woowacourse.pickgit.common.factory.PortfolioFactory;
import com.woowacourse.pickgit.exception.authentication.UnauthorizedException;
import com.woowacourse.pickgit.exception.portfolio.NoSuchPortfolioException;
import com.woowacourse.pickgit.portfolio.application.dto.request.PortfolioRequestDto;
import com.woowacourse.pickgit.portfolio.application.dto.request.UserDto;
import com.woowacourse.pickgit.unit.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

public class PortfolioControllerTest extends ControllerTest {

    private static final String ACCESS_TOKEN = "accessToken";

    @DisplayName("????????????")
    @Nested
    class Authorized {

        @DisplayName("?????????????????? ????????????.")
        @Nested
        class read {

            @DisplayName("?????? ??????????????? - ??????")
            @Test
            void read_LoginUserWithMine_Success() throws Exception {
                // given
                LoginUser dani = new LoginUser("dani", ACCESS_TOKEN);

                given(oAuthService.validateToken(any()))
                    .willReturn(true);
                given(oAuthService.findRequestUserByToken(any()))
                    .willReturn(dani);
                given(portfolioService.read(anyString(), any(UserDto.class)))
                    .willReturn(PortfolioFactory.mockPortfolioResponseDto(dani.getUsername()));

                String portfolio = objectMapper.writeValueAsString(
                    PortfolioFactory.mockPortfolioResponse(dani.getUsername())
                );

                // when
                ResultActions perform = mockMvc.perform(get("/api/portfolios/{username}", dani.getUsername())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

                // then
                perform
                    .andExpect(status().isOk())
                    .andExpect(content().string(portfolio));

                verify(oAuthService, times(1))
                    .validateToken(ACCESS_TOKEN);
                verify(oAuthService, times(1))
                    .findRequestUserByToken(ACCESS_TOKEN);
                verify(portfolioService, times(1))
                    .read(eq(dani.getUsername()), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-readMine-loggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("Bearer " + ACCESS_TOKEN)
                    ),
                    pathParameters(
                        parameterWithName("username").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    )
                ));
            }

            @DisplayName("?????? ???????????????, ???????????? ?????? - ??????")
            @Test
            void read_LoginUserWithYoursWithExisting_Success() throws Exception {
                // given
                LoginUser dani = new LoginUser("dani", ACCESS_TOKEN);
                String dada = "dada";

                given(oAuthService.validateToken(any()))
                    .willReturn(true);
                given(oAuthService.findRequestUserByToken(any()))
                    .willReturn(dani);
                given(portfolioService.read(anyString(), any(UserDto.class)))
                    .willReturn(PortfolioFactory.mockPortfolioResponseDto(dada));

                String portfolio = objectMapper.writeValueAsString(
                    PortfolioFactory.mockPortfolioResponse(dada)
                );

                // when
                ResultActions perform = mockMvc.perform(get("/api/portfolios/{username}", dada)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

                // then
                perform
                    .andExpect(status().isOk())
                    .andExpect(content().string(portfolio));

                verify(oAuthService, times(1))
                    .validateToken(ACCESS_TOKEN);
                verify(oAuthService, times(1))
                    .findRequestUserByToken(ACCESS_TOKEN);
                verify(portfolioService, times(1))
                    .read(eq(dada), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-readYoursWithExisting-loggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("Bearer " + ACCESS_TOKEN)
                    ),
                    pathParameters(
                        parameterWithName("username").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    )
                ));
            }

            @DisplayName("?????? ???????????????, ???????????? ?????? ?????? - ??????")
            @Test
            void read_LoginUserWithYoursWithNotExisting_400Exception() throws Exception {
                // given
                LoginUser dani = new LoginUser("dani", ACCESS_TOKEN);
                String dada = "dada";

                given(oAuthService.validateToken(any()))
                    .willReturn(true);
                given(oAuthService.findRequestUserByToken(any()))
                    .willReturn(dani);
                given(portfolioService.read(anyString(), any(UserDto.class)))
                    .willThrow(new NoSuchPortfolioException());

                // when
                ResultActions perform = mockMvc.perform(get("/api/portfolios/{username}", dada)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN));

                // then
                perform
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("R0001"));

                verify(oAuthService, times(1))
                    .validateToken(ACCESS_TOKEN);
                verify(oAuthService, times(1))
                    .findRequestUserByToken(ACCESS_TOKEN);
                verify(portfolioService, times(1))
                    .read(eq(dada), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-readYoursWithNotExisting-loggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("Bearer " + ACCESS_TOKEN)
                    ),
                    pathParameters(
                        parameterWithName("username").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("errorCode").description("?????? ??????")
                    )
                ));
            }
        }

        @DisplayName("?????????????????? ????????????.")
        @Nested
        class update {

            @DisplayName("?????? ??????????????? - ??????")
            @Test
            void update_LoginUserWithMine_Success() throws Exception {
                // given
                LoginUser dani = new LoginUser("dani", ACCESS_TOKEN);

                given(oAuthService.validateToken(any()))
                    .willReturn(true);
                given(oAuthService.findRequestUserByToken(any()))
                    .willReturn(dani);
                given(portfolioService.update(any(PortfolioRequestDto.class), any(UserDto.class)))
                    .willReturn(PortfolioFactory.mockPortfolioResponseDto(dani.getUsername()));

                String portfolio = objectMapper.writeValueAsString(
                    PortfolioFactory.mockPortfolioResponse(dani.getUsername())
                );

                // when
                ResultActions perform = mockMvc.perform(put("/api/portfolios")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(
                        PortfolioFactory.mockPortfolioRequestWithNewProfileAndIntroduction(
                            dani.getUsername()
                        )
                    ))
                );

                // then
                perform
                    .andExpect(status().isOk())
                    .andExpect(content().string(portfolio));

                verify(oAuthService, times(1))
                    .validateToken(ACCESS_TOKEN);
                verify(oAuthService, times(1))
                    .findRequestUserByToken(ACCESS_TOKEN);
                verify(portfolioService, times(1))
                    .update(any(PortfolioRequestDto.class), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-updateMine-loggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("Bearer " + ACCESS_TOKEN)
                    ),
                    requestFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    )
                ));
            }

            @DisplayName("?????? ??????????????? - ??????")
            @Test
            void update_LoginUserWithYours_401Exception() throws Exception {
                // given
                LoginUser dani = new LoginUser("dani", ACCESS_TOKEN);
                String dada = "dada";

                given(oAuthService.validateToken(any()))
                    .willReturn(true);
                given(oAuthService.findRequestUserByToken(any()))
                    .willReturn(dani);
                given(portfolioService.update(any(PortfolioRequestDto.class), any(UserDto.class)))
                    .willThrow(new UnauthorizedException());

                // when
                ResultActions perform = mockMvc.perform(put("/api/portfolios")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(
                        PortfolioFactory.mockPortfolioRequestWithNewProfileAndIntroduction(
                            dada
                        )
                    ))
                );

                // then
                perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("errorCode").value("A0002"));

                verify(oAuthService, times(1))
                    .validateToken(ACCESS_TOKEN);
                verify(oAuthService, times(1))
                    .findRequestUserByToken(ACCESS_TOKEN);
                verify(portfolioService, times(1))
                    .update(any(PortfolioRequestDto.class), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-updateYours-loggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION)
                            .description("Bearer " + ACCESS_TOKEN)
                    ),
                    requestFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("errorCode").description("?????? ??????")
                    )
                ));
            }
        }
    }

    @DisplayName("????????????")
    @Nested
    class Unauthorized {

        @DisplayName("?????????????????? ????????????.")
        @Nested
        class read {

            @DisplayName("?????? ???????????????, ???????????? ?????? - ??????")
            @Test
            void read_GuestUserWithYoursWithExisting_Success() throws Exception {
                // given
                String dani = "dani";

                given(oAuthService.findRequestUserByToken(any()))
                    .willCallRealMethod();
                given(portfolioService.read(anyString(), any(UserDto.class)))
                    .willReturn(PortfolioFactory.mockPortfolioResponseDto(dani));


                String portfolio = objectMapper.writeValueAsString(
                    PortfolioFactory.mockPortfolioResponse(dani)
                );

                // when
                ResultActions perform = mockMvc.perform(get("/api/portfolios/{username}", dani));

                // then
                perform
                    .andExpect(status().isOk())
                    .andExpect(content().string(portfolio));

                verify(oAuthService, times(1))
                    .findRequestUserByToken(null);
                verify(portfolioService, times(1))
                    .read(eq(dani), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-readYoursWithExisting-unLoggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("username").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    )
                ));
            }

            @DisplayName("?????? ???????????????, ???????????? ?????? ?????? - ??????")
            @Test
            void read_GuestUserWithYoursWithNotExisting_400Exception() throws Exception {
                // given
                String dani = "dani";

                given(oAuthService.findRequestUserByToken(any()))
                    .willCallRealMethod();
                given(portfolioService.read(anyString(), any(UserDto.class)))
                    .willThrow(new NoSuchPortfolioException());

                // when
                ResultActions perform = mockMvc.perform(get("/api/portfolios/{username}", dani));

                // then
                perform
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("R0001"));

                verify(oAuthService, times(1))
                    .findRequestUserByToken(null);
                verify(portfolioService, times(1))
                    .read(eq(dani), any(UserDto.class));

                // documentation
                perform.andDo(document("portfolio-readYoursWithNotExisting-unLoggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pathParameters(
                        parameterWithName("username").description("????????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("errorCode").description("?????? ??????")
                    )
                ));
            }
        }

        @DisplayName("?????????????????? ????????????.")
        @Nested
        class update {

            @DisplayName("?????? ??????????????? - ??????")
            @Test
            void update_GuestUserWithYours_401Exception() throws Exception {
                // given
                String dani = "dani";

                given(oAuthService.validateToken(any()))
                    .willReturn(false);

                // when
                ResultActions perform = mockMvc.perform(put("/api/portfolios")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(
                        PortfolioFactory.mockPortfolioRequestWithNewProfileAndIntroduction(dani)
                    ))
                );

                // then
                perform
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("errorCode").value("A0001"));

                verify(oAuthService, times(1))
                    .validateToken(null);

                // documentation
                perform.andDo(document("portfolio-updateYours-unLoggedIn",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    requestFields(
                        fieldWithPath("id").description("??????????????? id"),
                        fieldWithPath("name").description("??????????????? ????????? ??????"),
                        fieldWithPath("profileImageShown").description("????????? ????????? ?????? ??????"),
                        fieldWithPath("profileImageUrl").description("????????? ????????? ??????"),
                        fieldWithPath("introduction").description("????????????"),
                        fieldWithPath("createdAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("updatedAt").description("??????????????? ?????? ??????"),
                        fieldWithPath("contacts").description("????????? ??????"),
                        fieldWithPath("projects").description("???????????? ??????"),
                        fieldWithPath("sections").description("?????? ??????")
                    ),
                    responseFields(
                        fieldWithPath("errorCode").description("?????? ??????")
                    )
                ));
            }
        }
    }
}
