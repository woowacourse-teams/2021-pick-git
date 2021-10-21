package com.woowacourse.pickgit.config.db;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class DataSourceSelector {

    public static final String WRITE = "write";
    public static final String READ = "read";

    private String selected = WRITE;

    public void toWrite() {
        this.selected = WRITE;
    }

    public void toRead() {
        this.selected = READ;
    }

    public String getSelected() {
        return selected;
    }
}
