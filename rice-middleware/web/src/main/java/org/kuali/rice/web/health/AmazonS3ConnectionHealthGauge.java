package org.kuali.rice.web.health;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.health.HealthCheck;

import java.util.List;

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
