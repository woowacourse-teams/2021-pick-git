package com.woowacourse.pickgit.config.auth_interceptor_register.scanner;

import static java.util.stream.Collectors.toList;

import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ForGuestScanner {

    public List<Method> parseMethods(Class<?> controller) {
        return Arrays.stream(controller.getMethods())
            .filter(method -> method.isAnnotationPresent(ForLoginAndGuestUser.class))
            .collect(toList());
    }
}
