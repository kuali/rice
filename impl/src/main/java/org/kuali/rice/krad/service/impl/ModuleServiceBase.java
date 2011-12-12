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
package org.kuali.rice.krad.service.impl;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectNotLookupableException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * This class implements ModuleService interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModuleServiceBase implements ModuleService {

	protected static final Logger LOG = Logger.getLogger(ModuleServiceBase.class);

	protected ModuleConfiguration moduleConfiguration;
	protected BusinessObjectService businessObjectService;
	protected LookupService lookupService;
	protected BusinessObjectDictionaryService businessObjectDictionaryService;
	protected KualiModuleService kualiModuleService;
	protected ApplicationContext applicationContext;

	/***
	 * @see org.kuali.rice.krad.service.ModuleService#isResponsibleFor(java.lang.Class)
	 */
	public boolean isResponsibleFor(Class businessObjectClass) {
		if(getModuleConfiguration() == null)
			throw new IllegalStateException("Module configuration has not been initialized for the module service.");

		if (getModuleConfiguration().getPackagePrefixes() == null || businessObjectClass == null) {
			return false;
		}
		for (String prefix : getModuleConfiguration().getPackagePrefixes()) {
			if (businessObjectClass.getPackage().getName().startsWith(prefix)) {
				return true;
			}
		}
		if (ExternalizableBusinessObject.class.isAssignableFrom(businessObjectClass)) {
			Class externalizableBusinessObjectInterface = ExternalizableBusinessObjectUtils.determineExternalizableBusinessObjectSubInterface(businessObjectClass);
			if (externalizableBusinessObjectInterface != null) {
				for (String prefix : getModuleConfiguration().getPackagePrefixes()) {
					if (externalizableBusinessObjectInterface.getPackage().getName().startsWith(prefix)) {
						return true;
					}
				}
			}
		}
		return false;
	}



	/***
	 * @see org.kuali.rice.krad.service.ModuleService#isResponsibleFor(java.lang.Class)
	 */
	public boolean isResponsibleForJob(String jobName) {
		if(getModuleConfiguration() == null)
			throw new IllegalStateException("Module configuration has not been initialized for the module service.");

		if (getModuleConfiguration().getJobNames() == null || StringUtils.isEmpty(jobName))
			return false;

		return getModuleConfiguration().getJobNames().contains(jobName);
	}

    /***
     * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
    	Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
		ExternalizableBusinessObject businessObject = (ExternalizableBusinessObject)
			getBusinessObjectService().findByPrimaryKey(implementationClass, fieldValues);
        return (T) businessObject;
	}

    /***
     * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
     */
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues) {
		Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(externalizableBusinessObjectClass);
		return (List<T>) getBusinessObjectService().findMatching(implementationClass, fieldValues);
	}

	/***
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectsListForLookup(java.lang.Class, java.util.Map, boolean)
	 */
	public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
			Class<T> externalizableBusinessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
		Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(externalizableBusinessObjectClass);
		if (isExternalizableBusinessObjectLookupable(implementationClass)) {
			Map<String, String> searchCriteria = new HashMap<String, String>();
			for (Entry<String, Object> fieldValue : fieldValues.entrySet()) {
				if (fieldValue.getValue() != null) {
					searchCriteria.put(fieldValue.getKey(), fieldValue.getValue().toString());
				}
				else {
					searchCriteria.put(fieldValue.getKey(), null);
				}
			}
		    return (List<T>) getLookupService().findCollectionBySearchHelper(implementationClass, searchCriteria, unbounded);
		} else {
		   throw new BusinessObjectNotLookupableException("External business object is not a Lookupable:  " + implementationClass);
		}
	}

	public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass){
		Class clazz = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
		return KRADServiceLocator.getPersistenceStructureService().listPrimaryKeyFieldNames(clazz);
	}

	/***
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectDictionaryEntry(java.lang.Class)
	 */
	public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(
			Class businessObjectInterfaceClass) {
		Class boClass = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);

		return boClass==null?null:
			KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(boClass.getName());
	}

	public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
		if(!ExternalizableBusinessObject.class.isAssignableFrom(inquiryBusinessObjectClass)) {
	        return KRADConstants.EMPTY_STRING;
		}
		String businessObjectClassAttribute;

        Class implementationClass = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
        if (implementationClass == null) {
            LOG.error("Can't find ExternalizableBusinessObject implementation class for " + inquiryBusinessObjectClass.getName());
            throw new RuntimeException("Can't find ExternalizableBusinessObject implementation class for interface " + inquiryBusinessObjectClass.getName());
        }
        businessObjectClassAttribute = implementationClass.getName();
        return UrlFactory.parameterizeUrl(
        		getInquiryUrl(inquiryBusinessObjectClass),
        		getUrlParameters(businessObjectClassAttribute, parameters));
	}

	protected Properties getUrlParameters(String businessObjectClassAttribute, Map<String, String[]> parameters){
		Properties urlParameters = new Properties();
		for (String paramName : parameters.keySet()) {
			String[] parameterValues = parameters.get(paramName);
			if (parameterValues.length > 0) {
				urlParameters.put(paramName, parameterValues[0]);
			}
		}
		urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObjectClassAttribute);
		urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
		return urlParameters;
	}

	protected String getInquiryUrl(Class inquiryBusinessObjectClass){
		String riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.APPLICATION_URL_KEY);
		String inquiryUrl = riceBaseUrl;
		if (!inquiryUrl.endsWith("/")) {
			inquiryUrl = inquiryUrl + "/";
		}
		return inquiryUrl + "kr/" + KRADConstants.INQUIRY_ACTION;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectLookupUrl(java.lang.Class, java.util.Map)
	 */
	public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
		Properties urlParameters = new Properties();

		String riceBaseUrl = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.APPLICATION_URL_KEY);
		String lookupUrl = riceBaseUrl;
		if (!lookupUrl.endsWith("/")) {
			lookupUrl = lookupUrl + "/";
		}
		if (parameters.containsKey(KRADConstants.MULTIPLE_VALUE)) {
			lookupUrl = lookupUrl + "kr/" + KRADConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
		}
		else {
			lookupUrl = lookupUrl + "kr/" + KRADConstants.LOOKUP_ACTION;
		}
		for (String paramName : parameters.keySet()) {
			urlParameters.put(paramName, parameters.get(paramName));
		}

		Class clazz = getExternalizableBusinessObjectImplementation(inquiryBusinessObjectClass);
		urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, clazz==null?"":clazz.getName());

		return UrlFactory.parameterizeUrl(lookupUrl, urlParameters);
	}

	/***
	 *
	 * This method assumes that the property type for externalizable relationship in the business object is an interface
	 * and gets the concrete implementation for it
	 *
	 * @see org.kuali.rice.krad.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
	 */
	public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(
			BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {

		if(businessObject==null) return null;
		Class clazz;
		try{
			clazz = getExternalizableBusinessObjectImplementation(
					PropertyUtils.getPropertyType(businessObject, externalizableRelationshipName));
		} catch(Exception iex){
			LOG.warn("Exception:"+iex+" thrown while trying to get property type for property:"+externalizableRelationshipName+
					" from business object:"+businessObject);
			return null;
		}

		//Get the business object entry for this business object from data dictionary
		//using the class name (without the package) as key
		BusinessObjectEntry entry =
			KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntries().get(
					businessObject.getClass().getSimpleName());
		RelationshipDefinition relationshipDefinition = entry.getRelationshipDefinition(externalizableRelationshipName);
		List<PrimitiveAttributeDefinition> primitiveAttributeDefinitions = relationshipDefinition.getPrimitiveAttributes();

		Map<String, Object> fieldValuesInEBO = new HashMap<String, Object>();
		Object sourcePropertyValue;
		Object targetPropertyValue = null;
		boolean sourceTargetPropertyValuesSame = true;
		for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitiveAttributeDefinitions){
	    	sourcePropertyValue = ObjectUtils.getPropertyValue(
	    			businessObject, primitiveAttributeDefinition.getSourceName());
	    	if(currentInstanceExternalizableBO!=null)
	    		targetPropertyValue = ObjectUtils.getPropertyValue(currentInstanceExternalizableBO, primitiveAttributeDefinition.getTargetName());
		    if(sourcePropertyValue==null){
		        return null;
		    } else if(targetPropertyValue==null || (targetPropertyValue!=null && !targetPropertyValue.equals(sourcePropertyValue))){
		    	sourceTargetPropertyValuesSame = false;
		    }
		    fieldValuesInEBO.put(primitiveAttributeDefinition.getTargetName(), sourcePropertyValue);
		}

		if(!sourceTargetPropertyValuesSame)
			return (T) getExternalizableBusinessObject(clazz, fieldValuesInEBO);
		return currentInstanceExternalizableBO;
	}

	/***
	 *
	 * This method assumes that the externalizableClazz is an interface
	 * and gets the concrete implementation for it
	 *
	 * @see org.kuali.rice.krad.service.ModuleService#retrieveExternalizableBusinessObjectIfNecessary(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
	 */
	public List<? extends ExternalizableBusinessObject> retrieveExternalizableBusinessObjectsList(
			BusinessObject businessObject, String externalizableRelationshipName, Class externalizableClazz) {

		if(businessObject==null) return null;
		//Get the business object entry for this business object from data dictionary
		//using the class name (without the package) as key
		String className = businessObject.getClass().getName();
		String key = className.substring(className.lastIndexOf(".")+1);
		BusinessObjectEntry entry =
			KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntries().get(key);
		RelationshipDefinition relationshipDefinition = entry.getRelationshipDefinition(externalizableRelationshipName);
		List<PrimitiveAttributeDefinition> primitiveAttributeDefinitions = relationshipDefinition.getPrimitiveAttributes();
		Map<String, Object> fieldValuesInEBO = new HashMap<String, Object>();
		Object sourcePropertyValue;
		for(PrimitiveAttributeDefinition primitiveAttributeDefinition: primitiveAttributeDefinitions){
	    	sourcePropertyValue = ObjectUtils.getPropertyValue(
	    			businessObject, primitiveAttributeDefinition.getSourceName());
		    if(sourcePropertyValue==null){
		        return null;
		    }
		    fieldValuesInEBO.put(primitiveAttributeDefinition.getTargetName(), sourcePropertyValue);
		}
		return getExternalizableBusinessObjectsList(
				getExternalizableBusinessObjectImplementation(externalizableClazz), fieldValuesInEBO);
	}

	/**
	 * @see org.kuali.rice.krad.service.ModuleService#getExternalizableBusinessObjectImplementation(java.lang.Class)
	 */
	public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(Class<E> externalizableBusinessObjectInterface) {
		if (getModuleConfiguration() == null) {
			throw new IllegalStateException("Module configuration has not been initialized for the module service.");
		}
		int classModifiers = externalizableBusinessObjectInterface.getModifiers();
        Map<Class, Class> ebos = getModuleConfiguration().getExternalizableBusinessObjectImplementations();

		if (ebos.containsValue(externalizableBusinessObjectInterface)) {
			return externalizableBusinessObjectInterface;
		}
		if (getModuleConfiguration().getExternalizableBusinessObjectImplementations() == null) {
			return null;
		}
		else {
			Class<E> implementationClass = ebos.get(externalizableBusinessObjectInterface);
			int implClassModifiers = implementationClass.getModifiers();
			if (Modifier.isInterface(implClassModifiers) || Modifier.isAbstract(implClassModifiers)) {
				throw new RuntimeException("Implementation class must be non-abstract class: ebo interface: " + externalizableBusinessObjectInterface.getName() + " impl class: "
						+ implementationClass.getName() + " module: " + getModuleConfiguration().getNamespaceCode());
			}
			return implementationClass;
		}

	}

	/***
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		KualiModuleService kualiModuleService = null;
		try {
			kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
			if ( kualiModuleService == null ) {
				kualiModuleService = ((KualiModuleService)applicationContext.getBean( KRADServiceLocatorWeb.KUALI_MODULE_SERVICE ));
			}
		} catch ( NoSuchBeanDefinitionException ex ) {
			kualiModuleService = ((KualiModuleService)applicationContext.getBean( KRADServiceLocatorWeb.KUALI_MODULE_SERVICE ));
		}
		kualiModuleService.getInstalledModuleServices().add( this );
	}

	/**
	 * @return the moduleConfiguration
	 */
	public ModuleConfiguration getModuleConfiguration() {
		return this.moduleConfiguration;
	}

	/**
	 * @param moduleConfiguration the moduleConfiguration to set
	 */
	public void setModuleConfiguration(ModuleConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}

    /***
     * @see org.kuali.rice.krad.service.ModuleService#isExternalizable(java.lang.Class)
     */
    public boolean isExternalizable(Class boClazz){
    	if(boClazz==null) return false;
    	return ExternalizableBusinessObject.class.isAssignableFrom(boClazz);
    }

	public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
		return getBusinessObjectDictionaryService().isLookupable(boClass);
	}

	public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
		return getBusinessObjectDictionaryService().isInquirable(boClass);
	}

	public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass) {
		try {
			return (T) getExternalizableBusinessObjectImplementation(boClass).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Unable to create externalizable business object class", e);
		}
	}

	public DataObjectRelationship getBusinessObjectRelationship(Class boClass, String attributeName, String attributePrefix){
		return null;
	}



	public BusinessObjectDictionaryService getBusinessObjectDictionaryService () {
		if ( businessObjectDictionaryService == null ) {
			businessObjectDictionaryService = KNSServiceLocator.getBusinessObjectDictionaryService();
		}
		return businessObjectDictionaryService;
	}

	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

    /**
     * Gets the lookupService attribute.
     * @return Returns the lookupService.
     */
    protected LookupService getLookupService() {
        return lookupService != null ? lookupService : KRADServiceLocatorWeb.getLookupService();
    }

	/**
	 * @return the kualiModuleService
	 */
	public KualiModuleService getKualiModuleService() {
		return this.kualiModuleService;
	}

	/**
	 * @param kualiModuleService the kualiModuleService to set
	 */
	public void setKualiModuleService(KualiModuleService kualiModuleService) {
		this.kualiModuleService = kualiModuleService;
	}

	/**
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.service.ModuleService#listAlternatePrimaryKeyFieldNames(java.lang.Class)
	 */
	public List<List<String>> listAlternatePrimaryKeyFieldNames(
			Class businessObjectInterfaceClass) {
		return null;
	}



    /**
     * This method determines whether or not this module is currently locked
     * 
     * @see org.kuali.rice.krad.service.ModuleService#isLocked()
     */
    @Override
    public boolean isLocked() {
        ModuleConfiguration configuration = this.getModuleConfiguration();
        if(configuration != null) {
            String namespaceCode = configuration.getNamespaceCode();
            String componentCode = KRADConstants.DetailTypes.OLTP_LOCKOUT_DETAIL_TYPE;
            String parameterName = KRADConstants.SystemGroupParameterNames.OLTP_LOCKOUT_ACTIVE_IND;
            ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
            String shouldLockout = parameterService.getParameterValueAsString(namespaceCode, componentCode, parameterName);
            if(StringUtils.isNotBlank(shouldLockout)) {
                return parameterService.getParameterValueAsBoolean(namespaceCode, componentCode, parameterName);
            }
        }
        return false;
    }
}

