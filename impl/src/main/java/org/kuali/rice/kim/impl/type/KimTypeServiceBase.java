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
package org.kuali.rice.kim.impl.type;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.api.type.KimTypeService;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.comparator.StringValueComparator;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.KimAttributeDefinition;
import org.kuali.rice.krad.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.keyvalues.KimAttributeValuesFinder;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
			typeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return typeInfoService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected DictionaryValidationService getDictionaryValidationService() {
		if ( dictionaryValidationService == null ) {
			dictionaryValidationService = KRADServiceLocatorWeb.getDictionaryValidationService();
		}
		return dictionaryValidationService;
	}

	protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	/**
	 * Returns null, to indicate that there is no custom workflow document needed for this type.
	 *
	 * @see org.kuali.rice.kim.api.type.KimTypeService#getWorkflowDocumentTypeName()
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
	protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
		if ( storedAttributes == null || inputAttributes == null ) {
			return true;
		}
		for ( Map.Entry<String, String> entry : storedAttributes.entrySet() ) {
			if (inputAttributes.containsKey(entry.getKey()) && !StringUtils.equals(inputAttributes.get(entry.getKey()), entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	public Map<String, String> translateInputAttributes(Map<String, String> qualification){
		return qualification;
	}

	/**
	 * This is the default implementation.  It calls into the service for each attribute to
	 * validate it there.  No combination validation is done.  That should be done
	 * by overriding this method.
	 *
	 * @see org.kuali.rice.kim.api.type.KimTypeService#validateAttributes(Map<String, String>)
	 */
	@Override
	public Map<String, String> validateAttributes(String kimTypeId, Map<String, String> attributes) {
		Map<String,String> validationErrors = new HashMap<String, String>();
		if ( attributes == null ) {
			return Collections.emptyMap();
		}
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);
		
		for ( String attributeName : attributes.keySet() ) {
            KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeName);
			List<String> attributeErrors = null;
			try {
				if ( attr.getKimAttribute().getComponentName() == null) {
					attributeErrors = validateNonDataDictionaryAttribute(attributeName, attributes.get( attributeName ), true);
				} else {
					// create an object of the proper type per the component
		            Object componentObject = Class.forName( attr.getKimAttribute().getComponentName() ).newInstance();
		            // get the bean utils descriptor for accessing the attribute on that object
		            PropertyDescriptor propertyDescriptor = null;
		            if ( attr.getKimAttribute().getAttributeName() != null ) {
		            	propertyDescriptor = PropertyUtils.getPropertyDescriptor(componentObject, attr.getKimAttribute().getAttributeName());
						if ( propertyDescriptor != null ) {
							// set the value on the object so that it can be checked
							Object attributeValue = getAttributeValue(propertyDescriptor, attributes.get(attributeName));
							propertyDescriptor.getWriteMethod().invoke( componentObject, attributeValue);
							attributeErrors = validateDataDictionaryAttribute(kimTypeId, attr.getKimAttribute().getComponentName(), componentObject, propertyDescriptor);
						}
		            }
					if ( propertyDescriptor == null ) {
						LOG.warn( "Unable to obtain property descriptor for: " + attr.getKimAttribute().getComponentName() + "/" + attr.getKimAttribute().getAttributeName() );
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
				attributeValueObject = KRADUtils
                        .createObject(propertyType, new Class[]{String.class}, new Object[]{attributeValue});
			} else {
				attributeValueObject = attributeValue;
			}
		}
		return attributeValueObject;
	}
	
	protected Map<String, List<String>> validateReferencesExistAndActive( KimType kimType, Map<String, String> attributes, Map<String, String> previousValidationErrors) {
		Map<String, BusinessObject> componentClassInstances = new HashMap<String, BusinessObject>();
		Map<String, List<String>> errors = new HashMap<String, List<String>>();
		
		for ( String attributeName : attributes.keySet() ) {
			KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeName);
			
			if (StringUtils.isNotBlank(attr.getKimAttribute().getComponentName())) {
				if (!componentClassInstances.containsKey(attr.getKimAttribute().getComponentName())) {
					try {
						Class<?> componentClass = Class.forName( attr.getKimAttribute().getComponentName() );
						if (!BusinessObject.class.isAssignableFrom(componentClass)) {
							LOG.warn("Class " + componentClass.getName() + " does not implement BusinessObject.  Unable to perform reference existence and active validation");
							continue;
						}
						BusinessObject componentInstance = (BusinessObject) componentClass.newInstance();
						componentClassInstances.put(attr.getKimAttribute().getComponentName(), componentInstance);
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
				KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeToHighlightOnFail);
				if (attr != null) {
					if (StringUtils.isNotBlank(attr.getKimAttribute().getComponentName())) {
						attributeDisplayLabel = getDataDictionaryService().getAttributeLabel(attr.getKimAttribute().getComponentName(), attributeToHighlightOnFail);
					} else {
						attributeDisplayLabel = attr.getKimAttribute().getAttributeLabel();
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
    
	protected String getAttributeExclusiveMin(AttributeDefinition definition) {
        return definition == null ? null : definition.getExclusiveMin();
    }

	protected String getAttributeInclusiveMax(AttributeDefinition definition) {
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
            String exclusiveMin = getAttributeExclusiveMin(definition);
            if (StringUtils.isNotBlank(exclusiveMin)) {
                try {
                	BigDecimal exclusiveMinBigDecimal = new BigDecimal(exclusiveMin);
                    if (exclusiveMinBigDecimal.compareTo(new BigDecimal(attributeValue)) >= 0) {
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
            String inclusiveMax = getAttributeInclusiveMax(definition);
            if (StringUtils.isNotBlank(inclusiveMax)) {
                try {
                	BigDecimal inclusiveMaxBigDecimal = new BigDecimal(inclusiveMax);
                    if (inclusiveMaxBigDecimal.compareTo(new BigDecimal(attributeValue)) < 0) {
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




	/**
	 * @param namespaceCode
	 * @param typeAttribute
	 * @return an AttributeDefinition for the given KimTypeAttribute, or null no base AttributeDefinition 
	 * matches the typeAttribute parameter's attributeName.
	 */
	@SuppressWarnings("unchecked")
	protected AttributeDefinition getDataDictionaryAttributeDefinition( String namespaceCode, String kimTypeId, KimTypeAttribute typeAttribute) {
		// TODO: this method looks like it could use some refactoring
		KimDataDictionaryAttributeDefinition definition = null;
		String componentClassName = typeAttribute.getKimAttribute().getComponentName();
		String attributeName = typeAttribute.getKimAttribute().getAttributeName();
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
			baseDefinition = KRADServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getBusinessObjectAttributeDefinition(componentClassName, attributeName);
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
			definition.setKimAttrDefnId(typeAttribute.getKimAttribute().getId());
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



	protected AttributeDefinition getNonDataDictionaryAttributeDefinition(KimTypeAttribute typeAttribute) {
		KimAttributeDefinition definition = new KimAttributeDefinition();
		definition.setName(typeAttribute.getKimAttribute().getAttributeName());
		definition.setLabel(typeAttribute.getKimAttribute().getAttributeLabel());
		definition.setSortCode(typeAttribute.getSortCode());
		definition.setKimAttrDefnId(typeAttribute.getKimAttribute().getId());
		return definition;
	}

//	private Map<String,AttributeDefinitionMap> attributeDefinitionCache = new HashMap<String,AttributeDefinitionMap>();

	@Override
	public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
//		AttributeDefinitionMap definitions = attributeDefinitionCache.get( kimTypeId );
//		if ( definitions == null ) {
			List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
			AttributeDefinitionMap definitions = new AttributeDefinitionMap();
	        KimType kimType = getTypeInfoService().getKimType(kimTypeId);
	        if ( kimType != null ) {
				String nsCode = kimType.getNamespaceCode();	        
				for (KimTypeAttribute typeAttribute : kimType.getAttributeDefinitions()) {
					AttributeDefinition definition = null;
					if (typeAttribute.getKimAttribute().getComponentName() == null) {
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

	protected void validateRequiredAttributesAgainstReceived(Map<String, String> receivedAttributes){
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
	 * @see org.kuali.rice.kim.api.type.KimTypeService#getWorkflowRoutingAttributes(java.lang.String)
	 */
	@Override
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
		return Collections.unmodifiableList(workflowRoutingAttributes);
	}
	
	protected Map<String, String> validateUniqueAttributes(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes) {
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		if(uniqueAttributes==null || uniqueAttributes.isEmpty()){
			return Collections.emptyMap();
		} else{
			Map<String, String> m = new HashMap<String, String>();
            if(areAttributesEqual(uniqueAttributes, newAttributes, oldAttributes)){
				//add all unique attrs to error map
                for (String a : uniqueAttributes) {
                    m.put(a, RiceKeyConstants.ERROR_DUPLICATE_ENTRY);
                }

                return m;
			}
		}
		return Collections.emptyMap();
	}
	
	protected boolean areAttributesEqual(List<String> uniqueAttributeNames, Map<String, String> aSet1, Map<String, String> aSet2){
		StringValueComparator comparator = StringValueComparator.getInstance();
		for(String uniqueAttributeName: uniqueAttributeNames){
			String attrVal1 = getAttributeValue(aSet1, uniqueAttributeName);
			String attrVal2 = getAttributeValue(aSet2, uniqueAttributeName);
			if(comparator.compare(attrVal1, attrVal2)!=0){
				return false;
			}
		}
		return true;
	}

	protected String getAttributeValue(Map<String, String> aSet, String attributeName){
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

	protected List<String> getUniqueAttributes(String kimTypeId){
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        List<String> uniqueAttributes = new ArrayList<String>();
        if ( kimType != null ) {
	        for(KimTypeAttribute attributeDefinition: kimType.getAttributeDefinitions()){
	        	uniqueAttributes.add(attributeDefinition.getKimAttribute().getAttributeName());
	        }
        } else {
        	LOG.error("Unable to retrieve a KimTypeInfo for a null kimTypeId in getUniqueAttributes()");
        }
        return Collections.unmodifiableList(uniqueAttributes);
	}

	protected Map<String, String> validateUnmodifiableAttributes(String kimTypeId, Map<String, String> originalAttributes, Map<String, String> newAttributes){
		Map<String, String> validationErrors = new HashMap<String, String>();
		KimType kimType = getTypeInfoService().getKimType(kimTypeId);
		List<String> uniqueAttributes = getUniqueAttributes(kimTypeId);
		for(String attributeNameKey: uniqueAttributes){
			KimTypeAttribute attr = kimType.getAttributeDefinitionByName(attributeNameKey);
			String mainAttributeValue = getAttributeValue(originalAttributes, attributeNameKey);
			String delegationAttributeValue = getAttributeValue(newAttributes, attributeNameKey);
			List<String> attributeErrors = null;
			if(!StringUtils.equals(mainAttributeValue, delegationAttributeValue)){
				GlobalVariables.getMessageMap().putError(
					attributeNameKey, RiceKeyConstants.ERROR_CANT_BE_MODIFIED, 
					dataDictionaryService.getAttributeLabel(attr.getKimAttribute().getComponentName(), attributeNameKey));
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
	public Map<String, String> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes){
        Map<String, String> errors = new HashMap<String, String>();
        errors.putAll(validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes));
               ;
        return validateUnmodifiableAttributes(kimTypeId, newAttributes, oldAttributes);
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

    public static class KimTypeAttributeValidationException extends RuntimeException {

        public KimTypeAttributeValidationException(String message) {
            super( message );
        }

        public KimTypeAttributeValidationException( String message, Throwable cause ) {
            super( message, cause );
        }

        private static final long serialVersionUID = 8220618846321607801L;

    }
}
