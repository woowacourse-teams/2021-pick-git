package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes;

import com.woowacourse.pickgit.config.auth_interceptor_regester.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_regester.ForOnlyLoginUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class ClassOne {
    @ForOnlyLoginUser
    @GetMapping("/test5")
    public void test1() {

    }

    @ForOnlyLoginUser
    @PostMapping("/test5")
    public void test2() {

    }

    @ForLoginAndGuestUser
    @PutMapping("/test5")
    public void test3() {

    }

    @ForLoginAndGuestUser
    @DeleteMapping("/test5")
    public void test4() {

    }
}
