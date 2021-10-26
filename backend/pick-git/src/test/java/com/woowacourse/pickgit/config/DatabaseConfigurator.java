package com.woowacourse.pickgit.config;

import static com.woowacourse.pickgit.config.db.DataSourceSelector.READ;

import com.woowacourse.pickgit.config.db.DataSourceSelector;
import com.woowacourse.pickgit.config.db.SchemaGenerator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class DatabaseConfigurator implements InitializingBean {

    @Autowired
    private SchemaGenerator schemaGenerator;

    @Autowired
    private DataSourceSelector dataSourceSelector;

    @PersistenceContext

    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        dataSourceSelector.toWrite();
        entityManager.unwrap(Session.class).doWork(conn -> {
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(schemaGenerator.drop());
            }
        });

        dataSourceSelector.toWrite();
        entityManager.unwrap(Session.class).doWork(conn -> {
            try (Statement statement = conn.createStatement(); conn) {
                statement.executeUpdate(schemaGenerator.create());
            }
        });

        dataSourceSelector.toRead();
        entityManager.unwrap(Session.class).doWork(conn -> {
            try (Statement statement = conn.createStatement(); conn) {
                statement.executeUpdate(schemaGenerator.create());
            } catch (SQLSyntaxErrorException ignored) {}
        });

        entityManager.unwrap(Session.class).doWork(this::extractTableNames);
        dataSourceSelector.toWrite();
    }

    private void extractTableNames(Connection conn) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        ResultSet tables = conn
            .getMetaData()
            .getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        try (tables) {
            while (tables.next()) {
                tableNames.add(tables.getString("table_name"));
            }

            this.tableNames = tableNames;
        }
    }

    public void clear() {
        if(dataSourceSelector.getSelected().equals(READ)) {
            return;
        }

        entityManager.unwrap(Session.class).doWork(this::cleanUpDatabase);
    }

    private void cleanUpDatabase(Connection conn) throws SQLException {
        try (Statement statement = conn.createStatement()) {

            statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

            for (String tableName : tableNames) {

                statement.executeUpdate("TRUNCATE TABLE " + tableName);
                statement
                    .executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1");
            }

            statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    public void toRead() {
        dataSourceSelector.toRead();
    }

    public void toWrite() {
        dataSourceSelector.toWrite();
    }

    public String getSelectedDataSourceName() {
        return dataSourceSelector.getSelected();
    }
}
