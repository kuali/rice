/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.core.datadictionary;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectExtension;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.CompletionException;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.util.ClassLoaderUtils;
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

	// keyed by BusinessObject class
	private Map<String, BusinessObjectEntry> businessObjectEntries;
	// keyed by documentTypeName
	private Map<String, DocumentEntry> documentEntries;
	// keyed by other things
	private Map<Class, DocumentEntry> documentEntriesByDocumentClass;
	private Map<Class, DocumentEntry> documentEntriesByBusinessObjectClass;
	private Map<Class, DocumentEntry> documentEntriesByMaintainableClass;
	private Map<String, DataDictionaryEntry> entriesByJstlKey;
	
	// keyed by a class object, and the value is a set of classes that may block the class represented by the key from inactivation 
	private Map<Class, Set<InactivationBlockingMetadata>> inactivationBlockersForClass;
	
	private List<String> configFileLocations = new ArrayList<String>();

    public DataDictionary() {}
    
	
	public List<String> getConfigFileLocations() {
        return this.configFileLocations;
    }

    public void setConfigFileLocations(List<String> configFileLocations) {
        this.configFileLocations = configFileLocations;
    }
    
    public void addConfigFileLocation( String location ) throws IOException {
        indexSource( location );
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
            Thread t = new Thread( new DDValidationRunnable() );
            t.start();
        } else {
            new DDValidationRunnable().run();
        }
    }
	
    private class DDValidationRunnable implements Runnable {

        public void run() {
            LOG.info( "Starting DD Index Building" );
            buildDDIndicies();
            LOG.info( "Completed DD Index Building" );
            LOG.info( "Starting DD Validation" );
            validateDD();
            LOG.info( "Ending DD Validation" );
            LOG.info( "Started DD Inactivation Blocking Index Building" );
            buildDDInactivationBlockingIndices();
            LOG.info( "Completed DD Inactivation Blocking Index Building" );
        }
        
    }
    
    private void buildDDIndicies() {
        // primary indices
        businessObjectEntries = new HashMap<String, BusinessObjectEntry>();
        documentEntries = new HashMap<String, DocumentEntry>();

        // alternate indices
        documentEntriesByDocumentClass = new HashMap<Class, DocumentEntry>();
        documentEntriesByBusinessObjectClass = new HashMap<Class, DocumentEntry>();
        documentEntriesByMaintainableClass = new HashMap<Class, DocumentEntry>();
        entriesByJstlKey = new HashMap<String, DataDictionaryEntry>();
        
        // loop over all beans in the context
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            String entryName = entry.getBusinessObjectClass().getName();
            if ((businessObjectEntries.get(entry.getJstlKey()) != null) 
                    && !((BusinessObjectEntry)businessObjectEntries.get(entry.getJstlKey())).getBusinessObjectClass().equals(entry.getBusinessObjectClass())) {
                throw new DataDictionaryException(new StringBuffer("Two business object classes may not share the same jstl key: this=").append(entry.getBusinessObjectClass()).append(" / existing=").append(((BusinessObjectEntry)businessObjectEntries.get(entry.getJstlKey())).getBusinessObjectClass()).toString());
            }


            businessObjectEntries.put(entryName, entry);
            businessObjectEntries.put(entry.getBusinessObjectClass().getSimpleName(), entry);
            entriesByJstlKey.put(entry.getJstlKey(), entry);
        }
        Map<String,DocumentEntry> docBeans = ddBeans.getBeansOfType(DocumentEntry.class);
        for ( DocumentEntry entry : docBeans.values() ) {
            String entryName = entry.getDocumentTypeName();

            if ((entry instanceof TransactionalDocumentEntry) 
                    && (documentEntries.get(entry.getFullClassName()) != null) 
                    && !((DocumentEntry)documentEntries.get(entry.getFullClassName())).getDocumentTypeName()
                            .equals(entry.getDocumentTypeName())) {
                throw new DataDictionaryException(new StringBuffer("Two transactional document types may not share the same document class: this=")
                        .append(entry.getDocumentTypeName())
                        .append(" / existing=")
                        .append(((DocumentEntry)documentEntries.get(entry.getDocumentClass().getName())).getDocumentTypeName()).toString());
            }
            if ((entriesByJstlKey.get(entry.getJstlKey()) != null) && !((DocumentEntry)documentEntries.get(entry.getJstlKey())).getDocumentTypeName().equals(entry.getDocumentTypeName())) {
                throw new DataDictionaryException(new StringBuffer("Two document types may not share the same jstl key: this=").append(entry.getDocumentTypeName()).append(" / existing=").append(((DocumentEntry)documentEntries.get(entry.getJstlKey())).getDocumentTypeName()).toString());
            }

            documentEntries.put(entryName, entry);
            documentEntries.put(entry.getFullClassName(), entry);
            entriesByJstlKey.put(entry.getJstlKey(), entry);

            if (entry instanceof TransactionalDocumentEntry) {
                TransactionalDocumentEntry tde = (TransactionalDocumentEntry) entry;

                documentEntriesByDocumentClass.put(tde.getDocumentClass(), entry);
                documentEntries.put(tde.getDocumentClass().getSimpleName(), entry);
            }
            if (entry instanceof MaintenanceDocumentEntry) {
                MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) entry;

                documentEntriesByBusinessObjectClass.put(mde.getBusinessObjectClass(), entry);
                documentEntriesByMaintainableClass.put(mde.getMaintainableClass(), entry);
                documentEntries.put(mde.getBusinessObjectClass().getSimpleName() + "MaintenanceDocument", entry);
            }

            entry.validateAuthorizer();
            // TODO: remove this locator?
            KNSServiceLocator.getAuthorizationService().setupAuthorizations(entry);
        }
    }
    
    private void validateDD() {
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
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getBusinessObjectEntry '" + className + "'");
		}
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}
		// LOG.info("calling getBusinessObjectEntry truncated '" + className + "'");

		return businessObjectEntries.get(className);
	}

	/**
	 * @return List of businessObject classnames
	 */
	public List<String> getBusinessObjectClassNames() {
		List classNames = new ArrayList();
		classNames.addAll(businessObjectEntries.keySet());

		return Collections.unmodifiableList(classNames);
	}

	/**
	 * @return Map of (classname, BusinessObjectEntry) pairs
	 */
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return businessObjectEntries;
	}

	/**
	 * @param className
	 * @return DataDictionaryEntryBase for the named class, or null if none
	 *         exists
	 */
	public DataDictionaryEntry getDictionaryObjectEntry(String className) {
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDictionaryObjectEntry '" + className + "'");
		}
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}

		// look in the JSTL key cache
		DataDictionaryEntry entry = entriesByJstlKey.get(className);
		// check the BO list
		if ( entry == null ) {
		    entry = getBusinessObjectEntry(className);
		}
		// check the document list
		if ( entry == null ) {
		    entry = getDocumentEntry(className);
		}
		return entry;
	}

	public DocumentEntry getDocumentEntry(String documentTypeDDKey ) {
		if (StringUtils.isBlank(documentTypeDDKey)) {
			throw new IllegalArgumentException("invalid (blank) documentTypeName");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeDDKey + "'");
		}

		DocumentEntry de = documentEntries.get(documentTypeDDKey);	
		
		if ( de == null ) {
		    try {
    		    Class clazz = Class.forName( documentTypeDDKey );
    		    de = documentEntriesByBusinessObjectClass.get(clazz);
    		    if ( de == null ) {
    		        de = documentEntriesByMaintainableClass.get(clazz);
    		    }
		    } catch ( ClassNotFoundException ex ) {
		        LOG.warn( "Unable to find document entry for key: " + documentTypeDDKey );
		    }
		}
		
        return de;
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
		if (businessObjectClass == null) {
			throw new IllegalArgumentException("invalid (null) businessObjectClass");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling getDocumentEntry by businessObjectClass '" + businessObjectClass + "'");
		}

		return (MaintenanceDocumentEntry) documentEntriesByBusinessObjectClass.get(businessObjectClass);
	}

	public Map<String, DocumentEntry> getDocumentEntries() {
		return Collections.unmodifiableMap(this.documentEntries);
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

        Class attributeClass = null;

        // fail loudly if the attributeName isnt a member of rootClass
        if (!isPropertyOf(boClass, attributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootClass '" + boClass.getName() + "'");
        }

        BusinessObject boInstance;
        try {
            boInstance = (BusinessObject) boClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // attempt to retrieve the class of the property
        try {
            attributeClass = ObjectUtils.getPropertyType(boInstance, attributeName, getPersistenceStructureService());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return attributeClass;
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

    private void buildDDInactivationBlockingIndices() {
        inactivationBlockersForClass = new HashMap<Class, Set<InactivationBlockingMetadata>>();
        Map<String,BusinessObjectEntry> boBeans = ddBeans.getBeansOfType(BusinessObjectEntry.class);
        for ( BusinessObjectEntry entry : boBeans.values() ) {
            List<InactivationBlockingDefinition> inactivationBlockingDefinitions = entry.getInactivationBlockingDefinitions();
            if (inactivationBlockingDefinitions != null && !inactivationBlockingDefinitions.isEmpty()) {
                for (InactivationBlockingDefinition inactivationBlockingDefinition : inactivationBlockingDefinitions) {
                    registerInactivationBlockingDefinition(inactivationBlockingDefinition);
                }
            }
        }
    }
    
    
    private void registerInactivationBlockingDefinition(InactivationBlockingDefinition inactivationBlockingDefinition) {
        Set<InactivationBlockingMetadata> inactivationBlockingDefinitions = inactivationBlockersForClass.get(inactivationBlockingDefinition.getBlockedBusinessObjectClass());
        if (inactivationBlockingDefinitions == null) {
            inactivationBlockingDefinitions = new HashSet<InactivationBlockingMetadata>();
            inactivationBlockersForClass.put(inactivationBlockingDefinition.getBlockedBusinessObjectClass(), inactivationBlockingDefinitions);
        }
        boolean duplicateAdd = ! inactivationBlockingDefinitions.add(inactivationBlockingDefinition);
        if (duplicateAdd) {
            throw new DataDictionaryException("Detected duplicate InactivationBlockingDefinition for class " + inactivationBlockingDefinition.getBlockingReferenceBusinessObjectClass().getClass().getName());
        }
    }
    
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(Class blockedClass) {
        return inactivationBlockersForClass.get(blockedClass);
    }

}