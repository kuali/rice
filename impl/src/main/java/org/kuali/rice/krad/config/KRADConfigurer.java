/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.config;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.config.module.ModuleConfigurer;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KRADConfigurer extends ModuleConfigurer {

    private DataSource applicationDataSource;
    private DataSource serverDataSource;

    private boolean includeKnsSpringBeans;

    private static final String KRAD_SPRING_BEANS_PATH = "classpath:org/kuali/rice/krad/config/KRADSpringBeans.xml";
    private static final String KNS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml";

    @Override
    public void addAdditonalToConfig() {
        configureDataSource();
    }

    @Override
    public List<String> getPrimarySpringFiles() {
        final List<String> springFileLocations = new ArrayList<String>();
        springFileLocations.add(KRAD_SPRING_BEANS_PATH);

        /*if (isExposeServicesOnBus()) {
            //TODO FIXME hack!  KRAD should not be loading core!  (needed for now to publish core services)
            springFileLocations.add("classpath:org/kuali/rice/core/config/COREServiceBusSpringBeans.xml");
        }*/

        if (isIncludeKnsSpringBeans()) {
            springFileLocations.add(KNS_SPRING_BEANS_PATH);
        }

        return springFileLocations;
    }

    @Override
    public void doAdditionalContextStartedLogic() {
        loadDataDictionary();
        publishDataDictionaryComponents();
    }

    /**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     *
     * Also initializes the DateTimeService
     */
    protected void loadDataDictionary() {
        if (isLoadDataDictionary()) {
            LOG.info("KRAD Configurer - Loading DD");
            DataDictionaryService dds = KRADServiceLocatorWeb.getDataDictionaryService();
            if (dds == null) {
                dds = (DataDictionaryService) GlobalResourceLoader
                        .getService(KRADServiceLocatorWeb.DATA_DICTIONARY_SERVICE);
            }
            dds.getDataDictionary().parseDataDictionaryConfigurationFiles(false);

            if (isValidateDataDictionary()) {
                LOG.info("KRAD Configurer - Validating DD");
                dds.getDataDictionary().validateDD(isValidateDataDictionaryEboReferences());
            }

            // KULRICE-4513 After the Data Dictionary is loaded and validated, perform Data Dictionary bean overrides.
            dds.getDataDictionary().performBeanOverrides();
        }
    }

    protected void publishDataDictionaryComponents() {
        if (isComponentPublishingEnabled()) {
            long delay = getComponentPublishingDelay();
            LOG.info("Publishing of Data Dictionary components is enabled, scheduling publish after " + delay + " millisecond delay");
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            try {
                scheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        long s = System.currentTimeMillis();
                        LOG.info("Executing scheduled Data Dictionary component publishing...");
                        try {
                            KRADServiceLocatorInternal.getDataDictionaryComponentPublisherService().publishAllComponents();
                        } catch (RuntimeException e) {
                            LOG.error("Failed to publish data dictionary components.", e);
                            throw e;
                        } finally {
                            long e = System.currentTimeMillis();
                            LOG.info("... finished scheduled execution of Data Dictionary component publishing.  Took " + (e-s) + " milliseconds");
                        }
                    }
                }, delay, TimeUnit.MILLISECONDS);
            } finally {
                scheduler.shutdown();
            }
        }
    }

    /**
     * Returns true - KNS UI should always be included.
     *
     * @see org.kuali.rice.core.framework.config.module.ModuleConfigurer#shouldRenderWebInterface()
     */
    @Override
    public boolean shouldRenderWebInterface() {
        return true;
    }

    public boolean isLoadDataDictionary() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("load.data.dictionary", true);
    }

    public boolean isValidateDataDictionary() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("validate.data.dictionary", false);
    }

    public boolean isValidateDataDictionaryEboReferences() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty("validate.data.dictionary.ebo.references",
                false);
    }

    public boolean isComponentPublishingEnabled() {
        return ConfigContext.getCurrentContextConfig().getBooleanProperty(KRADConstants.Config.COMPONENT_PUBLISHING_ENABLED, false);
    }

    public long getComponentPublishingDelay() {
        return ConfigContext.getCurrentContextConfig().getNumericProperty(KRADConstants.Config.COMPONENT_PUBLISHING_DELAY, 0);
    }

    /**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     *
     * Also initializes the DateTimeService
     */
    protected void configureDataSource() {
        if (getApplicationDataSource() != null && getServerDataSource() == null) {
            throw new ConfigurationException(
                    "An application data source was defined but a server data source was not defined.  Both must be specified.");
        }
        if (getApplicationDataSource() == null && getServerDataSource() != null) {
            throw new ConfigurationException(
                    "A server data source was defined but an application data source was not defined.  Both must be specified.");
        }

        if (getApplicationDataSource() != null) {
            ConfigContext.getCurrentContextConfig()
                    .putObject(KRADConstants.KRAD_APPLICATION_DATASOURCE, getApplicationDataSource());
        }
        if (getServerDataSource() != null) {
            ConfigContext.getCurrentContextConfig()
                    .putObject(KRADConstants.KRAD_SERVER_DATASOURCE, getServerDataSource());
        }
    }

    public DataSource getApplicationDataSource() {
        return this.applicationDataSource;
    }

    public DataSource getServerDataSource() {
        return this.serverDataSource;
    }

    public void setApplicationDataSource(DataSource applicationDataSource) {
        this.applicationDataSource = applicationDataSource;
    }

    public void setServerDataSource(DataSource serverDataSource) {
        this.serverDataSource = serverDataSource;
    }

    /**
     * Indicates whether the legacy KNS module should be included which will include
     * the KNS spring beans file
     *
     * @return boolean true if kns should be supported, false if not
     */
    public boolean isIncludeKnsSpringBeans() {
        return includeKnsSpringBeans;
    }

    /**
     * Setter for the include kns support indicator
     *
     * @param includeKnsSpringBeans
     */
    public void setIncludeKnsSpringBeans(boolean includeKnsSpringBeans) {
        this.includeKnsSpringBeans = includeKnsSpringBeans;
    }
}
