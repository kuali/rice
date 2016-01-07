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
