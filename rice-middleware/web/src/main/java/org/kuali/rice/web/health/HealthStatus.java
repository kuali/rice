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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates the health status of the application.
 *
 * Can be one of either {@link #OK} or {@link #FAILED}. This class is designed to be serializable to JSON if using
 * the Jackson library.
 *
 * @author Eric Westfall
 */
public class HealthStatus {

    public static final String OK = "Ok";
    public static final String FAILED = "Failed";

    @JsonProperty("Status")
    private String statusCode;

    @JsonProperty("Message")
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    private String message;

    @JsonProperty("Metrics")
    private List<HealthMetric> metrics;

    public HealthStatus() {
        this(OK);
    }

    public HealthStatus(String statusCode) {
        setStatusCode(statusCode);
        setMetrics(new ArrayList<HealthMetric>());
    }

    @JsonIgnore
    public boolean isOk() {
        return OK.equals(statusCode);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        if (!statusCode.equals(OK) && !statusCode.equals(FAILED)) {
            throw new IllegalArgumentException("Status code must be one of '" + OK + "' or '" + FAILED + "'");
        }
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
        if (metrics == null) {
            throw new IllegalArgumentException("metrics list must not be null");
        }
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
