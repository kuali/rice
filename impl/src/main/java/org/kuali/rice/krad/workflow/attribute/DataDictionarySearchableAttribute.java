/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.krad.workflow.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.datadictionary.SearchingAttribute;
import org.kuali.rice.krad.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.krad.datadictionary.WorkflowAttributes;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowAttributePropertyResolutionService;

/**
 * This class...
 */
public class DataDictionarySearchableAttribute implements SearchableAttribute {

    private static final long serialVersionUID = 173059488280366451L;
	private static final Logger LOG = Logger.getLogger(DataDictionarySearchableAttribute.class);
    public static final String DATA_TYPE_BOOLEAN = "boolean";

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchContent(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public String getSearchContent(DocumentSearchContext documentSearchContext) {

        return "";
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchStorageValues(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        List<SearchableAttributeValue> saValues = new ArrayList<SearchableAttributeValue>();

        String docId = documentSearchContext.getDocumentId();

        DocumentService docService = KRADServiceLocatorWeb.getDocumentService();
        Document doc = null;
        try  {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        } catch (WorkflowException we) {
        	LOG.error( "Unable to retrieve document " + docId + " in getSearchStorageValues()", we);
        }

        SearchableAttributeStringValue searchableAttributeValue = new SearchableAttributeStringValue();
        searchableAttributeValue.setSearchableAttributeKey("documentDescription");
        if ( doc != null ) {
        	if ( doc.getDocumentHeader() != null ) {
                searchableAttributeValue.setSearchableAttributeValue(doc.getDocumentHeader().getDocumentDescription());
        	} else {
        		searchableAttributeValue.setupAttributeValue( "null document header" );
        	}
        } else {
    		searchableAttributeValue.setupAttributeValue( "null document" );
        }
        saValues.add(searchableAttributeValue);

        searchableAttributeValue = new SearchableAttributeStringValue();
        searchableAttributeValue.setSearchableAttributeKey("organizationDocumentNumber");
        if ( doc != null ) {
        	if ( doc.getDocumentHeader() != null ) {
                searchableAttributeValue.setSearchableAttributeValue(doc.getDocumentHeader().getOrganizationDocumentNumber());
        	} else {
        		searchableAttributeValue.setupAttributeValue( "null document header" );
        	}
        } else {
    		searchableAttributeValue.setupAttributeValue( "null document" );
        }
        saValues.add(searchableAttributeValue);

        if ( doc != null && doc instanceof MaintenanceDocument) {
            final Class<? extends BusinessObject> businessObjectClass = getBusinessObjectClass(documentSearchContext.getDocumentTypeName());
            if (businessObjectClass != null) {
                if (GlobalBusinessObject.class.isAssignableFrom(businessObjectClass)) {
                    final String documentNumber = documentSearchContext.getDocumentId();
                    final GlobalBusinessObject globalBO = retrieveGlobalBusinessObject(documentNumber, businessObjectClass);

                    if (globalBO != null) {
                        saValues.addAll(findAllSearchableAttributesForGlobalBusinessObject(globalBO));
                    }
                } else {
                    saValues.addAll(parsePrimaryKeyValuesFromDocument(businessObjectClass, (MaintenanceDocument)doc));
                }

            }
        }
        if ( doc != null ) {
            DocumentEntry docEntry = (DocumentEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());
            if ( docEntry != null ) {
		        WorkflowAttributes workflowAttributes = docEntry.getWorkflowAttributes();
		        WorkflowAttributePropertyResolutionService waprs = KRADServiceLocatorInternal
                        .getWorkflowAttributePropertyResolutionService();
		        saValues.addAll(waprs.resolveSearchableAttributeValues(doc, workflowAttributes));
            } else {
            	LOG.error( "Unable to find DD document entry for document type: " + documentSearchContext.getDocumentTypeName() );
            }
        }
        return saValues;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchingRows(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {

        List<Row> docSearchRows = new ArrayList<Row>();

        Class boClass = DocumentHeader.class;

        Field descriptionField = FieldUtils.getPropertyField(boClass, "documentDescription", true);
        descriptionField.setFieldDataType(KEWConstants.SearchableAttributeConstants.DATA_TYPE_STRING);

        Field orgDocNumberField = FieldUtils.getPropertyField(boClass, "organizationDocumentNumber", true);
        orgDocNumberField.setFieldDataType(KEWConstants.SearchableAttributeConstants.DATA_TYPE_STRING);

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.add(descriptionField);
        docSearchRows.add(new Row(fieldList));

        fieldList = new ArrayList<Field>();
        fieldList.add(orgDocNumberField);
        docSearchRows.add(new Row(fieldList));


        DocumentEntry entry = (DocumentEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());
        if (entry  == null)
            return docSearchRows;
        if (entry instanceof MaintenanceDocumentEntry) {
            Class<? extends BusinessObject> businessObjectClass = getBusinessObjectClass(documentSearchContext.getDocumentTypeName());
            Class<? extends Maintainable> maintainableClass = getMaintainableClass(documentSearchContext.getDocumentTypeName());

            KualiGlobalMaintainableImpl globalMaintainable = null;
            try {
                globalMaintainable = (KualiGlobalMaintainableImpl)maintainableClass.newInstance();
                businessObjectClass = globalMaintainable.getPrimaryEditedBusinessObjectClass();
            } catch (Exception ie) {
                //was not a globalMaintainable.
            }

            if (businessObjectClass != null)
                docSearchRows.addAll(createFieldRowsForBusinessObject(businessObjectClass));
        }

        WorkflowAttributes workflowAttributes = entry.getWorkflowAttributes();
        if (workflowAttributes != null)
            docSearchRows.addAll(createFieldRowsForWorkflowAttributes(workflowAttributes));

        return docSearchRows;
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext searchContext) {
        List<WorkflowAttributeValidationError> validationErrors = null;
        DictionaryValidationService validationService = KNSServiceLocator.getDictionaryValidationService();
        
        for (Object key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value != null) {
            	if (value instanceof String && !StringUtils.isEmpty((String)value)) {
            		validationService.validateAttributeFormat(searchContext.getDocumentTypeName(), (String)key, (String)value, (String)key);
            	} else if (value instanceof Collection && !((Collection)value).isEmpty()) {
            		// we're doing multi-select search; so we need to loop through all of the Strings
            		// we've been handed and validate each of them
            		for (Object v : ((Collection)value)) {
            			if (v instanceof String) {
            				validationService.validateAttributeFormat(searchContext.getDocumentTypeName(), (String)key, (String)v, (String)key);
            			}
            		}
            	}
            }
        }

        if(GlobalVariables.getMessageMap().hasErrors()){
        	validationErrors = new ArrayList<WorkflowAttributeValidationError>();
        	MessageMap deepCopy = (MessageMap)ObjectUtils.deepCopy(GlobalVariables.getMessageMap()); 
        	validationErrors.add(new WorkflowAttributeValidationError(null,null,deepCopy));
        	// we should now strip the error messages from the map because they have moved to validationErrors
        	GlobalVariables.getMessageMap().clearErrorMessages();
        }


        return validationErrors;
    }

    /**
     * Creates a list of search fields, one for each primary key of the maintained business object
     * @param businessObjectClass the class of the maintained business object
     * @return a List of KEW search fields
     */
    protected List<Row> createFieldRowsForWorkflowAttributes(WorkflowAttributes attrs) {
        List<Row> searchFields = new ArrayList<Row>();

        List<SearchingTypeDefinition> searchingTypeDefinitions = attrs.getSearchingTypeDefinitions();
        final WorkflowAttributePropertyResolutionService propertyResolutionService = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
        for (SearchingTypeDefinition definition: searchingTypeDefinitions) {
            SearchingAttribute attr = definition.getSearchingAttribute();

            final String attributeName = attr.getAttributeName();
            final String businessObjectClassName = attr.getBusinessObjectClassName();
            Class boClass = null;
            BusinessObject businessObject  = null;
            try {
                boClass = Class.forName(businessObjectClassName);
                businessObject = (BusinessObject)boClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Field searchField = FieldUtils.getPropertyField(boClass, attributeName, false);
            searchField.setColumnVisible(attr.isShowAttributeInResultSet());

            //TODO this is a workaround to hide the Field from the search criteria.
            //This should be removed once hiding the entire Row is working
            if (!attr.isShowAttributeInSearchCriteria()){
                searchField.setFieldType(Field.HIDDEN);
            }
            String fieldDataType = propertyResolutionService.determineFieldDataType(boClass, attributeName);
            if (fieldDataType.equals(DataDictionarySearchableAttribute.DATA_TYPE_BOOLEAN)) {
                fieldDataType = KEWConstants.SearchableAttributeConstants.DATA_TYPE_STRING;
            }

            // Allow inline range searching on dates and numbers
            if (fieldDataType.equals(KEWConstants.SearchableAttributeConstants.DATA_TYPE_FLOAT) ||
                fieldDataType.equals(KEWConstants.SearchableAttributeConstants.DATA_TYPE_LONG) ||
                fieldDataType.equals(KEWConstants.SearchableAttributeConstants.DATA_TYPE_DATE)) {

                searchField.setAllowInlineRange(true);
            }
            searchField.setFieldDataType(fieldDataType);
            List displayedFieldNames = new ArrayList();
            displayedFieldNames.add(attributeName);
            LookupUtils.setFieldQuickfinder(businessObject, attributeName, searchField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);

            Row row = new Row(fieldList);
            if (!attr.isShowAttributeInSearchCriteria()) {
                row.setHidden(true);
            }
            searchFields.add(row);
        }

        return searchFields;
    }


    /**
     *
     * @param businessObjectClass
     * @param documentContent
     * @return
     */
    protected List<SearchableAttributeValue> parsePrimaryKeyValuesFromDocument(Class<? extends BusinessObject> businessObjectClass, MaintenanceDocument document) {
        List<SearchableAttributeValue> values = new ArrayList<SearchableAttributeValue>();

        final List primaryKeyNames = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(businessObjectClass);

        for (Object primaryKeyNameAsObj : primaryKeyNames) {
            final String primaryKeyName = (String)primaryKeyNameAsObj;
            final SearchableAttributeValue searchableValue = parseSearchableAttributeValueForPrimaryKey(primaryKeyName, businessObjectClass, document);
            if (searchableValue != null) {
                values.add(searchableValue);
            }
        }
        return values;
    }

    /**
     * Creates a searchable attribute value for the given property name out of the document XML
     * @param propertyName the name of the property to return
     * @param businessObjectClass the class of the business object maintained
     * @param document the document XML
     * @return a generated SearchableAttributeValue, or null if a value could not be created
     */
    protected SearchableAttributeValue parseSearchableAttributeValueForPrimaryKey(String propertyName, Class<? extends BusinessObject> businessObjectClass, MaintenanceDocument document) {

        Maintainable maintainable  = document.getNewMaintainableObject();
        PersistableBusinessObject bo = maintainable.getBusinessObject();

        final Object propertyValue = ObjectUtils.getPropertyValue(bo, propertyName);
        if (propertyValue == null) return null;

        final WorkflowAttributePropertyResolutionService propertyResolutionService = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
        SearchableAttributeValue value = propertyResolutionService.buildSearchableAttribute(businessObjectClass, propertyName, propertyValue);
        return value;
    }

    /**
     * Returns the class of the object being maintained by the given maintenance document type name
     * @param documentTypeName the name of the document type to look up the maintained business object for
     * @return the class of the maintained business object
     */
    protected Class<? extends BusinessObject> getBusinessObjectClass(String documentTypeName) {
        MaintenanceDocumentEntry entry = retrieveMaintenanceDocumentEntry(documentTypeName);
        return (entry == null ? null : (Class<? extends BusinessObject>) entry.getDataObjectClass());
    }

    /**
     * Returns the maintainable of the object being maintained by the given maintenance document type name
     * @param documentTypeName the name of the document type to look up the maintained business object for
     * @return the Maintainable of the maintained business object
     */
    protected Class<? extends Maintainable> getMaintainableClass(String documentTypeName) {
        MaintenanceDocumentEntry entry = retrieveMaintenanceDocumentEntry(documentTypeName);
        return (entry == null ? null : entry.getMaintainableClass());
    }


    /**
     * Retrieves the maintenance document entry for the given document type name
     * @param documentTypeName the document type name to look up the data dictionary document entry for
     * @return the corresponding data dictionary entry for a maintenance document
     */
    protected MaintenanceDocumentEntry retrieveMaintenanceDocumentEntry(String documentTypeName) {
        return (MaintenanceDocumentEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(documentTypeName);
    }

    /**
     *
     * @param documentNumber
     * @param businessObjectClass
     * @param document
     * @return
     */
    protected GlobalBusinessObject retrieveGlobalBusinessObject(String documentNumber, Class<? extends BusinessObject> businessObjectClass) {
        GlobalBusinessObject globalBO = null;

        Map pkMap = new LinkedHashMap();
        pkMap.put(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        List returnedBOs = (List) KRADServiceLocator.getBusinessObjectService().findMatching(businessObjectClass, pkMap);
        if (returnedBOs.size() > 0) {
            globalBO = (GlobalBusinessObject)returnedBOs.get(0);
        }

        return globalBO;
    }

    /**
     *
     * @param globalBO
     * @return
     */
    protected List<SearchableAttributeValue> findAllSearchableAttributesForGlobalBusinessObject(GlobalBusinessObject globalBO) {
        List<SearchableAttributeValue> searchValues = new ArrayList<SearchableAttributeValue>();

        for (PersistableBusinessObject bo : globalBO.generateGlobalChangesToPersist()) {
            SearchableAttributeValue value = generateSearchableAttributeFromChange(bo);
            if (value != null) {
                searchValues.add(value);
            }
        }

        return searchValues;
    }

    /**
     *
     * @param changeToPersist
     * @return
     */
    protected SearchableAttributeValue generateSearchableAttributeFromChange(PersistableBusinessObject changeToPersist) {
        List primaryKeyNames = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(changeToPersist.getClass());

        for (Object primaryKeyNameAsObject : primaryKeyNames) {
            String primaryKeyName = (String)primaryKeyNameAsObject;
            Object value = ObjectUtils.getPropertyValue(changeToPersist, primaryKeyName);

            if (value != null) {

                final WorkflowAttributePropertyResolutionService propertyResolutionService = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
                SearchableAttributeValue saValue = propertyResolutionService.buildSearchableAttribute(changeToPersist.getClass(), primaryKeyName, value);
                return saValue;

            }
        }
        return null;
    }

    /**
     * Creates a list of search fields, one for each primary key of the maintained business object
     * @param businessObjectClass the class of the maintained business object
     * @return a List of KEW search fields
     */
    protected List<Row> createFieldRowsForBusinessObject(Class<? extends BusinessObject> businessObjectClass) {
        List<Row> searchFields = new ArrayList<Row>();

        final List primaryKeyNamesAsObjects = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(businessObjectClass);
        final BusinessObjectEntry boEntry = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObjectClass.getName());
        final WorkflowAttributePropertyResolutionService propertyResolutionService = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService();
        for (Object primaryKeyNameAsObject : primaryKeyNamesAsObjects) {

            String attributeName =  (String)primaryKeyNameAsObject;
            BusinessObject businessObject = null;
            try {
                businessObject = businessObjectClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Field searchField = FieldUtils.getPropertyField(businessObjectClass, attributeName, false);
            String dataType = propertyResolutionService.determineFieldDataType(businessObjectClass, attributeName);
            searchField.setFieldDataType(dataType);
            List<Field> fieldList = new ArrayList<Field>();

            List displayedFieldNames = new ArrayList();
            displayedFieldNames.add(attributeName);
            LookupUtils.setFieldQuickfinder(businessObject, attributeName, searchField, displayedFieldNames);

            fieldList.add(searchField);
            searchFields.add(new Row(fieldList));
        }

        return searchFields;
    }

}
