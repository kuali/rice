package org.kuali.rice.web.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link HealthMetric}
 *
 * @author Eric Westfall (ewestfal@gmail.com)
 */
public class HealthMetricTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Null_Name() {
        new HealthMetric(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Blank_Name() {
        new HealthMetric("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Bad_Name_No_Colon() {
        new HealthMetric("blah", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Bad_Name_Before_Colon() {
        new HealthMetric(":after", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Bad_Name_After_Colon() {
        new HealthMetric("before:", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Bad_Name_Just_A_Colon() {
        new HealthMetric(":", "value");
    }

    @Test
    public void testConstructor() {
        HealthMetric metric = new HealthMetric("before:after", "value");
        assertEquals("before", metric.getMeasure());
        assertEquals("after", metric.getMetric());
        assertEquals("value", metric.getValue());
    }

    @Test
    public void testConstructor_Null_Value() {
        HealthMetric metric = new HealthMetric("before:after", null);
        assertNull(metric.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullConstructor_Null_Measure() {
        new HealthMetric(null, "metric", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullConstructor_Blank_Measure() {
        new HealthMetric("", "metric", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullConstructor_Null_Metric() {
        new HealthMetric("measure", null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFullConstructor_Blank_Metric() {
        new HealthMetric("measure", "", "value");
    }

    @Test
    public void testFullConstructor() {
        HealthMetric metric = new HealthMetric("measure", "metric", "value");
        assertEquals("measure", metric.getMeasure());
        assertEquals("metric", metric.getMetric());
        assertEquals("value", metric.getValue());
    }

    @Test
    public void testFullConstructor_Null_Value() {
        HealthMetric metric = new HealthMetric("measure", "metric", null);
        assertNull(metric.getValue());
    }

    @Test
    public void testJsonSerialization() throws Exception {
        HealthMetric metric = new HealthMetric("my-measure", "my-metric", "abcdefg");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(metric);
        assertEquals("{\"Measure\":\"my-measure\",\"Metric\":\"my-metric\",\"Value\":\"abcdefg\"}", json);
    }

    @Test
    public void testJsonSerialization_Null_Value() throws Exception {
        HealthMetric metric = new HealthMetric("my-measure", "my-metric", null);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(metric);
        assertEquals("{\"Measure\":\"my-measure\",\"Metric\":\"my-metric\",\"Value\":null}", json);
    }

}
