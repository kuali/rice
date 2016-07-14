/**
 * Copyright 2005-2016 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.web.health;

import com.codahale.metrics.health.HealthCheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Unit test for {@link DatabaseConnectionHealthGauge}.
 *
 * @author Eric Westfall
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseConnectionHealthGaugeTest {

    private DatabaseConnectionHealthGauge gauge;

    @Mock
    private DataSource dataSource;
    @Mock
    private DatabasePlatform platform;

    @Before
    public void setUp() {
        this.gauge = new DatabaseConnectionHealthGauge(dataSource, platform);
        stubValidationQuery(platform);
    }

    @Test
    public void testCheck_Healthy() throws Exception {
        Statement statement = stubSuccessfulQueryExecution(dataSource);

        HealthCheck.Result result = gauge.check();
        assertTrue("Result should be healthy", result.isHealthy());

        // verify it tried to execute the validation query
        verify(statement).execute(platform.getValidationQuery());
    }

    @Test(expected = DataAccessException.class)
    public void testCheck_Unhealthy() throws Exception {
        Statement statement = stubSQLException(dataSource);

        try {
            // this should throw a subclass of DataAccessException from Spring since Spring will translate the
            // SQLException to something in it's DataAccessException exception hierarchy
            gauge.check();
        } finally {
            // verify it tried to execute the validation query
            verify(statement).execute(platform.getValidationQuery());
        }
    }

    @Test
    public void testGetValue_Healthy() throws Exception {
        Statement statement = stubSuccessfulQueryExecution(dataSource);
        assertTrue("Get value should return true since db connection check is healthy", gauge.getValue());
        // verify it tried to execute the validation query
        verify(statement).execute(platform.getValidationQuery());
    }

    @Test
    public void testGetValue_Unhealthy_Exception() throws Exception {
        Statement statement = stubSQLException(dataSource);
        assertFalse("Get value should return false since db connection check throw a SQLException", gauge.getValue());
        // verify it tried to execute the validation query
        verify(statement).execute(platform.getValidationQuery());
    }

    private void stubValidationQuery(DatabasePlatform platform) {
        when(platform.getValidationQuery()).thenReturn("select 1");
        assertEquals("select 1", platform.getValidationQuery());
    }

    private Statement stubSuccessfulQueryExecution(DataSource dataSource) throws SQLException {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        return statement;
    }

    private Statement stubSQLException(DataSource dataSource) throws SQLException {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Failed to execute sql"));
        return statement;
    }



}
