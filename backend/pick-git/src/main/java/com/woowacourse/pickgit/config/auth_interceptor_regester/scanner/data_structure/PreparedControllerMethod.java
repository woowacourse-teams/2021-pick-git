package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.data_structure;

import com.woowacourse.pickgit.config.auth_interceptor_regester.register_type.RegisterType;
import org.springframework.http.HttpMethod;

public class PreparedControllerMethod {
    private final String urls;
    private final HttpMethod httpMethod;
    private final RegisterType registerType;

    public PreparedControllerMethod(
        String url,
        HttpMethod httpMethod,
        RegisterType registerType
    ) {
        this.urls = url;
        this.httpMethod = httpMethod;
        this.registerType = registerType;
    }

    public String getUrls() {
        return urls;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }
}
