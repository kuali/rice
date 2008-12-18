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
package org.kuali.rice.kns.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.datadictionary.spring.DataDictionaryLocationConfigurer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ModuleConfiguration implements InitializingBean, ApplicationContextAware {

	//protected static Logger LOG = Logger.getLogger(ModuleConfiguration.class);
	
	protected String namespaceCode;
	protected ApplicationContext applicationContext;
	
	protected List<String> packagePrefixes;

	protected List<String> databaseRepositoryFilePaths;

	protected List<String> dataDictionaryPackages;

	protected List<String> scriptConfigurationFilePaths;

	protected List<String> jobNames;

	protected List<String> triggerNames;
	
	protected Map<Class, Class> externalizableBusinessObjectImplementations;
	
	protected boolean initializeDataDictionary;

	public ModuleConfiguration() {
		databaseRepositoryFilePaths = new ArrayList<String>();
		dataDictionaryPackages = new ArrayList<String>();
		scriptConfigurationFilePaths = new ArrayList<String>();
		jobNames = new ArrayList<String>();
		triggerNames = new ArrayList<String>();
	}
	
	/**
	 * @return the databaseRepositoryFilePaths
	 */
	public List<String> getDatabaseRepositoryFilePaths() {
		return this.databaseRepositoryFilePaths;
	}

	/**
	 * @param databaseRepositoryFilePaths the databaseRepositoryFilePaths to set
	 */
	public void setDatabaseRepositoryFilePaths(
			List<String> databaseRepositoryFilePaths) {
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	/**
	 * @return the dataDictionaryPackages
	 */
	public List<String> getDataDictionaryPackages() {
		return this.dataDictionaryPackages;
	}

	/**
	 * @param dataDictionaryPackages the dataDictionaryPackages to set
	 */
	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

	/**
	 * @return the externalizableBusinessObjectImplementations
	 */
	public Map<Class, Class> getExternalizableBusinessObjectImplementations() {
		return this.externalizableBusinessObjectImplementations;
	}

	/**
	 * @param externalizableBusinessObjectImplementations the externalizableBusinessObjectImplementations to set
	 */
	public void setExternalizableBusinessObjectImplementations(
			Map<Class, Class> externalizableBusinessObjectImplementations) {
		this.externalizableBusinessObjectImplementations = externalizableBusinessObjectImplementations;
	}

	public List<String> getPackagePrefixes(){
		return this.packagePrefixes;
	}
	
	public void setPackagePrefixes(List<String> packagePrefixes){
		this.packagePrefixes = packagePrefixes;
	}
	
	public void setInitializeDataDictionary(boolean initializeDataDictionary){
		this.initializeDataDictionary = initializeDataDictionary;
	}
	
	public List<String> getScriptConfigurationFilePaths(){
		return this.scriptConfigurationFilePaths;
	}
	
	/**
	 * @return the jobNames
	 */
	public List<String> getJobNames() {
		return this.jobNames;
	}

	/**
	 * @param jobNames the jobNames to set
	 */
	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}


	/**
	 * @return the triggerNames
	 */
	public List<String> getTriggerNames() {
		return this.triggerNames;
	}

	/**
	 * @param triggerNames the triggerNames to set
	 */
	public void setTriggerNames(List<String> triggerNames) {
		this.triggerNames = triggerNames;
	}

	/**
	 * @return the initializeDataDictionary
	 */
	public boolean isInitializeDataDictionary() {
		return this.initializeDataDictionary;
	}

	/**
	 * @param scriptConfigurationFilePaths the scriptConfigurationFilePaths to set
	 */
	public void setScriptConfigurationFilePaths(
			List<String> scriptConfigurationFilePaths) {
		this.scriptConfigurationFilePaths = scriptConfigurationFilePaths;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (isInitializeDataDictionary() && getDataDictionaryPackages() != null && !getDataDictionaryPackages().isEmpty() ) {
			DataDictionaryService dds = KNSServiceLocator.getDataDictionaryService();
			if ( dds == null ) {
				dds = (DataDictionaryService)applicationContext.getBean( KNSServiceLocator.DATA_DICTIONARY_SERVICE );
			}
			DataDictionaryLocationConfigurer ddl = new DataDictionaryLocationConfigurer( dds );
			ddl.setDataDictionaryPackages(getDataDictionaryPackages());
			ddl.afterPropertiesSet();
		}
		if (getDatabaseRepositoryFilePaths() != null) {
		    for (String repositoryLocation : getDatabaseRepositoryFilePaths()) {
				// Need the OJB persistence service because it is the only one ever using the database repository files
		    	PersistenceService persistenceService = KNSServiceLocator.getPersistenceServiceOjb();
		    	if ( persistenceService == null ) {
		    		persistenceService = (PersistenceService)applicationContext.getBean( KNSServiceLocator.PERSISTENCE_SERVICE_OJB  );
		    	}
		    	persistenceService.loadRepositoryDescriptor( repositoryLocation );
			}
		}
	}

	/**
	 * @return the namespaceCode
	 */
	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	/**
	 * @param namespaceCode the namespaceCode to set
	 */
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
