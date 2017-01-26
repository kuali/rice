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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link MemoryUsageRatio}
 *
 * @author Eric Westfall
 */
public class MemoryUsageRatioTest {

    @Test
    public void testGetValue() {
        MemoryUsageRatio ratioGauge = new MemoryUsageRatio(
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return 1L;
                }
            },
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return 2L;
                }
            }
        );
        assertEquals(0.5, ratioGauge.getValue().doubleValue(), 0);
    }


}
