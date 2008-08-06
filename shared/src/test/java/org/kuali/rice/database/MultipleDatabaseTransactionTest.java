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
package org.kuali.rice.database;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.derby.drda.NetworkServerControl;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.testharness.BaseRiceTestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Tests if transactions across multiple databases rollback properly when failures occur.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Ignore
public class MultipleDatabaseTransactionTest extends BaseRiceTestCase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MultipleDatabaseTransactionTest.class);

    private static final String TEST_TABLE = "TESTTABLE";

    @Test
    public void testTransactionRollbackAcrossTwoDatabases() throws Exception {
        NetworkServerControl server = new NetworkServerControl(InetAddress.getByName("localhost"), 1925);
        LOG.info("Starting server");
        server.start(null);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("org/kuali/rice/database/MultipleDatabaseTransactionTestSpring.xml");
        context.start();

        final DataSource dataSource = (DataSource) context.getBean("dataSource");

        try {
            TransactionTemplate template = (TransactionTemplate) context.getBean("transactionTemplate");
            assertNotNull(template);

            template.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    return jdbcTemplate.execute(new ConnectionCallback() {
                        public Object doInConnection(Connection connection) throws SQLException {
                            ResultSet resultSet = connection.getMetaData().getTables(null, null, TEST_TABLE, new String[]{"TABLE"});
                            if (resultSet.next()) {
                                // the table exists, drop it
                                LOG.info(TEST_TABLE + " exists, dropping it.");
                                Statement statement = connection.createStatement();
                                statement.execute("DROP TABLE " + TEST_TABLE);
                                statement.close();
                            }
                            resultSet.close();
                            Statement statement = connection.createStatement();
                            LOG.info("Creating " + TEST_TABLE);
                            statement.execute("CREATE TABLE " + TEST_TABLE + " (column1 SMALLINT)");
                            statement.close();
                            Statement statement2 = connection.createStatement();
                            LOG.info("Inserting into " + TEST_TABLE);
                            statement2.execute("INSERT INTO " + TEST_TABLE + " (column1) VALUES (1)");
                            statement2.close();
                            return null;
                        }
                    });
                }
            });

            template.execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    return jdbcTemplate.execute(new ConnectionCallback() {
                        public Object doInConnection(Connection connection) throws SQLException {
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + TEST_TABLE);
                            resultSet.next();                            
                            assertTrue(resultSet.getInt(1) == 1);
                            resultSet.close();
                            statement.close();
                            return null;
                        }
                    });
                }
            });

        } finally {
            context.stop();
            server.shutdown();
        }        
    }

}
