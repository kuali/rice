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
package org.kuali.rice.coreservice.impl.config;

import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows for configuring a client to integrate with the "core services" module in Kuali Rice.
 *
 * <p>The CoreServiceConfigurer supports two run modes:
 * <ol>
 * <li>REMOTE - loads the client which interacts remotely with the services</li>
 * <li>LOCAL - loads the service implementations and web components locally</li>
 * </ol>
 * </p>
 *
 * <p>Client applications should generally only use "remote" run mode (which is the default).</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CoreServiceConfigurer extends ModuleConfigurer {

    private static final String MODULE_NAME = "coreservice";
    // Added a datasource property so users can override which datasource the CoreService module uses
    public static final String CORESERVICE_DATASOURCE_OBJ = "coreService.datasource";

    private DataSource dataSource;

    public CoreServiceConfigurer() {
        super(MODULE_NAME);
        setValidRunModes(Arrays.asList(RunMode.REMOTE, RunMode.LOCAL));
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        LOG.info("CoreServiceConfigurer:getPrimarySpringFiles: getRunMode => " + getRunMode());
        List<String> springFileLocations = new ArrayList<String>();
        if (RunMode.REMOTE == getRunMode()) {
            springFileLocations.add(getDefaultConfigPackagePath() + "CoreServiceRemoteSpringBeans.xml");
        } else if (RunMode.LOCAL == getRunMode()) {
            springFileLocations.add(getDefaultConfigPackagePath() + "CoreServiceLocalSpringBeans.xml");
        }
        return springFileLocations;
    }

    @Override
    protected void addAdditonalToConfig() {
        super.addAdditonalToConfig();
        configureDataSource();
    }

    private void configureDataSource() {
        if (getDataSource() != null) {
            ConfigContext.getCurrentContextConfig().putObject(CORESERVICE_DATASOURCE_OBJ, getDataSource());
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
