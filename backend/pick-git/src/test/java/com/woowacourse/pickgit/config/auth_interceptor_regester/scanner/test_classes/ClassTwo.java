package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes;

import com.woowacourse.pickgit.config.auth_interceptor_regester.ForLoginUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class ClassTwo {

    @GetMapping("/test6")
    public void test1() {

    }

    @PostMapping("/test7")
    public void test2() {

    }

    @PutMapping("/test8")
    public void test3() {

    }

    @ForLoginUser
    @DeleteMapping("/test9")
    public void test4() {

    }
}
