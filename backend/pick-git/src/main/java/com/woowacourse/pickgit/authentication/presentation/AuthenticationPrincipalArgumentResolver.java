package com.woowacourse.pickgit.authentication.presentation;

import com.woowacourse.pickgit.authentication.application.OAuthService;
import com.woowacourse.pickgit.authentication.domain.Authenticated;
import com.woowacourse.pickgit.authentication.presentation.interceptor.AuthHeader;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final OAuthService oAuthService;

    public AuthenticationPrincipalArgumentResolver(
        OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authentication = (String) request.getAttribute(AuthHeader.AUTHENTICATION);

        return oAuthService.findRequestUserByToken(authentication);
    }
}
