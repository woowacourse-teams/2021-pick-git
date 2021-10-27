package com.woowacourse.pickgit.config.db;

import static com.woowacourse.pickgit.config.db.DataSourceSelector.READ;
import static com.woowacourse.pickgit.config.db.DataSourceSelector.WRITE;

import com.woowacourse.pickgit.config.count_data_source.CountDataSource;
import com.woowacourse.pickgit.config.count_data_source.QueryCounter;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
@Profile("test")
public class DataSourceConfiguration {

    @Primary
    @Bean
    public DataSource testDataSource(
        @Qualifier("testRoutingDataSource") DataSource testRoutingDataSource) {
        return new LazyConnectionDataSourceProxy(testRoutingDataSource);
    }

    @Bean
    public DataSource testRoutingDataSource(
        @Qualifier("readDataSource") DataSource readDataSource,
        @Qualifier("writeDataSource") DataSource writeDataSource,
        DataSourceSelector dataSourceSelector
    ) {
        ReplicationRoutingDataSource testRoutingDataSource
            = new ReplicationRoutingDataSource(dataSourceSelector);

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(WRITE, writeDataSource);
        dataSourceMap.put(READ, readDataSource);

        testRoutingDataSource.setTargetDataSources(dataSourceMap);
        testRoutingDataSource.setDefaultTargetDataSource(writeDataSource);

        return testRoutingDataSource;
    }

    @Bean
    public QueryCounter queryCounter() {
        return new QueryCounter();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-read")
    public DataSource readDataSourceOrigin() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource readDataSource() {
        return new CountDataSource(queryCounter(), readDataSourceOrigin());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource-write")
    public DataSource writeDataSource() {
        return DataSourceBuilder.create().build();
    }
}
