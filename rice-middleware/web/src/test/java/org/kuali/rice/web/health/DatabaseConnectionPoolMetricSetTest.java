/**
 * Copyright 2005-2018 The Kuali Foundation
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

import bitronix.tm.resource.jdbc.PoolingDataSource;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import org.apache.commons.dbcp.BasicDataSource;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link DatabaseConnectionPoolMetricSet}
 *
 * @author Eric Westfall
 */
@RunWith(MockitoJUnitRunner.class)
public class DatabaseConnectionPoolMetricSetTest {

    @Test
    public void testGetMetrics_XAPool() throws Exception {
        StandardXAPoolDataSource dataSource = mock(StandardXAPoolDataSource.class);
        when(dataSource.getLockedObjectCount()).thenReturn(10);
        when(dataSource.getMinSize()).thenReturn(5);
        when(dataSource.getMaxSize()).thenReturn(20);

        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("xapool:", dataSource);
        Map<String, Metric> metrics = metricSet.getMetrics();
        assertEquals(10, ((Gauge)metrics.get("xapool:pool.active")).getValue());
        assertEquals(5, ((Gauge)metrics.get("xapool:pool.min")).getValue());
        assertEquals(20, ((Gauge)metrics.get("xapool:pool.max")).getValue());
        assertEquals(0.5, ((Gauge)metrics.get("xapool:pool.usage")).getValue());
    }

    @Test
    public void testGetMetrics_Bitronix() throws Exception {
        PoolingDataSource dataSource = mock(PoolingDataSource.class);
        when(dataSource.getTotalPoolSize()).thenReturn(15L);
        when(dataSource.getInPoolSize()).thenReturn(5L);
        when(dataSource.getMinPoolSize()).thenReturn(5);
        when(dataSource.getMaxPoolSize()).thenReturn(20);

        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("btm:", dataSource);
        Map<String, Metric> metrics = metricSet.getMetrics();
        assertEquals(10, ((Gauge)metrics.get("btm:pool.active")).getValue());
        assertEquals(5, ((Gauge)metrics.get("btm:pool.min")).getValue());
        assertEquals(20, ((Gauge)metrics.get("btm:pool.max")).getValue());
        assertEquals(0.5, ((Gauge)metrics.get("btm:pool.usage")).getValue());
    }

    @Test
    public void testGetMetrics_DBCP() throws Exception {
        BasicDataSource dataSource = mock(BasicDataSource.class);
        when(dataSource.getNumActive()).thenReturn(10);
        when(dataSource.getMinIdle()).thenReturn(5);
        when(dataSource.getMaxActive()).thenReturn(20);

        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("dbcp:", dataSource);
        Map<String, Metric> metrics = metricSet.getMetrics();
        assertEquals(10, ((Gauge)metrics.get("dbcp:pool.active")).getValue());
        assertEquals(5, ((Gauge)metrics.get("dbcp:pool.min")).getValue());
        assertEquals(20, ((Gauge)metrics.get("dbcp:pool.max")).getValue());
        assertEquals(0.5, ((Gauge)metrics.get("dbcp:pool.usage")).getValue());
    }

    @Test
    public void testGetMetrics_Unknown() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("unknown:", dataSource);
        // in this case, getMetrics returns an empty map
        assertTrue("Metrics map should be empty", metricSet.getMetrics().isEmpty());
    }

    @Test
    public void testGetMetrics_Wrapped_XAPool() throws Exception {
        StandardXAPoolDataSource dataSource = mock(StandardXAPoolDataSource.class);
        when(dataSource.getLockedObjectCount()).thenReturn(10);
        when(dataSource.getMinSize()).thenReturn(5);
        when(dataSource.getMaxSize()).thenReturn(20);

        // create the wrapper
        DataSource wrapperDataSource = mock(DataSource.class);
        when(wrapperDataSource.isWrapperFor(StandardXAPoolDataSource.class)).thenReturn(true);
        when(wrapperDataSource.unwrap(StandardXAPoolDataSource.class)).thenReturn(dataSource);

        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("xapool:", wrapperDataSource);
        Map<String, Metric> metrics = metricSet.getMetrics();
        assertEquals(10, ((Gauge)metrics.get("xapool:pool.active")).getValue());
        assertEquals(5, ((Gauge)metrics.get("xapool:pool.min")).getValue());
        assertEquals(20, ((Gauge)metrics.get("xapool:pool.max")).getValue());
        assertEquals(0.5, ((Gauge)metrics.get("xapool:pool.usage")).getValue());
    }

    /**
     * Tests the case where the isWrapperFor or unwrap methods throw SQLException (which they are allowed to do though
     * not sure why they ever would)
     */
    @Test
    public void testGetMetrics_Wrapped_SQLException() throws Exception {
        StandardXAPoolDataSource dataSource = mock(StandardXAPoolDataSource.class);
        // create the wrapper
        DataSource wrapperDataSource = mock(DataSource.class);
        when(wrapperDataSource.isWrapperFor(StandardXAPoolDataSource.class)).thenThrow(new SQLException());

        DatabaseConnectionPoolMetricSet metricSet = new DatabaseConnectionPoolMetricSet("xapool:", wrapperDataSource);
        Map<String, Metric> metrics = metricSet.getMetrics();
        // in this case, getMetrics returns an empty map since we failed when trying to unwrap the connection
        assertTrue("Metrics map should be empty", metricSet.getMetrics().isEmpty());
    }


}
