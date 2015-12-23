package org.kuali.rice.web.health;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class HealthStatus {

    public static final String OK = "Ok";
    public static final String FAILED = "Failed";

    @JsonProperty("Status")
    private String statusCode;
    @JsonProperty("Metrics")
    private List<HealthMetric> metrics;

    public HealthStatus(String statusCode) {
        this.statusCode = statusCode;
        this.metrics = new ArrayList<>();
    }

    @JsonIgnore
    public boolean isOk() {
        return OK.equals(statusCode);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<HealthMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<HealthMetric> metrics) {
        this.metrics = metrics;
    }

}
