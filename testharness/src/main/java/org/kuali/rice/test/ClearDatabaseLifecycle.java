/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Lifecycle class to clean up the database for use in testing.
 * This lifecycle will not be run (even if it is listed in the lifecycles list)
 * if the 'use.use.clearDatabaseLifecycle' configuration property is defined, and is
 * not 'true'.  If the property is omitted the lifecycle runs as normal.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @since 0.9
 *
 */
public class ClearDatabaseLifecycle extends BaseLifecycle {

    protected static final Logger LOG = Logger.getLogger(ClearDatabaseLifecycle.class);

    private List<String> tablesToClear = new ArrayList<String>();
    private List<String> tablesNotToClear = new ArrayList<String>();

    public ClearDatabaseLifecycle() {
        addStandardTables();
    }

    public ClearDatabaseLifecycle(List<String> tablesToClear, List<String> tablesNotToClear) {
        this.tablesToClear = tablesToClear;
        this.tablesNotToClear = tablesNotToClear;
        addStandardTables();
    }

    protected void addStandardTables() {
        tablesNotToClear.add("BIN.*");
    }

    public static final String TEST_TABLE_NAME = "EN_UNITTEST_T";

    public void start() throws Exception {
        String useClearDatabaseLifecycle = Core.getCurrentContextConfig().getProperty("use.clearDatabaseLifecycle");

        if (useClearDatabaseLifecycle != null && !Boolean.valueOf(useClearDatabaseLifecycle)) {
            LOG.debug("Skipping ClearDatabaseLifecycle due to property: use.clearDatabaseLifecycle=" + useClearDatabaseLifecycle);
            return;
        }

        final DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        clearTables(TestHarnessServiceLocator.getJtaTransactionManager(), dataSource);
        super.start();
    }

    protected Boolean isTestTableInSchema(final DataSource dataSource) {
        Assert.assertNotNull("DataSource could not be located.", dataSource);
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (Boolean) new JdbcTemplate(dataSource).execute(new ConnectionCallback() {
            public Object doInConnection(final Connection connection) throws SQLException {
                final ResultSet resultSet = connection.getMetaData().getTables(null, connection.getMetaData().getUserName().toUpperCase(), TEST_TABLE_NAME, null);
                return new Boolean(resultSet.next());
            }
        });
    }

    protected void verifyTestEnvironment(final DataSource dataSource) {
        Assert.assertTrue("No table named '" + TEST_TABLE_NAME + "' was found in the configured database.  " + "You are attempting to run tests against a non-test database!!!", isTestTableInSchema(dataSource));
    }

    protected void clearTables(final PlatformTransactionManager transactionManager, final DataSource dataSource) {
        Assert.assertNotNull("DataSource could not be located.", dataSource);
        try {
            StopWatch s = new StopWatch();
            s.start();
            new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
                public Object doInTransaction(final TransactionStatus status) {
                    verifyTestEnvironment(dataSource);
                    return new JdbcTemplate(dataSource).execute(new StatementCallback() {
                        public Object doInStatement(Statement statement) throws SQLException {
                            String schemaName = statement.getConnection().getMetaData().getUserName().toUpperCase();
                            LOG.info("Clearing tables for schema " + schemaName);
                            if (StringUtils.isBlank(schemaName)) {
                                Assert.fail("Empty schema name given");
                            }
                            final List<String> reEnableConstraints = new ArrayList<String>();
                            final ResultSet resultSet = statement.getConnection().getMetaData().getTables(null, schemaName, null, new String[] { "TABLE" });
                            while (resultSet.next()) {
                                String tableName = resultSet.getString("TABLE_NAME");
                                if (shouldTableBeCleared(tableName)) {
                                    if (!isUsingDerby(statement.getConnection().getMetaData())) {
                                        ResultSet keyResultSet = statement.getConnection().getMetaData().getExportedKeys(null, schemaName, tableName);
                                        while (keyResultSet.next()) {
                                            final String fkName = keyResultSet.getString("FK_NAME");
                                            final String fkTableName = keyResultSet.getString("FKTABLE_NAME");
                                            final String disableConstraint = "ALTER TABLE " + fkTableName + " DISABLE CONSTRAINT " + fkName;
                                            LOG.info("Disabling constraints using statement ->" + disableConstraint + "<-");
                                            statement.addBatch(disableConstraint);
                                            reEnableConstraints.add("ALTER TABLE " + fkTableName + " ENABLE CONSTRAINT " + fkName);
                                        }
                                        keyResultSet.close();
                                    }
                                    String deleteStatement = "DELETE FROM " + tableName;
                                    LOG.info("Clearing contents using statement ->" + deleteStatement + "<-");
                                    statement.addBatch(deleteStatement);
                                }
                            }
                            for (final String constraint : reEnableConstraints) {
                                LOG.info("Enabling constraints using statement ->" + constraint + "<-");
                                statement.addBatch(constraint);
                            }
                            statement.executeBatch();
                            resultSet.close();
                            LOG.info("Tables successfully cleared for schema " + schemaName);
                            return null;
                        }
                    });
                }
            });
            s.stop();
            LOG.info("Time to clear tables: " + DurationFormatUtils.formatDurationHMS(s.getTime()));
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

    private boolean shouldTableBeCleared(String tableName) {
        if (getTablesToClear() != null && !getTablesToClear().isEmpty()) {
            for (String tableToClear : getTablesToClear()) {
                if (tableName.toUpperCase().matches(tableToClear.toUpperCase())) {
                    return true;
                }
            }
            return false;
        }
        if (getTablesNotToClear() != null && !getTablesNotToClear().isEmpty()) {
            for (String tableNotToClear : getTablesNotToClear()) {
                if (tableName.toUpperCase().matches(tableNotToClear.toUpperCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isUsingDerby(DatabaseMetaData metaData) throws SQLException {
        return metaData.getDriverName().toLowerCase().indexOf("derby") > -1;
    }

    public List<String> getTablesToClear() {
        return this.tablesToClear;
    }

    public List<String> getTablesNotToClear() {
        return this.tablesNotToClear;
    }
}