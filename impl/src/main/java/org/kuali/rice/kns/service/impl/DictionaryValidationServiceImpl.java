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
package org.kuali.rice.kns.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.ApcRuleDefinition;
import org.kuali.rice.kns.datadictionary.ReferenceDefinition;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypeUtils;

/**
 * Validates Documents, Business Objects, and Attributes against the data dictionary. Including min, max lengths, and validating
 * expressions. This is the default, Kuali delivered implementation.
 */
//@Transactional
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
    private KualiConfigurationService configService;

    private PersistenceStructureService persistenceStructureService;
    
    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDocument(org.kuali.rice.kns.document.Document)
     */
    public void validateDocument(Document document) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        // validate primitive values
        validatePrimitivesFromDescriptors(documentEntryName, document, PropertyUtils.getPropertyDescriptors(document.getClass()), "", true);
    }


    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDocumentAttribute(org.kuali.rice.kns.document.Document,
     *      java.lang.String,java.lang.String)
     */
    public void validateDocumentAttribute(Document document, String attributeName, String errorPrefix) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

        try {
            PropertyDescriptor attributeDescriptor = PropertyUtils.getPropertyDescriptor(document, attributeName);
            validatePrimitiveFromDescriptor(documentEntryName, document, attributeDescriptor, errorPrefix, true);
        }
        catch (NoSuchMethodException e) {
            throw new InfrastructureException("unable to find propertyDescriptor for property '" + attributeName + "'", e);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to access propertyDescriptor for property '" + attributeName + "'", e);
        }
        catch (InvocationTargetException e) {
            throw new InfrastructureException("unable to invoke methods for property '" + attributeName + "'", e);
        }
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDocumentRecursively
     */
    public void validateDocumentRecursively(Document document, int depth) {
        // validate primitives of document
        validateDocument(document);

        // call method to recursively find business objects and validate
        validateBusinessObjectsFromDescriptors(document, PropertyUtils.getPropertyDescriptors(document.getClass()), depth);
    }

    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth, boolean validateRequired) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        // validate primitive values of the document
        validatePrimitivesFromDescriptors(documentEntryName, document, PropertyUtils.getPropertyDescriptors(document.getClass()), "", validateRequired);
        
        if (maxDepth > 0) {
            validateUpdatabableReferencesRecursively(document, maxDepth - 1, validateRequired, false);
        }
    }
    
    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth, boolean validateRequired, boolean chompLastLetterSFromCollectionName) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        // validate primitive values of the document
        validatePrimitivesFromDescriptors(documentEntryName, document, PropertyUtils.getPropertyDescriptors(document.getClass()), "", validateRequired);
        
        if (maxDepth > 0) {
            validateUpdatabableReferencesRecursively(document, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName);
        }
    }
    
    private void validateUpdatabableReferencesRecursively(BusinessObject businessObject, int maxDepth, boolean validateRequired, boolean chompLastLetterSFromCollectionName) {
    if (ObjectUtils.isNull(businessObject)) {
	    return;
	}
	Map<String, Class> references = persistenceStructureService.listReferenceObjectFields(businessObject.getClass());
	for (String referenceName : references.keySet()) {
	    if (persistenceStructureService.isReferenceUpdatable(businessObject.getClass(), referenceName)) {
	        Object referenceObj = ObjectUtils.getPropertyValue(businessObject, referenceName);

	        if (ObjectUtils.isNull(referenceObj) || !(referenceObj instanceof PersistableBusinessObject)) {
	            continue;
	        }

	        BusinessObject referenceBusinessObject = (BusinessObject) referenceObj;
	        GlobalVariables.getErrorMap().addToErrorPath(referenceName);
	        validateBusinessObject(referenceBusinessObject, validateRequired);
	        if (maxDepth > 0) {
	            validateUpdatabableReferencesRecursively(referenceBusinessObject, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName);
	        }
	        GlobalVariables.getErrorMap().removeFromErrorPath(referenceName);

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
		    LOG.error("The reference named " + collectionName + " of BO class " + businessObject.getClass().getName() + " should be of type java.util.List to be validated properly.");
		    continue;
		}
		
		List list = (List) listObj;
		for (int i = 0; i < list.size(); i++) {
		    if (ObjectUtils.isNotNull(list.get(i)) && list.get(i) instanceof PersistableBusinessObject) {
			String errorPathAddition;
			if (chompLastLetterSFromCollectionName) {
			    errorPathAddition = StringUtils.chomp(collectionName, "s") + "[" + Integer.toString(i) + "]";
			}
			else {
			    errorPathAddition = collectionName + "[" + Integer.toString(i) + "]";
			}
			BusinessObject element = (BusinessObject) list.get(i);
			
			GlobalVariables.getErrorMap().addToErrorPath(errorPathAddition);
			validateBusinessObject(element, validateRequired);
			if (maxDepth > 0) {
			    validateUpdatabableReferencesRecursively(element, maxDepth - 1, validateRequired, chompLastLetterSFromCollectionName);
			}
			GlobalVariables.getErrorMap().removeFromErrorPath(errorPathAddition);
		    }
		}
	    }
	}
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.kns.bo.BusinessObject)
     */
    public void validateBusinessObject(BusinessObject businessObject) {
        validateBusinessObject(businessObject, true);
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.kns.bo.BusinessObject,boolean)
     */
    public void validateBusinessObject(BusinessObject businessObject, boolean validateRequired) {
        if (ObjectUtils.isNull(businessObject)) {
            return;
        }

        // validate the primitive attributes of the bo
        validatePrimitivesFromDescriptors(businessObject.getClass().getName(), businessObject, PropertyUtils.getPropertyDescriptors(businessObject.getClass()), "", validateRequired);
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.kns.bo.BusinessObject)
     */
    public boolean isBusinessObjectValid(BusinessObject businessObject) {
        return isBusinessObjectValid(businessObject, null);
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.kns.bo.BusinessObject, String)
     */
    public boolean isBusinessObjectValid(BusinessObject businessObject, String prefix) {
        boolean retval = false;
        final ErrorMap errorMap = GlobalVariables.getErrorMap();
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
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateAttributeFormat
     */
    public void validateAttributeFormat(String objectClassName, String attributeName, String attributeValue, String errorKey) {
        String longAttributeLabel = getDataDictionaryService().getAttributeLabel(objectClassName, attributeName);
        String shortAttributeLabel = getDataDictionaryService().getAttributeShortLabel(objectClassName, attributeName);
        String errorLabel = longAttributeLabel + " (" + shortAttributeLabel + ")";

        LOG.debug("(bo, attributeName, attributeValue) = (" + objectClassName + "," + attributeName + "," + attributeValue + ")");


        if (StringUtils.isNotBlank(attributeValue)) {
            Integer maxLength = getDataDictionaryService().getAttributeMaxLength(objectClassName, attributeName);
            if ((maxLength != null) && (maxLength.intValue() < attributeValue.length())) {
                GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_MAX_LENGTH, new String[] { errorLabel, maxLength.toString() });
                return;
            }
            Pattern validationExpression = getDataDictionaryService().getAttributeValidatingExpression(objectClassName, attributeName);
            if (validationExpression != null && !validationExpression.pattern().equals(".*")) {
                LOG.debug("(bo, attributeName, validationExpression) = (" + objectClassName + "," + attributeName + "," + validationExpression + ")");

                if (!validationExpression.matcher(attributeValue).matches()) {
                    boolean isError=true;
                    // Calling formatter class
                    Class<?> formatterClass=getDataDictionaryService().getAttributeFormatter(
                            objectClassName, attributeName);
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
                            LOG.debug(e.getMessage(), e);
                        }
                    }
                    if (isError) {
                        GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_INVALID_FORMAT, new String[] { errorLabel, attributeValue });
                    }
                    return;
                }
            }
            BigDecimal exclusiveMin = getDataDictionaryService().getAttributeExclusiveMin(objectClassName, attributeName);
            if (exclusiveMin != null) {
                try {
                    if (exclusiveMin.compareTo(new BigDecimal(attributeValue)) >= 0) {
                        GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_EXCLUSIVE_MIN,
                        // todo: Formatter for currency?
                                new String[] { errorLabel, exclusiveMin.toString() });
                        return;
                    }
                }
                catch (NumberFormatException e) {
                    // quash; this indicates that the DD contained a min for a non-numeric attribute
                }
            }
            BigDecimal inclusiveMax = getDataDictionaryService().getAttributeInclusiveMax(objectClassName, attributeName);
            if (inclusiveMax != null) {
                try {
                    if (inclusiveMax.compareTo(new BigDecimal(attributeValue)) < 0) {
                        GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_INCLUSIVE_MAX,
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

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateAttributeRequired
     */
    public void validateAttributeRequired(String objectClassName, String attributeName, Object attributeValue, Boolean forMaintenance, String errorKey) {
        // check if field is a required field for the business object
        if (attributeValue == null || (attributeValue instanceof String && StringUtils.isBlank((String) attributeValue))) {
            Boolean required = getDataDictionaryService().isAttributeRequired(objectClassName, attributeName);
            ControlDefinition controlDef = getDataDictionaryService().getAttributeControlDefinition(objectClassName, attributeName);

            if (required != null && required.booleanValue() && !(controlDef != null && controlDef.isHidden())) {

                // get label of attribute for message
                String errorLabel = getDataDictionaryService().getAttributeErrorLabel(objectClassName, attributeName);
                GlobalVariables.getErrorMap().putError(errorKey, RiceKeyConstants.ERROR_REQUIRED, errorLabel);
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
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            // validate the properties that are descended from BusinessObject
            if (propertyDescriptor.getPropertyType() != null && PersistableBusinessObject.class.isAssignableFrom(propertyDescriptor.getPropertyType()) && ObjectUtils.getPropertyValue(object, propertyDescriptor.getName()) != null) {
                BusinessObject bo = (BusinessObject) ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
                if (depth == 0) {
                    GlobalVariables.getErrorMap().addToErrorPath(propertyDescriptor.getName());
                    validateBusinessObject(bo);
                    GlobalVariables.getErrorMap().removeFromErrorPath(propertyDescriptor.getName());
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
                            GlobalVariables.getErrorMap().addToErrorPath(StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" + (new Integer(j)).toString() + "]");
                            validateBusinessObject((BusinessObject) propertyList.get(j));
                            GlobalVariables.getErrorMap().removeFromErrorPath(StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" + (new Integer(j)).toString() + "]");
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
     * iterates through property descriptors looking for primitives types, calls validate format and required check
     * 
     * @param entryName
     * @param object
     * @param propertyDescriptors
     * @param errorPrefix
     */
    private void validatePrimitivesFromDescriptors(String entryName, Object object, PropertyDescriptor[] propertyDescriptors, String errorPrefix, boolean validateRequired) {
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            validatePrimitiveFromDescriptor(entryName, object, propertyDescriptor, errorPrefix, validateRequired);
        }
    }

    /**
     * calls validate format and required check for the given propertyDescriptor
     * 
     * @param entryName
     * @param object
     * @param propertyDescriptor
     * @param errorPrefix
     */
    private void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor, String errorPrefix, boolean validateRequired) {
        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor && getDataDictionaryService().isAttributeDefined(entryName, propertyDescriptor.getName())) {
            Object value = ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
            Class propertyType = propertyDescriptor.getPropertyType();

            if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) || TypeUtils.isTemporalClass(propertyType)) {

                // check value format against dictionary
                if (value != null && StringUtils.isNotBlank(value.toString())) {
                    if (!TypeUtils.isTemporalClass(propertyType)) {
                        validateAttributeFormat(entryName, propertyDescriptor.getName(), value.toString(), errorPrefix + propertyDescriptor.getName());
                    }
                }
                else if (validateRequired) {
                    validateAttributeRequired(entryName, propertyDescriptor.getName(), value, Boolean.FALSE, errorPrefix + propertyDescriptor.getName());
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceExists(org.kuali.rice.kns.bo.BusinessObject,
     *      org.kuali.rice.kns.datadictionary.ReferenceDefinition)
     */
    public boolean validateReferenceExists(BusinessObject bo, ReferenceDefinition reference) {
        return validateReferenceExists(bo, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceExists(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String)
     */
    public boolean validateReferenceExists(BusinessObject bo, String referenceName) {

        // attempt to retrieve the specified object from the db
        BusinessObject referenceBo = businessObjectService.getReferenceIfExists(bo, referenceName);

        // if it isnt there, then it doesnt exist, return false
        if (ObjectUtils.isNotNull(referenceBo)) {
            return true;
        }

        // otherwise, it is there, return true
        else {
            return false;
        }

    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceIsActive(org.kuali.rice.kns.bo.BusinessObject,
     *      org.kuali.rice.kns.datadictionary.ReferenceDefinition)
     */
    public boolean validateReferenceIsActive(BusinessObject bo, ReferenceDefinition reference) {
        return validateReferenceIsActive(bo, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceIsActive(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, java.lang.String, boolean)
     */
    public boolean validateReferenceIsActive(BusinessObject bo, String referenceName) {

        // attempt to retrieve the specified object from the db
        BusinessObject referenceBo = businessObjectService.getReferenceIfExists(bo, referenceName);
        if (referenceBo == null) {
        	return false;
        }
        if (!(referenceBo instanceof Inactivateable) || ((Inactivateable) referenceBo).isActive()) {
            return true;
        }else{
        	return false;
        }
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceExistsAndIsActive(org.kuali.rice.kns.bo.BusinessObject,
     *      org.kuali.rice.kns.datadictionary.ReferenceDefinition)
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
            Class boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : bo.getClass();
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
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateReferenceExistsAndIsActive(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, java.lang.String, boolean, boolean, java.lang.String, java.lang.String)
     */

    public boolean validateReferenceExistsAndIsActive(BusinessObject bo, String referenceName, String attributeToHighlightOnFail, String displayFieldName) {
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
                if (!(bo instanceof Inactivateable) || ((Inactivateable) bo).isActive()) {
                    active = validateReferenceIsActive(bo, referenceName);
                    if (!active) {
                        GlobalVariables.getErrorMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_INACTIVE, displayFieldName);
                        success &= false;
                    }
                }
            }
            else {
                GlobalVariables.getErrorMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_EXISTENCE, displayFieldName);
                success &= false;
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDefaultExistenceChecks(org.kuali.rice.kns.bo.BusinessObject)
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
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDefaultExistenceChecksForTransDoc(org.kuali.rice.kns.document.TransactionalDocument)
	 */
	public boolean validateDefaultExistenceChecksForTransDoc(TransactionalDocument document) {
        boolean success = true;

        // get a collection of all the referenceDefinitions setup for this object
        Collection references = transactionalDocumentDictionaryService.getDefaultExistenceChecks(document);

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext();) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(document.getDocumentBusinessObject(), reference);
        }
        return success;
	}


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.kns.document.TransactionalDocument, org.kuali.rice.kns.bo.PersistableBusinessObject)
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
		                Class boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : document.getDocumentBusinessObject().getClass();
		                displayFieldName = dataDictionaryService.getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
		            }
		
		            success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(), reference.getAttributeToHighlightOnFail(), displayFieldName);
				}
	        }
        }
        return success;
	}

	/**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateApcRule(org.kuali.rice.kns.bo.BusinessObject,
     *      org.kuali.rice.kns.datadictionary.ApcRuleDefinition)
     */
    public boolean validateApcRule(BusinessObject bo, ApcRuleDefinition apcRule) {
        boolean success = true;
        Object attrValue;
        try {
            attrValue = PropertyUtils.getSimpleProperty(bo, apcRule.getAttributeName());
            // if the value we get back is null that means that the user didn't fill anything in
            // so the rule shouldn't fail because of this
            if (StringUtils.isEmpty((String) attrValue)) {
                return success;
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        String attrValueStr = attrValue.toString();
        if (!configService.evaluateConstrainedValue(apcRule.getParameterNamespace(), apcRule.getParameterDetailType(), apcRule.getParameterName(),attrValueStr)) {
            success &= false;
            GlobalVariables.getErrorMap().putError(apcRule.getAttributeName(), apcRule.getErrorMessage());
        }

        return success;
    }

    /**
     * @see org.kuali.rice.kns.service.DictionaryValidationService#validateApcRules(org.kuali.rice.kns.bo.BusinessObject)
     */
    public boolean validateApcRules(BusinessObject bo) {
        boolean success = true;

        // get a collection of all the apcRuleDefinitions setup for this object
        Collection rules = maintenanceDocumentDictionaryService.getApplyApcRules(bo.getClass());

        // walk through the rules, doing the tests on each
        for (Iterator iter = rules.iterator(); iter.hasNext();) {
            ApcRuleDefinition rule = (ApcRuleDefinition) iter.next();

            // do the existence and validation testing
            success &= validateApcRule(bo, rule);
        }
        return success;
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

    /**
     * Sets the configService attribute value
     * 
     * @param configService the configService to set
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }


    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

}