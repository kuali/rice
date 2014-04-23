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
package org.kuali.rice.krad.bo;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.Provider;
import org.kuali.rice.krad.data.provider.ProviderRegistry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class contains various configuration properties for a Rice module.
 *
 * <p>
 * The Rice framework is  composed of several separate modules, each of which is
 * responsible for providing a set of functionality. These include:
 * <ul>
 *      <li>KEW - the Rice enterprise workflow module
 *      <li>KIM - the Rice identity management module
 *      <li>KSB - the Rice service bus
 *      <li>KRAD - the Rice rapid application development module
 *      <li>KRMS - the Rice business rules management syste
 *      <li>eDocLite - a Rice framework for creating simple documents quickly
 *      <li>...as well as several others. Refer to the Rice documentation for a complete list.
 * </ul>
 * <br>
 * Client Applications will also have their own module configurations. A client application could create a single
 * module or multiple modules, depending on how it is organized.
 * <br>
 * This ModuleConfiguration object is created during Spring initialization. The properties of this ModuleConfiguration
 * are specified in the module's SpringBean definition XML configuration file.
 *</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ModuleConfiguration implements InitializingBean, ApplicationContextAware {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ModuleConfiguration.class);
    /**
     * the module's namespace.
     */
	protected String namespaceCode;
	protected ApplicationContext applicationContext;

    /**
     * the package name prefixes for classes used in this module
     */
	protected List<String> packagePrefixes;

    /**
     * a list of entity description files to be loaded during initialization of the persistence service.
     * <p>
     * Currently only used by OJB repository service implementation.
     * </p>
     */
	protected List<String> databaseRepositoryFilePaths;

    /**
     * the list of data dictionary packages to be loaded for this module by the data dictionary service during system
     * startup.
     */
	protected List<String> dataDictionaryPackages;

	protected List<String> scriptConfigurationFilePaths;

    protected List<String> resourceBundleNames;

	//optional
	protected String dataSourceName;

	protected Map<Class, Class> externalizableBusinessObjectImplementations;

	protected boolean initializeDataDictionary;

	protected Object persistenceService;

	protected ProviderRegistry providerRegistry;
	
    /**
     * the implementation of the data dictionary service to use for this module.
     */
	protected DataDictionaryService dataDictionaryService;

    protected List<Provider> providers = Collections.unmodifiableList(Collections.<Provider>emptyList());

    /**
     *  Constructor for a ModuleConfiguration.
     *
     *  <p>
     *  Initializes the arrays of this ModuleConfiguration to empty ArrayLists.
     *  </p>
     */
	public ModuleConfiguration() {
		databaseRepositoryFilePaths = new ArrayList<String>();
		dataDictionaryPackages = new ArrayList<String>();
		scriptConfigurationFilePaths = new ArrayList<String>();
        resourceBundleNames = new ArrayList<String>();
	}

    /**
     * Performs additional custom initialization after the bean is created and it's properties are set by the
     * Spring framework.
     *
     * <p>
     * Loads the data dictionary packages configured for this module.
     * Also loads any OJB database repository files configured.
     * </p>
     *
     * @throws Exception
     */
	@Override
    public void afterPropertiesSet() throws Exception {
        if (isInitializeDataDictionary() && getDataDictionaryPackages() != null &&
                !getDataDictionaryPackages().isEmpty()) {
            if (getDataDictionaryService() == null) {
                setDataDictionaryService(KRADServiceLocatorWeb.getDataDictionaryService());
            }

            if (getDataDictionaryService() == null) {
                setDataDictionaryService((DataDictionaryService) applicationContext.getBean(
                        KRADServiceLocatorWeb.DATA_DICTIONARY_SERVICE));
            }

            if (dataDictionaryService != null) {
                dataDictionaryService.addDataDictionaryLocations(getNamespaceCode(), getDataDictionaryPackages());
            }
        }

        loadOjbRepositoryFiles();

        if ( getProviders() != null ) {
            ProviderRegistry providerRegistry = getProviderRegistry();
            if ( providerRegistry != null ) {
                for ( Provider provider : getProviders() ) {
                    LOG.info( "Registering data module provider for module with " + getNamespaceCode() + ": " + provider);
                    providerRegistry.registerProvider(provider);
                }
            } else {
                LOG.error( "Provider registry not initialized.  Data module provider configuration will be incomplete. (" + getNamespaceCode() + ")" );
            }
        }
    }

    /**
     * This method is deprecated and won't do anything if the database repository file paths are null or empty.
     *
     * We use reflection here to avoid having to reference PersistenceService directly since it may or may not be on
     * our classpath depending on whether or not KSB is in use.
     */
    @Deprecated
    protected void loadOjbRepositoryFiles() {
        String persistenceServiceOjbName = "persistenceServiceOjb";
        if (getDatabaseRepositoryFilePaths() != null) {
            for (String repositoryLocation : getDatabaseRepositoryFilePaths()) {
                // Need the OJB persistence service because it is the only one ever using the database repository files
                if (getPersistenceService() == null) {
                    setPersistenceService(GlobalResourceLoader.getService(persistenceServiceOjbName));
                }
                if (persistenceService == null) {
                    setPersistenceService(applicationContext.getBean(persistenceServiceOjbName));
                }
                LOG.warn("Loading OJB Configuration in "
                        + getNamespaceCode()
                        + " module.  OJB is deprecated as of Rice 2.4: "
                        + repositoryLocation);
                try {
                    MethodUtils.invokeExactMethod(persistenceService, "loadRepositoryDescriptor", repositoryLocation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

	/**
     * Retrieves the database repository file paths to be used by the persistence service configured for this module.
     *
     * <p>
     * Used by the OBJ persistence service to load entity descriptors.
     * The file paths are returned as a List of Strings. If no file paths are configured,
     * an empty list is returned.  This method should never return null.
     * </p>
     *
	 * @return a List containing the databaseRepositoryFilePaths
     *
     * @deprecated OJB is deprecated
	 */
    @Deprecated
	public List<String> getDatabaseRepositoryFilePaths() {
		return this.databaseRepositoryFilePaths;
	}

	/**
     * Initializes the list of database repository files to load during persistence service initialization.
     *
     * <p>
     * The repository file names are listed in the module's Spring bean configuration file.
     * This property is set during Spring initialization.
     * </p>
     *
	 * @param databaseRepositoryFilePaths the List of entity descriptor files to load.
     *
     * @deprecated OJB is deprecated
     */
    @Deprecated
	public void setDatabaseRepositoryFilePaths(
			List<String> databaseRepositoryFilePaths) {
		this.trimList(databaseRepositoryFilePaths);
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	/**
     * Returns a list of data dictionary packages configured for this ModuleConfiguration.
     *
     * <p>
     * If no data dictionary packages are defined, will return an empty list.
     * Should never return null.
     * </p>
     *
	 * @return a List of Strings containing the names of the dataDictionaryPackages
	 */
	public List<String> getDataDictionaryPackages() {
		return this.dataDictionaryPackages;
	}

	/**
     * Initializes the list of data dictionary packages associated with this ModuleConfiguration.
     *
     * <p>
     * The data dictionary packages are listed in the module's Spring bean configuration file.
     * This property is set during Spring initialization.
     * </p>
     *
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
		if (this.externalizableBusinessObjectImplementations == null) {
            return null;
        }
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
     * List of resource bundle names that will provides messages for this module
     *
     * <p>
     * Each bundle will point to a resource property file that contain key/value message pairs. The properties
     * file should be on the classpath and the name is given by specifying the fully qualified class name
     * (dot notation).
     * </p>
     *
     * @return List<String> resource bundle names
     * @see java.util.ResourceBundle
     */
    public List<String> getResourceBundleNames() {
        return resourceBundleNames;
    }

    /**
     * Setter for the list of resource bundle names that provides messages for the module
     *
     * @param resourceBundleNames
     */
    public void setResourceBundleNames(List<String> resourceBundleNames) {
        this.resourceBundleNames = resourceBundleNames;
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
     * Sets the list of providers for this module
     * @param providers list of providers
     */
    public void setProviders(List<Provider> providers) {
        this.providers = Collections.unmodifiableList(new ArrayList<Provider>(providers));
    }

    public List<Provider> getProviders() {
        return providers;
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
     * @return the providerRegistry
     */
    public ProviderRegistry getProviderRegistry() {
        if (this.providerRegistry == null) {
            this.providerRegistry = KradDataServiceLocator.getProviderRegistry();
        }
        
        return this.providerRegistry;
    }

    /**
     * @param providerRegistry the providerRegistry to set
     */
    public void setProviderRegistry(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
	 * @return the persistenceService
	 */
    @Deprecated
	public Object getPersistenceService() {
		return this.persistenceService;
	}

	/**
	 * @param persistenceService the persistenceService to set
	 */
    @Deprecated
	public void setPersistenceService(Object persistenceService) {
		this.persistenceService = persistenceService;
	}

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                    .append("namespaceCode", namespaceCode)
                    .append("applicationContext", applicationContext.getDisplayName())
                    .append("dataSourceName", dataSourceName)
                    .toString();
    }

}
