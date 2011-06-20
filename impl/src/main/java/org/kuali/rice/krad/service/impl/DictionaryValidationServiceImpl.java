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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.Inactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.ComplexAttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
import org.kuali.rice.krad.datadictionary.validation.MaintenanceDocumentAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.SingleAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.provider.ConstraintProvider;
import org.kuali.rice.krad.datadictionary.validation.processor.CollectionConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.processor.ConstraintProcessor;
import org.kuali.rice.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.exception.ObjectNotABusinessObjectRuntimeException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowAttributePropertyResolutionService;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates Documents, Business Objects, and Attributes against the data dictionary. Including min, max lengths, and validating
 * expressions. This is the default, Kuali delivered implementation.
 * 
 * KULRICE - 3355 Modified to prevent infinite looping (to maxDepth) scenario when a parent references a child which references a parent
 */
public class DictionaryValidationServiceImpl implements DictionaryValidationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DictionaryValidationServiceImpl.class);

    /**
     * Constant defines a validation method for an attribute value.
     * <p>Value is "validate"
     */
    public static final String VALIDATE_METHOD="validate";
    
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private TransactionalDocumentDictionaryService transactionalDocumentDictionaryService;
    private PersistenceService persistenceService;
    private WorkflowAttributePropertyResolutionService workflowAttributePropertyResolutionService;

    private PersistenceStructureService persistenceStructureService;
    
    @SuppressWarnings("unchecked")
	private List<CollectionConstraintProcessor> collectionConstraintProcessors;
    @SuppressWarnings("unchecked")
    private List<ConstraintProvider> constraintProviders;
	@SuppressWarnings("unchecked")
	private List<ConstraintProcessor> elementConstraintProcessors;
	
	
    /** 
     * creates a new IdentitySet.
     * @return a new Set
     */
    private static Set<BusinessObject> newIdentitySet() {
    	return java.util.Collections.newSetFromMap(new IdentityHashMap<BusinessObject, Boolean>());
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object)
     */
	public DictionaryValidationResult validate(Object object) {
    	return validate(object, object.getClass().getName(), true);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, boolean)
     */
	public DictionaryValidationResult validate(Object object, boolean doOptionalProcessing) {
    	return validate(object, object.getClass().getName(), doOptionalProcessing);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String)
     */
	public DictionaryValidationResult validate(Object object, String entryName) {
    	return validate(object, entryName, true);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String, boolean)
     */
	public DictionaryValidationResult validate(Object object, String entryName, boolean doOptionalProcessing) {
    	return validate(object, entryName, (String)null, doOptionalProcessing);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String, java.lang.String)
     */
	public DictionaryValidationResult validate(Object object, String entryName, String attributeName) {
    	return validate(object, entryName, attributeName, true);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String, java.lang.String, boolean)
     */
	public DictionaryValidationResult validate(Object object, String entryName, String attributeName, boolean doOptionalProcessing) {
    	DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
    	AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
    	attributeValueReader.setAttributeName(attributeName);
    	return validate(attributeValueReader, doOptionalProcessing);
    }
    
    public DictionaryValidationResult validate(Object object, String entryName, DataDictionaryEntry entry, boolean doOptionalProcessing) {
    	AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object,entryName, entry);
    	return validate(attributeValueReader, doOptionalProcessing);
    }
    
    public void validate(String entryName, String attributeName, Object attributeValue) {
    	validate(entryName, attributeName, attributeValue, true);
    }
    
    public void validate(String entryName, String attributeName, Object attributeValue, boolean doOptionalProcessing) {
       	AttributeDefinition attributeDefinition = getDataDictionaryService().getAttributeDefinition(entryName, attributeName);
    	
    	if (attributeDefinition == null) {
    		// FIXME: JLR - this is what the code was doing effectively already, but seems weird not to throw an exception here if you try to validate 
    		// something that doesn't have an attribute definition
    		return;
    	}
    	
    	SingleAttributeValueReader attributeValueReader = new SingleAttributeValueReader(attributeValue, entryName, attributeName, attributeDefinition);
    	validate(attributeValueReader, doOptionalProcessing);
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
	public void validateDocument(Document document) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        validate(document, documentEntryName);        
    }


    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentAttribute(org.kuali.rice.krad.document.Document,
     *      java.lang.String,java.lang.String)
     */
    @Override
	public void validateDocumentAttribute(Document document, String attributeName, String errorPrefix) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        validate(document, documentEntryName, attributeName, true);        
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentRecursively
     */
    @Deprecated public void validateDocumentRecursively(Document document, int depth) {
        // validate primitives of document
        validateDocument(document);

        // call method to recursively find business objects and validate
        validateBusinessObjectsFromDescriptors(document, PropertyUtils.getPropertyDescriptors(document.getClass()), depth);
    }

    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth, boolean validateRequired) {
    	validateDocumentAndUpdatableReferencesRecursively(document, maxDepth, validateRequired, false);
    }
    
    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth, boolean validateRequired, boolean chompLastLetterSFromCollectionName) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        validate(document, documentEntryName);
        
        if (maxDepth > 0) {
            validateUpdatabableReferencesRecursively(document, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName,  newIdentitySet());
        }
    }
    
    private void validateUpdatabableReferencesRecursively(BusinessObject businessObject, int maxDepth, boolean validateRequired, boolean chompLastLetterSFromCollectionName, Set<BusinessObject> processedBOs) {
    	// if null or already processed, return
    	if (ObjectUtils.isNull(businessObject) || processedBOs.contains(businessObject)) {
    		return;
    	}
        processedBOs.add(businessObject);  // add bo to list to prevent excessive looping
    	Map<String, Class> references = persistenceStructureService.listReferenceObjectFields(businessObject.getClass());
    	for (String referenceName : references.keySet()) {
    		if (persistenceStructureService.isReferenceUpdatable(businessObject.getClass(), referenceName)) {
    			Object referenceObj = ObjectUtils.getPropertyValue(businessObject, referenceName);

    			if (ObjectUtils.isNull(referenceObj) || !(referenceObj instanceof PersistableBusinessObject)) {
    				continue;
    			}

    			BusinessObject referenceBusinessObject = (BusinessObject) referenceObj;
    			GlobalVariables.getMessageMap().addToErrorPath(referenceName);
    			validateBusinessObject(referenceBusinessObject, validateRequired);
    			if (maxDepth > 0) {
    				validateUpdatabableReferencesRecursively(referenceBusinessObject, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName, processedBOs);
    			}
    			GlobalVariables.getMessageMap().removeFromErrorPath(referenceName);

    		}
    	}
    	Map<String, Class> collections = persistenceStructureService.listCollectionObjectTypes(businessObject.getClass());
    	for (String collectionName : collections.keySet()) {
    		if (persistenceStructureService.isCollectionUpdatable(businessObject.getClass(), collectionName)) {
    			Object listObj = ObjectUtils.getPropertyValue(businessObject, collectionName);

    			if (ObjectUtils.isNull(listObj)) {
    				continue;
    			}

    			if (!(listObj instanceof List)) {
					if ( LOG.isInfoEnabled() ) {
						LOG.info("The reference named " + collectionName + " of BO class " + businessObject.getClass().getName() + " should be of type java.util.List to be validated properly.");
					}
    				continue;
    			}

    			List list = (List) listObj;
    			
    			//should we materialize the proxied collection or just skip validation here assuming an unmaterialized objects are valid?
    			ObjectUtils.materializeObjects(list);
    			
    			for (int i = 0; i < list.size(); i++) {
    				final Object o = list.get(i);
    				if (ObjectUtils.isNotNull(o) && o instanceof PersistableBusinessObject) {
    					final BusinessObject element = (BusinessObject) o;
    					
    					final String errorPathAddition;
    					if (chompLastLetterSFromCollectionName) {
    						errorPathAddition = StringUtils.chomp(collectionName, "s") + "[" + Integer.toString(i) + "]";
    					} else {
    						errorPathAddition = collectionName + "[" + Integer.toString(i) + "]";
    					}

    					GlobalVariables.getMessageMap().addToErrorPath(errorPathAddition);
    					validateBusinessObject(element, validateRequired);
    					if (maxDepth > 0) {
    						validateUpdatabableReferencesRecursively(element, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName, processedBOs);
    					}
    					GlobalVariables.getMessageMap().removeFromErrorPath(errorPathAddition);
    				}
    			}
    		}
    	}
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void validateBusinessObject(BusinessObject businessObject) {
        validateBusinessObject(businessObject, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject,boolean)
     */
    @Override
    public void validateBusinessObject(BusinessObject businessObject, boolean validateRequired) {
        if (ObjectUtils.isNull(businessObject)) {
            return;
        }
        
        validate(businessObject, businessObject.getClass().getName());        
    }

    /**
	 * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObjectOnMaintenanceDocument(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
	 * 
	 * @deprecated since 1.1
	 */
	@Deprecated public void validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName) {

		MaintenanceDocumentEntry entry = KRADServiceLocatorWeb.getMaintenanceDocumentDictionaryService().getMaintenanceDocumentEntry(docTypeName);
		validate(new MaintenanceDocumentAttributeValueReader(businessObject, docTypeName, entry, persistenceStructureService), true);
	}
	
//	protected void validateBusinessObjectOnMaintenanceDocumentHelper(BusinessObject businessObject, List<? extends MaintainableItemDefinition> itemDefinitions, String errorPrefix) {
//		
//		for (MaintainableItemDefinition itemDefinition : itemDefinitions) {
//			if (itemDefinition instanceof MaintainableFieldDefinition) {
//		        if (getDataDictionaryService().isAttributeDefined(businessObject.getClass(), itemDefinition.getName())) {
//		            Object value = ObjectUtils.getPropertyValue(businessObject, itemDefinition.getName());
//		            if (value != null && StringUtils.isNotBlank(value.toString())) {
//			            Class propertyType = ObjectUtils.getPropertyType(businessObject, itemDefinition.getName(), persistenceStructureService);
//			            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {
//			                // check value format against dictionary
//		                    if (!TypeUtils.isTemporalClass(propertyType)) {
//		                        validateAttributeFormat(businessObject.getClass().getName(), itemDefinition.getName(), value.toString(), errorPrefix + itemDefinition.getName());
//		                    }
//			            }
//		            }
//		        }
//			}
//			/*
//			TODO: reenable when we come up with a strategy to handle fields that are not editable
//			else if (itemDefinition instanceof MaintainableCollectionDefinition) {
//				MaintainableCollectionDefinition collectionDefinition = (MaintainableCollectionDefinition) itemDefinition;
//				Collection<BusinessObject> c = (Collection<BusinessObject>) ObjectUtils.getPropertyValue(businessObject, itemDefinition.getName());
//				if (c != null) {
//					int i = 0;
//					for (BusinessObject o : c) {
//						String newErrorPrefix = errorPrefix + itemDefinition.getName() + "[" + i + "].";
//						validateBusinessObjectOnMaintenanceDocumentHelper(o, collectionDefinition.getMaintainableCollections(), newErrorPrefix);
//						validateBusinessObjectOnMaintenanceDocumentHelper(o, collectionDefinition.getMaintainableFields(), newErrorPrefix);
//						i++;
//					}
//				}
//			}*/
//		}
//	}


	/**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.krad.bo.BusinessObject)
     */
    public boolean isBusinessObjectValid(BusinessObject businessObject) {
        return isBusinessObjectValid(businessObject, null);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.krad.bo.BusinessObject, String)
     */
    public boolean isBusinessObjectValid(BusinessObject businessObject, String prefix) {
        final MessageMap errorMap = GlobalVariables.getMessageMap();
        int originalErrorCount = errorMap.getErrorCount();

        errorMap.addToErrorPath(prefix);
        validateBusinessObject(businessObject);
        errorMap.removeFromErrorPath(prefix);

        return errorMap.getErrorCount() == originalErrorCount;
    }


    /**
     * @param businessObject - business object to validate
     */
    public void validateBusinessObjectsRecursively(BusinessObject businessObject, int depth) {
        if (ObjectUtils.isNull(businessObject)) {
            return;
        }

        // validate primitives and any specific bo validation
        validateBusinessObject(businessObject);

        // call method to recursively find business objects and validate
        validateBusinessObjectsFromDescriptors(businessObject, PropertyUtils.getPropertyDescriptors(businessObject.getClass()), depth);
    }


    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateAttributeFormat
     * objectClassName is the docTypeName
     * 
     * @deprecated since 1.1
     */
    @Deprecated public void validateAttributeFormat(String objectClassName, String attributeName, String attributeInValue, String errorKey) {
        // Retrieve the field's data type, or set to the string data type if an exception occurs when retrieving the class or the DD entry.
        String attributeDataType = null;
        try {
        	attributeDataType = getWorkflowAttributePropertyResolutionService().determineFieldDataType((Class<? extends BusinessObject>)Class.forName(
        			getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(objectClassName).getFullClassName()), attributeName);
        } catch(ClassNotFoundException e) {
        	attributeDataType = KRADConstants.DATA_TYPE_STRING;
        } catch (NullPointerException e) {
        	attributeDataType = KRADConstants.DATA_TYPE_STRING;
        }
        
        validateAttributeFormat(objectClassName, attributeName, attributeInValue, attributeDataType, errorKey);
    }

    /**
     * The attributeDataType parameter should be one of the data types specified by the SearchableAttribute interface; will
     * default to DATA_TYPE_STRING if a data type other than the ones from SearchableAttribute is specified.
     * 
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateAttributeFormat(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     * objectClassName is the docTypeName
     * 
     * @deprecated since 1.1
     */
    @Deprecated public void validateAttributeFormat(String objectClassName, String attributeName, String attributeInValue, String attributeDataType, String errorKey) {
    	boolean checkDateBounds = false; // this is used so we can check date bounds
    	Class<?> formatterClass = null;

    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeInValue + ")");
    	}

    	/*
    	 *  This will return a list of searchable attributes. so if the value is
    	 *  12/07/09 .. 12/08/09 it will return [12/07/09,12/08/09]
    	 */

    	final List<String> attributeValues = SQLUtils.getCleanedSearchableValues(attributeInValue, attributeDataType);

    	if(attributeValues == null || attributeValues.isEmpty()) {
    		return;
    	}
    	
    	for(String attributeValue : attributeValues){

    		// FIXME: JLR : Replacing this logic with KS-style validation is trickier, since KS validation requires a DataProvider object that can
    		// look back and find other attribute values aside from the one we're working on.
    		// Also - the date stuff below is implemented very differently.
    		//validator.validateAttributeField(businessObject, fieldName);
    		
    		if (StringUtils.isNotBlank(attributeValue)) {
    			Integer minLength = getDataDictionaryService().getAttributeMinLength(objectClassName, attributeName);
    			if ((minLength != null) && (minLength.intValue() > attributeValue.length())) {
    				String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
    				GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_MIN_LENGTH, new String[] { errorLabel, minLength.toString() });
    				return;
    			}
    			Integer maxLength = getDataDictionaryService().getAttributeMaxLength(objectClassName, attributeName);
    			if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
    				String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
    				GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, maxLength.toString() });
    				return;
    			}
    			Pattern validationExpression = getDataDictionaryService().getAttributeValidatingExpression(objectClassName, attributeName);
    			if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
    				if ( LOG.isDebugEnabled() ) {
    					LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");
    				}

    				if (!validationExpression.matcher(attributeValue).matches()) {
    					// Retrieving formatter class
    					if(formatterClass == null){
    						// this is just a cache check... all dates ranges get called twice
    						formatterClass=getDataDictionaryService().getAttributeFormatter(
    								objectClassName, attributeName);
    					}

    					if (formatterClass != null) {
    						boolean valuesAreValid = true;
    						boolean isError=true;
    						String errorKeyPrefix = "";
    						try {

    							// this is a special case for date ranges in order to set the proper error message
    							if (DateFormatter.class.isAssignableFrom(formatterClass)) {
    								String[] values = attributeInValue.split("\\.\\."); // is it a range
    								if(values.length == 2 && attributeValues.size() == 2){ // make sure it's not like a .. b | c
    									checkDateBounds = true; // now we need to check that a <= b
    									if(attributeValues.indexOf(attributeValue) == 0){ // only care about lower bound
    										errorKeyPrefix = KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX;
    									}
    								}
    							}

    							Method validatorMethod=formatterClass.getDeclaredMethod(
    									VALIDATE_METHOD, new Class<?>[] {String.class});
    							Object o=validatorMethod.invoke(
    									formatterClass.newInstance(), attributeValue);
    							if (o instanceof Boolean) {
    								isError = !((Boolean)o).booleanValue();
    							}
    							valuesAreValid &= !isError;
    						} catch (Exception e) {
    							if ( LOG.isDebugEnabled() ) {
    								LOG.debug(e.getMessage(), e);
    							}
    							isError =true;
    							valuesAreValid = false;
    						}
    						if (isError) {
    							checkDateBounds = false; // it's already invalid, no need to check date bounds
    							String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(objectClassName, attributeName);
    							String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(objectClassName, attributeName);
    							GlobalVariables.getMessageMap().putError(errorKeyPrefix + errorKey, errorMessageKey, errorMessageParameters);
    						}
    					} else {
    						// if it fails the default validation and has no formatter class then it's still a std failure.
    						String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(objectClassName, attributeName);
    						String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(objectClassName, attributeName);
    						GlobalVariables.getMessageMap().putError(errorKey, errorMessageKey, errorMessageParameters);
    					}
    				}

    			}
    			/*BigDecimal*/ String exclusiveMin = getDataDictionaryService().getAttributeExclusiveMin(objectClassName, attributeName);
    			if (exclusiveMin != null) {
    				try {
    					BigDecimal exclusiveMinBigDecimal = new BigDecimal(exclusiveMin);
    					if (exclusiveMinBigDecimal.compareTo(new BigDecimal(attributeValue)) >= 0) {
    						String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
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
    			/*BigDecimal*/ String inclusiveMax = getDataDictionaryService().getAttributeInclusiveMax(objectClassName, attributeName);
    			if (inclusiveMax != null) {
    				try {
    					BigDecimal inclusiveMaxBigDecimal = new BigDecimal(inclusiveMax);
    					if (inclusiveMaxBigDecimal.compareTo(new BigDecimal(attributeValue)) < 0) {
    						String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
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

    	if(checkDateBounds){
    		// this means that we only have 2 values and it's a date range.
    		java.sql.Timestamp lVal = null;
    		java.sql.Timestamp uVal = null;
    		try{
    			lVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(attributeValues.get(0));
    			uVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(attributeValues.get(1));
    		}catch(Exception ex){
    			// this shouldn't happen because the tests passed above.
    			String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(objectClassName, attributeName);
    			String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(objectClassName, attributeName);
    			GlobalVariables.getMessageMap().putError(
                        KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + errorKey, errorMessageKey, errorMessageParameters);
    		}

    		if(lVal != null && lVal.compareTo(uVal) > 0){ // check the bounds
    			String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(objectClassName, attributeName);
    			String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(objectClassName, attributeName);
    			GlobalVariables.getMessageMap().putError(
                        KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + errorKey, errorMessageKey + ".range", errorMessageParameters);
    		}
    	}
    }



    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateAttributeRequired
     */
    // FIXME: JLR - this is now redundant and should be using the same code as the required processing elsewhere, but the control definition stuff doesn't really fit
    // it doesn't seem to be used anywhere
    @Deprecated public void validateAttributeRequired(String objectClassName, String attributeName, Object attributeValue, Boolean forMaintenance, String errorKey) {
        // check if field is a required field for the business object
        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {
            Boolean required = getDataDictionaryService().isAttributeRequired(objectClassName, attributeName);
            ControlDefinition controlDef = getDataDictionaryService().getAttributeControlDefinition(objectClassName, attributeName);

            if (required != null && required.booleanValue() && !(controlDef != null && controlDef.isHidden())) {

                // get label of attribute for message
                String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
                GlobalVariables.getMessageMap().putError(errorKey, RiceKeyConstants.ERROR_REQUIRED, errorLabel);
            }
        }
    }

    /**
     * iterates through the property discriptors looking for business objects or lists of business objects. calls validate method
     * for each bo found
     * 
     * @param object
     * @param propertyDescriptors
     */
    private void validateBusinessObjectsFromDescriptors(Object object, PropertyDescriptor[] propertyDescriptors, int depth) {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // validate the properties that are descended from BusinessObject
            if (propertyDescriptor.getPropertyType() != null && PersistableBusinessObject.class.isAssignableFrom(propertyDescriptor.getPropertyType()) && ObjectUtils.getPropertyValue(object, propertyDescriptor.getName()) != null) {
                BusinessObject bo = (BusinessObject) ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
                if (depth == 0) {
                    GlobalVariables.getMessageMap().addToErrorPath(propertyDescriptor.getName());
                    validateBusinessObject(bo);
                    GlobalVariables.getMessageMap().removeFromErrorPath(propertyDescriptor.getName());
                }
                else {
                    validateBusinessObjectsRecursively(bo, depth - 1);
                }
            }

            /*
             * if property is a List, then walk the list and do the validation on each contained object that is a descendent of
             * BusinessObject
             */
            else if (propertyDescriptor.getPropertyType() != null && (List.class).isAssignableFrom(propertyDescriptor.getPropertyType()) && ObjectUtils.getPropertyValue(object, propertyDescriptor.getName()) != null) {
                List propertyList = (List) ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
                for (int j = 0; j < propertyList.size(); j++) {
                    if (propertyList.get(j) != null && propertyList.get(j) instanceof PersistableBusinessObject) {
                        if (depth == 0) {
                            GlobalVariables.getMessageMap().addToErrorPath(StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" + (new Integer(j)).toString() + "]");
                            validateBusinessObject((BusinessObject) propertyList.get(j));
                            GlobalVariables.getMessageMap().removeFromErrorPath(StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" + (new Integer(j)).toString() + "]");
                        }
                        else {
                            validateBusinessObjectsRecursively((BusinessObject) propertyList.get(j), depth - 1);
                        }
                    }
                }

            }
        }
    }

    /**
     * calls validate format and required check for the given propertyDescriptor
     * 
     * @param entryName
     * @param object
     * @param propertyDescriptor
     * @param errorPrefix
     * 
     * @deprecated since 1.1
     */
    @Deprecated public void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor, String errorPrefix, boolean validateRequired) {
    	
    	// validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor) { 
        	validate(object, entryName, propertyDescriptor.getName(), validateRequired);
        }
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExists(org.kuali.rice.krad.bo.BusinessObject,
     *      org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    public boolean validateReferenceExists(BusinessObject bo, ReferenceDefinition reference) {
        return validateReferenceExists(bo, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExists(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    public boolean validateReferenceExists(BusinessObject bo, String referenceName) {

        // attempt to retrieve the specified object from the db
        BusinessObject referenceBo = businessObjectService.getReferenceIfExists(bo, referenceName);

        // if it isn't there, then it doesn't exist, return false
        if (ObjectUtils.isNotNull(referenceBo)) {
            return true;
        }

        // otherwise, it is there, return true
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceIsActive(org.kuali.rice.krad.bo.BusinessObject,
     *      org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    public boolean validateReferenceIsActive(BusinessObject bo, ReferenceDefinition reference) {
        return validateReferenceIsActive(bo, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceIsActive(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, java.lang.String, boolean)
     */
    public boolean validateReferenceIsActive(BusinessObject bo, String referenceName) {

        // attempt to retrieve the specified object from the db
        BusinessObject referenceBo = businessObjectService.getReferenceIfExists(bo, referenceName);
        if (referenceBo == null) {
        	return false;
        }
        if (!(referenceBo instanceof Inactivatable) || ((Inactivatable) referenceBo).isActive()) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExistsAndIsActive(org.kuali.rice.krad.bo.BusinessObject,
     *      org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    public boolean validateReferenceExistsAndIsActive(BusinessObject bo, ReferenceDefinition reference) {
        boolean success = true;
        // intelligently use the fieldname from the reference, or get it out
        // of the dataDictionaryService
        String displayFieldName;
        if (reference.isDisplayFieldNameSet()) {
            displayFieldName = reference.getDisplayFieldName();
        }
        else {
            Class<?> boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : bo.getClass();
            displayFieldName = dataDictionaryService.getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
        }

        if (reference.isCollectionReference()) {
            success = validateCollectionReferenceExistsAndIsActive(bo, reference, displayFieldName, StringUtils.split(reference.getCollection(), "."), null);
        }
        else {
            success = validateReferenceExistsAndIsActive(bo, reference.getAttributeName(),reference.getAttributeToHighlightOnFail(), displayFieldName);
        }
        return success;
    }

    /**
     * @param bo the object to get the collection from
     * @param reference the <code>ReferenceDefinition</code> of the collection to validate
     * @param displayFieldName the name of the field
     * @param intermediateCollections array containing the path to the collection as tokens
     * @param pathToAttribute the rebuilt path to the ReferenceDefinition.attributeToHighlightOnFail which includes the index of
     *        each subcollection
     * @return
     */
    private boolean validateCollectionReferenceExistsAndIsActive(BusinessObject bo, ReferenceDefinition reference, String displayFieldName, String[] intermediateCollections, String pathToAttributeI) {
        boolean success = true;
        Collection<PersistableBusinessObject> referenceCollection;
        String collectionName = intermediateCollections[0];
        // remove current collection from intermediates
        intermediateCollections = (String[]) ArrayUtils.removeElement(intermediateCollections, collectionName);
        try {
            referenceCollection = (Collection) PropertyUtils.getProperty(bo, collectionName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        int pos = 0;
        Iterator<PersistableBusinessObject> iterator = referenceCollection.iterator();
        while (iterator.hasNext()) {
            String pathToAttribute = StringUtils.defaultString(pathToAttributeI) + collectionName + "[" + (pos++) + "].";
            // keep drilling down until we reach the nested collection we want
            if (intermediateCollections.length > 0) {
                success &= validateCollectionReferenceExistsAndIsActive(iterator.next(), reference, displayFieldName, intermediateCollections, pathToAttribute);
            }
            else {
                String attributeToHighlightOnFail = pathToAttribute + reference.getAttributeToHighlightOnFail();
                success &= validateReferenceExistsAndIsActive(iterator.next(), reference.getAttributeName(), attributeToHighlightOnFail, displayFieldName);
            }
        }

        return success;

    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExistsAndIsActive(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, java.lang.String, boolean, boolean, java.lang.String, java.lang.String)
     */

    public boolean validateReferenceExistsAndIsActive(BusinessObject bo, String referenceName, String attributeToHighlightOnFail, String displayFieldName) {
    	
    	// if we're dealing with a nested attribute, we need to resolve down to the BO where the primitive attribute is located
    	// this is primarily to deal with the case of a defaultExistenceCheck that uses an "extension", i.e referenceName
    	// would be extension.attributeName
    	if (ObjectUtils.isNestedAttribute(referenceName)) {
    		String nestedAttributePrefix = ObjectUtils.getNestedAttributePrefix(referenceName);
    		String nestedAttributePrimitive = ObjectUtils.getNestedAttributePrimitive(referenceName);
    		Object nestedObject = ObjectUtils.getPropertyValue(bo, nestedAttributePrefix);
    		if (!(nestedObject instanceof BusinessObject)) {
    			throw new ObjectNotABusinessObjectRuntimeException("Attribute requested (" + nestedAttributePrefix + ") is of class: " + "'" + nestedObject.getClass().getName() + "' and is not a " + "descendent of BusinessObject.");
    		}
    		return validateReferenceExistsAndIsActive((BusinessObject)nestedObject, nestedAttributePrimitive, attributeToHighlightOnFail, displayFieldName);
    	}
    	
        boolean success = true;
        boolean exists;
        boolean active;

        boolean fkFieldsPopulated = true;
        // need to check for DD relationship FKs
        List<String> fkFields = getDataDictionaryService().getRelationshipSourceAttributes(bo.getClass().getName(), referenceName);
        if (fkFields != null) {
            for (String fkFieldName : fkFields) {
                Object fkFieldValue = null;
                try {
                    fkFieldValue = PropertyUtils.getProperty(bo, fkFieldName);
                }
                // if we cant retrieve the field value, then
                // it doesnt have a value
                catch (IllegalAccessException e) {
                    fkFieldsPopulated = false;
                }
                catch (InvocationTargetException e) {
                    fkFieldsPopulated = false;
                }
                catch (NoSuchMethodException e) {
                    fkFieldsPopulated = false;
                }

                // test the value
                if (fkFieldValue == null) {
                    fkFieldsPopulated = false;
                }
                else if (String.class.isAssignableFrom(fkFieldValue.getClass())) {
                    if (StringUtils.isBlank((String) fkFieldValue)) {
                        fkFieldsPopulated = false;
                    }
                }
            }
        }
        else if ( bo instanceof PersistableBusinessObject ) { // if no DD relationship exists, check the persistence service
            fkFieldsPopulated = persistenceService.allForeignKeyValuesPopulatedForReference((PersistableBusinessObject)bo, referenceName);
        }

        // only bother if all the fk fields have values
        if (fkFieldsPopulated) {

            // do the existence test
            exists = validateReferenceExists(bo, referenceName);
            if (exists) {

                // do the active test, if appropriate
                if (!(bo instanceof Inactivatable) || ((Inactivatable) bo).isActive()) {
                    active = validateReferenceIsActive(bo, referenceName);
                    if (!active) {
                        GlobalVariables.getMessageMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_INACTIVE, displayFieldName);
                        success &= false;
                    }
                }
            }
            else {
                GlobalVariables.getMessageMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_EXISTENCE, displayFieldName);
                success &= false;
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecks(org.kuali.rice.krad.bo.BusinessObject)
     */
    public boolean validateDefaultExistenceChecks(BusinessObject bo) {

        boolean success = true;

        // get a collection of all the referenceDefinitions setup for this object
        Collection references = maintenanceDocumentDictionaryService.getDefaultExistenceChecks(bo.getClass());

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext();) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(bo, reference);
        }
        return success;
    }
    
    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.krad.bo.BusinessObject, org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
	public boolean validateDefaultExistenceChecksForNewCollectionItem(BusinessObject bo, BusinessObject newCollectionItem,
			String collectionName) {
        boolean success = true;
        
        if (StringUtils.isNotBlank(collectionName)) {
	        // get a collection of all the referenceDefinitions setup for this object
	        Collection references = maintenanceDocumentDictionaryService.getDefaultExistenceChecks(bo.getClass());
	
	        // walk through the references, doing the tests on each
	        for (Iterator iter = references.iterator(); iter.hasNext();) {
	            ReferenceDefinition reference = (ReferenceDefinition) iter.next();
				if(collectionName != null && collectionName.equals(reference.getCollection())){
					String displayFieldName;
		            if (reference.isDisplayFieldNameSet()) {
		                displayFieldName = reference.getDisplayFieldName();
		            }
		            else {
		                Class boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : bo.getClass();
		                displayFieldName = dataDictionaryService.getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
		            }
		
		            success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(), reference.getAttributeToHighlightOnFail(), displayFieldName);
				}
	        }
        }
        
        return success;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForTransDoc(org.kuali.rice.krad.document.TransactionalDocument)
	 */
	public boolean validateDefaultExistenceChecksForTransDoc(TransactionalDocument document) {
        boolean success = true;

        // get a collection of all the referenceDefinitions setup for this object
        Collection references = transactionalDocumentDictionaryService.getDefaultExistenceChecks(document);

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext();) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(document, reference);
        }
        return success;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.krad.document.TransactionalDocument, org.kuali.rice.krad.bo.PersistableBusinessObject)
	 */
	public boolean validateDefaultExistenceChecksForNewCollectionItem(
			TransactionalDocument document,
			BusinessObject newCollectionItem, String collectionName) {
        boolean success = true;
        if (StringUtils.isNotBlank(collectionName)) {
	        // get a collection of all the referenceDefinitions setup for this object
	        Collection references = transactionalDocumentDictionaryService.getDefaultExistenceChecks(document);
	
	        // walk through the references, doing the tests on each
	        for (Iterator iter = references.iterator(); iter.hasNext();) {
	            ReferenceDefinition reference = (ReferenceDefinition) iter.next();
				if(collectionName != null && collectionName.equals(reference.getCollection())){
					String displayFieldName;
		            if (reference.isDisplayFieldNameSet()) {
		                displayFieldName = reference.getDisplayFieldName();
		            }
		            else {
		                Class boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : document.getClass();
		                displayFieldName = dataDictionaryService.getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
		            }
		
		            success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(), reference.getAttributeToHighlightOnFail(), displayFieldName);
				}
	        }
        }
        return success;
	}

     
    /*
     * 1.1 validation methods 
     */
	
	/*
	 * This is the top-level validation method for all attribute value readers
	 */
	public DictionaryValidationResult validate(AttributeValueReader valueReader, boolean doOptionalProcessing) {    	
    	
    	DictionaryValidationResult result = new DictionaryValidationResult();
    	
    	if (valueReader.getAttributeName() == null){ 
    		validateObject(result, valueReader, doOptionalProcessing);
    	} else { 
	    	validateAttribute(result, valueReader, doOptionalProcessing);
    	}
    	
    	if (result.getNumberOfErrors() > 0) {
	    	for (Iterator<ConstraintValidationResult> iterator = result.iterator() ; iterator.hasNext() ;) {
	    		ConstraintValidationResult constraintValidationResult = iterator.next();
	    		if (constraintValidationResult.getStatus().getLevel() >= ErrorLevel.WARN.getLevel()) 
	    			setFieldError(constraintValidationResult.getEntryName(), constraintValidationResult.getAttributeName(), constraintValidationResult.getErrorKey(), constraintValidationResult.getErrorParameters());   		
	    	}
    	}
    	
    	return result;
    }
		
    private void processElementConstraints(DictionaryValidationResult result, Object value, Constrainable definition, AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
    	processConstraints(result, elementConstraintProcessors, value, definition, attributeValueReader, doOptionalProcessing);
    }
    
    private void processCollectionConstraints(DictionaryValidationResult result, Collection<?> collection, Constrainable definition, AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
    	processConstraints(result, collectionConstraintProcessors, collection, definition, attributeValueReader, doOptionalProcessing);
    }
    
    @SuppressWarnings("unchecked")
	private void processConstraints(DictionaryValidationResult result, List<? extends ConstraintProcessor> constraintProcessors, Object value, Constrainable definition, AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
		//TODO: Implement custom validators
    	
    	if (constraintProcessors != null) {
			Constrainable selectedDefinition = definition;
			AttributeValueReader selectedAttributeValueReader = attributeValueReader;
			
			
			// First - take the constrainable definition and get its constraints
			
			Queue<Constraint> constraintQueue = new LinkedList<Constraint>();
			
			// Using a for loop to iterate through constraint processors because ordering is important 
			for (ConstraintProcessor<Object, Constraint> processor : constraintProcessors) {
				
				// Let the calling method opt out of any optional processing
				if (!doOptionalProcessing && processor.isOptional()) {
					result.addSkipped(attributeValueReader, processor.getName());
					continue;
				}
				
				Class<? extends Constraint> constraintType = processor.getConstraintType();
				
				// Add all of the constraints for this constraint type for all providers to the queue
				for (ConstraintProvider constraintProvider : constraintProviders) {	
					if (constraintProvider.isSupported(selectedDefinition)) {
						Collection<Constraint> constraintList = constraintProvider.getConstraints(selectedDefinition, constraintType);
						if (constraintList != null)
							constraintQueue.addAll(constraintList);
					}
				}
				
				// If there are no constraints provided for this definition, then just skip it
				if (constraintQueue.isEmpty()) {
					result.addSkipped(attributeValueReader, processor.getName());
					continue;
				}
				
				Collection<Constraint> additionalConstraints = new LinkedList<Constraint>();
				
				// This loop is functionally identical to a for loop, but it has the advantage of letting us keep the queue around
				// and populate it with any new constraints contributed by the processor
				while (!constraintQueue.isEmpty()) {
					
					Constraint constraint = constraintQueue.poll();
					
					// If this constraint is not one that this process handles, then skip and add to the queue for the next processor;
					// obviously this would be redundant (we're only looking at constraints that this processor can process) except that
					// the previous processor might have stuck a new constraint (or constraints) on the queue
					if (!constraintType.isInstance(constraint)) {
						result.addSkipped(attributeValueReader, processor.getName());
						additionalConstraints.add(constraint);
						continue;
					}
					
					ProcessorResult processorResult = processor.process(result, value, constraint, selectedAttributeValueReader);
					
					Collection<Constraint> processorResultContraints = processorResult.getConstraints();
					if (processorResultContraints != null && processorResultContraints.size() > 0)
						additionalConstraints.addAll(processorResultContraints);
					
					// Change the selected definition to whatever was returned from the processor
					if (processorResult.isDefinitionProvided())
						selectedDefinition = processorResult.getDefinition();
					// Change the selected attribute value reader to whatever was returned from the processor
					if (processorResult.isAttributeValueReaderProvided())
						selectedAttributeValueReader = processorResult.getAttributeValueReader();
				}
				
				// After iterating through all the constraints for this processor, add additional constraints for following processors
				constraintQueue.addAll(additionalConstraints);
			}
		}
    }
    	
    private void setFieldError(String entryName, String attributeName, String key, String ... args) {
    	if (getDataDictionaryService() == null)
    		return;
    	
    	String errorLabel = getDataDictionaryService().getAttributeErrorLabel(entryName, attributeName);
    	// FIXME: There's got to be a cleaner way of doing this.
    	List<String> list = new LinkedList<String>();
    	list.add(errorLabel);
    	list.addAll(Arrays.asList(args));
    	String[] array = new String[list.size()];
    	array = list.toArray(array);
    	GlobalVariables.getMessageMap().putError(attributeName, key, array);
    }
	

    private void validateAttribute(DictionaryValidationResult result, AttributeValueReader attributeValueReader, boolean checkIfRequired) throws AttributeValidationException {
		Constrainable definition = attributeValueReader.getDefinition(attributeValueReader.getAttributeName());
        validateAttribute(result, definition, attributeValueReader, checkIfRequired);
    }
    
    private void validateAttribute(DictionaryValidationResult result, Constrainable definition, AttributeValueReader attributeValueReader,  boolean checkIfRequired) throws AttributeValidationException {
    	
    	if (definition == null)
    		throw new AttributeValidationException("Unable to validate constraints for attribute \"" + attributeValueReader.getAttributeName() + "\" on entry \"" + attributeValueReader.getEntryName() + "\" because no attribute definition can be found.");
    	
    	Object value = attributeValueReader.getValue();
    	
    	processElementConstraints(result, value, definition, attributeValueReader, checkIfRequired);
    }
    
    private void validateObject(DictionaryValidationResult result, AttributeValueReader attributeValueReader, boolean doOptionalProcessing) throws AttributeValidationException {
    	
    	// If the entry itself is constrainable then the attribute value reader will return it here and we'll need to check if it has any constraints
    	//FIXME: WJG - Do we want to make entry be constrainable?
    	Constrainable objectEntry = attributeValueReader.getEntry();
    	processElementConstraints(result, attributeValueReader.getObject(), objectEntry, attributeValueReader, doOptionalProcessing);

    	List<Constrainable> definitions = attributeValueReader.getDefinitions();

    	// Exit if the attribute value reader has no child definitions
    	if (null == definitions) 
    		return;
    	
    	//Process all attribute definitions
    	for (Constrainable definition : definitions) {   		
    		String attributeName = definition.getName();
			attributeValueReader.setAttributeName(attributeName);    			
			Object value = attributeValueReader.getValue(attributeName);
			
    		processElementConstraints(result, value, definition, attributeValueReader, doOptionalProcessing);
    	}
    	
    	//Process any constraints that may be defined on complex attributes
    	if (objectEntry instanceof DataDictionaryEntryBase){
    		List<ComplexAttributeDefinition> complexAttrDefinitions = ((DataDictionaryEntryBase)objectEntry).getComplexAttributes();

    		if (complexAttrDefinitions != null){
	        	for (ComplexAttributeDefinition complexAttrDefinition:complexAttrDefinitions){    		    		
	       			DataDictionaryEntry childEntry = complexAttrDefinition.getDataObjectEntry(); 
	       		
	        		String attributeName = complexAttrDefinition.getName();
	        		
	        		attributeValueReader.setAttributeName(attributeName);
	    			Object value = attributeValueReader.getValue();    		
	    			if (value != null){
	    				AttributeValueReader nestedAttributeValueReader = 
	    	    			new DictionaryObjectAttributeValueReader(value, childEntry.getFullClassName(), childEntry, attributeValueReader.getPath());
	    	    		validateObject(result, nestedAttributeValueReader, doOptionalProcessing);    		
	    			}
	
	    			processElementConstraints(result, value, complexAttrDefinition, attributeValueReader, doOptionalProcessing);	
	        	}
    		}
    	}

    	
    	//FIXME: I think we may want to use a new CollectionConstrainable interface instead to obtain from 
    	//DictionaryObjectAttributeValueReader
    	DataObjectEntry entry = (DataObjectEntry)attributeValueReader.getEntry();
    	if (entry != null) {
            for (CollectionDefinition collectionDefinition : entry.getCollections()){
                //TODO: Do we need to be able to handle simple collections (ie. String, etc)

                String childEntryName = collectionDefinition.getDataObjectClass();
                String attributeName = collectionDefinition.getName();
                attributeValueReader.setAttributeName(attributeName);
                Collection<?> collectionObject = attributeValueReader.getValue();
                DataDictionaryEntry childEntry = childEntryName != null ? getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(childEntryName) : null;
                if (collectionObject != null){
                    for (Object value:collectionObject){
                        //FIXME: It's inefficient to be creating new attribute reader for each item in collection
                        AttributeValueReader nestedAttributeValueReader =
                            new DictionaryObjectAttributeValueReader(value, childEntryName, childEntry, attributeValueReader.getPath());
                        validateObject(result, nestedAttributeValueReader, doOptionalProcessing);
                    }
                }

                processCollectionConstraints(result, collectionObject, collectionDefinition, attributeValueReader, doOptionalProcessing);
            }
        }
    }

    /**
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the maintenanceDocumentDictionaryService attribute value.
     * 
     * @param maintenanceDocumentDictionaryService The maintenanceDocumentDictionaryService to set.
     */
    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }


    /**
	 * @param transactionalDocumentDictionaryService the transactionalDocumentDictionaryService to set
	 */
	public void setTransactionalDocumentDictionaryService(
			TransactionalDocumentDictionaryService transactionalDocumentDictionaryService) {
		this.transactionalDocumentDictionaryService = transactionalDocumentDictionaryService;
	}


	/**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    private WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
    	if (workflowAttributePropertyResolutionService == null) {
    		workflowAttributePropertyResolutionService = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
    	}
    	return workflowAttributePropertyResolutionService;
    }

	/**
	 * @return the collectionConstraintProcessors
	 */
	@SuppressWarnings("unchecked")
	public List<CollectionConstraintProcessor> getCollectionConstraintProcessors() {
		return this.collectionConstraintProcessors;
	}

	/**
	 * @param collectionConstraintProcessors the collectionConstraintProcessors to set
	 */
	@SuppressWarnings("unchecked")
	public void setCollectionConstraintProcessors(
			List<CollectionConstraintProcessor> collectionConstraintProcessors) {
		this.collectionConstraintProcessors = collectionConstraintProcessors;
	}

	/**
	 * @return the constraintProviders
	 */
	@SuppressWarnings("unchecked")
	public List<ConstraintProvider> getConstraintProviders() {
		return this.constraintProviders;
	}

	/**
	 * @param constraintProviders the constraintProviders to set
	 */
	@SuppressWarnings("unchecked")
	public void setConstraintProviders(List<ConstraintProvider> constraintProviders) {
		this.constraintProviders = constraintProviders;
	}

	/**
	 * @return the elementConstraintProcessors
	 */
	@SuppressWarnings("unchecked")
	public List<ConstraintProcessor> getElementConstraintProcessors() {
		return this.elementConstraintProcessors;
	}

	/**
	 * @param elementConstraintProcessors the elementConstraintProcessors to set
	 */
	@SuppressWarnings("unchecked")
	public void setElementConstraintProcessors(
			List<ConstraintProcessor> elementConstraintProcessors) {
		this.elementConstraintProcessors = elementConstraintProcessors;
	}



}
