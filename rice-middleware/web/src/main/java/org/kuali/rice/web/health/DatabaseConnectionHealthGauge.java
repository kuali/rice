/**
 * Copyright 2005-2018 The Kuali Foundation
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

import com.codahale.metrics.Gauge;
import com.codahale.metrics.health.HealthCheck;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * A combination of health check and gauge which will check for successful connection to the given {@link DataSource}
 * using the validation query defined on the given {@link DatabasePlatform}.
 *
 * @author Eric Westfall
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
