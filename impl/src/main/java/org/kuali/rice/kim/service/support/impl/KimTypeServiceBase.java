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
package org.kuali.rice.kim.service.support.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.kns.datadictionary.RelationshipDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.validation.ValidationPattern;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KimAttributeValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.kns.service.*;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.StringValueComparator;
import org.kuali.rice.kns.web.ui.Field;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimTypeServiceBase implements KimTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimTypeServiceBase.class);

	private BusinessObjectService businessObjectService;
	private DictionaryValidationService dictionaryValidationService;
	private DataDictionaryService dataDictionaryService;
	private KimTypeInfoService typeInfoService;
	
	protected List<String> workflowRoutingAttributes = new ArrayList<String>();
	protected List<String> requiredAttributes = new ArrayList<String>();
	protected boolean checkRequiredAttributes = false;

	protected KimTypeInfoService getTypeInfoService() {
		if ( typeInfoService == null ) {
			typeInfoService = KIMServiceLocatorWeb.getTypeInfoService();
		}
		return typeInfoService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected DictionaryValidationService getDictionaryValidationService() {
		if ( dictionaryValidationService == null ) {
			dictionaryValidationService = KNSServiceLocatorWeb.getDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KNSServiceLocatorWeb.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 *
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	@Override
	public String getWorkflowDocumentTypeName() {
		return null;
	}

	/**
	 *
	 * This method matches input attribute set entries and standard attribute set entries using literal string match.
	 *
	 */
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		if ( storedAttributeSet == null || inputAttributeSet == null ) {
			return true;
		}
		for ( Map.Entry<String, String> entry : storedAttributeSet.entrySet() ) {
			if (inputAttributeSet.containsKey(entry.getKey()) && !StringUtils.equals(inputAttributeSet.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	public AttributeSet translateInputAttributeSet(AttributeSet qualification){
		return qualification;
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
	@Override
	public AttributeSet validateAttributes(String kimTypeId, AttributeSet attributes) {
		AttributeSet validationErrors = new AttributeSet();
		if ( attributes == null ) {
			return validationErrors;
		}
		KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
		
		for ( String attributeName : attributes.keySet() ) {
            KimTypeAttributeInfo attr = kimType.getAttributeDefinitionByName(attributeName);
			List<String> attributeErrors = null;
			try {
				if ( attr.getComponentName() == null) {
					attributeErrors = validateNonDataDictionaryAttribute(attributeName, attributes.get( attributeName ), true);
				} else {
					// create an object of the proper type per the component
		            Object componentObject = Class.forName( attr.getComponentName() ).newInstance();
		            // get the bean utils descriptor for accessing the attribute on that object
		            PropertyDescriptor propertyDescriptor = null;
		            if ( attr.getAttributeName() != null ) {
		            	propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attr.getAttributeName());
						if ( propertyDescriptor != null ) {
							// set the value on the object so that it can be checked
							Object attributeValue = getAttributeValue(propertyDescriptor, attributes.get(attributeName));
							propertyDescriptor.getWriteMethod().invoke( componentObject, attributeValue);
							attributeErrors = validateDataDictionaryAttribute(kimTypeId, attr.getComponentName(), componentObject, propertyDescriptor);
						}
		            }
					if ( propertyDescriptor == null ) {
						LOG.warn( "Unable to obtain property descriptor for: " + attr.getComponentName() + "/" + attr.getAttributeName() );
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
		
		Map<String, List<String>> referenceCheckErrors = validateReferencesExistAndActive(kimType, attributes, validationErrors);
		for ( String attributeName : referenceCheckErrors.keySet() ) {
			List<String> attributeErrors = referenceCheckErrors.get(attributeName);
			for ( String err : attributeErrors ) {
				validationErrors.put(attributeName, err);
			}
		}
		
		return validationErrors;
	}

	private Object getAttributeValue(PropertyDescriptor propertyDescriptor, String attributeValue){
		Object attributeValueObject = null;
		if(propertyDescriptor!=null && attributeValue!=null){
			Class<?> propertyType = propertyDescriptor.getPropertyType();
			if(propertyType!=String.class){
				attributeValueObject = KNSUtils.createObject(propertyType, new Class[]{String.class}, new Object[] {attributeValue});
			} else {
				attributeValueObject = attributeValue;
			}
		}
		return attributeValueObject;
	}
	
	protected Map<String, List<String>> validateReferencesExistAndActive( KimTypeInfo kimType, AttributeSet attributes, Map<String, String> previousValidationErrors) {
		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		Map<String, List<String>> errors = new HashMap<String, List<String>>();
		
		for ( String attributeName : attributes.keySet() ) {
			KimTypeAttributeInfo attr = kimType.getAttributeDefinitionByName(attributeName);
			
			if (StringUtils.isNotBlank(attr.getComponentName())) {
				if (!componentClassInstances.containsKey(attr.getComponentName())) {
					try {
						Class<?> componentClass = Class.forName( attr.getComponentName() );
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attr.getComponentName(), componentInstance);
					} catch (Exception e) {
						LOG.error("Unable to instantiate class for attribute: " + attributeName, e);
					}
				}
			}
		}
		
		// now that we have instances for each component class, try to populate them with any attribute we can, assuming there were no other validation errors associated with it
		for ( String attributeName : attributes.keySet() ) {
			if (!previousValidationErrors.containsKey(attributeName)) {
				for (Object componentInstance : componentClassInstances.values()) {
					try {
						ObjectUtils.setObjectProperty(componentInstance, attributeName, attributes.get(attributeName));
					} catch (NoSuchMethodException e) {
						// this is expected since not all attributes will be in all components
					} catch (Exception e) {
						LOG.error("Unable to set object property class: " + componentInstance.getClass().getName() + " property: " + attributeName, e);
					}
				}
			}
		}
		
		for (String componentClass : componentClassInstances.keySet()) {
			BusinessObject componentInstance = componentClassInstances.get(componentClass);
			
			List<RelationshipDefinition> relationships = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClass).getRelationships();
			if (relationships == null) {
				continue;
			}
			
			for (RelationshipDefinition relationshipDefinition : relationships) {
				List<PrimitiveAttributeDefinition> primitiveAttributes = relationshipDefinition.getPrimitiveAttributes();
				
				// this code assumes that the last defined primitiveAttribute is the attributeToHighlightOnFail
				String attributeToHighlightOnFail = primitiveAttributes.get(primitiveAttributes.size() - 1).getSourceName();
				
				// TODO: will this work for user ID attributes?
				
				if (!attributes.containsKey(attributeToHighlightOnFail)) {
					// if the attribute to highlight wasn't passed in, don't bother validating
					continue;
				}
				
				String attributeDisplayLabel;
				KimTypeAttributeInfo attr = kimType.getAttributeDefinitionByName(attributeToHighlightOnFail);
				if (attr != null) {
					if (StringUtils.isNotBlank(attr.getComponentName())) {
						attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attr.getComponentName(), attributeToHighlightOnFail);
					} else {
						attributeDisplayLabel = attr.getAttributeLabel();
					}

					getDictionaryValidationService().validateReferenceExistsAndIsActive(componentInstance, relationshipDefinition.getObjectAttributeName(),
							attributeToHighlightOnFail, attributeDisplayLabel);
				}
				
				errors.put(attributeToHighlightOnFail, extractErrorsFromGlobalVariablesErrorMap(attributeToHighlightOnFail));
			}
		}
		return errors;
	}
	
    protected void validateAttributeRequired(String kimTypeId, String objectClassName, String attributeName, Object attributeValue, String errorKey) {
        // check if field is a required field for the business object
        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {
        	AttributeDefinitionMap map = getAttributeDefinitions(kimTypeId);
        	AttributeDefinition definition = map.getByAttributeName(attributeName);
        	
            Boolean required = definition.isRequired();
            ControlDefinition controlDef = definition.getControl();

            if (required != null && required.booleanValue() && !(controlDef != null && controlDef.isHidden())) {

                // get label of attribute for message
                String errorLabel = getAttributeErrorLabel(definition);
                GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_REQUIRED, errorLabel);
            }
        }
    }
    
	protected List<String> validateDataDictionaryAttribute(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
		validatePrimitiveFromDescriptor(kimTypeId, entryName, object, propertyDescriptor);
		return extractErrorsFromGlobalVariablesErrorMap(propertyDescriptor.getName());
	}

    protected void validatePrimitiveFromDescriptor(String kimTypeId, String entryName, Object object, PropertyDescriptor propertyDescriptor) {
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && getDataDictionaryService().isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class<? extends Object> propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        validateAttributeFormat(kimTypeId, entryName, propertyDescriptor.getName(), value.toString(), propertyDescriptor.getName());
                    }
                }
                else {
                	// if it's blank, then we check whether the attribute should be required
                    validateAttributeRequired(kimTypeId, entryName, propertyDescriptor.getName(), value, propertyDescriptor.getName());
                }
            }
        }
    }
    
    /**
     * Constant defines a validation method for an attribute value.
     * <p>Value is "validate"
     */
    public static final String VALIDATE_METHOD="validate";
    
    protected String getAttributeErrorLabel(AttributeDefinition definition) {
        String longAttributeLabel = definition.getLabel();
        String shortAttributeLabel = definition.getShortLabel();
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }
    
    protected Pattern getAttributeValidatingExpression(AttributeDefinition definition) {
    	Pattern regex = null;
        if (definition != null) {
            if (definition.hasValidationPattern()) {
                regex = definition.getValidationPattern().getRegexPattern();
            } else {
                // workaround for existing calls which don't bother checking for null return values
                regex = Pattern.compile(".*");
            }
        }

        return regex;
    }
    
	protected Class<? extends Formatter> getAttributeFormatter(AttributeDefinition definition) {
        Class<? extends Formatter> formatterClass = null;
        if (definition != null && definition.hasFormatterClass()) {
        		try {
        			formatterClass = ClassLoaderUtils.getClass(definition.getFormatterClass(), Formatter.class);
        		} catch (ClassNotFoundException e) {
        			// supressing the ClassNotFoundException here to keep in consistent in the logic which is
        			// calling this code (it's doing a null check) though it really seems like we should
        			// be rethrowing this as a RuntimeException!  Either way, will log a WARN here.
        			LOG.warn("Failed to resolve formatter class: " + definition.getFormatterClass(), e);
        		}
        }
        return formatterClass;
    }
    
	public String getAttributeValidatingErrorMessageKey(AttributeDefinition definition) {
        if (definition != null) {
        	if (definition.hasValidationPattern()) {
        		ValidationPattern validationPattern = definition.getValidationPattern();
        		return validationPattern.getValidationErrorMessageKey();
        	}
        }
        return null;
	}
	
	public String[] getAttributeValidatingErrorMessageParameters(AttributeDefinition definition) {
        if (definition != null) {
        	if (definition.hasValidationPattern()) {
        		ValidationPattern validationPattern = definition.getValidationPattern();
        		String attributeLabel = getAttributeErrorLabel(definition);
        		return validationPattern.getValidationErrorMessageParameters(attributeLabel);
        	}
        }
        return null;
	}
    
	protected BigDecimal getAttributeExclusiveMin(AttributeDefinition definition) {
        return definition == null ? null : definition.getExclusiveMin();
    }

	protected BigDecimal getAttributeInclusiveMax(AttributeDefinition definition) {
        return definition == null ? null : definition.getInclusiveMax();
    }
	
    protected void validateAttributeFormat(String kimTypeId, String objectClassName, String attributeName, String attributeValue, String errorKey) {
    	AttributeDefinitionMap attributeDefinitions = getAttributeDefinitions(kimTypeId);
    	AttributeDefinition definition = attributeDefinitions.getByAttributeName(attributeName);
    	
        String errorLabel = getAttributeErrorLabel(definition);

        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");
        }

        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = definition.getMaxLength();
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, maxLength.toString() });
                return;
            }
            Pattern validationExpression = getAttributeValidatingExpression(definition);
            if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
            	}

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    // Calling formatter class
                    Class<?> formatterClass=getAttributeFormatter(definition);
                    if (formatterClass != null) {
                        try {
                            Method validatorMethod=formatterClass.getDeclaredMethod(
                                    VALIDATE_METHOD, new Class<?>[] {String.class});
                            Object o=validatorMethod.invoke(
                                    formatterClass.newInstance(), attributeValue);
                            if (o instanceof Boolean) {
                                isError=!((Boolean)o).booleanValue();
                            }
                        } catch (Exception e) {
                            LOG.warn(e.getMessage(), e);
                        }
                    }
                    if (isError) {
                    	String errorMessageKey = getAttributeValidatingErrorMessageKey(definition);
                    	String[] errorMessageParameters = getAttributeValidatingErrorMessageParameters(definition);
                        GlobalVariables.getMessageMap().putError(errorKey, errorMessageKey, errorMessageParameters);
                    }
                    return;
                }
            }
            BigDecimal exclusiveMin = getAttributeExclusiveMin(definition);
            if (exclusiveMin != null) {
                try {
                    if (exclusiveMin.compareTo(new BigDecimal(attributeValue)) >= 0) {
                        GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_EXCLUSIVE_MIN,
                        // todo: Formatter for currency?
                                new String[] { errorLabel, exclusiveMin.toString() });
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            BigDecimal inclusiveMax = getAttributeInclusiveMax(definition);
            if (inclusiveMax != null) {
                try {
                    if (inclusiveMax.compareTo(new BigDecimal(attributeValue)) < 0) {
                        GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_INCLUSIVE_MAX,
                        // todo: Formatter for currency?
                                new String[] { errorLabel, inclusiveMax.toString() });
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a max for a non-numeric attribute
                }
            }
        }
    }
    
	protected List<String> extractErrorsFromGlobalVariablesErrorMap(String attributeName) {
		Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
		List<String> errors = new ArrayList<String>();
        if (results instanceof String) {
        	errors.add((String)results);
        } else if ( results != null) {
        	if (results instanceof List) {
	        	List<?> errorList = (List<?>)results;
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
        GlobalVariables.getMessageMap().removeAllErrorMessagesForProperty(attributeName);
        return errors;
	}
	
	protected List<String> validateNonDataDictionaryAttribute(String attributeName, String attributeValue, boolean validateRequired) {
		return new ArrayList<String>();
	}

	protected List<KeyValue> getLocalDataDictionaryAttributeValues(KimTypeAttributeInfo attr) throws ClassNotFoundException {
		List<KeyValue> pairs = new ArrayList<KeyValue>();
		BusinessObjectEntry entry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(attr.getComponentName());
		if ( entry == null ) {
			LOG.warn( "Unable to obtain BusinessObjectEntry for component name: " + attr.getComponentName() );
			return pairs;
		}
		AttributeDefinition definition = entry.getAttributeDefinition(attr.getAttributeName());
		if ( definition == null ) {
			LOG.warn( "No attribute named " + attr.getAttributeName() + " found on BusinessObjectEntry for: " + attr.getComponentName() );
			return pairs;
		}
		String keyValuesFinderName = definition.getControl().getValuesFinderClass();
		if ( StringUtils.isNotBlank(keyValuesFinderName)) {
			try {
				KeyValuesFinder finder = (KeyValuesFinder)Class.forName(keyValuesFinderName).newInstance();
				if (finder instanceof PersistableBusinessObjectValuesFinder) {
	                ((PersistableBusinessObjectValuesFinder) finder).setBusinessObjectClass(ClassLoaderUtils.getClass(definition.getControl().getBusinessObjectClass()));
	                ((PersistableBusinessObjectValuesFinder) finder).setKeyAttributeName(definition.getControl().getKeyAttribute());
	                ((PersistableBusinessObjectValuesFinder) finder).setLabelAttributeName(definition.getControl().getLabelAttribute());
	                if (definition.getControl().getIncludeBlankRow() != null) {
		                ((PersistableBusinessObjectValuesFinder) finder).setIncludeBlankRow(definition.getControl().getIncludeBlankRow()); 
	                }
	                ((PersistableBusinessObjectValuesFinder) finder).setIncludeKeyInDescription(definition.getControl().getIncludeKeyInLabel());
				}
				pairs = finder.getKeyValues();
			} catch ( ClassNotFoundException ex ) {
				LOG.info( "Unable to find class: " + keyValuesFinderName + " in the current context." );
				throw ex;
			} catch (Exception e) {
				LOG.error("Unable to build a KeyValuesFinder for " + attr.getAttributeName(), e);
			}
		} else {
			LOG.warn( "No values finder class defined on the control definition (" + definition.getControl() + ") on BO / attr = " + attr.getComponentName() + " / " + attr.getAttributeName() );
		}
		return pairs;
	}

	protected List<KeyValue> getCustomValueFinderValues(KimTypeAttributeInfo attrib) {
		return new ArrayList<KeyValue>(0);
	}

	@Override
	public List<KeyValue> getAttributeValidValues(String kimTypeId, String attributeName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "getAttributeValidValues(" + kimTypeId + "," + attributeName + ")");			
		}
		KimTypeAttributeInfo attrib = KIMServiceLocatorWeb.getTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(attributeName);
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Found Attribute definition: " + attrib );
		}
		List<KeyValue> pairs = null;
		if ( StringUtils.isNotBlank(attrib.getComponentName()) ) {
			try {
				Class.forName(attrib.getComponentName());
				try {
					pairs = getLocalDataDictionaryAttributeValues(attrib);
				} catch ( ClassNotFoundException ex ) {
					LOG.error( "Got a ClassNotFoundException resolving a values finder - since this should have been executing in the context of the host system - this should not happen.");
					pairs = new ArrayList<KeyValue>(0);
				}
			} catch ( ClassNotFoundException ex ) {
				LOG.error( "Got a ClassNotFoundException resolving a component name (" + attrib.getComponentName() + ") - since this should have been executing in the context of the host system - this should not happen.");
			}
		} else {
			pairs = getCustomValueFinderValues(attrib);
		}
        return pairs;
	}

	/**
	 * @param namespaceCode
	 * @param typeAttribute
	 * @return an AttributeDefinition for the given KimTypeAttributeInfo, or null no base AttributeDefinition 
	 * matches the typeAttribute parameter's attributeName.
	 */
	@SuppressWarnings("unchecked")
	protected AttributeDefinition getDataDictionaryAttributeDefinition( String namespaceCode, String kimTypeId, KimTypeAttributeInfo typeAttribute) {
		// TODO: this method looks like it could use some refactoring
		KimDataDictionaryAttributeDefinition definition = null;
		String componentClassName = typeAttribute.getComponentName();
		String attributeName = typeAttribute.getAttributeName();
		AttributeDefinition baseDefinition = null;
		
		// try to resolve the component name - if not possible - try to pull the definition from the app mediation service
		try {
			Class.forName(componentClassName);
			try {
				baseDefinition = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(componentClassName).getAttributeDefinition(attributeName);
			} catch ( Exception ex ) {
				LOG.error( "Unable to get base DD definition for " + componentClassName + "." + attributeName, ex );
				return definition;
			}
		} catch (ClassNotFoundException ex) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Unable to find class " + componentClassName + " in available classloaders. Deferring to the service bus." );
			}
			baseDefinition = KNSServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getBusinessObjectAttributeDefinition(componentClassName, attributeName);
		}
		
		if (baseDefinition != null) {

			definition = new KimDataDictionaryAttributeDefinition();
			// copy all the base attributes
			definition.setName( baseDefinition.getName() );
			definition.setLabel( baseDefinition.getLabel() );
			definition.setShortLabel( baseDefinition.getShortLabel() );
			definition.setMaxLength( baseDefinition.getMaxLength() );
			definition.setRequired( baseDefinition.isRequired() );

			if (baseDefinition.getFormatterClass() != null) {
				definition.setFormatterClass(baseDefinition.getFormatterClass());
			}
			ControlDefinition control = copy(baseDefinition.getControl());
			if ( StringUtils.isNotBlank( control.getValuesFinderClass() ) ) {
//				try {
//					Class.forName(control.getValuesFinderClass());
//				} catch ( ClassNotFoundException ex ) {
					// not found locally, add the KimAttributeValuesFinder as a proxy
					control.setValuesFinderClass(KimAttributeValuesFinder.class.getName());
//				}
			}
			definition.setControl(control);
			definition.setSortCode(typeAttribute.getSortCode());
			definition.setKimAttrDefnId(typeAttribute.getKimAttributeId());
			definition.setKimTypeId(kimTypeId);

			definition.setForceUppercase(baseDefinition.getForceUppercase());
			
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
						definition.setLookupBoClass( lookupClass.getName() );
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
				LOG.warn("Unable to get DD data for: " + typeAttribute, e);
			}
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

	protected AttributeDefinition getNonDataDictionaryAttributeDefinition(KimTypeAttributeInfo typeAttribute) {
		KimNonDataDictionaryAttributeDefinition definition = new KimNonDataDictionaryAttributeDefinition();
		definition.setName(typeAttribute.getAttributeName());
		definition.setLabel(typeAttribute.getAttributeLabel());
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setKimAttrDefnId(typeAttribute.getKimAttributeId());
		return definition;
	}

