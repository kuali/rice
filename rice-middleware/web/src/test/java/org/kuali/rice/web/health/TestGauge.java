package org.kuali.rice.web.health;

import com.codahale.metrics.Gauge;

/**
 * A simple and mutable gauge that returns a Double value.
 *
 * @author Eric Westfall (ewestfal@gmail.com)
 */
class TestGauge implements Gauge<Double> {

    private Double value;

    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
