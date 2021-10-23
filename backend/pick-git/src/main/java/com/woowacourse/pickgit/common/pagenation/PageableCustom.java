package com.woowacourse.pickgit.common.pagenation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.domain.Sort.Direction;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableCustom {

    int size() default 10;

    int page() default 0;

    String[] sort() default {};

    Direction direction() default Direction.ASC;

}
