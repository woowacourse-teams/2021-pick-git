package com.woowacourse.s3_proxy.config.resolver;

import com.woowacourse.s3_proxy.common.file_validator.FileValidator;
import com.woowacourse.s3_proxy.web.presentation.dto.Files;
import com.woowacourse.s3_proxy.web.presentation.resolver.ExtensionValid;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public class ExtensionValidArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String FILE_NAME = "files";
    private static final String USER_NAME = "userName";

    private final List<FileValidator> fileValidators;

    public ExtensionValidArgumentResolver(List<FileValidator> fileValidators) {
        this.fileValidators = fileValidators;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtensionValid.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        MultipartHttpServletRequest multipartHttpServletRequest =
            getMultipartHttpServletRequest(webRequest);

        List<MultipartFile> files = multipartHttpServletRequest.getFiles(FILE_NAME);
        files.forEach(this::validateIsRightFile);

        String userName = (String) multipartHttpServletRequest.getAttribute(USER_NAME);

        return new Files.Request(userName, files);
    }

    private void validateIsRightFile(MultipartFile multipartFile) {
        fileValidators.forEach(fileValidator -> fileValidator.execute(multipartFile));
    }

    private MultipartHttpServletRequest getMultipartHttpServletRequest(
        NativeWebRequest webRequest
    ) {
        HttpServletRequest httpServletRequest =
            webRequest.getNativeRequest(HttpServletRequest.class);
        Objects.requireNonNull(httpServletRequest);

        return (MultipartHttpServletRequest) httpServletRequest;
    }
}
