package org.kuali.rice.web.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.StringUtils;

/**
 * Defines the name and value for a single metric.
 *
 * All metrics must have a "Measure" name, a "Metric" name and a value. The value may be any value that translates
 * property to JSON. The value may also be null.
 *
 * This class is annotated such that it should serialize to JSON when using the Jackson library.
 *
 * @author Eric Westfall (ewestfal@gmail.com)
 */
public class HealthMetric {

    @JsonProperty("Measure")
    private final String measure;

    @JsonProperty("Metric")
    private final String metric;

    @JsonProperty("Value")
    private final Object value;

    /**
     * Construct a new HealthMetric using the given two-art name and value. The format of the name is "Measure:Metric"
     * where "Measure" and "Metric" are replaced by the desired measure and metric name.
     *
     * Invoking new HealthMetric("a:b", "c") is equivalent to invoking new HealthMetric("a", "b", "c")
     *
     * @param name the name for this health metric, must be a string that includes two non-blank parts separated by a colon
     * @param value the value of this health metric
     */
    public HealthMetric(String name, Object value) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Metric name must not be blank");
        }
        String[] nameParts = name.split(":");
        if (nameParts.length != 2 || nameParts[0].isEmpty() || nameParts[1].isEmpty()) {
            throw new IllegalArgumentException("Metric name was not valid, should be two non-blank parts separated by ':'. Instead was " + name);
        }
        this.measure = nameParts[0];
        this.metric = nameParts[1];
        this.value = value;
    }

    /**
     * Construct a new HealthMetric with the given measure name, metric name, and value.
     *
     * @param measure the name of the measure
     * @param metric the name of the metric
     * @param value the value of this health metric
     */
    public HealthMetric(String measure, String metric, Object value) {
        if (StringUtils.isBlank(measure)) {
            throw new IllegalArgumentException("measure name must not be blank");
        }
        if (StringUtils.isBlank(metric)) {
            throw new IllegalArgumentException("metric name must not be blank");
        }
        this.measure = measure;
        this.metric = metric;
        this.value = value;
    }

    public String getMeasure() {
        return measure;
    }

    public String getMetric() {
        return metric;
    }

    public Object getValue() {
        return value;
    }

}
