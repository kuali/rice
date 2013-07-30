package org.kuali.rice.krad.data.platform;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MaxValueIncrementerFactory {

    private static final String ID_COLUMN_NAME = "id";

    private static final ConcurrentMap<DataSource, ConcurrentMap<String, DataFieldMaxValueIncrementer>> cache =
            new ConcurrentHashMap<DataSource, ConcurrentMap<String, DataFieldMaxValueIncrementer>>(8, 0.9f, 1);

    public static DataFieldMaxValueIncrementer getIncrementer(DataSource dataSource, String incrementerName) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null");
        }
        if (StringUtils.isBlank(incrementerName)) {
            throw new IllegalArgumentException("Incrementer name must not be null or blank");
        }
        // yes, we want to check if it's there first, then put if absent, for max speed! This is like ConcurrentMap's
        // version of double-checked locking.
        ConcurrentMap<String, DataFieldMaxValueIncrementer> incrementerCache = cache.get(dataSource);
        if (incrementerCache == null) {
            incrementerCache = cache.putIfAbsent(dataSource,
                    new ConcurrentHashMap<String, DataFieldMaxValueIncrementer>(8, 0.9f, 1));
            if (incrementerCache == null) {
                incrementerCache = cache.get(dataSource);
            }
        }

        // now check if we have a cached incrementer
        DataFieldMaxValueIncrementer incrementer = incrementerCache.get(incrementerName.toUpperCase());
        if (incrementer == null) {
            incrementer = incrementerCache.putIfAbsent(incrementerName.toUpperCase(), createIncrementer(dataSource,
                    incrementerName));
            if (incrementer == null) {
                incrementer = incrementerCache.get(incrementerName.toUpperCase());
            }
        }
        return incrementer;

    }

    private static DataFieldMaxValueIncrementer createIncrementer(DataSource dataSource, String incrementerName) {
        // TODO - make this easier to customize/configure externally...
        DatabasePlatformInfo platformInfo = DatabasePlatforms.detectPlatform(dataSource);
        DataFieldMaxValueIncrementer incrementer = null;
        if (DatabasePlatforms.ORACLE.equalsIgnoreCase(platformInfo.getName())) {
            incrementer = new OracleSequenceMaxValueIncrementer(dataSource, incrementerName);
        } else if (DatabasePlatforms.MYSQL.equalsIgnoreCase(platformInfo.getName())) {
            incrementer = new EnhancedMySQLMaxValueIncrementer(dataSource, incrementerName, ID_COLUMN_NAME);
        }
        if (incrementer == null) {
            throw new UnsupportedDatabasePlatformException(platformInfo);
        }
        if (incrementer instanceof InitializingBean) {
            try {
                ((InitializingBean) incrementer).afterPropertiesSet();
            } catch (Exception e) {
                throw new DataAccessResourceFailureException(
                        "Failed to initialize max value incrementer for given datasource and incrementer. dataSource="
                                + dataSource.toString()
                                + ", incrementerName = "
                                + incrementerName, e);
            }
        }
        return incrementer;
    }

    private static final class EnhancedMySQLMaxValueIncrementer extends AbstractColumnMaxValueIncrementer {

        private JdbcTemplate template;

        private EnhancedMySQLMaxValueIncrementer() {
        }

        private EnhancedMySQLMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
            super(dataSource, incrementerName, columnName);
        }

        @Override
        public synchronized void afterPropertiesSet() {
            super.afterPropertiesSet();
            template = new JdbcTemplate(getDataSource());
        }

        @Override
        protected synchronized long getNextKey() throws DataAccessException {
            return template.execute(new ConnectionCallback<Long>() {
                @Override
                public Long doInConnection(Connection con) throws SQLException, DataAccessException {
                    Statement statement = null;
                    ResultSet resultSet = null;
                    try {
                        statement = con.createStatement();
                        String sql = "INSERT INTO " + getIncrementerName() + " VALUES (NULL)";
                        statement.executeUpdate(sql);
                        sql = "SELECT LAST_INSERT_ID()";
                        resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            resultSet.first();
                            return resultSet.getLong(1);
                        } else {
                            throw new IncorrectResultSizeDataAccessException("Failed to get last_insert_id() for sequence incrementer table '" + getIncrementerName() + "'", 1);
                        }
                    } finally {
                        JdbcUtils.closeResultSet(resultSet);
                        JdbcUtils.closeStatement(statement);
                    }
                }
            }).longValue();
        }
    }

    private MaxValueIncrementerFactory() {}

}
