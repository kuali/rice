package org.kuali.rice.web.health;

import com.amazonaws.services.s3.AmazonS3;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 */
public class HealthServlet extends HttpServlet {

    // TODO make these defaults and then make them configurable
    private static final double HEAP_MEMORY_THRESHOLD = 0.95;
    private static final double NON_HEAP_MEMORY_THRESHOLD = 0.95;
    private static final double TOTAL_MEMORY_THRESHOLD = 0.95;
    private static final int DEADLOCK_THRESHOLD = 1;
    private static final double FILE_DESCRIPTOR_THRESHOLD = 0.95;
    private static final double POOL_USAGE_THRESHOLD = 1.0;

    private MetricRegistry metricRegistry;
    private HealthCheckRegistry healthCheckRegistry;

    @Override
    public void init() throws ServletException {
        this.metricRegistry = new MetricRegistry();
        this.healthCheckRegistry = new HealthCheckRegistry();

        monitorMemoryUsage();
        monitorThreads();
        monitorGarbageCollection();
        monitorBufferPools();
        monitorClassLoading();
        monitorFileDescriptors();
        monitorRuntime();
        monitorDataSources();
        monitorAmazonS3();
        monitorRedis();
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
        // registry memory metrics, we are going to rename this slightly using our format given the two name nature
        // of the metrics required by the health detail specification
        MemoryUsageGaugeSet gaugeSet = new MemoryUsageGaugeSet();
        Map<String, Metric> metrics = gaugeSet.getMetrics();
        for (String metricName : metrics.keySet()) {
            this.metricRegistry.register("memory:" + metricName, metrics.get(metricName));
        }

        Gauge<Double> heapUsage = this.metricRegistry.getGauges().get("memory:heap.usage");
        Gauge<Long> heapMaxMemory = this.metricRegistry.getGauges().get("memory:heap.max");
        if (heapMaxMemory.getValue() != -1) {
            this.healthCheckRegistry.register("memory:heap.usage", new MemoryUsageHealthCheck(heapUsage, HEAP_MEMORY_THRESHOLD));
        }

        Gauge<Double> nonHeapUsage = this.metricRegistry.getGauges().get("memory:non-heap.usage");
        Gauge<Long> nonHeapMaxMemory = this.metricRegistry.getGauges().get("memory:non-heap.max");
        if (nonHeapMaxMemory.getValue() != -1) {
            this.healthCheckRegistry.register("memory:non-heap.usage", new MemoryUsageHealthCheck(nonHeapUsage, NON_HEAP_MEMORY_THRESHOLD));
        }

        Gauge<Long> totalUsedMemory = this.metricRegistry.getGauges().get("memory:total.used");
        Gauge<Long> totalMaxMemory = this.metricRegistry.getGauges().get("memory:total.max");
        if (totalMaxMemory.getValue() != -1) {
            MemoryUsageRatio totalMemoryRatio = new MemoryUsageRatio(totalUsedMemory, totalMaxMemory);
            this.metricRegistry.register("memory:total.usage", totalMemoryRatio);
            this.healthCheckRegistry.register("memory:total.usage", new MemoryUsageHealthCheck(totalMemoryRatio, TOTAL_MEMORY_THRESHOLD));
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
                if (numDeadlocks >= DEADLOCK_THRESHOLD) {
                    return Result.unhealthy("There are " + numDeadlocks + " deadlocked threads which is greater than or equal to the threshold of " + DEADLOCK_THRESHOLD);
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
                if (value >= FILE_DESCRIPTOR_THRESHOLD) {
                    return Result.unhealthy("File descriptor usage ratio of " + value + " was greater than or equal to threshold of " + FILE_DESCRIPTOR_THRESHOLD);
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
        DataSource dataSource = (DataSource)ConfigContext.getCurrentContextConfig().getObject(RiceConstants.DATASOURCE_OBJ);
        DataSource nonTransactionalDataSource = (DataSource)ConfigContext.getCurrentContextConfig().getObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ);
        DataSource serverDataSource = (DataSource)ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SERVER_DATASOURCE_OBJ);
        DatabasePlatform databasePlatform = GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
        monitorDataSource("database.primary:", dataSource, databasePlatform);
        monitorDataSource("database.non-transactional:", nonTransactionalDataSource, databasePlatform);
        monitorDataSource("database.server:", serverDataSource, databasePlatform);
    }

    private void monitorAmazonS3() {
        // AmazonS3 may or may not be enabled, we will check
        AmazonS3 amazonS3 = GlobalResourceLoader.getService("amazonS3");
        if (amazonS3 != null) {
            AmazonS3ConnectionHealthGauge gauge = new AmazonS3ConnectionHealthGauge(amazonS3);
            String name = "amazonS3:connected";
            this.metricRegistry.register(name, gauge);
            this.healthCheckRegistry.register(name, gauge);
        }
    }

    private void monitorRedis() {
        // TODO
    }

    @SuppressWarnings("unchecked")
    private void monitorDataSource(String namePrefix, DataSource dataSource, DatabasePlatform databasePlatform) {
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
                this.healthCheckRegistry.register(poolUsageName, new DatabaseConnectionPoolHealthCheck(poolUsage, POOL_USAGE_THRESHOLD));
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

}
