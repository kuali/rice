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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.incrementer.AbstractColumnMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Factory for obtaining instances of {@link DataFieldMaxValueIncrementer} for a given {@link DataSource} and
 * incrementer name.
 *
 * <p>
 * These incrementers are used for getting generated incrementing values like that provided by a database-level sequence
 * generator.
 * </p>
 *
 * <p>
 * Note that not all database platforms support sequences natively, so incrementers can be returned that emulate
 * sequence-like behavior. The Spring Framework provides incrementer implementations for numerous different database
 * platforms. This classes uses {@link DatabasePlatforms} to determine the platform of the given {@link DataSource}.
 * </p>
 *
 * <p>
 * Note that this class will cache internally any incrementers for a given {@link DataSource} + Incrementer Name
 * combination.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class MaxValueIncrementerFactory {

    private static final String ID_COLUMN_NAME = "ID";

    /**
     * Prefix for property names used to identify the classname for a {@link DataFieldMaxValueIncrementer} to use for a
     * given platform.
     *
     * <p>To construct a full property name, concatenate this prefix with the platform name.</p>
     *
     * @see org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory
     */
    public static final String PLATFORM_INCREMENTER_PREFIX = "rice.krad.data.platform.incrementer.";

    private static final Map<DataSource, ConcurrentMap<String, DataFieldMaxValueIncrementer>> cache
            = Collections.synchronizedMap(new IdentityHashMap<DataSource, ConcurrentMap<String, DataFieldMaxValueIncrementer>>(8));

    /**
     * Either constructs a new incrementer or retrieves a cached instance for the given DataSource and target
     * incrementer name.
     *
     * @param dataSource the {@link DataSource} for which to retrieve the incrementer.
     * @param incrementerName the case-insensitive name of the incrementer to use, this will generally be the name of
     *        the database object which is used to implement the incrementer.
     * @return an incrementer that can be used to generate the next incremented value for the given incrementer against
     *         the specified {@link DataSource}.
     *
     * @throws IllegalArgumentException if dataSource or incrementerName are null or blank.
     */
    public static DataFieldMaxValueIncrementer getIncrementer(DataSource dataSource, String incrementerName) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource must not be null");
        }
        if (StringUtils.isBlank(incrementerName)) {
            throw new IllegalArgumentException("Incrementer name must not be null or blank");
        }

        // yes, we want to check if it's there first, then put if absent, for max speed! This is like ConcurrentMap's
        // version of double-checked locking.
        ConcurrentMap<String, DataFieldMaxValueIncrementer> incrementerCache = cache.get(dataSource);

        if (incrementerCache == null) {
            cache.put(dataSource,
                    new ConcurrentHashMap<String, DataFieldMaxValueIncrementer>(8, 0.9f, 1));
            if (incrementerCache == null) {
                incrementerCache = cache.get(dataSource);
            }
        }

        // now check if we have a cached incrementer
        DataFieldMaxValueIncrementer incrementer = incrementerCache.get(incrementerName.toUpperCase());
        if (incrementer == null) {
            incrementer = incrementerCache.putIfAbsent(incrementerName.toUpperCase(), createIncrementer(dataSource,
                    incrementerName));
            if (incrementer == null) {
                incrementer = incrementerCache.get(incrementerName.toUpperCase());
            }
        }
        return incrementer;

    }

    /**
     * Creates an {@link DataFieldMaxValueIncrementer} from a {@link DataSource}.
     *
     * @param dataSource the {@link DataSource} for which to retrieve the incrementer.
     * @param incrementerName the name of the incrementer.
     * @return an {@link DataFieldMaxValueIncrementer} from a {@link DataSource}.
     */
    private static DataFieldMaxValueIncrementer createIncrementer(DataSource dataSource, String incrementerName) {
        DatabasePlatformInfo platformInfo = DatabasePlatforms.detectPlatform(dataSource);
        DataFieldMaxValueIncrementer incrementer = getCustomizedIncrementer(platformInfo, dataSource,incrementerName,ID_COLUMN_NAME);
        if(incrementer != null){
            return incrementer;
        }

        if (DatabasePlatforms.ORACLE.equalsIgnoreCase(platformInfo.getName())) {
            incrementer = new OracleSequenceMaxValueIncrementer(dataSource, incrementerName);
        } else if (DatabasePlatforms.MYSQL.equalsIgnoreCase(platformInfo.getName())) {
            incrementer = new EnhancedMySQLMaxValueIncrementer(dataSource, incrementerName, ID_COLUMN_NAME);
        }
        if (incrementer == null) {
            throw new UnsupportedDatabasePlatformException(platformInfo);
        }
        if (incrementer instanceof InitializingBean) {
            try {
                ((InitializingBean) incrementer).afterPropertiesSet();
            } catch (Exception e) {
                throw new DataAccessResourceFailureException(
                        "Failed to initialize max value incrementer for given datasource and incrementer. dataSource="
                                + dataSource.toString()
                                + ", incrementerName = "
                                + incrementerName, e);
            }
        }
        return incrementer;
    }

    /**
     * Checks the config file for any references to
     * {@code rice.krad.data.platform.incrementer.(DATASOURCE, ex mysql, oracle).(VERSION optional)}.
     *
     * <p>If matching one found attempts to instantiate it to return back to factory for use.</p>
     *
     * @param platformInfo the {@link DatabasePlatformInfo}.
     * @param dataSource the {@link DataSource} for which to retrieve the incrementer.
     * @param incrementerName the name of the incrementer.
     * @param columnName the name of the column to increment.
     * @return a config set customized incrementer that matches and can be used to generate the next incremented value
     *         for the given incrementer against the specified {@link DataSource}
     * @throws InstantiationError if cannot instantiate passed in class.
     */
    private static DataFieldMaxValueIncrementer getCustomizedIncrementer(DatabasePlatformInfo platformInfo, DataSource dataSource, String incrementerName, String columnName){
        if(platformInfo == null){
            throw new  IllegalArgumentException("DataSource platform must not be null");
        }
        if(ConfigContext.getCurrentContextConfig() == null){
            return null;
        }
        Map<String,String> incrementerPropToIncrementer = ConfigContext.getCurrentContextConfig().
                                getPropertiesWithPrefix(PLATFORM_INCREMENTER_PREFIX, true);
        String platformNameVersion = platformInfo.getName().toLowerCase() + "." + platformInfo.getMajorVersion();
        String incrementerClassName = "";

         if(incrementerPropToIncrementer.containsKey(platformNameVersion)){
            incrementerClassName = incrementerPropToIncrementer.get(platformNameVersion);
         } else if(incrementerPropToIncrementer.containsKey(platformInfo.getName().toLowerCase())){
             incrementerClassName = incrementerPropToIncrementer.get(platformInfo.getName().toLowerCase());
         }

        if(StringUtils.isNotBlank(incrementerClassName)){
            try {
                Class incrementerClass = Class.forName(incrementerClassName);
                if(AbstractSequenceMaxValueIncrementer.class.isAssignableFrom(incrementerClass)){
                    AbstractSequenceMaxValueIncrementer abstractSequenceMaxValueIncrementer = (AbstractSequenceMaxValueIncrementer)incrementerClass.newInstance();
                    abstractSequenceMaxValueIncrementer.setDataSource(dataSource);
                    abstractSequenceMaxValueIncrementer.setIncrementerName(incrementerName);
                    return abstractSequenceMaxValueIncrementer;

                } else if(AbstractColumnMaxValueIncrementer.class.isAssignableFrom(incrementerClass)){
                    AbstractColumnMaxValueIncrementer abstractColumnMaxValueIncrementer = (AbstractColumnMaxValueIncrementer)incrementerClass.newInstance();
                    abstractColumnMaxValueIncrementer.setDataSource(dataSource);
                    abstractColumnMaxValueIncrementer.setIncrementerName(incrementerName);
                    abstractColumnMaxValueIncrementer.setColumnName(columnName);
                    return abstractColumnMaxValueIncrementer;
                } else {
                    throw new InstantiationError("Cannot create incrementer class "+incrementerClassName +" it has to extend "
                            + "AbstractSequenceMaxValueIncrementer or AbstractColumnMaxValueIncrementer");
                }
            } catch (Exception e){
                throw new InstantiationError("Could not instantiate custom incrementer "+incrementerClassName);
            }
        }
        return null;
    }

    /**
     * Defines an incrementer for MySQL.
     *
     * <p>
     * Since MySQL does not have any sense of a sequence, this class uses the concept of a sequence table, which is a
     * regular table that has an auto increment feature on it and is used only for that sequence.  When null values are
     * inserted into the table, the auto increment feature will insert the next value into that field, and then the
     * database will be queried for the last insert ID to get the next sequence value.
     * </p>
     */
    static final class EnhancedMySQLMaxValueIncrementer extends AbstractColumnMaxValueIncrementer {

        private JdbcTemplate template;

        /**
         * Creates an incrementer for MySQL.
         */
        private EnhancedMySQLMaxValueIncrementer() {}

        /**
         * Creates an incrementer for MySQL.
         *
         * @param dataSource the {@link DataSource} for which to retrieve the incrementer.
         * @param incrementerName the name of the incrementer.
         * @param columnName the name of the column to increment.
         */
        private EnhancedMySQLMaxValueIncrementer(DataSource dataSource, String incrementerName, String columnName) {
            super(dataSource, incrementerName, columnName);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void afterPropertiesSet() {
            super.afterPropertiesSet();
            template = new JdbcTemplate(getDataSource());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected synchronized long getNextKey() throws DataAccessException {
            return template.execute(new ConnectionCallback<Long>() {
                @Override
                public Long doInConnection(Connection con) throws SQLException, DataAccessException {
                    Statement statement = null;
                    ResultSet resultSet = null;
                    try {
                        statement = con.createStatement();
                        String sql = "INSERT INTO " + getIncrementerName() + " VALUES (NULL)";
                        statement.executeUpdate(sql);
                        sql = "SELECT LAST_INSERT_ID()";
                        resultSet = statement.executeQuery(sql);
                        if (resultSet != null) {
                            resultSet.first();
                            return resultSet.getLong(1);
                        } else {
                            throw new IncorrectResultSizeDataAccessException("Failed to get last_insert_id() for sequence incrementer table '" + getIncrementerName() + "'", 1);
                        }
                    } finally {
                        JdbcUtils.closeResultSet(resultSet);
                        JdbcUtils.closeStatement(statement);
                    }
                }
            }).longValue();
        }
    }

    /**
     * No-op constructor for final class.
     */
    private MaxValueIncrementerFactory() {}

}
