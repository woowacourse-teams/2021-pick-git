package com.woowacourse.pickgit.config.count_data_source;

import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class CountDataSource implements DataSource {

    private final QueryCounter queryCounter;
    private final DataSource dataSource;

    public CountDataSource(QueryCounter queryCounter, DataSource dataSource) {
        this.queryCounter = queryCounter;
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        return (Connection) Proxy.newProxyInstance(
            connection.getClass().getClassLoader(),
            connection.getClass().getInterfaces(),
            new ProxyConnectionHandler(queryCounter, connection)
        );
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
