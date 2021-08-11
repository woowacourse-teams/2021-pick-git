package com.woowacourse.pickgit.config.auth_interceptor_regester.register_type;

import com.woowacourse.pickgit.authentication.presentation.interceptor.PathMatchInterceptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;

public class IgnoreAuthenticateStorageForRegisterType implements StorageForRegisterType {
    private static final RegisterType TYPE = RegisterType.IGNORE_AUTHENTICATE;

    private Map<String, List<HttpMethod>> cache = new HashMap<>();

    @Override
    public void appendTo(PathMatchInterceptor pathMatchInterceptor) {
        cache.forEach(pathMatchInterceptor::addPathPatterns);
    }

    @Override
    public boolean isSatisfiedBy(RegisterType registerType) {
        return registerType == TYPE;
    }

    @Override
    public void put(String key, HttpMethod value) {
        if(!cache.containsKey(key)) {
            cache.put(key, new ArrayList<>());
        }

        cache.get(key).add(value);
    }

    @Override
    public RegisterType getType() {
        return TYPE;
    }
}
