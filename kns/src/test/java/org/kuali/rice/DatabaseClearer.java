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
import java.util.Map;
import java.util.Properties;

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
import edu.iu.uis.eden.clientapp.ClientConfig;
import edu.iu.uis.eden.config.BaseConfig;
import edu.iu.uis.eden.config.Config;
import edu.iu.uis.eden.core.Core;
import edu.iu.uis.eden.database.WorkflowManagedDataSource;

/**
 * Utility for removing all tables from a database.  Use with care.  Run as application from 
 * eclipse.
 * 
 * @author rkirkend
 */
public class DatabaseClearer {
    
    private static final Logger LOG = Logger.getLogger(DatabaseClearer.class);

    public static void main(String[] args) throws Exception {
        new DatabaseClearer().start();
    }
    
    public void start() throws Exception {
        setupConfig();
        ClassPathXmlApplicationContext bootstrapContext = new ClassPathXmlApplicationContext("TestBootstrapSpring.xml");
        TransactionManager transactionManager = (TransactionManager)bootstrapContext.getBean(KEWServiceLocator.JTA_TRANSACTION_MANAGER);
        if (transactionManager instanceof Current) {
            ((Current) transactionManager).setDefaultTimeout(EdenConstants.DEFAULT_TRANSACTION_TIMEOUT_SECONDS);
        }
        WorkflowManagedDataSource edenDataSource = (WorkflowManagedDataSource) bootstrapContext.getBean(KEWServiceLocator.EDEN_DATASOURCE);
        String edenSchemaName = edenDataSource.getUser().toUpperCase();
        clearTables((PlatformTransactionManager)bootstrapContext.getBean(KEWServiceLocator.TRANSACTION_MANAGER), edenDataSource, edenSchemaName);
        clearSequences((PlatformTransactionManager)bootstrapContext.getBean(KEWServiceLocator.TRANSACTION_MANAGER), edenDataSource, edenSchemaName);
        bootstrapContext.close();
    }
    
    public void setupConfig() throws Exception {
        Config config = new DatabaseClearerConfig();
        config.parseConfig();
        Core.init(config);
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
                JdbcTemplate template = new JdbcTemplate(dataSource);
                return template.execute(new StatementCallback() {
                    public Object doInStatement(Statement statement) throws SQLException {
                        ResultSet resultSet = statement.getConnection().getMetaData().getTables(null, edenSchemaName, null, new String[] { "TABLE" });
                        String output = "Droping tables: \n";
                        while (resultSet.next()) {
                            String tableName = resultSet.getString("TABLE_NAME");
                            output += tableName + "\n";
                            statement.addBatch("DROP TABLE "+tableName + " cascade constraints");
                        }
                        statement.executeBatch();
                        resultSet.close();
                        System.out.println(output);
                        return null;
                    }
                });
            }
        });
        LOG.info("Tables successfully drop for schema " + edenSchemaName);
    }
    
    public static void clearSequences(final PlatformTransactionManager transactionManager, final DataSource dataSource, final String edenSchemaName) {
        LOG.info("Clearing sequences for schema " + edenSchemaName);
        if (dataSource == null) {
            Assert.fail("Null data source given");
        }
        if (edenSchemaName == null || edenSchemaName.equals("")) {
            Assert.fail("Empty eden schema name given");
        }
        new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                JdbcTemplate template = new JdbcTemplate(dataSource);
                return template.execute(new StatementCallback() {
                    public Object doInStatement(Statement statement) throws SQLException {
                        ResultSet resultSet = statement.executeQuery("select sequence_name from user_sequences");
                        String output = "Droping tables: \n";
                        while (resultSet.next()) {
                            String sequenceName = resultSet.getString("sequence_name");
                            output += sequenceName + "\n";
                            statement.addBatch("drop sequence " + sequenceName);
                        }
                        statement.executeBatch();
                        resultSet.close();
                        System.out.println(output);
                        return null;
                    }
                });
            }
        });
        LOG.info("Sequences successfully drop for schema " + edenSchemaName);
    }
    
    public class DatabaseClearerConfig extends BaseConfig {
        
        public DatabaseClearerConfig() {
            super("classpath:rice-test-client-config.xml");
        }

        public Properties getBaseProperties() {
            if (Core.getRootConfig() != null) {
                return Core.getRootConfig().getProperties();
            }
            return null;
        }
        
        public Map getBaseObjects() {
            if (Core.getRootConfig() != null) {
                return Core.getRootConfig().getObjects();
            }
            return null;
        }
    }
}