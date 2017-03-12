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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.codahale.metrics.health.HealthCheck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link AmazonS3ConnectionHealthGauge}.
 *
 * @author Eric Westfall
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonS3ConnectionHealthGaugeTest {

    private AmazonS3ConnectionHealthGauge gauge;

    @Mock
    private AmazonS3 amazonS3;

    @Before
    public void setUp() {
        this.gauge = new AmazonS3ConnectionHealthGauge(amazonS3);
    }

    @Test
    public void testCheck_Healthy() throws Exception {
        stubListBuckets(amazonS3, 1);
        HealthCheck.Result result = gauge.check();
        assertTrue("Result should be healthy", result.isHealthy());
    }

    @Test
    public void testCheck_Unhealthy() throws Exception {
        stubListBuckets(amazonS3, 0);
        HealthCheck.Result result = gauge.check();
        assertFalse("Result should be unhealthy", result.isHealthy());
        assertNotNull("Result should have a message", result.getMessage());
    }

    @Test
    public void testGetValue_Healthy() throws Exception {
        stubListBuckets(amazonS3, 1);
        assertTrue("Get value should return true since S3 check is healthy", gauge.getValue());
    }

    @Test
    public void testGetValue_Unhealthy_NoBuckets() throws Exception {
        stubListBuckets(amazonS3, 0);
        assertFalse("Get value should return false since S3 check is unhealthy", gauge.getValue());
    }

    @Test
    public void testGetValue_Unhealthy_Exception() throws Exception {
        when(amazonS3.listBuckets()).thenThrow(new AmazonServiceException("Failed to contact S3"));
        assertFalse("Get value should return false since S3 check threw exception", gauge.getValue());
    }

    private void stubListBuckets(AmazonS3 amazonS3, int numBuckets) {
        List<Bucket> buckets = new ArrayList<>();
        for (int i = 0; i < numBuckets; i++) {
            buckets.add(mock(Bucket.class));
        }
        when(amazonS3.listBuckets()).thenReturn(buckets);
    }





}
