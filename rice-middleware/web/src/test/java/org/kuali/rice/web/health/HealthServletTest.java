/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.config.property.SimpleConfig;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader;
import org.kuali.rice.core.framework.resourceloader.SimpleServiceLocator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import javax.xml.namespace.QName;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link HealthServlet}
 *
 * @author Eric Westfall
 */
@RunWith(MockitoJUnitRunner.class)
public class HealthServletTest {

    @Mock
    private StandardXAPoolDataSource primaryDataSource;
    @Mock
    private BasicDataSource nonTransactionalDataSource;
    @Mock
    private PoolingDataSource serverDataSource;
    @Mock
    private DatabasePlatform databasePlatform;

    private SimpleConfig config;
    private SimpleServiceLocator serviceLocator;
    private HealthServlet healthServlet;

    @Before
    public void setUp() throws Exception {
        this.config = new SimpleConfig();
        this.config.putProperty("application.id", HealthServletTest.class.getName());
        this.config.putObject(RiceConstants.DATASOURCE_OBJ, primaryDataSource);
        this.config.putObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ, nonTransactionalDataSource);
        this.config.putObject(RiceConstants.SERVER_DATASOURCE_OBJ, serverDataSource);
        ConfigContext.init(this.config);
        stubDataSource(primaryDataSource);
        stubDataSource(nonTransactionalDataSource);
        stubDataSource(serverDataSource);

        this.serviceLocator = new SimpleServiceLocator();
        this.serviceLocator.addService(new QName(RiceConstants.DB_PLATFORM), databasePlatform);
        GlobalResourceLoader.addResourceLoaderFirst(new BaseResourceLoader(new QName(HealthServletTest.class.getName()), this.serviceLocator));
        GlobalResourceLoader.start();

        stubDatabasePlatform(databasePlatform);

