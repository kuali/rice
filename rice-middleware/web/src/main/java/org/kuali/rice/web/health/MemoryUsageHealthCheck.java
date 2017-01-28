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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.health.HealthCheck;

/**
 * A health check that checks whether the given {@link Gauge} (which should represent percentage usage of memory)
 * returns a value that is below the supplied unhealthy threshold.
 *
 * @author Eric Westfall
 */
public class MemoryUsageHealthCheck extends HealthCheck {

    private final Gauge<Double> gauge;
    private final double unhealthyThreshold;

    public MemoryUsageHealthCheck(Gauge<Double> gauge, double unhealthyThreshold) {
        this.gauge = gauge;
        this.unhealthyThreshold = unhealthyThreshold;
    }

    @Override
    protected Result check() throws Exception {
        Double value = gauge.getValue();
        if (value >= unhealthyThreshold) {
            return Result.unhealthy("Memory usage ratio of " + value + " was greater than or equal to threshold of " + unhealthyThreshold);
        }
        return Result.healthy();
    }
}
