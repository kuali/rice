/*
 * Copyright 2005-2007 The Kuali Foundation
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

package org.kuali.rice.kns.datadictionary;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.exception.CompletionException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**
 * Collection of named BusinessObjectEntry objects, each of which contains
 * information relating to the display, validation, and general maintenance of a
 * BusinessObject.
 * 
 * 
 */
public class DataDictionary {

    private DefaultListableBeanFactory ddBeans = new DefaultListableBeanFactory();
    private XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ddBeans);

	// logger
	private static final Log LOG = LogFactory.getLog(DataDictionary.class);

	/**
	 * The encapsulation of DataDictionary indices
	 */
	private DataDictionaryIndex ddIndex = new DataDictionaryIndex(ddBeans);

	/**
	 * The DataDictionaryMapper
	 * The default mapper simply consults the initialized indices
	 * on workflow document type
	 */
	private DataDictionaryMapper ddMapper = new DataDictionaryIndexMapper();

	private List<String> configFileLocations = new ArrayList<String>();

    public DataDictionary() { }
    
	public List<String> getConfigFileLocations() {
        return this.configFileLocations;
    }

    public void setConfigFileLocations(List<String> configFileLocations) {
        this.configFileLocations = configFileLocations;
    }
    
    public void addConfigFileLocation( String location ) throws IOException {
        indexSource( location );
    }

    /**
     * Sets the DataDictionaryMapper
     * @param mapper the datadictionary mapper
     */
    public void setDataDictionaryMapper(DataDictionaryMapper mapper) {
    	this.ddMapper = mapper;
    }
    
    private void indexSource(String sourceName) throws IOException {        
        if (sourceName == null) {
            throw new DataDictionaryException("Source Name given is null");
        }

        if (!sourceName.endsWith(".xml") ) {
            Resource resource = getFileResource(sourceName);
            if (resource.exists()) {
                indexSource(resource.getFile());
            } else {
                LOG.warn("Could not find " + sourceName);
                throw new DataDictionaryException("DD Resource " + sourceName + " not found");
            }
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("adding sourceName " + sourceName + " ");
            }
            Resource resource = getFileResource(sourceName);
            if (! resource.exists()) {
                throw new DataDictionaryException("DD Resource " + sourceName + " not found");  
            }
            String indexName = sourceName.substring(sourceName.lastIndexOf("/") + 1, sourceName.indexOf(".xml"));
            configFileLocations.add( sourceName );
        }
    }    

    private Resource getFileResource(String sourceName) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(ClassLoaderUtils.getDefaultClassLoader());
        return resourceLoader.getResource(sourceName);
    }

    private void indexSource(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                indexSource(file);
            } else if (file.getName().endsWith(".xml") ) {
                configFileLocations.add( "file:" + file.getAbsolutePath());
            } else {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Skipping non xml file " + file.getAbsolutePath() + " in DD load");
                }
            }
        }
    }
    
    public void parseDataDictionaryConfigurationFiles( boolean allowConcurrentValidation ) {
        // expand configuration locations into files

        LOG.info( "Starting DD XML File Load" );
        String[] configFileLocationsArray = new String[configFileLocations.size()];
        configFileLocationsArray = configFileLocations.toArray( configFileLocationsArray );
        configFileLocations.clear(); // empty the list out so other items can be added
        try {
            xmlReader.loadBeanDefinitions( configFileLocationsArray );
        } catch (Exception e) {
            LOG.error("Error loading bean definitions", e);
            throw new DataDictionaryException("Error loading bean definitions: " + e.getLocalizedMessage());
        }
        LOG.info( "Completed DD XML File Load" );
        if ( allowConcurrentValidation ) {
            Thread t = new Thread(ddIndex);
            t.start();
        } else {
            ddIndex.run();
        }
    }
	    
    static boolean validateEBOs = true;
    
    public void validateDD( boolean validateEbos ) {
    	DataDictionary.validateEBOs = validateEbos;
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            entry.completeValidation();
        }
        Map<String,DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
        for ( DocumentEntry entry : docBeans.values() ) {
            entry.completeValidation();
        }
    }
    
    public void validateDD() {
    	DataDictionary.validateEBOs = true;
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            entry.completeValidation();
        }
        Map<String,DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
        for ( DocumentEntry entry : docBeans.values() ) {
            entry.completeValidation();
        }
    }

	/**
	 * @param className
	 * @return BusinessObjectEntry for the named class, or null if none exists
	 */
	public BusinessObjectEntry getBusinessObjectEntry(String className ) {
		return ddMapper.getBusinessObjectEntry(ddIndex, className);
	}

	/**
	 * This method gets the business object entry for a concrete class
	 * 
	 * @param className
	 * @return
	 */
	public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(String className){
		return ddMapper.getBusinessObjectEntryForConcreteClass(ddIndex, className);
	}
	
	/**
	 * @return List of businessObject classnames
	 */
	public List<String> getBusinessObjectClassNames() {
		return ddMapper.getBusinessObjectClassNames(ddIndex);
	}

	/**
	 * @return Map of (classname, BusinessObjectEntry) pairs
	 */
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return ddMapper.getBusinessObjectEntries(ddIndex);
	}

	/**
	 * @param className
	 * @return DataDictionaryEntryBase for the named class, or null if none
	 *         exists
	 */
	public DataDictionaryEntry getDictionaryObjectEntry(String className) {
		return ddMapper.getDictionaryObjectEntry(ddIndex, className);
	}

	/**
	 * Returns the KNS document entry for the given lookup key.  The documentTypeDDKey is interpreted
	 * successively in the following ways until a mapping is found (or none if found):
	 * <ol>
	 * <li>KEW/workflow document type</li>
	 * <li>business object class name</li>
	 * <li>maintainable class name</li>
	 * </ol>
	 * This mapping is compiled when DataDictionary files are parsed on startup (or demand).  Currently this
	 * means the mapping is static, and one-to-one (one KNS document maps directly to one and only
	 * one key).
	 * 
	 * @param documentTypeDDKey the KEW/workflow document type name
	 * @return the KNS DocumentEntry if it exists
	 */
	public DocumentEntry getDocumentEntry(String documentTypeDDKey ) {
		return ddMapper.getDocumentEntry(ddIndex, documentTypeDDKey);
	}

	/**
	 * Note: only MaintenanceDocuments are indexed by businessObject Class
	 * 
	 * This is a special case that is referenced in one location. Do we need
	 * another map for this stuff??
	 * 
	 * @param businessObjectClass
	 * @return DocumentEntry associated with the given Class, or null if there
	 *         is none
	 */
	public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(Class businessObjectClass) {
		return ddMapper.getMaintenanceDocumentEntryForBusinessObjectClass(ddIndex, businessObjectClass);
	}

	public Map<String, DocumentEntry> getDocumentEntries() {
		return ddMapper.getDocumentEntries(ddIndex);
	}

    /**
     * @param clazz
     * @param propertyName
     * @return true if the given propertyName names a property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public static boolean isPropertyOf(Class targetClass, String propertyName) {
        if (targetClass == null) {
            throw new IllegalArgumentException("invalid (null) targetClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);

        boolean isPropertyOf = (propertyDescriptor != null);
        return isPropertyOf;
    }

    /**
     * @param clazz
     * @param propertyName
     * @return true if the given propertyName names a Collection property of the given class
     * @throws CompletionException if there is a problem accessing the named property on the given class
     */
    public static boolean isCollectionPropertyOf(Class targetClass, String propertyName) {
        boolean isCollectionPropertyOf = false;

        PropertyDescriptor propertyDescriptor = buildReadDescriptor(targetClass, propertyName);
        if (propertyDescriptor != null) {
            Class clazz = propertyDescriptor.getPropertyType();

            if ((clazz != null) && Collection.class.isAssignableFrom(clazz)) {
                isCollectionPropertyOf = true;
            }
        }

        return isCollectionPropertyOf;
    }

    public static PersistenceStructureService persistenceStructureService;
    
    /**
     * @return the persistenceStructureService
     */
    public static PersistenceStructureService getPersistenceStructureService() {
        if ( persistenceStructureService == null ) {
            persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
        }
        return persistenceStructureService;
    }
    
    /**
     * This method determines the Class of the attributeName passed in. Null will be returned if the member is not available, or if
     * a reflection exception is thrown.
     * 
     * @param rootClass - Class that the attributeName property exists in.
     * @param attributeName - Name of the attribute you want a class for.
     * @return The Class of the attributeName, if the attribute exists on the rootClass. Null otherwise.
     */
    public static Class getAttributeClass(Class boClass, String attributeName) {

        // fail loudly if the attributeName isnt a member of rootClass
        if (!isPropertyOf(boClass, attributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootClass '" + boClass.getName() + "'");
        }

    	//Implementing Externalizable Business Object Services...
        //The boClass can be an interface, hence handling this separately, 
        //since the original method was throwing exception if the class could not be instantiated.
        if(boClass.isInterface())
        	return getAttributeClassWhenBOIsInterface(boClass, attributeName);
        else
        	return getAttributeClassWhenBOIsClass(boClass, attributeName);        	

    }

    /**
     * 
     * This method gets the property type of the given attributeName when the bo class is a concrete class
     * 
     * @param boClass
     * @param attributeName
     * @return
     */
    private static Class getAttributeClassWhenBOIsClass(Class boClass, String attributeName){
    	BusinessObject boInstance;
        try {
            boInstance = (BusinessObject) boClass.newInstance();
        } catch (Exception e) {
        	throw new RuntimeException("Unable to instantiate BO: " + boClass, e);
        }

        // attempt to retrieve the class of the property
        try {
            return ObjectUtils.getPropertyType(boInstance, attributeName, getPersistenceStructureService());
        } catch (Exception e) {
            throw new RuntimeException("Unable to determine property type for: " + boClass.getName() + "." + attributeName, e);
        }
    }

    /**
     * 
     * This method gets the property type of the given attributeName when the bo class is an interface
     * This method will also work if the bo class is not an interface, 
     * but that case requires special handling, hence a separate method getAttributeClassWhenBOIsClass 
     * 
     * @param boClass
     * @param attributeName
     * @return
     */
    private static Class getAttributeClassWhenBOIsInterface(Class boClass, String attributeName){
        if (boClass == null) {
            throw new IllegalArgumentException("invalid (null) boClass");
        }
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = attributeName.split("\\.");
        int lastLevel = intermediateProperties.length - 1;
        Class currentClass = boClass;

        for (int i = 0; i <= lastLevel; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);

            if (propertyDescriptor != null) {

                Class propertyType = propertyDescriptor.getPropertyType();
                if ( propertyType.equals( PersistableBusinessObjectExtension.class ) ) {
                    propertyType = getPersistenceStructureService().getBusinessObjectAttributeClass( currentClass, currentPropertyName );                    
                }
                if (Collection.class.isAssignableFrom(propertyType)) {
                	// TODO: determine property type using generics type definition
                	throw new AttributeValidationException("Can't determine the Class of Collection elements because when the business object is an (possibly ExternalizableBusinessObject) interface.");
                }
                else {
                    currentClass = propertyType;
                }
            }
            else {
            	throw new AttributeValidationException("Can't find getter method of " + boClass.getName() + " for property " + attributeName);
            }
        }
        return currentClass;
    }
    
    /**
     * This method determines the Class of the elements in the collectionName passed in.
     * 
     * @param boClass Class that the collectionName collection exists in.
     * @param collectionName the name of the collection you want the element class for
     * @return
     */
    public static Class getCollectionElementClass(Class boClass, String collectionName) {
        if (boClass == null) {
            throw new IllegalArgumentException("invalid (null) boClass");
        }
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = collectionName.split("\\.");
        Class currentClass = boClass;

        for (int i = 0; i <intermediateProperties.length; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);


                if (propertyDescriptor != null) {

                    Class type = propertyDescriptor.getPropertyType();
                    if (Collection.class.isAssignableFrom(type)) {

                        if (getPersistenceStructureService().isPersistable(currentClass)) {

                            Map<String, Class> collectionClasses = new HashMap<String, Class>();
                            collectionClasses = getPersistenceStructureService().listCollectionObjectTypes(currentClass);
                            currentClass = collectionClasses.get(currentPropertyName);

                        }
                        else {
                            throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");
                        }

                    }
                    else {

                        currentClass = propertyDescriptor.getPropertyType();

                    }
                }
            }

        return currentClass;
    }

    static private Map<String, Map<String, PropertyDescriptor>> cache = new TreeMap<String, Map<String, PropertyDescriptor>>();

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    public static PropertyDescriptor buildReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor propertyDescriptor = null;

        String[] intermediateProperties = propertyName.split("\\.");
        int lastLevel = intermediateProperties.length - 1;
        Class currentClass = propertyClass;

        for (int i = 0; i <= lastLevel; ++i) {

            String currentPropertyName = intermediateProperties[i];
            propertyDescriptor = buildSimpleReadDescriptor(currentClass, currentPropertyName);

            if (i < lastLevel) {

                if (propertyDescriptor != null) {

                    Class propertyType = propertyDescriptor.getPropertyType();
                    if ( propertyType.equals( PersistableBusinessObjectExtension.class ) ) {
                        propertyType = getPersistenceStructureService().getBusinessObjectAttributeClass( currentClass, currentPropertyName );                    
                    }
                    if (Collection.class.isAssignableFrom(propertyType)) {

                        if (getPersistenceStructureService().isPersistable(currentClass)) {

                            Map<String, Class> collectionClasses = new HashMap<String, Class>();
                            collectionClasses = getPersistenceStructureService().listCollectionObjectTypes(currentClass);
                            currentClass = collectionClasses.get(currentPropertyName);

                        }
                        else {

                            throw new RuntimeException("Can't determine the Class of Collection elements because persistenceStructureService.isPersistable(" + currentClass.getName() + ") returns false.");

                        }

                    }
                    else {

                        currentClass = propertyType;

                    }

                }

            }

        }

        return propertyDescriptor;
    }

    /**
     * @param propertyClass
     * @param propertyName
     * @return PropertyDescriptor for the getter for the named property of the given class, if one exists.
     */
    public static PropertyDescriptor buildSimpleReadDescriptor(Class propertyClass, String propertyName) {
        if (propertyClass == null) {
            throw new IllegalArgumentException("invalid (null) propertyClass");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("invalid (blank) propertyName");
        }

        PropertyDescriptor p = null;

        // check to see if we've cached this descriptor already. if yes, return true.
        String propertyClassName = propertyClass.getName();
        Map<String, PropertyDescriptor> m = cache.get(propertyClassName);
        if (null != m) {
            p = m.get(propertyName);
            if (null != p) {
                return p;
            }
        }

        String prefix = StringUtils.capitalize(propertyName);
        String getName = "get" + prefix;
        String isName = "is" + prefix;

        try {

            p = new PropertyDescriptor(propertyName, propertyClass, getName, null);

        }
        catch (IntrospectionException e) {
            try {

                p = new PropertyDescriptor(propertyName, propertyClass, isName, null);

            }
            catch (IntrospectionException f) {
                // ignore it
            }
        }

        // cache the property descriptor if we found it.
        if (null != p) {

            if (null == m) {

                m = new TreeMap<String, PropertyDescriptor>();
                cache.put(propertyClassName, m);

            }

            m.put(propertyName, p);

        }

        return p;
    }

    public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(Class blockedClass) {
    	return ddMapper.getAllInactivationBlockingMetadatas(ddIndex, blockedClass);
    }
    
    /**
     * This method gathers beans of type BeanOverride and invokes each one's performOverride() method.
     */
    // KULRICE-4513
    public void performBeanOverrides()
    {
    	Collection<BeanOverride> beanOverrides = ddBeans.getBeansOfType(BeanOverride.class).values();
    	
    	if (beanOverrides.isEmpty()){
    		LOG.info("DataDictionary.performOverrides(): No beans to override");
    	}
		for (BeanOverride beanOverride : beanOverrides) {
			
			Object bean = ddBeans.getBean(beanOverride.getBeanName());
			beanOverride.performOverride(bean);
			LOG.info("DataDictionary.performOverrides(): Performing override on bean: " + bean.toString());
		}
    }
}
