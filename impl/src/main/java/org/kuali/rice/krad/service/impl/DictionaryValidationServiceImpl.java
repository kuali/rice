/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.ComplexAttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
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
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowAttributePropertyResolutionService;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Validates Documents, Business Objects, and Attributes against the data dictionary. Including min, max lengths, and
 * validating expressions. This is the default, Kuali delivered implementation.
 *
 * KULRICE - 3355 Modified to prevent infinite looping (to maxDepth) scenario when a parent references a child which
 * references a parent
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DictionaryValidationServiceImpl implements DictionaryValidationService {
    private static org.apache.log4j.Logger LOG =
            org.apache.log4j.Logger.getLogger(DictionaryValidationServiceImpl.class);

    /**
     * Constant defines a validation method for an attribute value.
     * <p>Value is "validate"
     */
    public static final String VALIDATE_METHOD = "validate";

    protected DataDictionaryService dataDictionaryService;
    protected BusinessObjectService businessObjectService;
    protected PersistenceService persistenceService;
    protected DocumentDictionaryService documentDictionaryService;
    protected WorkflowAttributePropertyResolutionService workflowAttributePropertyResolutionService;
    protected PersistenceStructureService persistenceStructureService;

    @SuppressWarnings("unchecked")
    private List<CollectionConstraintProcessor> collectionConstraintProcessors;
    @SuppressWarnings("unchecked")
    private List<ConstraintProvider> constraintProviders;
    @SuppressWarnings("unchecked")
    private List<ConstraintProcessor> elementConstraintProcessors;

    /**
     * creates a new IdentitySet.
     *
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
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String,
     *      boolean)
     */
    public DictionaryValidationResult validate(Object object, String entryName, boolean doOptionalProcessing) {
        return validate(object, entryName, (String) null, doOptionalProcessing);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String,
     *      java.lang.String)
     */
    public DictionaryValidationResult validate(Object object, String entryName, String attributeName) {
        return validate(object, entryName, attributeName, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String,
     *      java.lang.String, boolean)
     */
    public DictionaryValidationResult validate(Object object, String entryName, String attributeName,
            boolean doOptionalProcessing) {
        DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        attributeValueReader.setAttributeName(attributeName);
        return validate(attributeValueReader, doOptionalProcessing);
    }

    public DictionaryValidationResult validate(Object object, String entryName, DataDictionaryEntry entry,
            boolean doOptionalProcessing) {
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        return validate(attributeValueReader, doOptionalProcessing);
    }

    public void validate(String entryName, String attributeName, Object attributeValue) {
        validate(entryName, attributeName, attributeValue, true);
    }

    public void validate(String entryName, String attributeName, Object attributeValue, boolean doOptionalProcessing) {
        AttributeDefinition attributeDefinition =
                getDataDictionaryService().getAttributeDefinition(entryName, attributeName);

        if (attributeDefinition == null) {
            // FIXME: JLR - this is what the code was doing effectively already, but seems weird not to throw an exception here if you try to validate
            // something that doesn't have an attribute definition
            return;
        }

        SingleAttributeValueReader attributeValueReader =
                new SingleAttributeValueReader(attributeValue, entryName, attributeName, attributeDefinition);
        validate(attributeValueReader, doOptionalProcessing);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
	public void validateDocument(Document document) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        validate(document, documentEntryName);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentAttribute(org.kuali.rice.krad.document.Document,
     *      java.lang.String, java.lang.String)
     */
    @Override
	public void validateDocumentAttribute(Document document, String attributeName, String errorPrefix) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        validate(document, documentEntryName, attributeName, true);
    }

    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth,
            boolean validateRequired) {
        validateDocumentAndUpdatableReferencesRecursively(document, maxDepth, validateRequired, false);
    }
    
    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth, 
            boolean validateRequired, boolean chompLastLetterSFromCollectionName) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        validate(document, documentEntryName);

        if (maxDepth > 0) {
            validateUpdatabableReferencesRecursively(document, maxDepth - 1, validateRequired,
                    chompLastLetterSFromCollectionName, newIdentitySet());
        }
    }

    private void validateUpdatabableReferencesRecursively(BusinessObject businessObject, int maxDepth,
            boolean validateRequired, boolean chompLastLetterSFromCollectionName, Set<BusinessObject> processedBOs) {
        // if null or already processed, return
        if (ObjectUtils.isNull(businessObject) || processedBOs.contains(businessObject)) {
            return;
        }
        processedBOs.add(businessObject);  // add bo to list to prevent excessive looping
        Map<String, Class> references =
                persistenceStructureService.listReferenceObjectFields(businessObject.getClass());
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
                    validateUpdatabableReferencesRecursively(referenceBusinessObject, maxDepth - 1, validateRequired,
                            chompLastLetterSFromCollectionName, processedBOs);
                }
                GlobalVariables.getMessageMap().removeFromErrorPath(referenceName);
            }
        }
        Map<String, Class> collections =
                persistenceStructureService.listCollectionObjectTypes(businessObject.getClass());
        for (String collectionName : collections.keySet()) {
            if (persistenceStructureService.isCollectionUpdatable(businessObject.getClass(), collectionName)) {
                Object listObj = ObjectUtils.getPropertyValue(businessObject, collectionName);

                if (ObjectUtils.isNull(listObj)) {
                    continue;
                }

                if (!(listObj instanceof List)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("The reference named " + collectionName + " of BO class " +
                                businessObject.getClass().getName() +
                                " should be of type java.util.List to be validated properly.");
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
                            errorPathAddition =
                                    StringUtils.chomp(collectionName, "s") + "[" + Integer.toString(i) + "]";
                        } else {
                            errorPathAddition = collectionName + "[" + Integer.toString(i) + "]";
                        }

                        GlobalVariables.getMessageMap().addToErrorPath(errorPathAddition);
                        validateBusinessObject(element, validateRequired);
                        if (maxDepth > 0) {
                            validateUpdatabableReferencesRecursively(element, maxDepth - 1, validateRequired,
                                    chompLastLetterSFromCollectionName, processedBOs);
                        }
                        GlobalVariables.getMessageMap().removeFromErrorPath(errorPathAddition);
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.krad.bo.BusinessObject)
     */
    public boolean isBusinessObjectValid(BusinessObject businessObject) {
        return isBusinessObjectValid(businessObject, null);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.krad.bo.BusinessObject,
     *      String)
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
        validateBusinessObjectsFromDescriptors(businessObject,
                PropertyUtils.getPropertyDescriptors(businessObject.getClass()), depth);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void validateBusinessObject(BusinessObject businessObject) {
        validateBusinessObject(businessObject, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject,
     *      boolean)
     */
    @Override
    public void validateBusinessObject(BusinessObject businessObject, boolean validateRequired) {
        if (ObjectUtils.isNull(businessObject)) {
            return;
        }

        validate(businessObject, businessObject.getClass().getName());
    }

    /**
     * iterates through the property discriptors looking for business objects or lists of business objects. calls
     * validate method
     * for each bo found
     *
     * @param object
     * @param propertyDescriptors
     */
    protected void validateBusinessObjectsFromDescriptors(Object object, PropertyDescriptor[] propertyDescriptors,
            int depth) {
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // validate the properties that are descended from BusinessObject
            if (propertyDescriptor.getPropertyType() != null &&
                    PersistableBusinessObject.class.isAssignableFrom(propertyDescriptor.getPropertyType()) &&
                    ObjectUtils.getPropertyValue(object, propertyDescriptor.getName()) != null) {
                BusinessObject bo = (BusinessObject) ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
                if (depth == 0) {
                    GlobalVariables.getMessageMap().addToErrorPath(propertyDescriptor.getName());
                    validateBusinessObject(bo);
                    GlobalVariables.getMessageMap().removeFromErrorPath(propertyDescriptor.getName());
                } else {
                    validateBusinessObjectsRecursively(bo, depth - 1);
                }
            }

            /*
             * if property is a List, then walk the list and do the validation on each contained object that is a descendent of
             * BusinessObject
             */
            else if (propertyDescriptor.getPropertyType() != null &&
                    (List.class).isAssignableFrom(propertyDescriptor.getPropertyType()) &&
                    ObjectUtils.getPropertyValue(object, propertyDescriptor.getName()) != null) {
                List propertyList = (List) ObjectUtils.getPropertyValue(object, propertyDescriptor.getName());
                for (int j = 0; j < propertyList.size(); j++) {
                    if (propertyList.get(j) != null && propertyList.get(j) instanceof PersistableBusinessObject) {
                        if (depth == 0) {
                            GlobalVariables.getMessageMap().addToErrorPath(
                                    StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" +
                                            (new Integer(j)).toString() + "]");
                            validateBusinessObject((BusinessObject) propertyList.get(j));
                            GlobalVariables.getMessageMap().removeFromErrorPath(
                                    StringUtils.chomp(propertyDescriptor.getName(), "s") + "[" +
                                            (new Integer(j)).toString() + "]");
                        } else {
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
     * @deprecated since 1.1
     */
    @Deprecated
    public void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor,
            String errorPrefix, boolean validateRequired) {

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
        if (!(referenceBo instanceof MutableInactivatable) || ((MutableInactivatable) referenceBo).isActive()) {
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
        } else {
            Class<?> boClass =
                    reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() : bo.getClass();
            displayFieldName =
                    dataDictionaryService.getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
        }

        if (reference.isCollectionReference()) {
            success = validateCollectionReferenceExistsAndIsActive(bo, reference, displayFieldName,
                    StringUtils.split(reference.getCollection(), "."), null);
        } else {
            success = validateReferenceExistsAndIsActive(bo, reference.getAttributeName(),
                    reference.getAttributeToHighlightOnFail(), displayFieldName);
        }
        return success;
    }

    /**
     * @param bo the object to get the collection from
     * @param reference the <code>ReferenceDefinition</code> of the collection to validate
     * @param displayFieldName the name of the field
     * @param intermediateCollections array containing the path to the collection as tokens
     * @param pathToAttribute the rebuilt path to the ReferenceDefinition.attributeToHighlightOnFail which includes the
     * index of
     * each subcollection
     * @return
     */
    private boolean validateCollectionReferenceExistsAndIsActive(BusinessObject bo, ReferenceDefinition reference,
            String displayFieldName, String[] intermediateCollections, String pathToAttributeI) {
        boolean success = true;
        Collection<PersistableBusinessObject> referenceCollection;
        String collectionName = intermediateCollections[0];
        // remove current collection from intermediates
        intermediateCollections = (String[]) ArrayUtils.removeElement(intermediateCollections, collectionName);
        try {
            referenceCollection = (Collection) PropertyUtils.getProperty(bo, collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int pos = 0;
        Iterator<PersistableBusinessObject> iterator = referenceCollection.iterator();
        while (iterator.hasNext()) {
            String pathToAttribute =
                    StringUtils.defaultString(pathToAttributeI) + collectionName + "[" + (pos++) + "].";
            // keep drilling down until we reach the nested collection we want
            if (intermediateCollections.length > 0) {
                success &= validateCollectionReferenceExistsAndIsActive(iterator.next(), reference, displayFieldName,
                        intermediateCollections, pathToAttribute);
            } else {
                String attributeToHighlightOnFail = pathToAttribute + reference.getAttributeToHighlightOnFail();
                success &= validateReferenceExistsAndIsActive(iterator.next(), reference.getAttributeName(),
                        attributeToHighlightOnFail, displayFieldName);
            }
        }

        return success;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExistsAndIsActive(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String, java.lang.String, boolean, boolean, java.lang.String, java.lang.String)
     */

    public boolean validateReferenceExistsAndIsActive(BusinessObject bo, String referenceName,
            String attributeToHighlightOnFail, String displayFieldName) {

        // if we're dealing with a nested attribute, we need to resolve down to the BO where the primitive attribute is located
        // this is primarily to deal with the case of a defaultExistenceCheck that uses an "extension", i.e referenceName
        // would be extension.attributeName
        if (ObjectUtils.isNestedAttribute(referenceName)) {
            String nestedAttributePrefix = ObjectUtils.getNestedAttributePrefix(referenceName);
            String nestedAttributePrimitive = ObjectUtils.getNestedAttributePrimitive(referenceName);
            Object nestedObject = ObjectUtils.getPropertyValue(bo, nestedAttributePrefix);
            if (!(nestedObject instanceof BusinessObject)) {
                throw new ObjectNotABusinessObjectRuntimeException(
                        "Attribute requested (" + nestedAttributePrefix + ") is of class: " + "'" +
                                nestedObject.getClass().getName() + "' and is not a " +
                                "descendent of BusinessObject.");
            }
            return validateReferenceExistsAndIsActive((BusinessObject) nestedObject, nestedAttributePrimitive,
                    attributeToHighlightOnFail, displayFieldName);
        }

        boolean success = true;
        boolean exists;
        boolean active;

        boolean fkFieldsPopulated = true;
        // need to check for DD relationship FKs
        List<String> fkFields =
                getDataDictionaryService().getRelationshipSourceAttributes(bo.getClass().getName(), referenceName);
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
                } catch (InvocationTargetException e) {
                    fkFieldsPopulated = false;
                } catch (NoSuchMethodException e) {
                    fkFieldsPopulated = false;
                }

                // test the value
                if (fkFieldValue == null) {
                    fkFieldsPopulated = false;
                } else if (String.class.isAssignableFrom(fkFieldValue.getClass())) {
                    if (StringUtils.isBlank((String) fkFieldValue)) {
                        fkFieldsPopulated = false;
                    }
                }
            }
        } else if (bo instanceof PersistableBusinessObject) { // if no DD relationship exists, check the persistence service
            fkFieldsPopulated = persistenceService
                    .allForeignKeyValuesPopulatedForReference((PersistableBusinessObject) bo, referenceName);
        }

        // only bother if all the fk fields have values
        if (fkFieldsPopulated) {

            // do the existence test
            exists = validateReferenceExists(bo, referenceName);
            if (exists) {

                // do the active test, if appropriate
                if (!(bo instanceof MutableInactivatable) || ((MutableInactivatable) bo).isActive()) {
                    active = validateReferenceIsActive(bo, referenceName);
                    if (!active) {
                        GlobalVariables.getMessageMap()
                                .putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_INACTIVE,
                                        displayFieldName);
                        success &= false;
                    }
                }
            } else {
                GlobalVariables.getMessageMap()
                        .putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_EXISTENCE, displayFieldName);
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
        Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(bo.getClass());

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext(); ) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(bo, reference);
        }
        return success;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.krad.bo.BusinessObject,
     *      org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    public boolean validateDefaultExistenceChecksForNewCollectionItem(BusinessObject bo,
            BusinessObject newCollectionItem, String collectionName) {
        boolean success = true;

        if (StringUtils.isNotBlank(collectionName)) {
            // get a collection of all the referenceDefinitions setup for this object
            Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(bo.getClass());

            // walk through the references, doing the tests on each
            for (Iterator iter = references.iterator(); iter.hasNext(); ) {
                ReferenceDefinition reference = (ReferenceDefinition) iter.next();
                if (collectionName != null && collectionName.equals(reference.getCollection())) {
                    String displayFieldName;
                    if (reference.isDisplayFieldNameSet()) {
                        displayFieldName = reference.getDisplayFieldName();
                    } else {
                        Class boClass =
                                reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() :
                                        bo.getClass();
                        displayFieldName = dataDictionaryService
                                .getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
                    }

                    success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(),
                            reference.getAttributeToHighlightOnFail(), displayFieldName);
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
        Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(document);

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext(); ) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(document, reference);
        }
        return success;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.krad.document.TransactionalDocument,
     *      org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    public boolean validateDefaultExistenceChecksForNewCollectionItem(TransactionalDocument document,
            BusinessObject newCollectionItem, String collectionName) {
        boolean success = true;
        if (StringUtils.isNotBlank(collectionName)) {
            // get a collection of all the referenceDefinitions setup for this object
            Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(document);

            // walk through the references, doing the tests on each
            for (Iterator iter = references.iterator(); iter.hasNext(); ) {
                ReferenceDefinition reference = (ReferenceDefinition) iter.next();
                if (collectionName != null && collectionName.equals(reference.getCollection())) {
                    String displayFieldName;
                    if (reference.isDisplayFieldNameSet()) {
                        displayFieldName = reference.getDisplayFieldName();
                    } else {
                        Class boClass =
                                reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() :
                                        document.getClass();
                        displayFieldName = dataDictionaryService
                                .getAttributeLabel(boClass, reference.getAttributeToHighlightOnFail());
                    }

                    success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(),
                            reference.getAttributeToHighlightOnFail(), displayFieldName);
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

        if (valueReader.getAttributeName() == null) {
            validateObject(result, valueReader, doOptionalProcessing, true);
        } else {
            validateAttribute(result, valueReader, doOptionalProcessing);
        }

        if (result.getNumberOfErrors() > 0) {
            for (Iterator<ConstraintValidationResult> iterator = result.iterator(); iterator.hasNext(); ) {
                ConstraintValidationResult constraintValidationResult = iterator.next();
                if (constraintValidationResult.getStatus().getLevel() >= ErrorLevel.WARN.getLevel()){                    
                    String attributePath = constraintValidationResult.getAttributePath();
                    if (attributePath == null || attributePath.isEmpty()){
                        attributePath = constraintValidationResult.getAttributeName();
                    }
                    setFieldError(constraintValidationResult.getEntryName(),
                            attributePath, constraintValidationResult.getErrorKey(),
                            constraintValidationResult.getErrorParameters());
                }
            }
        }

        return result;
    }

    private void processElementConstraints(DictionaryValidationResult result, Object value, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
        processConstraints(result, elementConstraintProcessors, value, definition, attributeValueReader,
                doOptionalProcessing);
    }

    private void processCollectionConstraints(DictionaryValidationResult result, Collection<?> collection,
            Constrainable definition, AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
        processConstraints(result, collectionConstraintProcessors, collection, definition, attributeValueReader,
                doOptionalProcessing);
    }

    @SuppressWarnings("unchecked")
    private void processConstraints(DictionaryValidationResult result,
            List<? extends ConstraintProcessor> constraintProcessors, Object value, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean doOptionalProcessing) {
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
                        Collection<Constraint> constraintList =
                                constraintProvider.getConstraints(selectedDefinition, constraintType);
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

                    ProcessorResult processorResult =
                            processor.process(result, value, constraint, selectedAttributeValueReader);

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

    private void setFieldError(String entryName, String attributeName, String key, String... args) {
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

    private void validateAttribute(DictionaryValidationResult result, AttributeValueReader attributeValueReader,
            boolean checkIfRequired) throws AttributeValidationException {
        Constrainable definition = attributeValueReader.getDefinition(attributeValueReader.getAttributeName());
        validateAttribute(result, definition, attributeValueReader, checkIfRequired);
    }

    private void validateAttribute(DictionaryValidationResult result, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean checkIfRequired) throws AttributeValidationException {

        if (definition == null)
            throw new AttributeValidationException(
                    "Unable to validate constraints for attribute \"" + attributeValueReader.getAttributeName() +
                            "\" on entry \"" + attributeValueReader.getEntryName() +
                            "\" because no attribute definition can be found.");

        Object value = attributeValueReader.getValue();

        processElementConstraints(result, value, definition, attributeValueReader, checkIfRequired);
    }

    private void validateObject(DictionaryValidationResult result, AttributeValueReader attributeValueReader, 
            boolean doOptionalProcessing, boolean processAttributes) throws AttributeValidationException {

        // If the entry itself is constrainable then the attribute value reader will return it here and we'll need to check if it has any constraints
        Constrainable objectEntry = attributeValueReader.getEntry();
        processElementConstraints(result, attributeValueReader.getObject(), objectEntry, attributeValueReader,
                doOptionalProcessing);

        List<Constrainable> definitions = attributeValueReader.getDefinitions();

        // Exit if the attribute value reader has no child definitions
        if (null == definitions)
            return;

        //Process all attribute definitions (unless being skipped)
        if (processAttributes){
            for (Constrainable definition : definitions) {
                String attributeName = definition.getName();
                attributeValueReader.setAttributeName(attributeName);

                if (attributeValueReader.isReadable()) {
                    Object value = attributeValueReader.getValue(attributeName);

                    processElementConstraints(result, value, definition, attributeValueReader, doOptionalProcessing);
                }
            }
        }

        //Process any constraints that may be defined on complex attributes
        if (objectEntry instanceof DataDictionaryEntryBase) {
            List<ComplexAttributeDefinition> complexAttrDefinitions =
                    ((DataDictionaryEntryBase) objectEntry).getComplexAttributes();

            if (complexAttrDefinitions != null) {
                for (ComplexAttributeDefinition complexAttrDefinition : complexAttrDefinitions) {
                    String attributeName = complexAttrDefinition.getName();
                    attributeValueReader.setAttributeName(attributeName);

                    if (attributeValueReader.isReadable()) {
                        Object value = attributeValueReader.getValue();

                        DataDictionaryEntry childEntry = complexAttrDefinition.getDataObjectEntry();
                        if (value != null) {
                            AttributeValueReader nestedAttributeValueReader = new DictionaryObjectAttributeValueReader(
                                    value, childEntry.getFullClassName(), childEntry, attributeValueReader.getPath());
                            //Validate nested object, however skip attribute definition porcessing on
                            //nested object entry, since they have already been processed above.
                            validateObject(result, nestedAttributeValueReader, doOptionalProcessing, false);
                        }

                        processElementConstraints(result, value, complexAttrDefinition, attributeValueReader,
                                doOptionalProcessing);
                    }
                }
            }
        }

        //FIXME: I think we may want to use a new CollectionConstrainable interface instead to obtain from
        //DictionaryObjectAttributeValueReader
        DataObjectEntry entry = (DataObjectEntry) attributeValueReader.getEntry();
        if (entry != null) {
            for (CollectionDefinition collectionDefinition : entry.getCollections()) {
                //TODO: Do we need to be able to handle simple collections (ie. String, etc)

                String childEntryName = collectionDefinition.getDataObjectClass();
                String attributeName = collectionDefinition.getName();
                attributeValueReader.setAttributeName(attributeName);

                if (attributeValueReader.isReadable()) {
                    Collection<?> collectionObject = attributeValueReader.getValue();
                    DataDictionaryEntry childEntry = childEntryName != null ?
                            getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(childEntryName) :
                            null;
                    if (collectionObject != null) {
                        int index = 0;
                        for (Object value : collectionObject) {
                            //NOTE: This path is only correct for collections that guarantee order
                            String objectAttributePath = attributeValueReader.getPath() + "[" + index + "]";

                            //FIXME: It's inefficient to be creating new attribute reader for each item in collection
                            AttributeValueReader nestedAttributeValueReader = new DictionaryObjectAttributeValueReader(
                                    value, childEntryName, childEntry, objectAttributePath);
                            validateObject(result, nestedAttributeValueReader, doOptionalProcessing, true);
                            index++;
                        }
                    }

                    processCollectionConstraints(result, collectionObject, collectionDefinition, attributeValueReader,
                            doOptionalProcessing);
                }
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

    protected WorkflowAttributePropertyResolutionService getWorkflowAttributePropertyResolutionService() {
        if (workflowAttributePropertyResolutionService == null) {
            workflowAttributePropertyResolutionService =
                    KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
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
    public void setCollectionConstraintProcessors(List<CollectionConstraintProcessor> collectionConstraintProcessors) {
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
    public void setElementConstraintProcessors(List<ConstraintProcessor> elementConstraintProcessors) {
        this.elementConstraintProcessors = elementConstraintProcessors;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
