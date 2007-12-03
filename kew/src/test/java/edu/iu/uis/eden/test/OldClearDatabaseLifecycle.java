/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.kuali.rice.database.XAPoolDataSource;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * TODO: This class is temporary until I can get workflow upgraded to use the newest rice test case.
 *
 */
public class OldClearDatabaseLifecycle extends BaseLifecycle {

    protected static final Logger LOG = Logger.getLogger(OldClearDatabaseLifecycle.class);

    private static final String SPRING_FILE = "edu/iu/uis/eden/test/TestBootstrapSpring.xml";
    private static final String DATA_SOURCE = "dataSource";
    private static final String TRANSACTION_MANAGER = "transactionManager";

    private List<String> tablesToClear;
    private List<String> tablesNotToClear;

    public OldClearDatabaseLifecycle() {}

    public OldClearDatabaseLifecycle(List<String> tablesToClear, List<String> tablesNotToClear) {
    	this.tablesToClear = tablesToClear;
    	this.tablesNotToClear = tablesNotToClear;
    }

    public OldClearDatabaseLifecycle(List<String> tablesToClear) {
    	this.tablesToClear = tablesToClear;
    }

    private static final String TEST_TABLE_NAME = "EN_UNITTEST_T";

    public void start() throws Exception {
    	try {
        final ClassPathXmlApplicationContext bootstrapContext = new ClassPathXmlApplicationContext(SPRING_FILE);
        final XAPoolDataSource dataSource = (XAPoolDataSource) bootstrapContext.getBean(DATA_SOURCE);
        final String schemaName = dataSource.getUser().toUpperCase();
        try {
        	clearTables((PlatformTransactionManager)bootstrapContext.getBean(TRANSACTION_MANAGER), dataSource, schemaName);
        } finally {
        	bootstrapContext.close();
        }
        super.start();
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw e;
    	}
    }

    protected void verifyTestEnvironment(final DataSource dataSource) {
        Assert.assertNotNull("DataSource could not be located.", dataSource);

        final JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute(new ConnectionCallback() {
            public Object doInConnection(final Connection connection) throws SQLException {
                final ResultSet resultSet = connection.getMetaData().getTables(null, null, TEST_TABLE_NAME, null);
                Assert.assertTrue("No table named '"+TEST_TABLE_NAME+"' was found in the configured database.  " +
                    "You are attempting to run tests against a non-test database!!!", resultSet.next());
                return null;
            }
        });
    }

    protected void clearTables(final PlatformTransactionManager transactionManager, final DataSource dataSource, final String schemaName) {
    	LOG.info("Clearing tables for schema " + schemaName);
    	Assert.assertNotNull("DataSource could not be located.", dataSource);

        if (schemaName == null || schemaName.equals("")) {
            Assert.fail("Empty schema name given");
        }
        new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus status) {
                verifyTestEnvironment(dataSource);
                return new JdbcTemplate(dataSource).execute(new StatementCallback() {
                    public Object doInStatement(Statement statement) throws SQLException {
                        final List<String> reEnableConstraints = new ArrayList<String>();
                    	final ResultSet resultSet = statement.getConnection().getMetaData().getTables(null, schemaName, null, new String[] { "TABLE" });
                        while (resultSet.next()) {
                            String tableName = resultSet.getString("TABLE_NAME");
                            // TODO this is currently targetting only en and quartz tables, this should probably become parameterizable in the
                            // ClearDatabaseLifecycle or maybe this constraint can be removed?
                            if (tableName.startsWith("EN_") || tableName.startsWith("KR_QRTZ_")) { /*&& !dontClear.contains(tableName)) {*/
                            	if (getTablesToClear() != null && !getTablesToClear().isEmpty() && !getTablesToClear().contains(tableName)) {
                            		continue;
                            	}
                            	if (getTablesNotToClear() != null && !getTablesNotToClear().isEmpty() && getTablesNotToClear().contains(tableName)) {
                            		continue;
                            	}
                            	ResultSet keyResultSet = statement.getConnection().getMetaData().getExportedKeys(null, schemaName, tableName);
                            	while (keyResultSet.next()) {
                            		final String fkName = keyResultSet.getString("FK_NAME");
                            		final String fkTableName = keyResultSet.getString("FKTABLE_NAME");
                            		final String disableConstraint = "ALTER TABLE "+fkTableName+" DISABLE CONSTRAINT "+fkName;
                            		LOG.info("Disabling constraints using statement ->" + disableConstraint + "<-");
                            		statement.addBatch(disableConstraint);
                            		reEnableConstraints.add("ALTER TABLE "+fkTableName+" ENABLE CONSTRAINT "+fkName);
                            	}
                            	keyResultSet.close();
                            	String deleteStatement = "DELETE FROM "+tableName;
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
                        return null;
                    }
                });
            }
        });
        LOG.info("Tables successfully cleared for schema " + schemaName);
    }

    public List<String> getTablesToClear() {
    	return this.tablesToClear;
    }

    public List<String> getTablesNotToClear() {
    	return this.tablesNotToClear;
    }

}