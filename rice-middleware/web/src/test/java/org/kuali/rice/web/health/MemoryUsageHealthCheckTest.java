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

import com.codahale.metrics.health.HealthCheck;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link MemoryUsageHealthCheck}
 *
 * @author Eric Westfall
 */
public class MemoryUsageHealthCheckTest {

    private MemoryUsageHealthCheck healthCheck;
    private TestGauge gauge;

    @Before
    public void setUp() {
        this.gauge = new TestGauge();
        this.healthCheck = new MemoryUsageHealthCheck(gauge, 0.5);
    }

    @Test
    public void testCheck_Healthy_UnderThreshold() throws Exception {
        gauge.setValue(0.25);
        HealthCheck.Result result = healthCheck.check();
        assertTrue("Result should be healthy since value is below threshold", result.isHealthy());
    }

    @Test
    public void testCheck_Unhealhty_OverThreshold() throws Exception {
        gauge.setValue(0.75);
        HealthCheck.Result result = healthCheck.check();
        assertFalse("Result should be unhealthy since value is above threshold", result.isHealthy());
    }

    @Test
    public void testCheck_Unhealhty_EqualThreshold() throws Exception {
        gauge.setValue(0.5);
        HealthCheck.Result result = healthCheck.check();
        assertFalse("Result should be unhealthy since value is equal to the threshold", result.isHealthy());
    }

}
