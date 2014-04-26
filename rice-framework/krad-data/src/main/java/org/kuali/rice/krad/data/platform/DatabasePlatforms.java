/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.data.platform;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Contains constants and utilities related to the supported database platforms.
 *
 * <p>
 * We use a String to represent the platform name as opposed to an Enum because this allows for the potential to
 * configure and use custom platforms at runtime without requiring internal code modification to support a new platform.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DatabasePlatforms {

    /**
     * The name of the Oracle platform.
     */
    public static final String ORACLE = "Oracle";

    /**
     * The name of the MySQL platform.
     */
    public static final String MYSQL = "MySQL";

    private static final Map<DataSource, DatabasePlatformInfo> platformCache
            = Collections.synchronizedMap(new IdentityHashMap<DataSource, DatabasePlatformInfo>(8));

    /**
     * Gets the platform information from the {@link DataSource}.
     *
     * @param dataSource the {@link DataSource} to consult.
     * @return the platform information from the {@link DataSource}.
     */
    public static DatabasePlatformInfo detectPlatform(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null.");
        }
        DatabasePlatformInfo platformInfo = platformCache.get(dataSource);
        if (platformInfo == null) {
            JdbcTemplate template = new JdbcTemplate(dataSource);
                platformCache.put(dataSource, template.execute(new ConnectionCallback<DatabasePlatformInfo>() {
                            @Override
                            public DatabasePlatformInfo doInConnection(
                                    Connection connection) throws SQLException, DataAccessException {
                                DatabaseMetaData metadata = connection.getMetaData();
                                String vendorName = metadata.getDatabaseProductName();
                                int version = metadata.getDatabaseMajorVersion();
                                return new DatabasePlatformInfo(vendorName, version);
                            }
                    }));
            if (platformInfo == null) {
                platformInfo = platformCache.get(dataSource);
            }
        }
        return platformInfo;
    }

    /**
     * No-op constructor for final class.
     */
    private DatabasePlatforms() {}

}
