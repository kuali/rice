package org.kuali.rice.web.health;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthMetric {

    @JsonProperty("Measure")
    private String measure;

    @JsonProperty("Metric")
    private String metric;

    @JsonProperty("Value")
    private Object value;

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
