package com.woowacourse.pickgit.config.database;

import java.util.List;

public class SlaveNames {

    private final String[] value;
    private int counter = 0;

    public SlaveNames(List<String> slaveDataSourceProperties) {
        this(slaveDataSourceProperties.toArray(String[]::new));
    }

    public SlaveNames(String[] value) {
        this.value = value;
    }

    public String getNextName() {
        int index = counter;
        counter = (counter + 1) % value.length;
        return value[index];
    }
}
