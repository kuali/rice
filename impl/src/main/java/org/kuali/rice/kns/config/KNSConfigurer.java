/*
 * Copyright 2007-2008 The Kuali Foundation
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

import javax.sql.DataSource;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

public class KNSConfigurer extends ModuleConfigurer {

	private DataSource applicationDataSource;
	private DataSource serverDataSource;

	private static final String KNS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml";
	private static final String KNS_KSB_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSServiceBusSpringBeans.xml";

	public KNSConfigurer() {
	    super();
	    setModuleName( "KR" );
	    setHasWebInterface(true);
	    // KNS never runs in a remote or thin mode
	    VALID_RUN_MODES.remove( REMOTE_RUN_MODE );
	    VALID_RUN_MODES.remove( THIN_RUN_MODE );
    }

    @Override
	public Config loadConfig(Config parentConfig) throws Exception {
        Config currentConfig = super.loadConfig(parentConfig);
        configureDataSource(currentConfig);
        return currentConfig;
    }

	@Override
	public String getSpringFileLocations(){
		if ( isExposeServicesOnBus() ) {
			return KNS_SPRING_BEANS_PATH + "," + KNS_KSB_SPRING_BEANS_PATH;
		}
		return KNS_SPRING_BEANS_PATH;
	}

   /**
     * Returns true - KNS UI should always be included.
     *
     * @see org.kuali.rice.core.config.ModuleConfigurer#shouldRenderWebInterface()
     */
    @Override
    public boolean shouldRenderWebInterface() {
        return true;
    }

    /**
     * Returns true - KNS UI should always be included.
     *
     * @see org.kuali.rice.core.config.ModuleConfigurer#hasWebInterface()
     */
    @Override
    public boolean hasWebInterface() {
        return true;
    }
    
	public boolean isLoadDataDictionary() {
		return Boolean.valueOf(this.config.getProperty("load.data.dictionary")).booleanValue();
	}

	public boolean isValidateDataDictionary() {
		return Boolean.valueOf(this.config.getProperty("validate.data.dictionary")).booleanValue();
	}

	public boolean isValidateDataDictionaryEboReferences() {
		return Boolean.valueOf(this.config.getProperty("validate.data.dictionary.ebo.references")).booleanValue();
	}

	/**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     * 
     * Also initializes the DateTimeService
     */
    @Override
    public void onEvent(RiceConfigEvent event) {
        if (event instanceof AfterStartEvent) {
    		if (isLoadDataDictionary()) {
                LOG.info("KNS Configurer - Loading DD");
    			DataDictionaryService dds = KNSServiceLocator.getDataDictionaryService();
    			if ( dds == null ) {
    				dds = (DataDictionaryService)RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBean( KNSServiceLocator.DATA_DICTIONARY_SERVICE );
    			}
    			dds.getDataDictionary().parseDataDictionaryConfigurationFiles(false);

    			if ( isValidateDataDictionary() ) {
                    LOG.info("KNS Configurer - Validating DD");
    				dds.getDataDictionary().validateDD( isValidateDataDictionaryEboReferences() );
    			}
    			// KULRICE-4513 After the Data Dictionary is loaded and validated, perform Data Dictionary bean overrides.
    			dds.getDataDictionary().performBeanOverrides();
    		}
    		KNSServiceLocator.getDateTimeService().initializeDateTimeService();
    	}
    }


    protected void configureDataSource(Config config) {
        if (getApplicationDataSource() != null && getServerDataSource() == null) {
            throw new ConfigurationException("An application data source was defined but a server data source was not defined.  Both must be specified.");
        }
        if (getApplicationDataSource() == null && getServerDataSource() != null) {
            throw new ConfigurationException("A server data source was defined but an application data source was not defined.  Both must be specified.");
        }

        if (getApplicationDataSource() != null) {
            config.putObject(KNSConstants.KNS_APPLICATION_DATASOURCE, getApplicationDataSource());
        }
        if (getServerDataSource() != null) {
            config.putObject(KNSConstants.KNS_SERVER_DATASOURCE, getServerDataSource());
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
