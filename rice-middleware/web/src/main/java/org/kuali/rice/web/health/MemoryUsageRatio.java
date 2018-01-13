/**
 * Copyright 2005-2018 The Kuali Foundation
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
import com.codahale.metrics.RatioGauge;

/**
 * A simple {@link RatioGauge} that calculates the ratio of used memory to maximum memory.
 *
 * @author Eric Westfall
 */
public class MemoryUsageRatio extends RatioGauge {

    private final Gauge<Long> totalUsed;
    private final Gauge<Long> totalMax;

    public MemoryUsageRatio(Gauge<Long> totalUsed, Gauge<Long> totalMax) {
        this.totalUsed = totalUsed;
        this.totalMax = totalMax;
    }

    @Override
    protected Ratio getRatio() {
        return Ratio.of(totalUsed.getValue(), totalMax.getValue());
    }

}
