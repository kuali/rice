/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kns.config;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.impl.config.module.ModuleConfigurer;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.KNSConstants;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class KNSConfigurer extends ModuleConfigurer {

	private DataSource applicationDataSource;
	private DataSource serverDataSource;

	private static final String KNS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml";
	private static final String KNS_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSServiceBusSpringBeans.xml";

	@Override
	public void addAdditonalToConfig() {
		configureDataSource();
	}

	@Override
	public List<String> getPrimarySpringFiles() {
		final List<String> springFileLocations = new ArrayList<String>();
		//springFileLocations.add(KNS_SPRING_BEANS_PATH);

		if ( isExposeServicesOnBus() ) {
			springFileLocations.add(KNS_KSB_SPRING_BEANS_PATH);
		}
		return springFileLocations;
	}
	
	@Override
	public void doAdditionalContextStartedLogic() {
		loadDataDictionary();
	}
	
	/**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     * 
     * Also initializes the DateTimeService
     */
    private void loadDataDictionary() {
		if (isLoadDataDictionary()) {
            LOG.info("KNS Configurer - Loading DD");
			DataDictionaryService dds = KNSServiceLocatorWeb.getDataDictionaryService();
			if ( dds == null ) {
				dds = (DataDictionaryService) GlobalResourceLoader.getService( KNSServiceLocatorWeb.DATA_DICTIONARY_SERVICE );
			}
			dds.getDataDictionary().parseDataDictionaryConfigurationFiles(false);

			if ( isValidateDataDictionary() ) {
                LOG.info("KNS Configurer - Validating DD");
				dds.getDataDictionary().validateDD( isValidateDataDictionaryEboReferences() );
			}
			// KULRICE-4513 After the Data Dictionary is loaded and validated, perform Data Dictionary bean overrides.
			dds.getDataDictionary().performBeanOverrides();
		}		
	}

   /**
     * Returns true - KNS UI should always be included.
     *
     * @see org.kuali.rice.core.api.config.ModuleConfigurer#shouldRenderWebInterface()
     */
    @Override
    public boolean shouldRenderWebInterface() {
        return true;
    }
    
	public boolean isLoadDataDictionary() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("load.data.dictionary")).booleanValue();
	}

	public boolean isValidateDataDictionary() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("validate.data.dictionary")).booleanValue();
	}

	public boolean isValidateDataDictionaryEboReferences() {
		return Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("validate.data.dictionary.ebo.references")).booleanValue();
	}

	/**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     * 
     * Also initializes the DateTimeService
     */
    protected void configureDataSource() {
        if (getApplicationDataSource() != null && getServerDataSource() == null) {
            throw new ConfigurationException("An application data source was defined but a server data source was not defined.  Both must be specified.");
        }
        if (getApplicationDataSource() == null && getServerDataSource() != null) {
            throw new ConfigurationException("A server data source was defined but an application data source was not defined.  Both must be specified.");
        }

        if (getApplicationDataSource() != null) {
        	ConfigContext.getCurrentContextConfig().putObject(KNSConstants.KNS_APPLICATION_DATASOURCE, getApplicationDataSource());
        }
        if (getServerDataSource() != null) {
        	ConfigContext.getCurrentContextConfig().putObject(KNSConstants.KNS_SERVER_DATASOURCE, getServerDataSource());
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
}