//	private Map<String,AttributeDefinitionMap> attributeDefinitionCache = new HashMap<String,AttributeDefinitionMap>();

	@Override
	public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
//		AttributeDefinitionMap definitions = attributeDefinitionCache.get( kimTypeId );
//		if ( definitions == null ) {
			List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
			AttributeDefinitionMap definitions = new AttributeDefinitionMap();
	        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
	        if ( kimType != null ) {
				String nsCode = kimType.getNamespaceCode();	        
				for (KimTypeAttributeInfo typeAttribute : kimType.getAttributeDefinitions()) {
					AttributeDefinition definition = null;
					if (typeAttribute.getComponentName() == null) {
						definition = getNonDataDictionaryAttributeDefinition(typeAttribute);
					} else {
						definition = getDataDictionaryAttributeDefinition(nsCode,kimTypeId,typeAttribute);
					}

					if (definition != null) {
						if(uniqueAttributes!=null && uniqueAttributes.contains(definition.getName())){
							definition.setUnique(true);
						}
						// Perform a parameterized substitution on the applicationUrl
						//					String url = typeAttribute.getApplicationUrl();
						//					url = Utilities.substituteConfigParameters(nsCode, url);
						//					kai.setApplicationUrl(url);

						// TODO : use id for defnid ?
						//		definition.setId(typeAttribute.getKimAttributeId());
						// FIXME: I don't like this - if two attributes have the same sort code, they will overwrite each other
						definitions.put(typeAttribute.getSortCode(), definition);
					}
				}
				// attributeDefinitionCache.put( kimTypeId, definitions );
	        } else {
	        	LOG.warn( "Unable to resolve KIM Type: " + kimTypeId + " - returning an empty AttributeDefinitionMap." );
	        }