        this.healthServlet = new HealthServlet();
    }

    private void stubDataSource(DataSource dataSource) throws SQLException {
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);

        if (dataSource instanceof StandardXAPoolDataSource) {
            StandardXAPoolDataSource ds = (StandardXAPoolDataSource)dataSource;
            when(ds.getLockedObjectCount()).thenReturn(10);
            when(ds.getMinSize()).thenReturn(5);
            when(ds.getMaxSize()).thenReturn(20);
        } else if (dataSource instanceof PoolingDataSource) {
            PoolingDataSource ds = (PoolingDataSource)dataSource;
            when(ds.getTotalPoolSize()).thenReturn(15L);
            when(ds.getInPoolSize()).thenReturn(5L);
            when(ds.getMinPoolSize()).thenReturn(5);
            when(ds.getMaxPoolSize()).thenReturn(20);
        } else if (dataSource instanceof BasicDataSource) {
            BasicDataSource ds = (BasicDataSource)dataSource;
            when(ds.getNumActive()).thenReturn(10);
            when(ds.getMinIdle()).thenReturn(5);
            when(ds.getMaxActive()).thenReturn(20);
        } else {
            fail("Invalid datasource class: " + dataSource.getClass());
        }

    }

    private void stubDatabasePlatform(DatabasePlatform platform) {
        when(platform.getValidationQuery()).thenReturn("select 1");
        assertEquals("select 1", platform.getValidationQuery());
    }

    @After
    public void tearDown() throws Exception {
        ConfigContext.init(new SimpleConfig());
        GlobalResourceLoader.stop();
    }

    @Test
    public void testService_No_Details_Ok() throws Exception {
        healthServlet.init();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost:8080/rice-standalone/health");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        healthServlet.service(request, response);
        assertEquals("Response code should be 204", 204, response.getStatus());
        String content = response.getContentAsString();
        assertTrue("Content should be empty", content.isEmpty());
    }

    @Test
    public void testService_No_Details_Failed() throws Exception {
        // set memory usage threshold at 0 to guarantee a failure
        this.config.putProperty("rice.health.memory.total.usageThreshold", "0.0");

        healthServlet.init();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost:8080/rice-standalone/health");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        healthServlet.service(request, response);
        assertEquals("Response code should be 503", 503, response.getStatus());
        String content = response.getContentAsString();
        assertTrue("Content should be empty", content.isEmpty());
    }

    @Test
    public void testService_Details_Ok() throws Exception {
        // we'll use the defaults
        ConfigContext.init(this.config);

        MockHttpServletResponse response = initAndExecuteDetailedCheck(healthServlet);
        assertEquals("Response code should be 200", 200, response.getStatus());
        JsonNode root = parseContent(response.getContentAsString());
        assertEquals("Ok", root.get("Status").asText());
        assertFalse(root.has("Message"));
        Map<String, String> metricMap = loadMetricMap(root);

        // database connections
        assertEquals("true", metricMap.get("database.primary:connected"));
        assertEquals("true", metricMap.get("database.non-transactional:connected"));
        assertEquals("true", metricMap.get("database.server:connected"));

        // database pools
        assertEquals("20", metricMap.get("database.primary:pool.max"));
        assertEquals("20", metricMap.get("database.non-transactional:pool.max"));
        assertEquals("20", metricMap.get("database.server:pool.max"));

        // buffer pool
        //
        // hard to know exactly what these will be in different environments, let's just make sure there is at least one
        // key in the map that starts with "buffer-pool:"
        assertTrue("At least one metric name should start with 'buffer-pool:'", containsKeyStartsWith("buffer-pool:", metricMap));

        // classloader
        String classloaderLoadedValue = metricMap.get("classloader:loaded");
        assertNotNull(classloaderLoadedValue);
        assertTrue(Long.parseLong(classloaderLoadedValue) > 0);

        // file descriptor
        String fileDescriptorUsageValue = metricMap.get("file-descriptor:usage");
        assertNotNull(fileDescriptorUsageValue);
        double fileDescriptorUsage = Double.parseDouble(fileDescriptorUsageValue);
        assertTrue(fileDescriptorUsage > 0);
        assertTrue(fileDescriptorUsage < 1);

        // garbage collector
        //
        // hard to know exactly what these will be in different environments, let's just make sure there is at least one
        // key in the map that starts with "garbage-collector"
        assertTrue("At least one metric name should start with 'garbage-collector:'", containsKeyStartsWith("garbage-collector:", metricMap));

        // memory
        String totalMemoryUsageValue = metricMap.get("memory:total.usage");
        assertNotNull(totalMemoryUsageValue);
        double totalMemoryUsage = Double.parseDouble(totalMemoryUsageValue);
        assertTrue(totalMemoryUsage > 0);
        assertTrue(totalMemoryUsage < 1);

        // uptime
        String uptimeValue = metricMap.get("runtime:uptime");
        assertNotNull(uptimeValue);
        assertTrue(Long.parseLong(uptimeValue) > 0);

        // threads
        String deadlockCountValue = metricMap.get("thread:deadlock.count");
        assertNotNull(deadlockCountValue);
        assertEquals(0, Integer.parseInt(deadlockCountValue));

    }

    @Test
    public void testService_Details_Failed_HeapMemoryThreshold() throws Exception {
        // configure "rice.health.memory.heap.usageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.HEAP_MEMORY_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);

        MockHttpServletResponse response = initAndExecuteDetailedCheck(healthServlet);
        assertEquals("Response code should be 503", 503, response.getStatus());
        JsonNode root = parseContent(response.getContentAsString());
        assertEquals("Failed", root.get("Status").asText());
        assertTrue(root.has("Message"));
    }

    // note that we don't test rice.health.memory.nonHeap.usageThreshold because the max on non-heap space is usually
    // -1 which means there is no max. In that case the health check doesn't even run

    @Test
    public void testService_Details_Failed_TotalMemoryThreshold() throws Exception {
        // configure "rice.health.memory.total.usageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.TOTAL_MEMORY_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Failed_DeadlockThreshold() throws Exception {
        // configure "rice.health.thread.deadlockThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.DEADLOCK_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Failed_FileDescriptorThreshold() throws Exception {
        // configure "rice.health.fileDescriptor.usageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.FILE_DESCRIPTOR_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Failed_PrimaryPoolUsageThreshold() throws Exception {
        // configure "rice.health.database.primary.connectionPoolUsageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.PRIMARY_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Failed_NonTransactionalPoolUsageThreshold() throws Exception {
        // configure "rice.health.database.nonTransactional.connectionPoolUsageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.NON_TRANSACTIONAL_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Failed_ServerPoolUsageThreshold() throws Exception {
        // configure "rice.health.database.server.connectionPoolUsageThreshold" to a threshold that we know will fail
        this.config.putProperty(HealthServlet.Config.SERVER_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);
        assertFailedResponse(healthServlet);
    }

    @Test
    public void testService_Details_Multiple_Failures() throws Exception {
        // configure all of the connection pool health checks so that they fail
        this.config.putProperty(HealthServlet.Config.PRIMARY_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        this.config.putProperty(HealthServlet.Config.NON_TRANSACTIONAL_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        this.config.putProperty(HealthServlet.Config.SERVER_POOL_USAGE_THRESHOLD_PROPERTY, "0");
        ConfigContext.init(this.config);

        MockHttpServletResponse response = initAndExecuteDetailedCheck(healthServlet);
        assertEquals("Response code should be 503", 503, response.getStatus());
        JsonNode root = parseContent(response.getContentAsString());
        assertEquals("Failed", root.get("Status").asText());
        assertTrue(root.has("Message"));
        String message = root.get("Message").asText();
        assertFalse(StringUtils.isBlank(message));

        Pattern pattern = Pattern.compile("\\* database\\.primary:pool\\.usage -> .+");
        Matcher matcher = pattern.matcher(message);
        assertTrue(matcher.find());

        pattern = Pattern.compile("\\* database\\.non-transactional:pool\\.usage -> .+");
        matcher = pattern.matcher(message);
        assertTrue(matcher.find());

        pattern = Pattern.compile("\\* database\\.server:pool\\.usage -> .+");
        matcher = pattern.matcher(message);
        assertTrue(matcher.find());

        pattern = Pattern.compile("\\* ");
        matcher = pattern.matcher(message);
        // find should return true three times because there should be three of them
        assertTrue(matcher.find());
        assertTrue(matcher.find());
        assertTrue(matcher.find());
        // we've found all occurrences, should return false on next invocation
        assertFalse(matcher.find());
    }

    private MockHttpServletResponse initAndExecuteDetailedCheck(HealthServlet healthServlet) throws Exception {
        healthServlet.init();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost:8080/rice-standalone/health");
        request.setMethod("GET");
        request.setParameter("detail", "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        healthServlet.service(request, response);
        String content = response.getContentAsString();
        assertEquals("application/json", response.getContentType());
        assertFalse(content.isEmpty());
        return response;
    }

    private JsonNode parseContent(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(content);

    }

    private Map<String, String> loadMetricMap(JsonNode root) {
        Map<String, String> metricMap = new HashMap<>();
        Iterator<JsonNode> metricsIt = root.get("Metrics").getElements();
        while (metricsIt.hasNext()) {
            JsonNode metricNode = metricsIt.next();
            String measure = metricNode.get("Measure").asText();
            String metric = metricNode.get("Metric").asText();
            String value = metricNode.get("Value").asText();
            metricMap.put(measure + ":" + metric, value);
        }
        return metricMap;
    }

    private void assertFailedResponse(HealthServlet healthServlet) throws Exception {
        MockHttpServletResponse response = initAndExecuteDetailedCheck(healthServlet);
        assertEquals("Response code should be 503", 503, response.getStatus());
        JsonNode root = parseContent(response.getContentAsString());
        assertEquals("Failed", root.get("Status").asText());
        assertTrue(root.has("Message"));
        assertFalse(StringUtils.isBlank(root.get("Message").asText()));
    }

    private boolean containsKeyStartsWith(String keyPrefix, Map<String, String> map) {
        for (String name : map.keySet()) {
            if (name.startsWith(keyPrefix)) {
                return true;
            }
        }
        return false;
    }

}
