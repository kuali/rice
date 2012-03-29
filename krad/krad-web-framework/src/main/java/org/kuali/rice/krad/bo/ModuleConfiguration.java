/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.bo;

import org.kuali.rice.krad.datadictionary.DataDictionaryLocationConfigurer;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.EntityManager;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class contains various configuration properties for a Rice module.
 * <p>
 * The Rice framework is composed of several separate modules each of which is
 * responsible for providing a set of functionality. These include:
 *      KEW - an enterprise workflow module
 *      KIM - an identity management module
 *      KRAD - an rapid application development module
 *      as well as several others. Refer to the Rice documentation for a complete list.
 * Applications will also have their own module configurations.
 * Each module has its own namespace and application context. And may define its own data source, entity manager, data dictionary service,
 * and other properties.
 * The ModuleConfiguration is created during Spring initialization.
 *</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

	//optional
	protected String dataSourceName;

	//optional
	protected EntityManager entityManager;

	protected Map<Class, Class> externalizableBusinessObjectImplementations;

	protected boolean initializeDataDictionary;

	protected PersistenceService persistenceService;

	protected DataDictionaryService dataDictionaryService;

    /**
     *  Constructor for a ModuleConfiguration.
     *  Initializes the arrays to empty ArrayLists.
     */
	public ModuleConfiguration() {
		databaseRepositoryFilePaths = new ArrayList<String>();
		dataDictionaryPackages = new ArrayList<String>();
		scriptConfigurationFilePaths = new ArrayList<String>();
		jobNames = new ArrayList<String>();
		triggerNames = new ArrayList<String>();
	}

	/**
     * Retrieves the database repository file paths configured for this module.
     * The file paths are returned as a List of Strings. If no file paths are configured,
     * an empty list is returned.  This method should never return null.
	 * @return a List containing the databaseRepositoryFilePaths
	 */
	public List<String> getDatabaseRepositoryFilePaths() {
		return this.databaseRepositoryFilePaths;
	}

	/**
     * Sets the List of database repository file paths.
	 * @param databaseRepositoryFilePaths the List of databaseRepositoryFilePaths to set
	 */
	public void setDatabaseRepositoryFilePaths(
			List<String> databaseRepositoryFilePaths) {
		this.trimList(databaseRepositoryFilePaths);	
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	/**
     * Returns a list of data dictionary packages configured for this ModuleConfiguration.
     * If no data dictionary packages are defined, will return an empty list.
     * Should never return null.
	 * @return a List of Strings containingthe names of the dataDictionaryPackages
	 */
	public List<String> getDataDictionaryPackages() {
		return this.dataDictionaryPackages;
	}

	/**
     * Sets the list of data dictionary packages associated with this ModuleConfiguration.
	 * @param dataDictionaryPackages a List of Strings containing the dataDictionaryPackages.
	 */
	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.trimList(dataDictionaryPackages);			
		this.dataDictionaryPackages = dataDictionaryPackages;		
	}	
	
	/**
	 * @return the externalizableBusinessObjectImplementations
	 */
	public Map<Class, Class> getExternalizableBusinessObjectImplementations() {
		if (this.externalizableBusinessObjectImplementations == null)
			return null;
		return Collections.unmodifiableMap(this.externalizableBusinessObjectImplementations);
	}

	/**
	 * @param externalizableBusinessObjectImplementations the externalizableBusinessObjectImplementations to set
	 */
	public void setExternalizableBusinessObjectImplementations(
			Map<Class, Class> externalizableBusinessObjectImplementations) {
		if (externalizableBusinessObjectImplementations != null) {
			for (Class implClass : externalizableBusinessObjectImplementations.values()) {
				int implModifiers = implClass.getModifiers();
				if (Modifier.isInterface(implModifiers) || Modifier.isAbstract(implModifiers)) {
					throw new RuntimeException("Externalizable business object implementation class " +
							implClass.getName() + " must be a non-interface, non-abstract class");
				}
			}
		}
		this.externalizableBusinessObjectImplementations = externalizableBusinessObjectImplementations;
	}

	public List<String> getPackagePrefixes(){
		return this.packagePrefixes;
	}

	public void setPackagePrefixes(List<String> packagePrefixes){
		this.trimList(packagePrefixes);
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

	@Override
	public void afterPropertiesSet() throws Exception {
		if (isInitializeDataDictionary() && getDataDictionaryPackages() != null && !getDataDictionaryPackages().isEmpty() ) {
			if ( getDataDictionaryService() == null ) {
				setDataDictionaryService(KRADServiceLocatorWeb.getDataDictionaryService());
			}
			if ( getDataDictionaryService() == null ) {
				setDataDictionaryService((DataDictionaryService)applicationContext.getBean( KRADServiceLocatorWeb.DATA_DICTIONARY_SERVICE ));
			}
			DataDictionaryLocationConfigurer ddl = new DataDictionaryLocationConfigurer( getDataDictionaryService() );
			ddl.setDataDictionaryPackages(getDataDictionaryPackages());
			ddl.afterPropertiesSet();
		}
		if (getDatabaseRepositoryFilePaths() != null) {
		    for (String repositoryLocation : getDatabaseRepositoryFilePaths()) {
				// Need the OJB persistence service because it is the only one ever using the database repository files
		    	if (getPersistenceService() == null) {
		    		setPersistenceService(KRADServiceLocatorWeb.getPersistenceServiceOjb());
		    	}
		    	if ( persistenceService == null ) {
		    		setPersistenceService((PersistenceService)applicationContext.getBean( KRADServiceLocatorWeb.PERSISTENCE_SERVICE_OJB  ));
		    	}
		    	getPersistenceService().loadRepositoryDescriptor( repositoryLocation );
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the dataDictionaryService
	 */
	public DataDictionaryService getDataDictionaryService() {
		return this.dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	/**
	 * @return the persistenceService
	 */
	public PersistenceService getPersistenceService() {
		return this.persistenceService;
	}

	/**
	 * @param persistenceService the persistenceService to set
	 */
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
	 * 
	 * This method passes by reference. It will alter the list passed in.
	 * 
	 * @param stringList
	 */
	protected void trimList(List<String> stringList){
		if(stringList != null){
			// we need to trim whitespace from the stringList. Because trim() creates a new string 
			// we have to explicitly put the new string back into the list
			for(int i=0; i<stringList.size(); i++){
				String elmt = stringList.get(i);				
				elmt = elmt.trim();
				stringList.set(i, elmt);
			}			
		}
	}

}
