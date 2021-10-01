package com.woowacourse.pickgit.portfolio.domain.common;

public interface Updatable<T> {

    void update(T t);

    boolean semanticallyEquals(Object o);

    int semanticallyHashcode();
}
