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

import com.codahale.metrics.*;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

/**
 * Implements an endpoint for providing health information for a Kuali Rice server.
 *
 * @author Eric Westfall
 */
public class HealthServlet extends HttpServlet {
    
    private MetricRegistry metricRegistry;
    private HealthCheckRegistry healthCheckRegistry;
    private Config config;

    @Override
    public void init() throws ServletException {
        this.metricRegistry = new MetricRegistry();
        this.healthCheckRegistry = new HealthCheckRegistry();
        this.config = new Config();

        monitorMemoryUsage();
        monitorThreads();
        monitorGarbageCollection();
        monitorBufferPools();
        monitorClassLoading();
        monitorFileDescriptors();
        monitorRuntime();
        monitorDataSources();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HealthStatus status = checkHealth();

        String includeDetail = req.getParameter("detail");
        if ("true".equals(includeDetail)) {
            if (status.isOk()) {
                resp.setStatus(200);
            } else {
                resp.setStatus(503);
            }

            ObjectMapper mapper = new ObjectMapper();
            resp.setContentType("application/json");
            mapper.writeValue(resp.getOutputStream(), status);

        } else {
            if (status.isOk()) {
                resp.setStatus(204);
            } else {
                resp.setStatus(503);
            }
        }
        resp.getOutputStream().flush();
    }

    @SuppressWarnings("unchecked")
    private void monitorMemoryUsage() {
        // registry memory metrics, we are going to rename this slightly using our format given the format required
        // by the health detail specification
        MemoryUsageGaugeSet gaugeSet = new MemoryUsageGaugeSet();
        Map<String, Metric> metrics = gaugeSet.getMetrics();
        for (String metricName : metrics.keySet()) {
            this.metricRegistry.register("memory:" + metricName, metrics.get(metricName));
        }

        Gauge<Double> heapUsage = this.metricRegistry.getGauges().get("memory:heap.usage");
        Gauge<Long> heapMaxMemory = this.metricRegistry.getGauges().get("memory:heap.max");
        if (heapMaxMemory.getValue() != -1) {
            this.healthCheckRegistry.register("memory:heap.usage", new MemoryUsageHealthCheck(heapUsage, config.heapMemoryUsageThreshold()));
        }

        Gauge<Double> nonHeapUsage = this.metricRegistry.getGauges().get("memory:non-heap.usage");
        Gauge<Long> nonHeapMaxMemory = this.metricRegistry.getGauges().get("memory:non-heap.max");
        if (nonHeapMaxMemory.getValue() != -1) {
            this.healthCheckRegistry.register("memory:non-heap.usage", new MemoryUsageHealthCheck(nonHeapUsage, config.nonHeapMemoryUsageThreshold()));
        }

        Gauge<Long> totalUsedMemory = this.metricRegistry.getGauges().get("memory:total.used");
        Gauge<Long> totalMaxMemory = this.metricRegistry.getGauges().get("memory:total.max");
        if (totalMaxMemory.getValue() != -1) {
            MemoryUsageRatio totalMemoryRatio = new MemoryUsageRatio(totalUsedMemory, totalMaxMemory);
            this.metricRegistry.register("memory:total.usage", totalMemoryRatio);
            this.healthCheckRegistry.register("memory:total.usage", new MemoryUsageHealthCheck(totalMemoryRatio, config.totalMemoryUsageThreshold()));
        }
    }

