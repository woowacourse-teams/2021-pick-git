package com.woowacourse.pickgit.config.count_data_source;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

//@Configuration
//@ActiveProfiles("test")
//public class QueryCounterConfiguration {
//
//    @Value("${spring.datasource-read.jdbcUrl}")
//    private String url;
//
//    @Value("${spring.datasource-read.username}")
//    private String username;
//
//    @Bean
//    public QueryCounter queryCounter() {
//        return new QueryCounter();
//    }
//
//    @Bean
//    public DataSource dataSource() {
//        DataSource dataSource = DataSourceBuilder.create()
//            .driverClassName("org.h2.Driver")
//            .url(url)
//            .username(username)
//            .password("").build();
//        return new CountDataSource(queryCounter(), dataSource);
//    }
//}
