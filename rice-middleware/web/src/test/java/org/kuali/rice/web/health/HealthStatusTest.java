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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for {@link HealthStatus}
 *
 * @author Eric Westfall
 */
public class HealthStatusTest {

    private HealthStatus status;

    @Before
    public void setUp() {
        status = new HealthStatus();
    }

    @Test
    public void testEmptyConstructor() {
        assertEquals(HealthStatus.OK, status.getStatusCode());
        assertTrue(status.isOk());
        assertEquals(0, status.getMetrics().size());
        assertNull(status.getMessage());
    }

    @Test
    public void testConstructor() {
        HealthStatus okStatus = new HealthStatus(HealthStatus.OK);
        assertEquals(HealthStatus.OK, okStatus.getStatusCode());
        assertTrue(okStatus.isOk());

        HealthStatus failedStatus = new HealthStatus(HealthStatus.FAILED);
        assertEquals(HealthStatus.FAILED, failedStatus.getStatusCode());
        assertFalse(failedStatus.isOk());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_Invalid_Status() {
        new HealthStatus("blah!");
    }

    @Test
    public void testSetStatusCode() {
        assertTrue(status.isOk());
        status.setStatusCode(HealthStatus.FAILED);
        assertFalse(status.isOk());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetStatusCode_Invalid_Code() {
        assertTrue(status.isOk());
        status.setStatusCode("bad");
    }

    @Test
    public void testSetMessage() {
        assertNull(status.getMessage());
        status.setMessage("my message");
        assertEquals("my message", status.getMessage());
        // set it back to null and make sure that works
        status.setMessage(null);
        assertNull(status.getMessage());
    }

    @Test
    public void testSetMetrics() {
        List<HealthMetric> metrics = new ArrayList<>();
        metrics.add(new HealthMetric("a", "b", "c"));
        metrics.add(new HealthMetric("1", "2", "3"));
        status.setMetrics(metrics);
        assertEquals(2, status.getMetrics().size());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSetMetrics_Null() {
        status.setMetrics(null);
    }

    @Test
    public void testAppendMessage() {
        status.appendMessage("a:b", "ab");
        assertEquals("* a:b -> ab", status.getMessage());
        status.appendMessage("1:2", "12");
        assertEquals("* a:b -> ab * 1:2 -> 12", status.getMessage());
    }

    @Test
    public void testJsonSerialization_Null_Message_Empty_Array() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(status);
        assertEquals("{\"Status\":\"Ok\",\"Metrics\":[]}", json);

        status.setStatusCode(HealthStatus.FAILED);
        json = mapper.writeValueAsString(status);
        assertEquals("{\"Status\":\"Failed\",\"Metrics\":[]}", json);

        // make sure the empty string also doesn't result in message getting serialized
        status.setMessage("");
        json = mapper.writeValueAsString(status);
        assertEquals("{\"Status\":\"Failed\",\"Metrics\":[]}", json);
    }

    @Test
    public void testJsonSerialization() throws Exception {
        status.getMetrics().add(new HealthMetric("a", "b", "c"));
        status.getMetrics().add(new HealthMetric("1", "2", "3"));
        status.appendMessage("a:b", "something's broken");
        status.setStatusCode(HealthStatus.FAILED);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(status);
        assertEquals("{\"Status\":\"Failed\",\"Message\":\"* a:b -> something's broken\"," +
                "\"Metrics\":[{\"Measure\":\"a\",\"Metric\":\"b\",\"Value\":\"c\"}," +
                "{\"Measure\":\"1\",\"Metric\":\"2\",\"Value\":\"3\"}]}", json);
    }


}
