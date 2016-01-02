package org.kuali.rice.web.health;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.health.HealthCheck;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * A combination of health check and gauge which will check for successful connection to the given {@link DataSource}
 * using the validation query defined on the given {@link DatabasePlatform}.
 *
 * @author Eric Westfall (ewestfal@gmail.com)
 */
public class DatabaseConnectionHealthGauge extends HealthCheck implements Gauge<Boolean> {

    private final DataSource dataSource;
    private final DatabasePlatform platform;

    public DatabaseConnectionHealthGauge(DataSource dataSource, DatabasePlatform platform) {
        this.dataSource = dataSource;
        this.platform = platform;
    }

    @Override
    public Boolean getValue() {
        Result result = execute();
        return result.isHealthy();
    }

    @Override
    protected Result check() throws Exception {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute(platform.getValidationQuery());
        // if it's unhealthy, above method will throw an exception
        return Result.healthy();
    }

}
