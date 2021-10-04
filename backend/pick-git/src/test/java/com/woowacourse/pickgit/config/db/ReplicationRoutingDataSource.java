package com.woowacourse.pickgit.config.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private final DataSourceSelector dataSourceSelector;

    public ReplicationRoutingDataSource(DataSourceSelector dataSourceSelector) {
        this.dataSourceSelector = dataSourceSelector;
    }

    @Override
    protected String determineCurrentLookupKey() {
        return dataSourceSelector.getSelected();
    }
}
