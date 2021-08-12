package com.woowacourse.pickgit.config.auth_interceptor_register.scanner.test_classes;

import com.woowacourse.pickgit.config.auth_interceptor_register.ForLoginAndGuestUser;
import com.woowacourse.pickgit.config.auth_interceptor_register.ForOnlyLoginUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class ClassThree {

    @ForOnlyLoginUser
    @GetMapping("/test1")
    public void test1() {

    }

    @ForOnlyLoginUser
    @PostMapping("/test2/{testId}/test")
    public void test2() {

    }

    @ForLoginAndGuestUser
    @PutMapping("/test3")
    public void test3() {

    }

    @ForLoginAndGuestUser
    @DeleteMapping("/test4/{testId}")
    public void test4() {

    }
}
