package com.woowacourse.pickgit.config.count_data_source;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProxyConnectionHandler implements InvocationHandler {

    private final QueryCounter queryCounter;
    private final Connection connection;

    public ProxyConnectionHandler(
        QueryCounter queryCounter,
        Connection connection
    ) {
        this.queryCounter = queryCounter;
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (queryCounter.isCountable()) {
            if ("prepareStatement".equals(method.getName())) {
                return getPreparedStatement(method, args);
            }
        }
        return method.invoke(connection, args);
    }

    private PreparedStatement getPreparedStatement(
        Method method,
        Object[] args
    ) throws IllegalAccessException, InvocationTargetException {
        final PreparedStatement result = (PreparedStatement) method.invoke(connection, args);

        for (Object arg : args) {
            if (isQueryStatement(arg)) {
                queryCounter.countOne();
                break;
            }
        }
        return result;
    }

    private boolean isQueryStatement(Object statement) {
        if (statement.getClass().isAssignableFrom(String.class)) {
            String queryStatement = (String) statement;
            return queryStatement.startsWith("select");
        }
        return false;
    }
}
