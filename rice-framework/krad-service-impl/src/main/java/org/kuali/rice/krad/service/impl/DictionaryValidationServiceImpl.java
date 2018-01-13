/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.ComplexAttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.ReferenceDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
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
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.ConstraintStateUtils;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.beans.PropertyAccessorUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
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
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            DictionaryValidationServiceImpl.class);

    /**
     * Constant defines a validation method for an attribute value.
     * <p>Value is "validate"
     */
    public static final String VALIDATE_METHOD = "validate";

    protected DataDictionaryService dataDictionaryService;
    //    protected BusinessObjectService businessObjectService;
    //    protected PersistenceService persistenceService;
    protected DocumentDictionaryService documentDictionaryService;
    //    protected PersistenceStructureService persistenceStructureService;
    @Deprecated
    private LegacyDataAdapter legacyDataAdapter;

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
    protected final Set<Object> newIdentitySet() {
        return java.util.Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object)
     */
    @Override
    public DictionaryValidationResult validate(Object object) {
        return validate(object, object.getClass().getName(), (String) null, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validate(java.lang.Object, java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public DictionaryValidationResult validate(Object object, String entryName, String attributeName,
            boolean doOptionalProcessing) {
        StateMapping stateMapping = null;
        String validationState = null;
        DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            stateMapping = entry.getStateMapping();
            if (stateMapping != null) {
                validationState = stateMapping.getCurrentState(object);
            }
        }

        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        attributeValueReader.setAttributeName(attributeName);
        return validate(attributeValueReader, doOptionalProcessing, validationState, stateMapping);
    }

    /**
     * @see DictionaryValidationService#validateAgainstNextState(Object)
     */
    @Override
    public DictionaryValidationResult validateAgainstNextState(Object object) {
        String entryName = object.getClass().getName();
        StateMapping stateMapping = null;
        String validationState = null;
        DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            stateMapping = entry.getStateMapping();
            if (stateMapping != null) {
                validationState = stateMapping.getNextState(object);
            }
        }
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        return validate(attributeValueReader, true, validationState, stateMapping);
    }

    /**
     * @see DictionaryValidationService#validateAgainstState(Object, String)
     */
    @Override
    public DictionaryValidationResult validateAgainstState(Object object, String validationState) {
        String entryName = object.getClass().getName();
        StateMapping stateMapping = null;
        DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            stateMapping = entry.getStateMapping();
            if (stateMapping != null && StringUtils.isBlank(validationState)) {
                validationState = stateMapping.getCurrentState(object);
            }
        }

        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        return validate(attributeValueReader, true, validationState, stateMapping);
    }

    /**
     * @see DictionaryValidationService#validate(Object, String, DataDictionaryEntry, boolean)
     */
    @Override
    public DictionaryValidationResult validate(Object object, String entryName, DataDictionaryEntry entry,
            boolean doOptionalProcessing) {
        StateMapping stateMapping = null;
        String validationState = null;
        if (entry != null) {
            stateMapping = entry.getStateMapping();
            if (stateMapping != null) {
                validationState = stateMapping.getCurrentState(object);
            }
        }
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, entryName, entry);
        return validate(attributeValueReader, doOptionalProcessing, validationState, stateMapping);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocument(org.kuali.rice.krad.document.Document)
     */
    @Override
    public void validateDocument(Document document) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        validate(document, documentEntryName, (String) null, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentAttribute(org.kuali.rice.krad.document.Document,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void validateDocumentAttribute(Document document, String attributeName, String errorPrefix) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        validate(document, documentEntryName, attributeName, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentAndUpdatableReferencesRecursively(org.kuali.rice.krad.document.Document,
     * int, boolean)
     */
    @Override
    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth,
            boolean validateRequired) {
        validateDocumentAndUpdatableReferencesRecursively(document, maxDepth, validateRequired, false);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDocumentAndUpdatableReferencesRecursively(org.kuali.rice.krad.document.Document,
     * int, boolean, boolean)
     */
    @Override
    public void validateDocumentAndUpdatableReferencesRecursively(Document document, int maxDepth,
            boolean validateRequired, boolean chompLastLetterSFromCollectionName) {
        String documentEntryName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        validate(document, documentEntryName, (String) null, true);

        if (maxDepth > 0) {
            validateUpdatabableReferencesRecursively(document, maxDepth - 1, validateRequired,
                    chompLastLetterSFromCollectionName, newIdentitySet());
        }
    }

    protected void validateUpdatabableReferencesRecursively(Object businessObject, int maxDepth,
            boolean validateRequired, boolean chompLastLetterSFromCollectionName, Set<Object> processedBOs) {
        // if null or already processed, return
        if (KRADUtils.isNull(businessObject) || processedBOs.contains(businessObject)) {
            return;
        }
        processedBOs.add(businessObject);  // add bo to list to prevent excessive looping
        Map<String, Class> references = getLegacyDataAdapter().listReferenceObjectFields(businessObject.getClass());
        for (String referenceName : references.keySet()) {
            if (getLegacyDataAdapter().isReferenceUpdatable(businessObject.getClass(), referenceName)) {
                Object referenceObj = KradDataServiceLocator.getDataObjectService().wrap(businessObject)
                        .getPropertyValueNullSafe(referenceName);

                if (KRADUtils.isNull(referenceObj) || !(referenceObj instanceof PersistableBusinessObject)) {
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
        Map<String, Class> collections = getLegacyDataAdapter().listCollectionObjectTypes(businessObject.getClass());
        for (String collectionName : collections.keySet()) {
            if (getLegacyDataAdapter().isCollectionUpdatable(businessObject.getClass(), collectionName)) {
                Object listObj = KradDataServiceLocator.getDataObjectService().wrap(businessObject)
                        .getPropertyValueNullSafe(collectionName);

                if (KRADUtils.isNull(listObj)) {
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
                KRADUtils.materializeObjects(list);

                for (int i = 0; i < list.size(); i++) {
                    final Object o = list.get(i);
                    if (KRADUtils.isNotNull(o) && o instanceof PersistableBusinessObject) {
                        final BusinessObject element = (BusinessObject) o;

                        final String errorPathAddition;
                        if (chompLastLetterSFromCollectionName) {
                            errorPathAddition = StringUtils.chomp(collectionName, "s")
                                    + "["
                                    + Integer.toString(i)
                                    + "]";
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
    @Override
    public boolean isBusinessObjectValid(Object businessObject) {
        return isBusinessObjectValid(businessObject, null);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#isBusinessObjectValid(org.kuali.rice.krad.bo.BusinessObject,
     * String)
     */
    @Override
    public boolean isBusinessObjectValid(Object businessObject, String prefix) {
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
    public void validateBusinessObjectsRecursively(Object businessObject, int depth) {
        if (KRADUtils.isNull(businessObject)) {
            return;
        }

        // validate primitives and any specific bo validation
        validateBusinessObject(businessObject);

        // call method to recursively find business objects and validate
        validateBusinessObjectsFromDescriptors(businessObject, PropertyUtils.getPropertyDescriptors(
                businessObject.getClass()), depth);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void validateBusinessObject(Object businessObject) {
        validateBusinessObject(businessObject, true);
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateBusinessObject(org.kuali.rice.krad.bo.BusinessObject,
     * boolean)
     */
    @Override
    public void validateBusinessObject(Object businessObject, boolean validateRequired) {
        if (KRADUtils.isNull(businessObject)) {
            return;
        }

        validate(businessObject, businessObject.getClass().getName(), (String) null, validateRequired);
    }

    /**
     * iterates through the property descriptors looking for business objects or lists of business objects. calls
     * validate method
     * for each bo found
     *
     * @param object
     * @param propertyDescriptors
     */
    protected void validateBusinessObjectsFromDescriptors(Object object, PropertyDescriptor[] propertyDescriptors,
            int depth) {
        DataObjectWrapper<Object> wrapper = KradDataServiceLocator.getDataObjectService().wrap(object);

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            // validate the properties that are descended from BusinessObject
            if (propertyDescriptor.getPropertyType() != null &&
                    PersistableBusinessObject.class.isAssignableFrom(propertyDescriptor.getPropertyType()) &&
                    wrapper.getPropertyValueNullSafe(propertyDescriptor.getName()) != null) {
                BusinessObject bo = (BusinessObject) wrapper.getPropertyValueNullSafe(propertyDescriptor.getName());
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
                    wrapper.getPropertyValueNullSafe(propertyDescriptor.getName()) != null) {
                List propertyList = (List) wrapper.getPropertyValueNullSafe(propertyDescriptor.getName());
                for (int j = 0; j < propertyList.size(); j++) {
                    if (propertyList.get(j) != null && propertyList.get(j) instanceof PersistableBusinessObject) {
                        if (depth == 0) {
                            GlobalVariables.getMessageMap().addToErrorPath(StringUtils.chomp(
                                    propertyDescriptor.getName(), "s") + "[" +
                                    (new Integer(j)).toString() + "]");
                            validateBusinessObject((BusinessObject) propertyList.get(j));
                            GlobalVariables.getMessageMap().removeFromErrorPath(StringUtils.chomp(
                                    propertyDescriptor.getName(), "s") + "[" +
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
    @Override
    @Deprecated
    public void validatePrimitiveFromDescriptor(String entryName, Object object, PropertyDescriptor propertyDescriptor,
            String errorPrefix, boolean validateRequired) {

        // validate the primitive attributes if defined in the dictionary
        if (null != propertyDescriptor) {
            validate(object, entryName, propertyDescriptor.getName(), validateRequired);
        }
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExists(java.lang.Object dataObject,
     * org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    @Override
    public boolean validateReferenceExists(Object dataObject, ReferenceDefinition reference) {
        return validateReferenceExists(dataObject, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExists(java.lang.Object dataObject,
     * java.lang.String)
     */
    @Override
    public boolean validateReferenceExists(Object dataObject, String referenceName) {

        // attempt to retrieve the specified object from the db
        Object referenceDataObject = getLegacyDataAdapter().getReferenceIfExists(dataObject, referenceName);

        // if it isn't there, then it doesn't exist, return false
        if (KRADUtils.isNotNull(referenceDataObject)) {
            return true;
        }

        // otherwise, it is there, return true
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceIsActive(java.lang.Object
     * dataObject,
     * org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    @Override
    public boolean validateReferenceIsActive(Object dataObject, ReferenceDefinition reference) {
        return validateReferenceIsActive(dataObject, reference.getAttributeName());
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceIsActive(java.lang.Object
     * dataObject,
     * String)
     */
    @Override
    public boolean validateReferenceIsActive(Object dataObject, String referenceName) {
        // attempt to retrieve the specified object from the db
        Object referenceDataObject = getLegacyDataAdapter().getReferenceIfExists(dataObject, referenceName);
        if (referenceDataObject == null) {
            return false;
        }
        // mutable is related only to BusinessObject classes
        if (!(referenceDataObject instanceof Inactivatable) || ((Inactivatable) referenceDataObject).isActive()) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExistsAndIsActive(java.lang.Object
     * dataObject,
     * org.kuali.rice.krad.datadictionary.ReferenceDefinition)
     */
    @Override
    public boolean validateReferenceExistsAndIsActive(Object dataObject, ReferenceDefinition reference) {
        boolean success = true;
        // intelligently use the fieldname from the reference, or get it out
        // of the dataDictionaryService
        String displayFieldName;
        if (reference.isDisplayFieldNameSet()) {
            displayFieldName = reference.getDisplayFieldName();
        } else {
            Class<?> boClass = reference.isCollectionReference() ? reference.getCollectionBusinessObjectClass() :
                    dataObject.getClass();
            displayFieldName = dataDictionaryService.getAttributeLabel(boClass,
                    reference.getAttributeToHighlightOnFail());
        }

        if (reference.isCollectionReference()) {
            success = validateCollectionReferenceExistsAndIsActive(dataObject, reference, displayFieldName,
                    StringUtils.split(reference.getCollection(), "."), null);
        } else {
            success = validateReferenceExistsAndIsActive(dataObject, reference.getAttributeName(),
                    reference.getAttributeToHighlightOnFail(), displayFieldName);
        }
        return success;
    }

    /**
     * @param dataObject the object to get the collection from
     * @param reference the <code>ReferenceDefinition</code> of the collection to validate
     * @param displayFieldName the name of the field
     * @param intermediateCollections array containing the path to the collection as tokens
     * @param pathToAttributeI the rebuilt path to the ReferenceDefinition.attributeToHighlightOnFail which includes
     * the
     * index of
     * each subcollection
     * @return
     */
    private boolean validateCollectionReferenceExistsAndIsActive(Object dataObject, ReferenceDefinition reference,
            String displayFieldName, String[] intermediateCollections, String pathToAttributeI) {
        boolean success = true;
        Collection<?> referenceCollection;
        String collectionName = intermediateCollections[0];
        // remove current collection from intermediates
        intermediateCollections = (String[]) ArrayUtils.removeElement(intermediateCollections, collectionName);
        try {
            referenceCollection = (Collection) PropertyUtils.getProperty(dataObject, collectionName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int pos = 0;
        Iterator<?> iterator = referenceCollection.iterator();
        while (iterator.hasNext()) {
            String pathToAttribute = StringUtils.defaultString(pathToAttributeI)
                    + collectionName + "[" + (pos++) + "].";
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
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateReferenceExistsAndIsActive(java.lang.Object
     * dataObject,
     * String, String, String)
     */
    @Override
    public boolean validateReferenceExistsAndIsActive(Object dataObject, String referenceName,
            String attributeToHighlightOnFail, String displayFieldName) {

        // if we're dealing with a nested attribute, we need to resolve down to the BO where the primitive attribute is located
        // this is primarily to deal with the case of a defaultExistenceCheck that uses an "extension", i.e referenceName
        // would be extension.attributeName
        if (PropertyAccessorUtils.isNestedOrIndexedProperty(referenceName)) {
            String nestedAttributePrefix = KRADUtils.getNestedAttributePrefix(referenceName);
            String nestedAttributePrimitive = KRADUtils.getNestedAttributePrimitive(referenceName);
            Object nestedObject = KradDataServiceLocator.getDataObjectService().wrap(dataObject)
                    .getPropertyValueNullSafe(nestedAttributePrefix);
            return validateReferenceExistsAndIsActive(nestedObject, nestedAttributePrimitive,
                    attributeToHighlightOnFail, displayFieldName);
        }

        boolean hasReferences = validateFkFieldsPopulated(dataObject, referenceName);
        boolean referenceExists = hasReferences && validateReferenceExists(dataObject, referenceName);
        boolean canIncludeActiveReference = referenceExists && (!(dataObject instanceof Inactivatable) ||
                ((Inactivatable) dataObject).isActive());
        boolean referenceActive = canIncludeActiveReference && validateReferenceIsActive(dataObject, referenceName);

        if(hasReferences && !referenceExists) {
            GlobalVariables.getMessageMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_EXISTENCE,
                    displayFieldName);
            return false;
        } else if(canIncludeActiveReference && !referenceActive) {
            GlobalVariables.getMessageMap().putError(attributeToHighlightOnFail, RiceKeyConstants.ERROR_INACTIVE,
                    displayFieldName);
            return false;
        }

        return true;
    }

    private boolean validateFkFieldsPopulated(Object dataObject, String referenceName) {
        // need to check for DD relationship FKs
        List<String> fkFields = getDataDictionaryService().getRelationshipSourceAttributes(
                dataObject.getClass().getName(), referenceName);
        if (fkFields != null) {
            for (String fkFieldName : fkFields) {
                Object fkFieldValue = null;
                try {
                    fkFieldValue = PropertyUtils.getProperty(dataObject, fkFieldName);
                }
                // if we cant retrieve the field value, then
                // it doesnt have a value
                catch (IllegalAccessException e) {
                    return false;
                } catch (InvocationTargetException e) {
                    return false;
                } catch (NoSuchMethodException e) {
                    return false;
                }

                // test the value
                if (fkFieldValue == null) {
                    return false;
                } else if (String.class.isAssignableFrom(fkFieldValue.getClass())) {
                    if (StringUtils.isBlank((String) fkFieldValue)) {
                        return false;
                    }
                }
            }
        } else { // if no DD relationship exists, check the persistence service / data object service
            return getLegacyDataAdapter().allForeignKeyValuesPopulatedForReference(dataObject, referenceName);
        }
        return true;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecks(java.lang.Object
     * dataObject)
     */
    @Override
    public boolean validateDefaultExistenceChecks(Object dataObject) {
        boolean success = true;

        // get a collection of all the referenceDefinitions setup for this object
        Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(dataObject.getClass());

        // walk through the references, doing the tests on each
        for (Iterator iter = references.iterator(); iter.hasNext(); ) {
            ReferenceDefinition reference = (ReferenceDefinition) iter.next();

            // do the existence and validation testing
            success &= validateReferenceExistsAndIsActive(dataObject, reference);
        }
        return success;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(java.lang.Object
     * dataObject,
     * java.lang.Object newCollectionLine, java.lang.String collectionName)
     */
    @Override
    public boolean validateDefaultExistenceChecksForNewCollectionItem(Object dataObject, Object newCollectionItem,
            String collectionName) {
        boolean success = true;

        if (StringUtils.isNotBlank(collectionName)) {
            // get a collection of all the referenceDefinitions setup for this object
            Collection references = getDocumentDictionaryService().getDefaultExistenceChecks(dataObject.getClass());

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
                                        dataObject.getClass();
                        displayFieldName = dataDictionaryService.getAttributeLabel(boClass,
                                reference.getAttributeToHighlightOnFail());
                    }

                    success &= validateReferenceExistsAndIsActive(newCollectionItem, reference.getAttributeName(),
                            reference.getAttributeToHighlightOnFail(), displayFieldName);
                }
            }
        }

        return success;
    }

    /**
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForTransDoc(org.kuali.rice.krad.document.TransactionalDocument)
     */
    @Override
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
     * @see org.kuali.rice.krad.service.DictionaryValidationService#validateDefaultExistenceChecksForNewCollectionItem(org.kuali.rice.krad.document.TransactionalDocument,
     * org.kuali.rice.krad.bo.BusinessObject, String)
     */
    @Override
    public boolean validateDefaultExistenceChecksForNewCollectionItem(TransactionalDocument document,
            Object newCollectionItem, String collectionName) {
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
                        displayFieldName = dataDictionaryService.getAttributeLabel(boClass,
                                reference.getAttributeToHighlightOnFail());
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

    /**
     * Validates using the defined AttributeValueReader (which allows access the object being validated) against
     * the validationState and stateMapping (if specified).
     *
     * <p>If state information is null, validates the constraints as stateless (ie all constraints apply regardless of
     * their states attribute).</p>
     *
     * @param valueReader - an object to validate
     * @param doOptionalProcessing true if the validation should do optional validation (e.g. to check if empty values
     * are required or not), false otherwise
     * @param validationState
     * @param stateMapping
     * @return
     */
    @Override
    public DictionaryValidationResult validate(AttributeValueReader valueReader, boolean doOptionalProcessing,
            String validationState, StateMapping stateMapping) {
        DictionaryValidationResult result = new DictionaryValidationResult();

        if (valueReader.getAttributeName() == null) {
            validateObject(result, valueReader, doOptionalProcessing, true, validationState, stateMapping);
        } else {
            validateAttribute(result, valueReader, doOptionalProcessing, validationState, stateMapping);
        }

        if (result.getNumberOfErrors() > 0) {

            String[] prefixParams = new String[1];
            String prefixMessageKey = UifConstants.Messages.STATE_PREFIX;
            if (stateMapping != null) {
                prefixParams[0] = stateMapping.getStateNameMessage(validationState);
            }

            if (StringUtils.isBlank(prefixParams[0])) {
                prefixMessageKey = null;
            }

            for (Iterator<ConstraintValidationResult> iterator = result.iterator(); iterator.hasNext(); ) {
                ConstraintValidationResult constraintValidationResult = iterator.next();
                if (constraintValidationResult.getStatus().getLevel() >= ErrorLevel.WARN.getLevel()) {
                    String attributePath = constraintValidationResult.getAttributePath();
                    if (attributePath == null || attributePath.isEmpty()) {
                        attributePath = constraintValidationResult.getAttributeName();
                    }

                    if (constraintValidationResult.getConstraintLabelKey() != null) {
                        ErrorMessage errorMessage = new ErrorMessage(constraintValidationResult.getConstraintLabelKey(),
                                constraintValidationResult.getErrorParameters());
                        errorMessage.setMessagePrefixKey(prefixMessageKey);
                        errorMessage.setMessagePrefixParameters(prefixParams);
                        GlobalVariables.getMessageMap().putError(attributePath, errorMessage);
                    } else {
                        ErrorMessage errorMessage = new ErrorMessage(constraintValidationResult.getErrorKey(),
                                constraintValidationResult.getErrorParameters());
                        errorMessage.setMessagePrefixKey(prefixMessageKey);
                        errorMessage.setMessagePrefixParameters(prefixParams);
                        GlobalVariables.getMessageMap().putError(attributePath, errorMessage);
                    }
                }
            }
        }

        return result;
    }

    /**
     * process constraints for the provided value using the element constraint processors
     *
     * @param result - used to store the validation results
     * @param value - the object on which constraints are to be processed - the value of a complex attribute
     * @param definition - a Data Dictionary definition e.g. {@code ComplexAttributeDefinition}
     * @param attributeValueReader - a class that encapsulate access to both dictionary metadata and object field
     * values
     * @param doOptionalProcessing - true if the validation should do optional validation, false otherwise
     */
    protected void processElementConstraints(DictionaryValidationResult result, Object value, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean doOptionalProcessing, String validationState,
            StateMapping stateMapping) {
        processConstraints(result, elementConstraintProcessors, value, definition, attributeValueReader,
                doOptionalProcessing, validationState, stateMapping);
    }

    /**
     * process constraints for the provided collection using the collection constraint processors
     *
     * @param result - used to store the validation results
     * @param collection - the object on which constraints are to be processed - a collection
     * @param definition - a Data Dictionary definition e.g. {@code CollectionDefinition}
     * @param attributeValueReader - a class that encapsulate access to both dictionary metadata and object field
     * values
     * @param doOptionalProcessing - true if the validation should do optional validation, false otherwise
     */
    protected void processCollectionConstraints(DictionaryValidationResult result, Collection<?> collection,
            Constrainable definition, AttributeValueReader attributeValueReader, boolean doOptionalProcessing,
            String validationState, StateMapping stateMapping) {
        processConstraints(result, collectionConstraintProcessors, collection, definition, attributeValueReader,
                doOptionalProcessing, validationState, stateMapping);
    }

    /**
     * process constraints for the provided value using the provided constraint processors
     *
     * @param result - used to store the validation results
     * @param value - the object on which constraints are to be processed - a collection or the value of an attribute
     * @param definition - a Data Dictionary definition e.g. {@code ComplexAttributeDefinition} or {@code
     * CollectionDefinition}
     * @param attributeValueReader - a class that encapsulate access to both dictionary metadata and object field
     * values
     * @param doOptionalProcessing - true if the validation should do optional validation, false otherwise
     */
    @SuppressWarnings("unchecked")
    private void processConstraints(DictionaryValidationResult result,
            List<? extends ConstraintProcessor> constraintProcessors, Object value, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean doOptionalProcessing, String validationState,
            StateMapping stateMapping) {
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
                        Collection<Constraint> constraintList = constraintProvider.getConstraints(selectedDefinition,
                                constraintType);
                        if (constraintList != null) {
                            constraintQueue.addAll(constraintList);
                        }
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

                    constraint = ConstraintStateUtils.getApplicableConstraint(constraint, validationState,
                            stateMapping);

                    if (constraint != null) {
                        ProcessorResult processorResult = processor.process(result, value, constraint,
                                selectedAttributeValueReader);

                        Collection<Constraint> processorResultContraints = processorResult.getConstraints();
                        if (processorResultContraints != null && processorResultContraints.size() > 0) {
                            constraintQueue.addAll(processorResultContraints);
                        }

                        // Change the selected definition to whatever was returned from the processor
                        if (processorResult.isDefinitionProvided()) {
                            selectedDefinition = processorResult.getDefinition();
                        }
                        // Change the selected attribute value reader to whatever was returned from the processor
                        if (processorResult.isAttributeValueReaderProvided()) {
                            selectedAttributeValueReader = processorResult.getAttributeValueReader();
                        }
                    }
                }

                // After iterating through all the constraints for this processor, add the ones that werent consumed by this processor to the queue
                constraintQueue.addAll(additionalConstraints);
            }
        }
    }

    /**
     * validates an attribute
     *
     * @param result - used to store the validation results
     * @param attributeValueReader - a class that encapsulate access to both dictionary metadata and object field
     * values
     * @param checkIfRequired - check if empty values are required or not
     * @throws AttributeValidationException
     */
    protected void validateAttribute(DictionaryValidationResult result, AttributeValueReader attributeValueReader,
            boolean checkIfRequired, String validationState,
            StateMapping stateMapping) throws AttributeValidationException {
        Constrainable definition = attributeValueReader.getDefinition(attributeValueReader.getAttributeName());
        validateAttribute(result, definition, attributeValueReader, checkIfRequired, validationState, stateMapping);
    }

    /**
     * Validates the attribute specified by definition
     *
     * @param definition -   the constrainable attribute definition of a specific attribute name
     * @throws AttributeValidationException
     */
    protected void validateAttribute(DictionaryValidationResult result, Constrainable definition,
            AttributeValueReader attributeValueReader, boolean checkIfRequired, String validationState,
            StateMapping stateMapping) throws AttributeValidationException {

        if (definition == null) {
            throw new AttributeValidationException(
                    "Unable to validate constraints for attribute \"" + attributeValueReader.getAttributeName() +
                            "\" on entry \"" + attributeValueReader.getEntryName() +
                            "\" because no attribute definition can be found.");
        }

        Object value = attributeValueReader.getValue();

        processElementConstraints(result, value, definition, attributeValueReader, checkIfRequired, validationState,
                stateMapping);
    }

    /**
     * validates an object and its attributes recursively
     *
     * @param result - used to store the validation results
     * @param attributeValueReader - a class that encapsulate access to both dictionary metadata and object field
     * values
     * @param doOptionalProcessing - true if the validation should do optional validation, false otherwise
     * @param processAttributes - if true process all attribute definitions, skip if false
     * @throws AttributeValidationException
     */
    protected void validateObject(DictionaryValidationResult result, AttributeValueReader attributeValueReader,
            boolean doOptionalProcessing, boolean processAttributes, String validationState,
            StateMapping stateMapping) throws AttributeValidationException {

        // If the entry itself is constrainable then the attribute value reader will return it here and we'll need to check if it has any constraints
        Constrainable objectEntry = attributeValueReader.getEntry();
        processElementConstraints(result, attributeValueReader.getObject(), objectEntry, attributeValueReader,
                doOptionalProcessing, validationState, stateMapping);

        List<Constrainable> definitions = attributeValueReader.getDefinitions();

        // Exit if the attribute value reader has no child definitions
        if (null == definitions) {
            return;
        }

        //Process all attribute definitions (unless being skipped)
        if (processAttributes) {
            for (Constrainable definition : definitions) {
                String attributeName = definition.getName();
                attributeValueReader.setAttributeName(attributeName);

                if (attributeValueReader.isReadable()) {
                    Object value = attributeValueReader.getValue(attributeName);

                    processElementConstraints(result, value, definition, attributeValueReader, doOptionalProcessing,
                            validationState, stateMapping);
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
                            nestedAttributeValueReader.setAttributeName(attributeValueReader.getAttributeName());
                            //Validate nested object, however skip attribute definition porcessing on
                            //nested object entry, since they have already been processed above.
                            validateObject(result, nestedAttributeValueReader, doOptionalProcessing, false,
                                    validationState, stateMapping);
                        }

                        processElementConstraints(result, value, complexAttrDefinition, attributeValueReader,
                                doOptionalProcessing, validationState, stateMapping);
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
                            validateObject(result, nestedAttributeValueReader, doOptionalProcessing, true,
                                    validationState, stateMapping);
                            index++;
                        }
                    }

                    processCollectionConstraints(result, collectionObject, collectionDefinition, attributeValueReader,
                            doOptionalProcessing, validationState, stateMapping);
                }
            }
        }
    }

    /**
     * gets the {@link DataDictionaryService}
     *
     * @return Returns the dataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * sets the {@link DataDictionaryService}
     *
     * @param dataDictionaryService The dataDictionaryService to set
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * gets the list of {@link CollectionConstraintProcessor}
     *
     * <p>Collection constraint processors are classes that determine if a feature of a collection of objects
     * satisfies some constraint</p>
     *
     * @return the collectionConstraintProcessors
     */
    @SuppressWarnings("unchecked")
    public List<CollectionConstraintProcessor> getCollectionConstraintProcessors() {
        return this.collectionConstraintProcessors;
    }

    /**
     * sets the list of {@link CollectionConstraintProcessor}
     *
     * @param collectionConstraintProcessors the collectionConstraintProcessors to set
     */
    @SuppressWarnings("unchecked")
    public void setCollectionConstraintProcessors(List<CollectionConstraintProcessor> collectionConstraintProcessors) {
        this.collectionConstraintProcessors = collectionConstraintProcessors;
    }

    /**
     * gets the list of {@link ConstraintProvider}s
     *
     * <p>Constraint providers are classes that map specific constraint types to a constraint resolver,
     * which takes a constrainable definition</p>
     *
     * @return the constraintProviders
     */
    @SuppressWarnings("unchecked")
    public List<ConstraintProvider> getConstraintProviders() {
        return this.constraintProviders;
    }

    /**
     * sets a list of {@link ConstraintProvider}
     *
     * @param constraintProviders the constraintProviders to set
     */
    @SuppressWarnings("unchecked")
    public void setConstraintProviders(List<ConstraintProvider> constraintProviders) {
        this.constraintProviders = constraintProviders;
    }

    /**
     * gets the list of element {@link ConstraintProcessor}
     *
     * <p>Element constraint processors are classes that determine if a passed value is valid
     * for a specific constraint at the individual object or object attribute level</p>
     *
     * @return the elementConstraintProcessors
     */
    @SuppressWarnings("unchecked")
    public List<ConstraintProcessor> getElementConstraintProcessors() {
        return this.elementConstraintProcessors;
    }

    /**
     * sets the list of {@link ConstraintProcessor}
     *
     * @param elementConstraintProcessors the elementConstraintProcessors to set
     */
    @SuppressWarnings("unchecked")
    public void setElementConstraintProcessors(List<ConstraintProcessor> elementConstraintProcessors) {
        this.elementConstraintProcessors = elementConstraintProcessors;
    }

    /**
     * gets the locally saved instance of @{link DocumentDictionaryService}
     *
     * <p>If the instance in this class has not be set, retrieve it using
     * {@link KRADServiceLocatorWeb#getDocumentDictionaryService()} and save locally</p>
     *
     * @return the locally saved instance of {@code DocumentDictionaryService}
     */
    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    /**
     * sets the {@link DocumentDictionaryService}
     *
     * @param documentDictionaryService - the {@code DocumentDictionaryService} to set
     */
    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    @Deprecated
    public LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return legacyDataAdapter;
    }
}
