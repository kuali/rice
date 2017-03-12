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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.health.HealthCheck;

import java.util.List;

/**
 * A combination of health check and gauge which will check connection with Amazon's S3 service using the provided
 * {@link AmazonS3} client.
 *
 * @author Eric Westfall
 */
public class AmazonS3ConnectionHealthGauge extends HealthCheck implements Gauge<Boolean> {

    private final AmazonS3 amazonS3;

    public AmazonS3ConnectionHealthGauge(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public Boolean getValue() {
        Result result = execute();
        return result.isHealthy();
    }

    @Override
    protected Result check() throws Exception {
        List<Bucket> buckets = amazonS3.listBuckets();
        if (buckets.isEmpty()) {
            return Result.unhealthy("Amazon S3 returned an empty list of buckets, there should be at least one.");
        }
        // if there are connection difficulties, the listBuckets method should throw an exception which is handled by the superclass execute method
        return Result.healthy();
    }

}
