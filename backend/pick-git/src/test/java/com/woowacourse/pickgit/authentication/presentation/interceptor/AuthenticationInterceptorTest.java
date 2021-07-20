package com.woowacourse.pickgit.authentication.presentation.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.woowacourse.pickgit.authentication.application.JwtTokenProvider;
import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.infrastructure.AuthorizationExtractor;
import com.woowacourse.pickgit.authentication.infrastructure.JwtTokenProviderImpl;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTest {

    private JwtTokenProvider jwtTokenProvider;

    private OAuthService oAuthService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthenticationInterceptor authenticationInterceptor;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProviderImpl("pick-git", 3600000);
        oAuthService = new OAuthService(null, jwtTokenProvider, null, null);
        authenticationInterceptor = new AuthenticationInterceptor(oAuthService);
    }

    @DisplayName("CORS 프리플라이트 요청이면 true를 반환한다.")
    @Test
    void preHandle_CORS_True() throws Exception {
        // mock
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.OPTIONS.toString());
        when(httpServletRequest.getHeader("Access-Control-Request-Headers")).thenReturn("X-PINGOTHER, Content-Type");
        when(httpServletRequest.getHeader("Access-Control-Request-Method")).thenReturn("POST");
        when(httpServletRequest.getHeader("Origin")).thenReturn("http://pick-git.example");

        // then
        assertThat(authenticationInterceptor.preHandle(httpServletRequest, null, null)).isTrue();
    }

    @DisplayName("유효한 토큰의 요청이면 HttpServletRequest에 토큰 정보를 저장하고 true를 반환한다.")
    @Test
    void preHandle_ValidToken_True() throws Exception {
        // given
        String validToken = "Bearer " + jwtTokenProvider.createToken("pick-git");

        // mock, when
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.OPTIONS.toString());
        when(httpServletRequest.getHeaders(AuthorizationExtractor.AUTHORIZATION)).thenReturn(Collections.enumeration(
            List.of(validToken)));

        // then
        assertThat(authenticationInterceptor.preHandle(httpServletRequest, null, null)).isTrue();
        verify(httpServletRequest, times(2)).setAttribute(any(String.class), any(String.class));
    }

    @DisplayName("유효하지 않은 토큰의 요청이면 예외를 던진다.")
    @Test
    void preHandle_InvalidToken_ThrowException() {
        // given
        String bearerToken = "Bearer " + "invalid token";

        // mock
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.OPTIONS.toString());
        when(httpServletRequest.getHeaders(AuthorizationExtractor.AUTHORIZATION)).thenReturn(Collections.enumeration(
            List.of(bearerToken)));

        // when, then
        assertThatThrownBy(() -> authenticationInterceptor.preHandle(httpServletRequest, null, null))
            .isInstanceOf(InvalidTokenException.class);
    }

    @DisplayName("유효기간이 지난 토큰의 경우 예외가 발생한다.")
    @Test
    void preHandle_ExpiredToken_ThrowException() {
        // given
        jwtTokenProvider.changeExpirationTime(0);
        String bearerToken = "Bearer " + jwtTokenProvider.createToken("pick-git");

        // mock
        when(httpServletRequest.getMethod()).thenReturn(HttpMethod.GET.toString());
        when(httpServletRequest.getHeaders(AuthorizationExtractor.AUTHORIZATION)).thenReturn(Collections.enumeration(
            List.of(bearerToken)));

        // when, then
        assertThatThrownBy(() -> authenticationInterceptor.preHandle(httpServletRequest, null, null))
            .isInstanceOf(InvalidTokenException.class);
    }
}
