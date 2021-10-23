package com.woowacourse.pickgit.common.pagenation;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PaginationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PageableCustom.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        PageableCustom pageableCustom = parameter.getParameterAnnotation(PageableCustom.class);

        String page = request.getParameter("page");
        String limit = request.getParameter("limit");

        return PageRequest.of(
            parsePage(page, pageableCustom),
            parseLimit(limit, pageableCustom),
            pageableCustom.direction()
        );
    }

    private int parsePage(String page, PageableCustom pageableCustom) {
        try {
            return Integer.parseInt(page);
        } catch (NumberFormatException ignore) {
            return pageableCustom.page();
        }
    }

    private int parseLimit(String limit, PageableCustom pageableCustom) {
        try {
            return Integer.parseInt(limit);
        } catch (NumberFormatException ignore) {
            return pageableCustom.size();
        }
    }
}
