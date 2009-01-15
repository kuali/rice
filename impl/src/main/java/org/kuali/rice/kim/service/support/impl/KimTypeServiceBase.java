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
package org.kuali.rice.kim.service.support.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.KimAttributesTranslator;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeServiceBase implements KimTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimTypeServiceBase.class);

	protected List<String> acceptedAttributeNames = new ArrayList<String>();
	
	protected List<KimAttributesTranslator> kimAttributesTranslators = new ArrayList<KimAttributesTranslator>();
	protected BusinessObjectService businessObjectService;
	protected DictionaryValidationService dictionaryValidationService;
	protected DataDictionaryService dataDictionaryService;
//	protected Class<? extends KimAttributes> kimAttributesClass;

	public BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	public DictionaryValidationService getDictionaryValidationService() {
		if ( dictionaryValidationService == null ) {
			dictionaryValidationService = KNSServiceLocator.getDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	public DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 * 
	 * This method matches input attribute set entries and standard attribute set entries using literal string match.
	 * 
	 */
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		if ( storedAttributeSet == null && inputAttributeSet == null ) {
			return true;
		} else if ( (storedAttributeSet == null && inputAttributeSet != null) 
				|| (inputAttributeSet == null && storedAttributeSet != null) ) {
			return false;
		}
		for ( Map.Entry<String, String> entry : storedAttributeSet.entrySet() ) {
			if ( !inputAttributeSet.containsKey(entry.getKey() ) ) {
				return false;
			}
			if ( !StringUtils.equals(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	public AttributeSet translateInputAttributeSet(AttributeSet qualification){
		if ( qualification == null ) {
			return null;
		}
		if ( getKimAttributesTranslators() == null || getKimAttributesTranslators().isEmpty() ) {
		    return qualification;
		}
		AttributeSet translatedQualification = new AttributeSet();
		translatedQualification.putAll(qualification);
		List<String> attributeNames;
		for(KimAttributesTranslator translator: getKimAttributesTranslators()){
			attributeNames = new ArrayList<String>();
			attributeNames.addAll(translatedQualification.keySet());
			if(translator.supportsTranslationOfAttributes(attributeNames)){
				translatedQualification = translator.translateAttributes(translatedQualification);
			}
		}
		return translatedQualification;
	}
	
	/**
	 * 
	 * This method ...
	 */
	public boolean performMatches(AttributeSet inputAttributeSet, List<AttributeSet> storedAttributeSets){
		for ( AttributeSet storedAttributeSet : storedAttributeSets ) {
			// if one matches, return true
			if ( performMatch(inputAttributeSet, storedAttributeSet) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttributes(AttributeSet)
	 */
	public AttributeSet validateAttributes(AttributeSet attributes) {
		AttributeSet validationErrors = new AttributeSet();
		if ( attributes == null ) {
			return validationErrors;
		}
		for ( String attributeName : attributes.keySet() ) {
			Map<String,String> criteria = new HashMap<String,String>();
            criteria.put("attributeName", attributeName);
            // TODO: load entire attribute table and store in memory
            KimAttributeImpl attributeImpl = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
			List<String> attributeErrors = null;
			try {
				if ( attributeImpl.getComponentName() == null) {
					attributeErrors = validateNonDataDictionaryAttribute(attributeName, attributes.get( attributeName ), true);
				} else {
					// create an object of the proper type per the component 
		            Object componentObject = Class.forName( attributeImpl.getComponentName() ).newInstance();
		            // get the bean utils descriptor for accessing the attribute on that object 
					PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attributeImpl.getAttributeName());
					if ( propertyDescriptor != null ) {
						// set the value on the object so that it can be checked
						propertyDescriptor.getWriteMethod().invoke( componentObject, attributes.get( attributeName ) );
						attributeErrors = validateDataDictionaryAttribute(attributeImpl.getComponentName(), componentObject, propertyDescriptor, true);
					} else {
						LOG.warn( "Unable to obtain property descriptor for: " + attributeImpl.getClass().getName() + "/" + attributeImpl.getAttributeName() );
					}
				}
			} catch (Exception e) {
				LOG.error("Unable to validate attribute: " + attributeName, e);
			}
			
			if ( attributeErrors != null ) {
				for ( String err : attributeErrors ) {
					validationErrors.put(attributeName, err);
				}
			}
		}
		return validationErrors;
	}
	
	protected List<String> validateDataDictionaryAttribute(String entryName, Object object, PropertyDescriptor propertyDescriptor, boolean validateRequired) {
		getDictionaryValidationService().validatePrimitiveFromDescriptor(entryName, object, propertyDescriptor, "", validateRequired);

		Object results = GlobalVariables.getErrorMap().get(propertyDescriptor.getName());
		List errors = new ArrayList();
        if (results instanceof String) {
        	errors.add((String)results);
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List errorList = (List)results;
	        	for (Object msg : errorList) {
	        		ErrorMessage errorMessage = (ErrorMessage)msg;
	        		String retVal = errorMessage.getErrorKey()+":";
	        		for (String param : errorMessage.getMessageParameters()) {
	        			retVal = retVal + param +";";
	        		}
	        		errors.add(retVal);
				}
	        } else {
	        	String [] temp = (String []) results;
	        	for (String string : temp) {
					errors.add(string);
				}
	        }
        }
        GlobalVariables.getErrorMap().remove(propertyDescriptor.getName());
		return errors;
	}

	protected List<String> validateNonDataDictionaryAttribute(String attributeName, String attributeValue, boolean validateRequired) {
		return new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	protected List<KeyLabelPair> getDataDictionaryAttributeValues(KimAttributeImpl attributeImpl) {
		AttributeDefinition definition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(attributeImpl.getComponentName()).getAttributeDefinition(attributeImpl.getAttributeName());
		List<KeyLabelPair> pairs = new ArrayList<KeyLabelPair>();
		Class<? extends KeyValuesFinder> keyValuesFinderName = (Class<? extends KeyValuesFinder>)definition.getControl().getValuesFinderClass();		
		try {
			KeyValuesFinder finder = keyValuesFinderName.newInstance();
			pairs = finder.getKeyValues();
		} catch (Exception e) {
			LOG.error("Unable to build a KeyValuesFinder for " + attributeImpl.getAttributeName(), e);
		}
		return pairs;
	}
	 
	protected List<KeyLabelPair> getNonDataDictionaryAttributeValues(String attributeName) {
		return new ArrayList<KeyLabelPair>();
	}
	
	// FIXME: the attributeName is not guaranteed to be unique!
	public List<KeyLabelPair> getAttributeValidValues(String attributeName) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("attributeName", attributeName);
        KimAttributeImpl attributeImpl = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
        return attributeImpl.getComponentName() == null ? getNonDataDictionaryAttributeValues(attributeName) : getDataDictionaryAttributeValues(attributeImpl);
	}
	
	@SuppressWarnings("unchecked")
	protected AttributeDefinition getDataDictionaryAttributeDefinition(KimTypeAttributeImpl typeAttribute) {
		KimDataDictionaryAttributeDefinition definition = new KimDataDictionaryAttributeDefinition();
		String componentClassName = typeAttribute.getKimAttribute().getComponentName();
		String attributeName = typeAttribute.getKimAttribute().getAttributeName();
		AttributeDefinition baseDefinition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClassName).getAttributeDefinition(attributeName);
		
		// copy all the base attributes
		definition.setName( baseDefinition.getName() );
		definition.setLabel( baseDefinition.getLabel() );
		definition.setShortLabel( baseDefinition.getShortLabel() );
		definition.setMaxLength( baseDefinition.getMaxLength() );
		definition.setRequired( baseDefinition.isRequired() );
		
		if (baseDefinition.getFormatterClass() != null) {
			definition.setFormatterClass(Formatter.findFormatter(baseDefinition.getFormatterClass()));
		}
		ControlDefinition control = copy(baseDefinition.getControl());
//		if (control.getValuesFinderClass() != null) {
//			control.setValuesFinderClass(KimAttributeValuesFinder.class);
//		}
		definition.setControl(control);
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setApplicationUrl(typeAttribute.getKimAttribute().getApplicationUrl());
		
		Map<String, String> lookupInputPropertyConversionsMap = new HashMap<String, String>();
		Map<String, String> lookupReturnPropertyConversionsMap = new HashMap<String, String>();
		try {
			Class<? extends BusinessObject> componentClass = (Class<? extends BusinessObject>)Class.forName(componentClassName);
			BusinessObject sampleComponent = componentClass.newInstance();
			List<String> displayedFieldNames = new ArrayList<String>( 1 );
			displayedFieldNames.add( attributeName );
			Field field = FieldUtils.getPropertyField(componentClass, attributeName, false);
			if ( field != null ) {
				field = LookupUtils.setFieldQuickfinder( sampleComponent, attributeName, field, displayedFieldNames );
				if ( StringUtils.isNotBlank( field.getQuickFinderClassNameImpl() ) ) {
					Class<? extends BusinessObject> lookupClass = (Class<? extends BusinessObject>)Class.forName( field.getQuickFinderClassNameImpl() );
					definition.setLookupBoClass( lookupClass );
					if ( field.getLookupParameters() != null ) {
						String [] lookupInputPropertyConversions = field.getLookupParameters().split(",");
						for (String string : lookupInputPropertyConversions) {
							String [] keyVal = string.split(":");
							lookupInputPropertyConversionsMap.put(keyVal[0], keyVal[1]);
						}
						definition.setLookupInputPropertyConversions(lookupInputPropertyConversionsMap);
					}
					if ( field.getFieldConversions() != null ) {
						String [] lookupReturnPropertyConversions = field.getFieldConversions().split(",");
						for (String string : lookupReturnPropertyConversions) {
							String [] keyVal = string.split(":");
							lookupReturnPropertyConversionsMap.put(keyVal[0], keyVal[1]);
						}
						definition.setLookupReturnPropertyConversions(lookupReturnPropertyConversionsMap);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Unable to get DD data for: " + typeAttribute.getKimAttribute(), e);
		}
		return definition;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T copy(final T original) {
		if ( original == null ) {
			return null;
		}
		T copy = null;
		try {
			copy = (T) original.getClass().newInstance();
			Class copyClass = copy.getClass();
	    	do {
	    		for (java.lang.reflect.Field copyField : copyClass.getDeclaredFields()) {
    				copyField.setAccessible(true);
    				int mods = copyField.getModifiers();
    				if (!Modifier.isFinal(mods) && !Modifier.isStatic(mods)) {
    					copyField.set(copy, copyField.get(original));
    				}
	    		}
	    		copyClass = copyClass.getSuperclass();
	    	} while (copyClass != null && !(copyClass.equals(Object.class)));		
		} catch (Exception e) {
			LOG.error("Unable to copy " + original, e);
		}
		return copy;
	}
	
	protected AttributeDefinition getNonDataDictionaryAttributeDefinition(KimTypeAttributeImpl typeAttribute) {
		KimNonDataDictionaryAttributeDefinition definition = new KimNonDataDictionaryAttributeDefinition();
		definition.setName(typeAttribute.getKimAttribute().getAttributeName());
		definition.setLabel(typeAttribute.getKimAttribute().getAttributeLabel());
		definition.setSortCode(typeAttribute.getSortCode());
		return definition;
	}
	
	private Map<String,AttributeDefinitionMap> attributeDefinitionCache = new HashMap<String,AttributeDefinitionMap>();
	
	public AttributeDefinitionMap getAttributeDefinitions(KimTypeImpl kimType) {
		AttributeDefinitionMap definitions = attributeDefinitionCache.get( kimType.getKimTypeId() );
		if ( definitions == null ) {
			definitions = new AttributeDefinitionMap();
			for (KimTypeAttributeImpl typeAttribute : kimType.getAttributeDefinitions()) {
				AttributeDefinition definition = null;
				if (typeAttribute.getKimAttribute().getComponentName() == null) {
					definition = getNonDataDictionaryAttributeDefinition(typeAttribute);
				} else {
					definition = getDataDictionaryAttributeDefinition(typeAttribute);
				}
				// TODO : use id for defnid ?
				definition.setId(typeAttribute.getKimAttributeId());
				// FIXME: I don't like this - if two attributes have the same sort code, they will overwrite each other
				definitions.put(typeAttribute.getSortCode(), definition);
			}
			attributeDefinitionCache.put( kimType.getKimTypeId(), definitions );
		}
		return definitions;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getAcceptedAttributeNames()
	 */
	public List<String> getAcceptedAttributeNames() {
		return this.acceptedAttributeNames;
	}

	public void setAcceptedAttributeNames(List<String> acceptedQualificationAttributeNames) {
		this.acceptedAttributeNames = acceptedQualificationAttributeNames;
	}
	
	public void addAcceptedAttributeName( String acceptedAttributeName ) {
		acceptedAttributeNames.add( acceptedAttributeName );
	}

	/**
	 * @see org.kuali.rice.kim.service.support.KimTypeService#supportsAttributes(java.util.List)
	 */
	public boolean supportsAttributes(List<String> attributeNames) {
		for ( String attributeName : attributeNames ) {
			if ( !acceptedAttributeNames.contains( attributeName ) ) {
				return false;
			}
		}
		return true;
	}	
	
	/**
	 * @return the kimAttributesTranslators
	 */
	public List<KimAttributesTranslator> getKimAttributesTranslators() {
		return this.kimAttributesTranslators;
	}

	/**
	 * @param kimAttributesTranslators the kimAttributesTranslators to set
	 */
	public void setKimAttributesTranslators(
			List<KimAttributesTranslator> kimAttributesTranslators) {
		this.kimAttributesTranslators = kimAttributesTranslators;
	}
	
	public void addAttributeTranslator( KimAttributesTranslator attributesTranslator ) {
		kimAttributesTranslators.add( attributesTranslator );
	}
	
	protected final String COMMA_SEPARATOR = ", ";
	protected final String QUALIFICATION_RECEIVED_ATTIBUTES_NAME = "qualification";
	protected final String ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME = "role qualifiers";
	protected final String REQUESTED_DETAILS_RECEIVED_ATTIBUTES_NAME = "requested details";
	protected final String STORED_DETAILS_RECEIVED_ATTIBUTES_NAME = "stored details";

	protected void validateRequiredAttributesAgainstReceived(
			List<String> requiredAttributes, AttributeSet receivedAttributes, String receivedAttributesName){
		//TODO: Remove comment
		/*List<String> missingAttributes = new ArrayList<String>();
		for(String requiredAttribute: requiredAttributes){
			if(receivedAttributes==null || !receivedAttributes.containsKey(requiredAttribute))
				missingAttributes.add(requiredAttribute);
		}
        if(missingAttributes.size()>0) {
        	String errorMessage = "";
        	for(String missingAttribute: missingAttributes){
        		errorMessage += missingAttribute + COMMA_SEPARATOR;
        	}
        	errorMessage = errorMessage.substring(0, 
        			errorMessage.length()-COMMA_SEPARATOR.length()) + " not found in "+receivedAttributesName+" .";
            throw new RuntimeException(errorMessage);
        }*/
	}
	
}
