/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.config;

import java.util.LinkedList;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceService;

public class KNSConfigurer extends ModuleConfigurer {

	private List<String> databaseRepositoryFilePaths;

	private List<String> dataDictionaryPackages;

	private boolean loadDataDictionary = true;
	private boolean validateDataDictionary = false;
	
	private static final String KNS_SPRING_BEANS_PATH = "classpath:org/kuali/rice/kns/config/KNSSpringBeans.xml";
	
	public KNSConfigurer() {
	    super();
	    setModuleName( "KR" );
	    setHasWebInterface(true);
	    // KNS never runs in a remote mode
	    VALID_RUN_MODES.remove( REMOTE_RUN_MODE );
    }
	
	@Override
	public String getSpringFileLocations(){
		// TODO: check the run mode and include only the appropriate spring bean files
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
	
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		//lifecycles.add(new OJBConfigurer());
		//lifecycles.add(KNSResourceLoaderFactory.createRootKNSResourceLoader());
		if (isLoadDataDictionary()) {
			lifecycles.add(new Lifecycle() {
				boolean started = false;

				public boolean isStarted() {
					return this.started;
				}

				public void start() throws Exception {
					if (getDatabaseRepositoryFilePaths() != null) {
						for (String repositoryFilePath : getDatabaseRepositoryFilePaths()) {
					    	PersistenceService persistenceService = KNSServiceLocator.getPersistenceServiceOjb();
					    	if ( persistenceService == null ) {
					    		persistenceService = (PersistenceService)RiceResourceLoaderFactory.getSpringResourceLoader().getContext().getBean( KNSServiceLocator.PERSISTENCE_SERVICE_OJB  );
					    	}
					    	persistenceService.loadRepositoryDescriptor( repositoryFilePath );
						}
					}
					
					this.started = true;
				}

				public void stop() throws Exception {
					this.started = false;
				}
			});
		}
		return lifecycles;
	}

	/**
     * Used to "poke" the Data Dictionary again after the Spring Context is initialized.  This is to
     * allow for modules loaded with KualiModule after the KNS has already been initialized to work.
     */
    @Override
    public void onEvent(RiceConfigEvent event) throws Exception {
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
    				dds.getDataDictionary().validateDD();
    			}
    		}
    	}
    }
	
	public List<String> getDatabaseRepositoryFilePaths() {
		return databaseRepositoryFilePaths;
	}

	public void setDatabaseRepositoryFilePaths(List<String> databaseRepositoryFilePaths) {
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	public List<String> getDataDictionaryPackages() {
		return dataDictionaryPackages;
	}

	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

	/**
	 * @return the loadDataDictionary
	 */
	public boolean isLoadDataDictionary() {
		return this.loadDataDictionary;
	}

	/**
	 * @param loadDataDictionary the loadDataDictionary to set
	 */
	public void setLoadDataDictionary(boolean loadDataDictionary) {
		this.loadDataDictionary = loadDataDictionary;
	}

	/**
	 * @return the validateDataDictionary
	 */
	public boolean isValidateDataDictionary() {
		return this.validateDataDictionary;
	}

	/**
	 * @param validateDataDictionary the validateDataDictionary to set
	 */
	public void setValidateDataDictionary(boolean validateDataDictionary) {
		this.validateDataDictionary = validateDataDictionary;
	}
}