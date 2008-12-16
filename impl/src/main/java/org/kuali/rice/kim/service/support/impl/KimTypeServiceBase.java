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
import java.lang.reflect.InvocationTargetException;
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
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatterSubString;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KimAttributeValuesFinder;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
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

	protected List<String> acceptedAttributeNames = new ArrayList<String>();;
	
	protected List<KimAttributesTranslator> kimAttributesTranslators = new ArrayList<KimAttributesTranslator>();

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
	 * This method matches input attribute set entries and standard attribute set entries using liternal string match.
	 * 
	 */
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
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

	/**
	 * 
	 * This method matches input attribute set entries and standard attribute set entries using wild card match.
	 * "*" is the only wildcard supported currently.
	 */
	protected boolean performMatchUsingWildcard(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		for ( Map.Entry<String, String> entry : storedAttributeSet.entrySet() ) {
			if ( !inputAttributeSet.containsKey(entry.getKey() ) ) {
				return false;
			}
			if ( !KimCommonUtils.matchInputWithWildcard(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}
	
	public AttributeSet translateInputAttributeSet(AttributeSet qualification){
		if ( qualification == null ) {
			return null;
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
		for ( Map.Entry<String,String> attribute : attributes.entrySet() ) {
			Map<String,String> criteria = new HashMap<String,String>();
            criteria.put("attributeName", attribute.getKey());
            KimAttributeImpl attributeImpl = (KimAttributeImpl) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
            
			List<String> attributeErrors = null;
			try {
				PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(attributeImpl, attributeImpl.getAttributeName());
				if (attributeImpl.getComponentName() == null) {
					attributeErrors = validateNonDataDictionaryAttribute(null, attributeImpl, propertyDescriptor, true);
				} else {
					attributeErrors = validateDataDictionaryAttribute(attributeImpl.getComponentName(), attributeImpl, propertyDescriptor, true);
				}
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				LOG.error(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				LOG.error(e.getMessage(), e);
			}
			
			if ( attributeErrors != null ) {
				for ( String err : attributeErrors ) {
					validationErrors.put(attribute.getKey(), err);
				}
			}
		}
		return validationErrors;
	}
	
	protected List<String> validateDataDictionaryAttribute(String entryName, Object object, PropertyDescriptor propertyDescriptor, boolean validateRequired) {
		KNSServiceLocator.getDictionaryValidationService().validatePrimitiveFromDescriptor(entryName, object, propertyDescriptor, "", validateRequired);
	
		List<String> errors = new ArrayList<String>();
		Object results = GlobalVariables.getErrorMap().get(propertyDescriptor.getName());
        if (results instanceof String) {
        	errors.add((String)results);
        } else {
        	String [] temp = (String []) results;
        	for (String string : temp) {
				errors.add(string);
			}
        }
		
        GlobalVariables.getErrorMap().clear();
        
		return errors;
	}

	protected List<String> validateNonDataDictionaryAttribute(String entryName, Object object, PropertyDescriptor propertyDescriptor, boolean validateRequired) {
		return new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	protected List<KeyLabelPair> getDataDictionaryAttributeValues(String attributeName) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("attributeName", attributeName);
        KimAttributeImpl attributeImpl = (KimAttributeImpl) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
		AttributeDefinition definition = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(attributeImpl.getComponentName()).getAttributeDefinition(attributeName);
		List<KeyLabelPair> pairs = new ArrayList<KeyLabelPair>();
		Class<? extends KeyValuesFinder> keyValuesFinderName = (Class<? extends KeyValuesFinder>)definition.getControl().getValuesFinderClass();		
		try {
			KeyValuesFinder finder = keyValuesFinderName.newInstance();
			pairs = finder.getKeyValues();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return pairs;
	}
	 
	protected List<KeyLabelPair> getNonDataDictionaryAttributeValues(String attributeName) {
		return new ArrayList<KeyLabelPair>();
	}
	
	public List<KeyLabelPair> getAttributeValidValues(String attributeName) {
		Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("attributeName", attributeName);
        KimAttributeImpl attributeImpl = (KimAttributeImpl) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
        return attributeImpl.getComponentName() == null ? getNonDataDictionaryAttributeValues(attributeName) : getDataDictionaryAttributeValues(attributeName);
	}
	
	@SuppressWarnings("unchecked")
	protected AttributeDefinition getDataDictionaryAttributeDefinition(KimTypeAttributeImpl typeAttribute) {
		KimDataDictionaryAttributeDefinition definition = new KimDataDictionaryAttributeDefinition(); 
		definition.setDataDictionaryAttributeDefinition(KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(typeAttribute.getKimAttribute().getComponentName()).getAttributeDefinition(typeAttribute.getKimAttribute().getAttributeName()));
		if (definition.getDataDictionaryAttributeDefinition().getFormatterClass() != null) {
			definition.setFormatterClass(Formatter.findFormatter(definition.getDataDictionaryAttributeDefinition().getFormatterClass()));
		}
		ControlDefinition control = copy(definition.getDataDictionaryAttributeDefinition().getControl());
		if (control.getValuesFinderClass() != null) {
			control.setValuesFinderClass(KimAttributeValuesFinder.class);
		}
		definition.setControl(control);
		Mask mask = copy(definition.getDataDictionaryAttributeDefinition().getDisplayMask());
		if (mask != null) {
			MaskFormatterSubString formatter = new MaskFormatterSubString();
			formatter.setMaskLength(definition.getDataDictionaryAttributeDefinition().getMaxLength());
			mask.setMaskFormatter(formatter);
		}
		definition.setDisplayMask(mask);
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setApplicationUrl(typeAttribute.getKimAttribute().getApplicationUrl());
		
		Map<String, String> lookupInputPropertyConversionsMap = new HashMap<String, String>();
		Map<String, String> lookupReturnPropertyConversionsMap = new HashMap<String, String>();
		try {
			Field field = FieldUtils.getPropertyField(Class.forName(typeAttribute.getKimAttribute().getComponentName()), typeAttribute.getKimAttribute().getAttributeName(), false);
			String [] lookupInputPropertyConversions = field.getLookupParameters().split(",");
			for (String string : lookupInputPropertyConversions) {
				String [] keyVal = string.split(":");
				lookupInputPropertyConversionsMap.put(keyVal[0], keyVal[1]);
			}
			definition.setLookupInputPropertyConversions(lookupInputPropertyConversionsMap);
			String [] lookupReturnPropertyConversions = field.getFieldConversions().split(",");
			for (String string : lookupReturnPropertyConversions) {
				String [] keyVal = string.split(":");
				lookupReturnPropertyConversionsMap.put(keyVal[0], keyVal[1]);
			}
			definition.setLookupReturnPropertyConversions(lookupReturnPropertyConversionsMap);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return definition;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T copy(final T original) {
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
			LOG.error(e.getMessage(), e);
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
	
	public AttributeDefinitionMap getAttributeDefinitions(KimTypeImpl kimType) {
		AttributeDefinitionMap definitions = new AttributeDefinitionMap();
		for (KimTypeAttributeImpl typeAttribute : kimType.getAttributeDefinitions()) {
			AttributeDefinition definition;
			if (typeAttribute.getKimAttribute().getComponentName() == null) {
				definition = getNonDataDictionaryAttributeDefinition(typeAttribute);
			} else {
				definition = getDataDictionaryAttributeDefinition(typeAttribute);
			}
			definitions.put(typeAttribute.getSortCode(), definition);
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
		/*List<String> missingAttributes = new ArrayList<String>();
		for(String requiredAttribute: requiredAttributes){
			if(!receivedAttributes.containsKey(requiredAttribute))
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
