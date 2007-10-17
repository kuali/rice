/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.notification.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Adapted from Rice ClearDatabaseLifecycle.  This is a standalone utility class
 * that does not implement or depend on the Rice Lifecycle interface.
 * {{@link #clearTables(PlatformTransactionManager, DataSource, String, List, boolean, String)} can
 * be called directly on this class, or an instance can be created and used as a command.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClearDatabaseUtil {
    private static final Logger LOG = Logger.getLogger(ClearDatabaseUtil.class);

    public static final String DEFAULT_TEST_TABLE_NAME = "EN_UNITTEST_T";

    /**
     * The PlatformTransactionManager under which to clear tables in a separate transaction
     */
    private final PlatformTransactionManager transactionManager;
    /**
     * The datasource to use
     */
    private final DataSource dataSource;
    /**
     * Schema name
     */
    private final String schemaName;
    /**
     * A list of regular expressions which should match names of tables to be cleared
     */
    private final List<String> tablesToClear;
    /**
     * The name of a table which serves as a marker to indicate that the database is in fact
     * used for unit testing and that all data can be deleted
     */
    private final String testTableNamePattern;
    /**
     * Whether we should determine the constraints on tables, and drop them before deleting and then
     * reapply them afterwards.
     */
    private final boolean handleConstraints;

    public ClearDatabaseUtil(PlatformTransactionManager transactionManager, DataSource dataSource, String schema, List<String> tablesToClear) {
        this(transactionManager, dataSource, schema, tablesToClear, true, DEFAULT_TEST_TABLE_NAME);
    }

    public ClearDatabaseUtil(PlatformTransactionManager transactionManager, DataSource dataSource, String schema, List<String> tablesToClear, boolean handleConstraints) {
        this(transactionManager, dataSource, schema, tablesToClear, handleConstraints, DEFAULT_TEST_TABLE_NAME);
    }

    public ClearDatabaseUtil(PlatformTransactionManager transactionManager, DataSource dataSource, String schema, List<String> tablesToClear, boolean handleConstraints, String testTable) {
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.schemaName = schema;
        this.tablesToClear = tablesToClear;
        this.testTableNamePattern = testTable;
        this.handleConstraints = handleConstraints;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTestTableNamePattern() {
        return testTableNamePattern;
    }

    public boolean isHandleConstraints() {
        return handleConstraints;
    }

    public List<String> getTablesToClear() {
        return this.tablesToClear;
    }

    public void clearTables() {
        clearTables(transactionManager, dataSource, schemaName, tablesToClear, handleConstraints, testTableNamePattern);
    }

    public static void clearTables(final PlatformTransactionManager transactionManager, final DataSource dataSource, final String schemaName, final List<String> tablesToClear, final boolean handleConstraints, final String testTable) {
        LOG.info("Clearing tables for schema " + schemaName);
        Assert.assertNotNull("DataSource could not be located.", dataSource);

        if (schemaName == null || schemaName.equals("")) {
            Assert.fail("Empty schema name given");
        }
        new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus status) {
                verifyTestEnvironment(dataSource, testTable);
                return new JdbcTemplate(dataSource).execute(new StatementCallback() {
                    public Object doInStatement(Statement statement) throws SQLException {
                        final List<String> reEnableConstraints = new ArrayList<String>();
                        final ResultSet resultSet = statement.getConnection().getMetaData().getTables(null, schemaName, null, new String[] { "TABLE" });
                        while (resultSet.next()) {
                            String tableName = resultSet.getString("TABLE_NAME");
                            if (tablesToClear != null && !tablesToClear.isEmpty()) {
                                boolean match = false;
                                for (String regex: tablesToClear) {
                                    if (tableName.matches(regex)) {
                                        match = true;
                                        break;
                                    }
                                }
                                if (!match) continue;
                            }
                            if (handleConstraints) {
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
                        for (final String constraint : reEnableConstraints) {
                            LOG.info("Enabling constraints using statement ->" + constraint + "<-");
                            statement.addBatch(constraint);
                        }
                        statement.executeBatch();
                        resultSet.close();
                        return null;
                    }
                });
            }
        });
        LOG.info("Tables successfully cleared for schema " + schemaName);
    }

    private static boolean isTestTableInSchema(final DataSource dataSource, final String testTableNamePattern) {
        Assert.assertNotNull("DataSource could not be located.", dataSource);
        return (Boolean) new JdbcTemplate(dataSource).execute(new ConnectionCallback() {
            public Object doInConnection(final Connection connection) throws SQLException {
                final ResultSet resultSet = connection.getMetaData().getTables(null, null, testTableNamePattern, null);
                return resultSet.next();
            }
        });
    }

    private static void verifyTestEnvironment(DataSource dataSource, String testTableNamePattern) {
        Assert.assertTrue("No table named '" + testTableNamePattern + "' was found in the configured database.  " +
                          "You are attempting to run tests against a non-test database!!!",
                          isTestTableInSchema(dataSource, testTableNamePattern));
    }
}
