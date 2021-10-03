package com.woowacourse.pickgit.config.db;

import static com.woowacourse.pickgit.config.db.DataSourceSelector.READ;
import static com.woowacourse.pickgit.config.db.DataSourceSelector.TRANSACTIONAL;
import static com.woowacourse.pickgit.config.db.DataSourceSelector.WRITE;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private final DataSourceSelector dataSourceSelector;

    public ReplicationRoutingDataSource(DataSourceSelector dataSourceSelector) {
        this.dataSourceSelector = dataSourceSelector;
    }

    @Override
    protected String determineCurrentLookupKey() {
        if(dataSourceSelector.getSelected().equals(TRANSACTIONAL)) {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            if (isReadOnly) {
                return READ;
            }

            return WRITE;
        }

        return dataSourceSelector.getSelected();
    }
}
