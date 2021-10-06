package com.woowacourse.pickgit.config.count_data_source;

import lombok.Getter;

@Getter
public class QueryCounter {

    private Count count;
    private boolean countable;

    public QueryCounter() {
        this.count = new Count(0L);
        countable = false;
    }

    public void startCount() {
        this.countable = true;
        this.count = new Count(0L);
    }

    public void countOne() {
        count = count.countOne();
    }

    public void endCount() {
        this.countable = false;
    }

    public boolean isCountable() {
        return countable;
    }
}
