package org.kuali.rice.web.health;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class HealthStatus {

    public static final String OK = "Ok";
    public static final String FAILED = "Failed";

    @JsonProperty("Status")
    private String statusCode;

    @JsonProperty("Message")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonProperty("Metrics")
    private List<HealthMetric> metrics;

    public HealthStatus() {
        this(OK);
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HealthMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<HealthMetric> metrics) {
        this.metrics = metrics;
    }

    public void appendMessage(String metricName, String message) {
        String fullMessage = "* " + metricName + " -> " + message;
        if (getMessage() == null) {
            setMessage(fullMessage);
        } else {
            setMessage(getMessage() + " " + fullMessage);
        }
    }

}
