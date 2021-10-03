package com.woowacourse.pickgit.config;

import com.woowacourse.pickgit.config.db.DataSourceSelector;
import com.woowacourse.pickgit.config.db.SchemaGenerator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class DatabaseCleaner implements InitializingBean {

    @Autowired
    private SchemaGenerator schemaGenerator;

    @Autowired
    private DataSourceSelector dataSourceSelector;

    @Autowired
    private DataSource dataSource;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws SQLException {
        String ddl = schemaGenerator.generate();

        dataSourceSelector.toRead();
        createTables(dataSource.getConnection(), ddl);

        dataSourceSelector.toWrite();
        createTables(dataSource.getConnection(), ddl);

        extractTableNames(dataSource.getConnection());
    }

    private void createTables(Connection conn, String ddl) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate(ddl);
    }

    private void extractTableNames(Connection conn) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        ResultSet tables = conn
            .getMetaData()
            .getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        while (tables.next()) {
            tableNames.add(tables.getString("table_name"));
        }

        this.tableNames = tableNames;
    }

    public void execute() {
        try {
            cleanUpDatabase(dataSource.getConnection());
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void cleanUpDatabase(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        for (String tableName : tableNames) {

            statement.executeUpdate("TRUNCATE TABLE " + tableName);
            statement.executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1");
        }

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
