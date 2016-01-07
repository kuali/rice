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
