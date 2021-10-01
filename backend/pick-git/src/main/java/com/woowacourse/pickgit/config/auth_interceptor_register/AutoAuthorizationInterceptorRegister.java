package com.woowacourse.pickgit.config.auth_interceptor_register;

import com.woowacourse.pickgit.authentication.presentation.interceptor.PathMatchInterceptor;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.RegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.register_type.StorageForRegisterType;
import com.woowacourse.pickgit.config.auth_interceptor_register.scanner.data_structure.MergedInterceptorParameterByMethod;
import java.util.List;

public class AutoAuthorizationInterceptorRegister {

    private final List<StorageForRegisterType> storageForRegisterTypes;
    private final PathMatchInterceptor authenticationInterceptor;
    private final PathMatchInterceptor ignoreAuthenticationInterceptor;
    private final UriParser uriParser;

    private AutoAuthorizationInterceptorRegister(
        List<StorageForRegisterType> storageForRegisterTypes,
        PathMatchInterceptor authenticationInterceptor,
        PathMatchInterceptor ignoreAuthenticationInterceptor,
        UriParser uriParser
    ) {
        this.storageForRegisterTypes = storageForRegisterTypes;
        this.authenticationInterceptor = authenticationInterceptor;
        this.ignoreAuthenticationInterceptor = ignoreAuthenticationInterceptor;
        this.uriParser = uriParser;
    }

    public void execute() {
        List<MergedInterceptorParameterByMethod> mergedInterceptorParameterByMethod =
            uriParser.getMergedInterceptorParameterByMethod();
        addPathPatterns(mergedInterceptorParameterByMethod);
    }

    private void addPathPatterns(
        List<MergedInterceptorParameterByMethod> mergedInterceptorParameterByMethods
    ) {
        for (MergedInterceptorParameterByMethod registerCandidate : mergedInterceptorParameterByMethods) {
            StorageForRegisterType storageForRegisterType = storageForRegisterTypes.stream()
                .filter(storage -> storage.isSatisfiedBy(registerCandidate.getRegisterType()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("registerType을 컨트롤 할 수 없습니다."));

            storageForRegisterType
                .put(registerCandidate.getUrls(), registerCandidate.getHttpMethod());
        }

        for (StorageForRegisterType storageForRegisterType : storageForRegisterTypes) {
            if (storageForRegisterType.isSatisfiedBy(RegisterType.AUTHENTICATE)) {
                storageForRegisterType.appendTo(authenticationInterceptor);
            } else {
                storageForRegisterType.appendTo(ignoreAuthenticationInterceptor);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        List<StorageForRegisterType> storageForRegisterTypes;
        private PathMatchInterceptor authenticationInterceptor;
        private PathMatchInterceptor ignoreAuthenticationInterceptor;
        private UriParser uriParser;

        public Builder storageForRegisterTypes(
            List<StorageForRegisterType> storageForRegisterTypes
        ) {
            this.storageForRegisterTypes = storageForRegisterTypes;
            return this;
        }

        public Builder authenticationInterceptor(
            PathMatchInterceptor authenticationInterceptor
        ) {
            this.authenticationInterceptor = authenticationInterceptor;
            return this;
        }

        public Builder ignoreAuthenticationInterceptor(
            PathMatchInterceptor ignoreAuthenticationInterceptor
        ) {
            this.ignoreAuthenticationInterceptor = ignoreAuthenticationInterceptor;
            return this;
        }

        public Builder uriParser(UriParser uriParser) {
            this.uriParser = uriParser;
            return this;
        }

        public AutoAuthorizationInterceptorRegister build() {
            return new AutoAuthorizationInterceptorRegister(
                storageForRegisterTypes,
                authenticationInterceptor,
                ignoreAuthenticationInterceptor,
                uriParser
            );
        }
    }
}
