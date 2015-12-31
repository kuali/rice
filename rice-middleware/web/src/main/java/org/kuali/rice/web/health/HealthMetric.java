package org.kuali.rice.web.health;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthMetric {

    @JsonProperty("Measure")
    private String measure;

    @JsonProperty("Metric")
    private String metric;

    @JsonProperty("Value")
    private Object value;

    public HealthMetric(String name, Object value) {
        String[] nameParts = name.split(":");
        if (nameParts.length != 2) {
            throw new IllegalArgumentException("Metric name was not valid, should be two parts separated by ':'. Instead was " + name);
        }
        this.measure = nameParts[0];
        this.metric = nameParts[1];
        this.value = value;
    }

    public HealthMetric(String measure, String metric, Object value) {
        this.measure = measure;
        this.metric = metric;
        this.value = value;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
