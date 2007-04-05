/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.objectweb.jotm.Current;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.core.BaseLifecycle;
import edu.iu.uis.eden.database.WorkflowManagedDataSource;

public class ClearDatabaseLifecycle extends BaseLifecycle {
    
    private static final Logger LOG = Logger.getLogger(ClearDatabaseLifecycle.class);
    
    private static final String TEST_TABLE_NAME = "EN_UNITTEST_T";
    
    public void start() throws Exception {
        ClassPathXmlApplicationContext bootstrapContext = new ClassPathXmlApplicationContext("TestBootstrapSpring.xml");
        TransactionManager transactionManager = (TransactionManager)bootstrapContext.getBean(KEWServiceLocator.JTA_TRANSACTION_MANAGER);
        if (transactionManager instanceof Current) {
            ((Current) transactionManager).setDefaultTimeout(EdenConstants.DEFAULT_TRANSACTION_TIMEOUT_SECONDS);
        }
        WorkflowManagedDataSource edenDataSource = (WorkflowManagedDataSource) bootstrapContext.getBean(KEWServiceLocator.EDEN_DATASOURCE);
        String edenSchemaName = edenDataSource.getUser().toUpperCase();
        clearTables((PlatformTransactionManager)bootstrapContext.getBean(KEWServiceLocator.TRANSACTION_MANAGER), edenDataSource, edenSchemaName);
        bootstrapContext.close();
        super.start();
    }
    
    public static void verifyTestEnvironment(DataSource dataSource) {
        if (dataSource == null) {
            Assert.fail("Could not locate the EDEN data source.");
        }
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException {
                ResultSet resultSet = connection.getMetaData().getTables(null, null, TEST_TABLE_NAME, null);
                if (!resultSet.next()) {
                    Assert.fail("No table named '"+TEST_TABLE_NAME+"' was found in the configured database.  " +
                            "You are attempting to run tests against a non-test database!!!");
                }
                return null;
            }
        });
    }
    
    public static void clearTables(final PlatformTransactionManager transactionManager, final DataSource dataSource, final String edenSchemaName) {
        LOG.info("Clearing tables for schema " + edenSchemaName);
        if (dataSource == null) {
            Assert.fail("Null data source given");
        }
        if (edenSchemaName == null || edenSchemaName.equals("")) {
            Assert.fail("Empty eden schema name given");
        }
        new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                verifyTestEnvironment(dataSource);
                JdbcTemplate template = new JdbcTemplate(dataSource);
                return template.execute(new StatementCallback() {
                    public Object doInStatement(Statement statement) throws SQLException {
                        ResultSet resultSet = statement.getConnection().getMetaData().getTables(null, edenSchemaName, null, new String[] { "TABLE" });
                        while (resultSet.next()) {
                            String tableName = resultSet.getString("TABLE_NAME");
//                            String sqlStatement = "DELETE FROM "+tableName + " cascade constraints";
                            String sqlStatement = "DELETE "+tableName;
                            LOG.info("Clearing contents using statement ->" + sqlStatement + "<-");
                            try {
                                statement.execute(sqlStatement);
                            } catch (Exception e) {
                                statement.addBatch("DELETE FROM "+tableName);
                            }
                                

                        }
//                        statement.executeBatch();
                        resultSet.close();
                        return null;
                    }
                });
            }
        });
        LOG.info("Tables successfully cleared for schema " + edenSchemaName);
    }
    
}
