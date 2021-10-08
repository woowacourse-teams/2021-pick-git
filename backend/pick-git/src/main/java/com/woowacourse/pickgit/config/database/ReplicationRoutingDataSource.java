package com.woowacourse.pickgit.config.database;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ReplicationRoutingDataSource.class);

    private SlaveNames slaveNames;

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        List<String> replicas = targetDataSources.keySet().stream()
            .map(Object::toString)
            .filter(string -> string.contains("slave"))
            .collect(toList());

        this.slaveNames = new SlaveNames(replicas);
    }

    @Override
    protected String determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (isReadOnly) {
            String slaveName = slaveNames.getNextName();

            LOGGER.info("Slave DB name: {}", slaveName);

            return slaveName;
        }

        return "master";
    }
}
