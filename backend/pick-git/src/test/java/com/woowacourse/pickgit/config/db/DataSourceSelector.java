package com.woowacourse.pickgit.config.db;

import org.springframework.stereotype.Component;

@Component
public class DataSourceSelector {

    protected static final String WRITE = "write";
    protected static final String READ = "read";
    protected static final String TRANSACTIONAL = "transactional";

    private String selected = WRITE;

    public void toWrite() {
        this.selected = WRITE;
    }

    public void toRead() {
        this.selected = READ;
    }

    public void toTransactional() {
        this.selected = TRANSACTIONAL;
    }

    public void doInRead(Runnable runnable) {
        String before = this.selected;
        toRead();
        runnable.run();
        this.selected = before;
    }

    public String getSelected() {
        return selected;
    }
}
