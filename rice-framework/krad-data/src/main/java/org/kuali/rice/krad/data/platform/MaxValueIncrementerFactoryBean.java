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
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;

/**
 * A factory bean for putting a {@link DataFieldMaxValueIncrementer} in the Spring context.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaxValueIncrementerFactoryBean implements FactoryBean<DataFieldMaxValueIncrementer>, InitializingBean {

    private DataSource dataSource;
    private String incrementerName;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataFieldMaxValueIncrementer getObject() throws Exception {
        return MaxValueIncrementerFactory.getIncrementer(dataSource, incrementerName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<DataFieldMaxValueIncrementer> getObjectType() {
        return DataFieldMaxValueIncrementer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSingleton() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (dataSource == null) {
            throw new IllegalStateException("Property 'dataSource' is required");
        }

        if (StringUtils.isBlank(incrementerName)) {
            throw new IllegalStateException("Property 'incrementerName' is required");
        }
    }

    /**
     * Gets the {@link DataSource} for which to retrieve the incrementer.
     *
     * @return the {@link DataSource} for which to retrieve the incrementer.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Setter for the {@link DataSource}.
     *
     * @param dataSource the {@link DataSource} for which to retrieve the incrementer.
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Gets the name of the incrementer.
     *
     * @return the name of the incrementer.
     */
    public String getIncrementerName() {
        return incrementerName;
    }

    /**
     * Setter for the incrementer name.
     *
     * @param incrementerName the name of the incrementer.
     */
    public void setIncrementerName(String incrementerName) {
        this.incrementerName = incrementerName;
    }
}