//		}
		return definitions;
	}

	protected final String COMMA_SEPARATOR = ", ";

	protected void validateRequiredAttributesAgainstReceived(AttributeSet receivedAttributes){
		// abort if type does not want the qualifiers to be checked
		if ( !isCheckRequiredAttributes() ) {
			return;
		}
		// abort if the list is empty, no attributes need to be checked
		if ( requiredAttributes == null || requiredAttributes.isEmpty() ) {
			return;
		}
		List<String> missingAttributes = new ArrayList<String>();
		// if attributes are null or empty, they're all missing
		if ( receivedAttributes == null || receivedAttributes.isEmpty() ) {
			return;		
		} else {
			for( String requiredAttribute : requiredAttributes ) {
				if( !receivedAttributes.containsKey(requiredAttribute) ) {
					missingAttributes.add(requiredAttribute);
				}
			}
		}
        if(missingAttributes.size()>0) {
        	StringBuffer errorMessage = new StringBuffer();
        	Iterator<String> attribIter = missingAttributes.iterator();
        	while ( attribIter.hasNext() ) {
        		errorMessage.append( attribIter.next() );
        		if( attribIter.hasNext() ) {
        			errorMessage.append( COMMA_SEPARATOR );
        		}
        	}
        	errorMessage.append( " not found in required attributes for this type." );
            throw new KimTypeAttributeValidationException(errorMessage.toString());
        }
	}

	/**
	 * Returns an empty list, indicating that no attributes from this
     * type should be passed to workflow.
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowRoutingAttributes(java.lang.String)
	 */
	@Override
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
		return workflowRoutingAttributes;
	}
	
	@Override
	public boolean validateUniqueAttributes(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
		boolean areAttributesUnique = true;
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		if(uniqueAttributes==null || uniqueAttributes.isEmpty()){
			areAttributesUnique = true;
		} else{
			if(areAttributesEqual(uniqueAttributes, newAttributes, oldAttributes)){
				areAttributesUnique = false;
			}
		}
		return areAttributesUnique;
	}
	
	protected boolean areAttributesEqual(List<String> uniqueAttributeNames, AttributeSet aSet1, AttributeSet aSet2){
		String attrVal1;
		String attrVal2;
		StringValueComparator comparator = StringValueComparator.getInstance();
		for(String uniqueAttributeName: uniqueAttributeNames){
			attrVal1 = getAttributeValue(aSet1, uniqueAttributeName);
			attrVal2 = getAttributeValue(aSet2, uniqueAttributeName);
			attrVal1 = attrVal1==null?"":attrVal1;
			attrVal2 = attrVal2==null?"":attrVal2;
			if(comparator.compare(attrVal1, attrVal2)!=0){
				return false;
			}
		}
		return true;
	}

	protected AttributeSet getErrorAttributeSet(String attributeNameKey, String errorKey, String[] errorArguments){
		AttributeSet validationErrors = new AttributeSet();
		GlobalVariables.getMessageMap().putError(attributeNameKey, errorKey, errorArguments);
		List<String> attributeErrors = extractErrorsFromGlobalVariablesErrorMap(attributeNameKey);
		if(attributeErrors!=null){
			for(String err:attributeErrors){
				validationErrors.put(attributeNameKey, err);
			}
		}
		return validationErrors;
	}
	
    protected boolean areAllAttributeValuesEmpty(AttributeSet attributes){
    	boolean areAllAttributesEmpty = true;
    	if(attributes!=null) {
			for(String attributeNameKey: attributes.keySet()){
				if(StringUtils.isNotEmpty(attributes.get(attributeNameKey))){
					areAllAttributesEmpty = false;
					break;
				}
			}
		}
    	return areAllAttributesEmpty;
    }

	protected String getAttributeValue(AttributeSet aSet, String attributeName){
		if(StringUtils.isEmpty(attributeName)) {
			return null;
		}
		for(String attributeNameKey: aSet.keySet()){
			if(attributeName.equals(attributeNameKey)) {
				return aSet.get(attributeNameKey);
			}
		}
		return null;
	}
	
	@Override
	public List<String> getUniqueAttributes(String kimTypeId){
		KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
        List<String> uniqueAttributes = new ArrayList<String>();
        if ( kimType != null ) {
	        for(KimTypeAttributeInfo attributeDefinition: kimType.getAttributeDefinitions()){
	        	uniqueAttributes.add(attributeDefinition.getAttributeName());
	        }
        } else {
        	LOG.error("Unable to retrieve a KimTypeInfo for a null kimTypeId in getUniqueAttributes()");
        }
        return uniqueAttributes;
	}

	@Override
	public AttributeSet validateUnmodifiableAttributes(String kimTypeId, AttributeSet originalAttributeSet, AttributeSet newAttributeSet){
		AttributeSet validationErrors = new AttributeSet();
		List<String> attributeErrors = null;
		KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		for(String attributeNameKey: uniqueAttributes){
			KimTypeAttributeInfo attr = kimType.getAttributeDefinitionByName(attributeNameKey);
			String mainAttributeValue = getAttributeValue(originalAttributeSet, attributeNameKey);
			String delegationAttributeValue = getAttributeValue(newAttributeSet, attributeNameKey);
			attributeErrors = null;
			if(!StringUtils.equals(mainAttributeValue, delegationAttributeValue)){
				GlobalVariables.getMessageMap().putError(
					attributeNameKey, RiceKeyConstants.ERROR_CANT_BE_MODIFIED, 
					dataDictionaryService.getAttributeLabel(attr.getComponentName(), attributeNameKey));
				attributeErrors = extractErrorsFromGlobalVariablesErrorMap(attributeNameKey);
			}
			if(attributeErrors!=null){
				for(String err:attributeErrors){
					validationErrors.put(attributeNameKey, err);
				}
			}
		}
		return validationErrors;
	}

	public boolean isCheckRequiredAttributes() {
		return this.checkRequiredAttributes;
	}

	public void setCheckRequiredAttributes(boolean checkRequiredAttributes) {
		this.checkRequiredAttributes = checkRequiredAttributes;
	}

	@Override
	public AttributeSet validateAttributesAgainstExisting(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
		return new AttributeSet();
	}

	protected String getClosestParentDocumentTypeName(
			DocumentType documentType,
			Set<String> potentialParentDocumentTypeNames) {
		if ( potentialParentDocumentTypeNames == null || documentType == null ) {
			return null;
		}
		if (potentialParentDocumentTypeNames.contains(documentType.getName())) {
			return documentType.getName();
		} 
		if ((documentType.getDocTypeParentId() == null)
				|| documentType.getDocTypeParentId().equals(
						documentType.getDocumentTypeId())) {
			return null;
		} 
		return getClosestParentDocumentTypeName(documentType
				.getParentDocType(), potentialParentDocumentTypeNames);
	}
}
