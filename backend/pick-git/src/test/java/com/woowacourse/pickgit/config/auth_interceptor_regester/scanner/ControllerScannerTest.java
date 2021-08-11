package com.woowacourse.pickgit.config.auth_interceptor_regester.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassOne;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassThree;
import com.woowacourse.pickgit.config.auth_interceptor_regester.scanner.test_classes.ClassTwo;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerScannerTest {

    @DisplayName("컨트롤러만 파싱한다.")
    @Test
    void getControllers() {
        String classOne = ClassOne.class.getCanonicalName();
        String classTwo = ClassTwo.class.getCanonicalName();
        String classThree = ClassThree.class.getCanonicalName();

        ControllerScanner controllerScanner = new ControllerScanner(
            List.of(classOne, classTwo, classThree)
        );

        List<Class<?>> controllers = controllerScanner.getControllers();

        assertThat(controllers).containsAll(List.of(
            ClassTwo.class, ClassThree.class
        ));
    }
}