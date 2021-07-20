package com.woowacourse.pickgit.authentication.presentation.interceptor;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.infrastructure.AuthorizationExtractor;
import com.woowacourse.pickgit.exception.authentication.InvalidTokenException;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    private final OAuthService oAuthService;

    public AuthenticationInterceptor(
        OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean preHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler) throws Exception {
        if (isPreflightRequest(request)) {
            return true;
        }
        String authentication = AuthorizationExtractor.extract(request);
        if (!oAuthService.validateToken(authentication)) {
            throw new InvalidTokenException();
        }
        request.setAttribute(AuthHeader.AUTHENTICATION, authentication);
        return true;
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return isOptions(request)
            && hasAccessControlRequestHeaders(request)
            && hasAccessControlRequestMethod(request)
            && hasOrigin(request);
    }

    private boolean isOptions(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.toString());
    }

    private boolean hasAccessControlRequestHeaders(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS));
    }

    private boolean hasAccessControlRequestMethod(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD));
    }

    private boolean hasOrigin(HttpServletRequest request) {
        return Objects.nonNull(request.getHeader(HttpHeaders.ORIGIN));
    }
}
