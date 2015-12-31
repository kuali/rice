package org.kuali.rice.web.health;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.RatioGauge;

public class MemoryUsageRatio extends RatioGauge {

    private final Gauge<Long> totalUsed;
    private final Gauge<Long> totalMax;

    public MemoryUsageRatio(Gauge<Long> totalUsed, Gauge<Long> totalMax) {
        this.totalUsed = totalUsed;
        this.totalMax = totalMax;
    }

    @Override
    public Ratio getRatio() {
        return Ratio.of(totalUsed.getValue(), totalMax.getValue());
    }

}
