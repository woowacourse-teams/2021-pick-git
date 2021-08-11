package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.config.auth_interceptor_regester.ForLoginUser;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ForLoginUserScanner {
    public List<Method> parseMethods(Class<?> controller) {
        return Arrays.stream(controller.getMethods())
            .filter(method -> method.isAnnotationPresent(ForLoginUser.class))
            .collect(toList());
    }
}
