package com.woowacourse.pickgit.common.request_builder.method_builder;

import com.woowacourse.pickgit.common.request_builder.LoginBuilder;
import com.woowacourse.pickgit.common.request_builder.parameters.Parameters;
import org.springframework.http.HttpMethod;

public abstract class MethodBuilder {
    private final String url;
    private final Object[] params;

    public MethodBuilder(
        String url,
        Object... params
    ) {
        this.url = url;
        this.params = params;
    }

    protected <T extends Parameters> LoginBuilder<T> getPostLoginBuilder(
        Class<T> parameterType
    ) {
        return new LoginBuilder<>(parameterType, HttpMethod.POST, url, params);
    }

    protected <T extends Parameters> LoginBuilder<T> getGetLoginBuilder(
        Class<T> parameterType
    ) {
        return new LoginBuilder<>(parameterType, HttpMethod.GET, url, params);
    }

    protected <T extends Parameters> LoginBuilder<T> getPutLoginBuilder(
        Class<T> parameterType
    ) {
        return new LoginBuilder<>(parameterType, HttpMethod.PUT, url, params);
    }

    protected <T extends Parameters> LoginBuilder<T> getDeleteLoginBuilder(
        Class<T> parameterType
    ) {
        return new LoginBuilder<>(parameterType, HttpMethod.DELETE, url, params);
    }
}
