package com.woowacourse.pickgit.config.count_data_source;

public class Count {

    private long value;

    public Count(long value) {
        this.value = value;
    }

    public Count countOne() {
        return new Count(++value);
    }

    public long getValue() {
        return value;
    }
}
