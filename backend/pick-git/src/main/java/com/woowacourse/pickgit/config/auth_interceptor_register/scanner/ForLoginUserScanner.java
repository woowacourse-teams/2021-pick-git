package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ForLoginUserScanner {

    public List<Method> parseMethods(Class<?> controller) {
        return Arrays.stream(controller.getMethods())
            .filter(method -> method.isAnnotationPresent(ForOnlyLoginUser.class))
            .collect(toList());
    }
}
