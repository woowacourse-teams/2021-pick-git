package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes;

import com.woowacourse.pickgit.config.auth_interceptor_regester.ForGuest;
import com.woowacourse.pickgit.config.auth_interceptor_regester.ForLoginUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class ClassOne {
    @ForLoginUser
    @GetMapping("/test5")
    public void test1() {

    }

    @ForLoginUser
    @PostMapping("/test5")
    public void test2() {

    }

    @ForGuest
    @PutMapping("/test5")
    public void test3() {

    }

    @ForGuest
    @DeleteMapping("/test5")
    public void test4() {

    }
}