    @SuppressWarnings("unchecked")
    private void monitorThreads() {
        ThreadStatesGaugeSet gaugeSet = new ThreadStatesGaugeSet();
        Map<String, Metric> metrics = gaugeSet.getMetrics();
        for (String name : metrics.keySet()) {
            this.metricRegistry.register("thread:" + name, metrics.get(name));
        }

        // register health check for deadlock count
        String deadlockCountName = "thread:deadlock.count";
        final Gauge<Integer> deadlockCount = this.metricRegistry.getGauges().get(deadlockCountName);
        this.healthCheckRegistry.register("thread:deadlock.count", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                int numDeadlocks = deadlockCount.getValue();
                if (numDeadlocks >= config.deadlockThreshold()) {
                    return Result.unhealthy("There are " + numDeadlocks + " deadlocked threads which is greater than or equal to the threshold of " + config.deadlockThreshold());
                }
                return Result.healthy();
            }
        });
    }

    private void monitorGarbageCollection() {
        GarbageCollectorMetricSet metricSet = new GarbageCollectorMetricSet();
        Map<String, Metric> metrics = metricSet.getMetrics();
        for (String name : metrics.keySet()) {
            this.metricRegistry.register("garbage-collector:" + name, metrics.get(name));
        }
    }

    private void monitorBufferPools() {
        BufferPoolMetricSet metricSet = new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer());
        Map<String, Metric> metrics = metricSet.getMetrics();
        for (String name : metrics.keySet()) {
            this.metricRegistry.register("buffer-pool:" + name, metrics.get(name));
        }

    }

    private void monitorClassLoading() {
        ClassLoadingGaugeSet metricSet = new ClassLoadingGaugeSet();
        Map<String, Metric> metrics = metricSet.getMetrics();
        for (String name : metrics.keySet()) {
            this.metricRegistry.register("classloader:" + name, metrics.get(name));
        }
    }

    private void monitorFileDescriptors() {
        final FileDescriptorRatioGauge gauge = new FileDescriptorRatioGauge();
        String name = "file-descriptor:usage";
        this.metricRegistry.register(name, gauge);
        this.healthCheckRegistry.register(name, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                double value = gauge.getValue();
                if (value >= config.fileDescriptorUsageThreshold()) {
                    return Result.unhealthy("File descriptor usage ratio of " + value + " was greater than or equal to threshold of " + config.fileDescriptorUsageThreshold());
                }
                return Result.healthy();
            }
        });
    }

    private void monitorRuntime() {
        final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        this.metricRegistry.register("runtime:uptime", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return runtime.getUptime();
            }
        });
    }

    private void monitorDataSources() {
        DataSource dataSource = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.DATASOURCE_OBJ);
        DataSource nonTransactionalDataSource = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ);
        DataSource serverDataSource = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SERVER_DATASOURCE_OBJ);
        DatabasePlatform databasePlatform = GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
        monitorDataSource("database.primary:", dataSource, databasePlatform, config.primaryConnectionPoolUsageThreshold());
        monitorDataSource("database.non-transactional:", nonTransactionalDataSource, databasePlatform, config.nonTransactionalConnectionPoolUsageThreshold());
        monitorDataSource("database.server:", serverDataSource, databasePlatform, config.serverConnectionPoolUsageThreshold());
    }

    @SuppressWarnings("unchecked")
    private void monitorDataSource(String namePrefix, DataSource dataSource, DatabasePlatform databasePlatform, double threshold) {
        if (databasePlatform != null && dataSource != null) {
            // register connection metric
            String name = namePrefix + "connected";
            DatabaseConnectionHealthGauge healthGauge = new DatabaseConnectionHealthGauge(dataSource, databasePlatform);
            this.metricRegistry.register(name, healthGauge);
            this.healthCheckRegistry.register(name, healthGauge);

            // register pool metrics
            String poolUsageName = namePrefix + DatabaseConnectionPoolMetricSet.USAGE;
            DatabaseConnectionPoolMetricSet poolMetrics = new DatabaseConnectionPoolMetricSet(namePrefix, dataSource);
            this.metricRegistry.registerAll(poolMetrics);
            Gauge<Double> poolUsage = this.metricRegistry.getGauges().get(poolUsageName);
            if (poolUsage != null) {
                this.healthCheckRegistry.register(poolUsageName, new DatabaseConnectionPoolHealthCheck(poolUsage, threshold));
            }
        }
    }

    private HealthStatus checkHealth() {
        HealthStatus status = new HealthStatus();
        runHealthChecks(status);
        reportMetrics(status);
        return status;
    }

    private void runHealthChecks(HealthStatus status) {
        Map<String, HealthCheck.Result> results = this.healthCheckRegistry.runHealthChecks();
        for (String name : results.keySet()) {
            HealthCheck.Result result = results.get(name);
            if (!result.isHealthy()) {
                status.setStatusCode(HealthStatus.FAILED);
                status.appendMessage(name, result.getMessage());
            }
        }
    }

    private void reportMetrics(HealthStatus status) {
        reportGauges(this.metricRegistry.getGauges(), status);
        reportCounters(metricRegistry.getCounters(), status);
        reportHistograms(metricRegistry.getHistograms(), status);
        reportMeters(metricRegistry.getMeters(), status);
        reportTimers(metricRegistry.getTimers(), status);
    }

    private void reportGauges(Map<String, Gauge> gaugues, HealthStatus status) {
        for (String name : gaugues.keySet()) {
            Gauge gauge = gaugues.get(name);
            status.getMetrics().add(new HealthMetric(name, gauge.getValue()));
        }
    }

    private void reportCounters(Map<String, Counter> counters, HealthStatus status) {
        for (String name : counters.keySet()) {
            Counter counter = counters.get(name);
            status.getMetrics().add(new HealthMetric(name, counter.getCount()));
        }
    }

    private void reportHistograms(Map<String, Histogram> histograms, HealthStatus status) {
        for (String name : histograms.keySet()) {
            Histogram histogram = histograms.get(name);
            status.getMetrics().add(new HealthMetric(name, histogram.getCount()));
        }
    }

    private void reportMeters(Map<String, Meter> meters, HealthStatus status) {
        for (String name : meters.keySet()) {
            Meter meter = meters.get(name);
            status.getMetrics().add(new HealthMetric(name, meter.getCount()));
        }
    }

    private void reportTimers(Map<String, Timer> timers, HealthStatus status) {
        for (String name : timers.keySet()) {
            Timer timer = timers.get(name);
            status.getMetrics().add(new HealthMetric(name, timer.getCount()));
        }
    }
    
    public static final class Config {

        public static final String HEAP_MEMORY_THRESHOLD_PROPERTY = "rice.health.memory.heap.usageThreshold";
        public static final String NON_HEAP_MEMORY_THRESHOLD_PROPERTY = "rice.health.memory.nonHeap.usageThreshold";
        public static final String TOTAL_MEMORY_THRESHOLD_PROPERTY = "rice.health.memory.total.usageThreshold";
        public static final String DEADLOCK_THRESHOLD_PROPERTY = "rice.health.thread.deadlockThreshold";
        public static final String FILE_DESCRIPTOR_THRESHOLD_PROPERTY = "rice.health.fileDescriptor.usageThreshold";
        public static final String PRIMARY_POOL_USAGE_THRESHOLD_PROPERTY = "rice.health.database.primary.connectionPoolUsageThreshold";
        public static final String NON_TRANSACTIONAL_POOL_USAGE_THRESHOLD_PROPERTY = "rice.health.database.nonTransactional.connectionPoolUsageThreshold";
        public static final String SERVER_POOL_USAGE_THRESHOLD_PROPERTY = "rice.health.database.server.connectionPoolUsageThreshold";

        private static final double HEAP_MEMORY_THRESHOLD_DEFAULT = 0.95;
        private static final double NON_HEAP_MEMORY_THRESHOLD_DEFAULT = 0.95;
        private static final double TOTAL_MEMORY_THRESHOLD_DEFAULT = 0.95;
        private static final int DEADLOCK_THRESHOLD_DEFAULT = 1;
        private static final double FILE_DESCRIPTOR_THRESHOLD_DEFAULT = 0.95;
        private static final double POOL_USAGE_THRESHOLD_DEFAULT = 1.0;


        double heapMemoryUsageThreshold() {
            return getDouble(HEAP_MEMORY_THRESHOLD_PROPERTY, HEAP_MEMORY_THRESHOLD_DEFAULT);
        }

        double nonHeapMemoryUsageThreshold() {
            return getDouble(NON_HEAP_MEMORY_THRESHOLD_PROPERTY, NON_HEAP_MEMORY_THRESHOLD_DEFAULT);
        }

        double totalMemoryUsageThreshold() {
            return getDouble(TOTAL_MEMORY_THRESHOLD_PROPERTY, TOTAL_MEMORY_THRESHOLD_DEFAULT);
        }

        int deadlockThreshold() {
            return getInt(DEADLOCK_THRESHOLD_PROPERTY, DEADLOCK_THRESHOLD_DEFAULT);
        }

        double fileDescriptorUsageThreshold() {
            return getDouble(FILE_DESCRIPTOR_THRESHOLD_PROPERTY, FILE_DESCRIPTOR_THRESHOLD_DEFAULT);
        }

        double primaryConnectionPoolUsageThreshold() {
            return getDouble(PRIMARY_POOL_USAGE_THRESHOLD_PROPERTY, POOL_USAGE_THRESHOLD_DEFAULT);
        }

        double nonTransactionalConnectionPoolUsageThreshold() {
            return getDouble(NON_TRANSACTIONAL_POOL_USAGE_THRESHOLD_PROPERTY, POOL_USAGE_THRESHOLD_DEFAULT);
        }

        double serverConnectionPoolUsageThreshold() {
            return getDouble(SERVER_POOL_USAGE_THRESHOLD_PROPERTY, POOL_USAGE_THRESHOLD_DEFAULT);
        }

        private double getDouble(String propertyName, double defaultValue) {
            String propertyValue = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
            if (propertyValue != null) {
                return Double.parseDouble(propertyValue);
            }
            return defaultValue;
        }

        private int getInt(String propertyName, int defaultValue) {
            String propertyValue = ConfigContext.getCurrentContextConfig().getProperty(propertyName);
            if (propertyValue != null) {
                return Integer.parseInt(propertyValue);
            }
            return defaultValue;
        }
        
    }

}
