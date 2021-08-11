package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.data_structure;

import com.woowacourse.pickgit.config.auth_interceptor_regester.register_type.RegisterType;
import java.util.List;
import org.springframework.http.HttpMethod;

public class PrepreparedControllerMethod {
    private final List<String> urls;
    private final HttpMethod httpMethod;
    private final RegisterType registerType;

    public PrepreparedControllerMethod(List<String> urls, HttpMethod httpMethod,
        RegisterType registerType) {
        this.urls = urls;
        this.httpMethod = httpMethod;
        this.registerType = registerType;
    }

    public List<String> getUrls() {
        return urls;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }
}
