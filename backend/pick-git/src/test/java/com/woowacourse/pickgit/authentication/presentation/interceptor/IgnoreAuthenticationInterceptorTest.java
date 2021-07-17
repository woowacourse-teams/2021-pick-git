package com.woowacourse.pickgit.authentication.presentation.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
class IgnoreAuthenticationInterceptorTest {

    private JwtTokenProvider jwtTokenProvider;

    private OAuthService oAuthService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private IgnoreAuthenticationInterceptor ignoreAuthenticationInterceptor;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProviderImpl("pick-git", 3600000);
        oAuthService = new OAuthService(null, jwtTokenProvider, null, null);
        ignoreAuthenticationInterceptor = new IgnoreAuthenticationInterceptor(oAuthService);
    }

    @DisplayName("유효한 토큰을 가진 회원이면 true를 반환한다.")
    @Test
    void preHandle_WithValidToken_ReturnTrue() throws Exception {
        // given
        String validToken = "Bearer " + jwtTokenProvider.createToken("pick-git");

        // mock
        given(request.getMethod()).willReturn(HttpMethod.GET.toString());
        given(request.getHeaders(AuthorizationExtractor.AUTHORIZATION))
            .willReturn(Collections.enumeration(List.of(validToken)));

        // when, then
        assertThat(ignoreAuthenticationInterceptor.preHandle(request, null, null)).isTrue();
        verify(request, times(2)).setAttribute(anyString(), anyString());
    }

    @DisplayName("토큰이 아예 없으면 true를 반환한다. (GuestUser)")
    @Test
    void preHandle_WithOutToken_ReturnTrue() throws Exception {
        // mock
        given(request.getMethod()).willReturn(HttpMethod.GET.toString());
        given(request.getHeaders(AuthorizationExtractor.AUTHORIZATION))
            .willReturn(Collections.emptyEnumeration());

        // when, then
        assertThat(ignoreAuthenticationInterceptor.preHandle(request, null, null)).isTrue();
    }

    @DisplayName("토큰이 존재하지만 유효하지 않으면 InvalidTokenException을 던진다.")
    @Test
    void preHandle_WithInvalidToken_ThrowException() {
        // given
        String invalidToken = "Bearer invalid token";

        // mock
        given(request.getMethod()).willReturn(HttpMethod.GET.toString());
        given(request.getHeaders(AuthorizationExtractor.AUTHORIZATION))
            .willReturn(Collections.enumeration(List.of(invalidToken)));

        // when, then
        assertThatThrownBy(() -> ignoreAuthenticationInterceptor.preHandle(request, null, null))
            .isInstanceOf(InvalidTokenException.class)
            .extracting("errorCode")
            .isEqualTo("A0001");
    }
}
