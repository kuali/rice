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
package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.doctype.ApplicationDocumentStatus;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCriteriaConfiguration;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class adapts the RemotableAttributeField instances from the various attributes
 * associated with a document type and combines with the "default" rows for the search,
 * returning the final List of Row objects to render for the document search.
 *
 * <p>Implementation note:</p>
 * <p>
 * This implementation relies on applicationDocumentStatus, and dateApplicationDocumentStatusChanged conditional fields
 * being defined in the DD for basic display purposes.  These fields are conditionally shown depending on whether
 * a document supporting application document statuses has been specified.  Further, the applicationDocumentStatus field
 * is dynamically switched to a dropdown when the document specifies an explicit enumeration of valid statuses (this
 * control switching is something that is not possible via declarative DD, at the time of this writing).
 * </p>
 * <p>
 * In addition the routeNodeName field is dynamically populated with the list of route nodes for the specified document
 * type.
 * <p>
 * Note: an alternative to programmatically providing dynamic select values is to define a value finder declaratively in
 * DD.  KeyValueFinder however does not have access to request state, including the required document type, which would mean
 * resorting to GlobalVariables inspection.  In reluctance to add yet another dependency on this external state, the fixups
 * are done programmatically in this class. (see {@link #applyApplicationDocumentStatusCustomizations(org.kuali.rice.kns.web.ui.Field, org.kuali.rice.kew.doctype.bo.DocumentType)},
 * {@link #applyRouteNodeNameCustomizations(org.kuali.rice.kns.web.ui.Field, org.kuali.rice.kew.doctype.bo.DocumentType)}).
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentSearchCriteriaProcessorKEWAdapter implements DocumentSearchCriteriaProcessor {
    /**
     * Name if the hidden input field containing basic/detailed search toggle state
     */
    public static final String ADVANCED_SEARCH_FIELD = "isAdvancedSearch";
    /**
     * Name if the hidden input field containing non-superuser/superuser search toggle state
     */
    public static final String SUPERUSER_SEARCH_FIELD = "superUserSearch";
    /**
     * Name if the hidden input field containing the clear saved search flag
     */
    public static final String CLEARSAVED_SEARCH_FIELD = "resetSavedSearch";

    /**
     * Indicates where document attributes should be placed inside search criteria
     */
    private static final String DOCUMENT_ATTRIBUTE_FIELD_MARKER = "DOCUMENT_ATTRIBUTE_FIELD_MARKER";

    private static final String APPLICATION_DOCUMENT_STATUS = "applicationDocumentStatus";
    private static final String DATE_APP_DOC_STATUS_CHANGED_FROM = "rangeLowerBoundKeyPrefix_dateApplicationDocumentStatusChanged";
    private static final String DATE_APP_DOC_STATUS_CHANGED = "dateApplicationDocumentStatusChanged";
    private static final String ROUTE_NODE_NAME = "routeNodeName";
    private static final String ROUTE_NODE_LOGIC = "routeNodeLogic";

    private static final String[] BASIC_FIELD_NAMES = {
            "documentTypeName",
            "initiatorPrincipalName",
            "documentId",
            "groupViewerId",
            "dateCreated",
            DOCUMENT_ATTRIBUTE_FIELD_MARKER,
            "saveName"
    };

    private static final String[] ADVANCED_FIELD_NAMES = {
            "documentTypeName",
            "initiatorPrincipalName",
            "approverPrincipalName",
            "viewerPrincipalName",
            "groupViewerName",
            "groupViewerId",
            "documentId",
            "applicationDocumentId",
            "statusCode",
            APPLICATION_DOCUMENT_STATUS,
            DATE_APP_DOC_STATUS_CHANGED,
            ROUTE_NODE_NAME,
            ROUTE_NODE_LOGIC,
            "dateCreated",
            "dateApproved",
            "dateLastModified",
            "dateFinalized",
            "title",
            DOCUMENT_ATTRIBUTE_FIELD_MARKER,
            "saveName"
    };

    /**
     * Fields that are only applicable if a document type has been specified
     */
    private static final Collection<String> DOCUMENTTYPE_DEPENDENT_FIELDS = Arrays.asList(new String[] { DOCUMENT_ATTRIBUTE_FIELD_MARKER, APPLICATION_DOCUMENT_STATUS, DATE_APP_DOC_STATUS_CHANGED_FROM, DATE_APP_DOC_STATUS_CHANGED, ROUTE_NODE_NAME, ROUTE_NODE_LOGIC });
    /**
     * Fields that are only applicable if application document status is in use (assumes documenttype dependency)
     */
    private static final Collection<String> DOCSTATUS_DEPENDENT_FIELDS = Arrays.asList(new String[] { APPLICATION_DOCUMENT_STATUS, DATE_APP_DOC_STATUS_CHANGED_FROM, DATE_APP_DOC_STATUS_CHANGED });


    @Override
    public List<Row> getRows(DocumentType documentType, List<Row> defaultRows, boolean advancedSearch, boolean superUserSearch) {
        List<Row> rows = null;
        if(advancedSearch) {
            rows = loadRowsForAdvancedSearch(defaultRows, documentType);
        } else {
            rows = loadRowsForBasicSearch(defaultRows, documentType);
        }
        addHiddenFields(rows, advancedSearch, superUserSearch);
        return rows;
    }

    protected List<Row> loadRowsForAdvancedSearch(List<Row> defaultRows, DocumentType documentType) {
        List<Row> rows = new ArrayList<Row>();
        loadRowsWithFields(rows, defaultRows, ADVANCED_FIELD_NAMES, documentType);
        return rows;
    }

    protected List<Row> loadRowsForBasicSearch(List<Row> defaultRows, DocumentType documentType) {
        List<Row> rows = new ArrayList<Row>();
        loadRowsWithFields(rows, defaultRows, BASIC_FIELD_NAMES, documentType);
        return rows;
    }

    /**
     * Generates the document search form fields given the DataDictionary-defined fields, the DocumentType,
     * and whether basic, detailed, or superuser search is being rendered.
     * If the document type policy DOCUMENT_STATUS_POLICY is set to "app", or "both"
     * Then display the doc search criteria fields.
     * If the documentType.validApplicationStatuses are defined, then the criteria field is a drop down.
     * If the validApplication statuses are NOT defined, then the criteria field is a text input.
     * @param rowsToLoad the list of rows to update
     * @param defaultRows the DataDictionary-derived default form rows
     * @param fieldNames a list of field names corresponding to the fields to render according to the current document search state
     * @param documentType the document type, if specified in the search form
     */
    protected void loadRowsWithFields(List<Row> rowsToLoad, List<Row> defaultRows, String[] fieldNames,
            DocumentType documentType) {

        for (String fieldName : fieldNames) {
            if (DOCUMENTTYPE_DEPENDENT_FIELDS.contains(fieldName) && documentType == null) {
                continue;
            }
            // assuming DOCSTATUS_DEPENDENT_FIELDS are also documentType-dependent, this block is only executed when documentType is present
            if (DOCSTATUS_DEPENDENT_FIELDS.contains(fieldName) && !documentType.isAppDocStatusInUse()) {
                continue;
            }
            if (fieldName.equals(DOCUMENT_ATTRIBUTE_FIELD_MARKER)) {
                rowsToLoad.addAll(getDocumentAttributeRows(documentType));
                continue;
            }
            // now add all matching rows given
            // 1) the field is doc type and doc status independent
            // 2) the field is doc type dependent and the doctype is specified
            // 3) the field is doc status dependent and the doctype is specified and doc status is in use
            for (Row row : defaultRows) {
                boolean matched = false;
                // we must iterate over each field without short-circuiting to make sure to inspect the
                // APPLICATION_DOCUMENT_STATUS field, which needs customizations
                for (Field field : row.getFields()) {
                    // dp "endsWith" here because lower bounds properties come
                    // across like "rangeLowerBoundKeyPrefix_dateCreated"
                    if (field.getPropertyName().equals(fieldName) || field.getPropertyName().endsWith("_" + fieldName)) {
                        matched = true;
                        if (APPLICATION_DOCUMENT_STATUS.equals(field.getPropertyName())) {
                            // If Application Document Status policy is in effect for this document type,
                            // add search attributes for document status, and transition dates.
                            // Note: document status field is a drop down if valid statuses are defined, a text input field otherwise.
                            applyApplicationDocumentStatusCustomizations(field, documentType);
                            break;
                        } else if (ROUTE_NODE_NAME.equals(field.getPropertyName())) {
                            // populates routenodename dropdown with documenttype nodes
                            applyRouteNodeNameCustomizations(field, documentType);
                        }
                    }
                }
                if (matched) {
                    rowsToLoad.add(row);
                }
            }
        }
    }

    /**
     * Returns fields for the search attributes defined on the document
     */
    protected List<Row> getDocumentAttributeRows(DocumentType documentType) {
        List<Row> documentAttributeRows = new ArrayList<Row>();
        DocumentSearchCriteriaConfiguration configuration =
                KEWServiceLocator.getDocumentSearchCustomizationMediator().
                        getDocumentSearchCriteriaConfiguration(documentType);
        if (configuration != null) {
            List<RemotableAttributeField> remotableAttributeFields = configuration.getFlattenedSearchAttributeFields();
            if (remotableAttributeFields != null && !remotableAttributeFields.isEmpty()) {
                documentAttributeRows.addAll(FieldUtils.convertRemotableAttributeFields(remotableAttributeFields));
            }
        }
        List<Row> fixedDocumentAttributeRows = new ArrayList<Row>();
        for (Row row : documentAttributeRows) {
            List<Field> fields = row.getFields();
            for (Field field : fields) {
                //force the max length for now if not set
                if(field.getMaxLength() == 0) {
                    field.setMaxLength(100);
                }
                if(field.isDatePicker() && field.isRanged()) {
                    Field newDate = FieldUtils.createRangeDateField(field);
                    List<Field> newFields = new ArrayList<Field>();
                    newFields.add(newDate);
                    fixedDocumentAttributeRows.addAll(FieldUtils.wrapFields(newFields));
                }
                // prepend all document attribute field names with "documentAttribute."
                field.setPropertyName(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + field.getPropertyName());
                if (StringUtils.isNotBlank(field.getLookupParameters())) {
                    field.setLookupParameters(prefixLookupParameters(field.getLookupParameters()));
                }
                if (StringUtils.isNotBlank(field.getFieldConversions())) {
                    field.setFieldConversions(prefixFieldConversions(field.getFieldConversions()));
                }
            }
            fixedDocumentAttributeRows.add(row);
        }

        return fixedDocumentAttributeRows;
    }

    /**
     * Modifies the DataDictionary-defined applicationDocumentStatus field control to reflect whether the DocumentType
     * has specified a list of valid application document statuses (in which case a select control is rendered), or whether
     * it is free form (in which case a text control is rendered)
     *
     * @param field the applicationDocumentStatus field
     * @param documentType the document type
     */
    protected void applyApplicationDocumentStatusCustomizations(Field field, DocumentType documentType) {
        List<ApplicationDocumentStatus> validStatuses = documentType.getValidApplicationStatuses();
        if (validStatuses == null || validStatuses.size() == 0){
            // use a text input field
            // StandardSearchCriteriaField(String fieldKey, String propertyName, String fieldType, String datePickerKey, String labelMessageKey, String helpMessageKeyArgument, boolean hidden, String displayOnlyPropertyName, String lookupableImplServiceName, boolean lookupTypeRequired)
            // new StandardSearchCriteriaField(DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS,"criteria.appDocStatus",StandardSearchCriteriaField.TEXT,null,null,"DocSearchApplicationDocStatus",false,null,null,false));
            // String fieldKey DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS
            field.setFieldType(Field.TEXT);
        } else {
            // dropdown
            // String fieldKey DocumentSearchCriteriaProcessor.CRITERIA_KEY_APP_DOC_STATUS + "_VALUES"
            field.setFieldType(Field.DROPDOWN);
            List<KeyValue> validValues = new ArrayList<KeyValue>();
            for (ApplicationDocumentStatus status: validStatuses) {
                validValues.add(new ConcreteKeyValue(status.getStatusName(), status.getStatusName()));
            }
            field.setFieldValidValues(validValues);
            //dropDown.setOptionsCollectionProperty("validApplicationStatuses");
            //dropDown.setCollectionKeyProperty("statusName");
            //dropDown.setCollectionLabelProperty("statusName");
            //dropDown.setEmptyCollectionMessage("Select a document status.");
        }
    }

    protected void applyRouteNodeNameCustomizations(Field field, DocumentType documentType) {
        List<RouteNode> nodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(documentType, true);
        List<KeyValue> values = new ArrayList<KeyValue>(nodes.size());
        for (RouteNode node: nodes) {
            values.add(new ConcreteKeyValue(node.getName(), node.getName()));
        }
        field.setFieldValidValues(values);
    }

    protected void addHiddenFields(List<Row> rows, boolean advancedSearch, boolean superUserSearch) {
        Row hiddenRow = new Row();
        hiddenRow.setHidden(true);

        Field detailedField = new Field();
        detailedField.setPropertyName(ADVANCED_SEARCH_FIELD);
        detailedField.setPropertyValue(advancedSearch ? "YES" : "NO");
        detailedField.setFieldType(Field.HIDDEN);

        Field superUserSearchField = new Field();
        superUserSearchField.setPropertyName(SUPERUSER_SEARCH_FIELD);
        superUserSearchField.setPropertyValue(superUserSearch ? "YES" : "NO");
        superUserSearchField.setFieldType(Field.HIDDEN);

        Field clearSavedSearchField = new Field();
        clearSavedSearchField .setPropertyName(CLEARSAVED_SEARCH_FIELD);
        clearSavedSearchField .setPropertyValue(superUserSearch ? "YES" : "NO");
        clearSavedSearchField .setFieldType(Field.HIDDEN);

        List<Field> hiddenFields = new ArrayList<Field>();
        hiddenFields.add(detailedField);
        hiddenFields.add(superUserSearchField);
        hiddenFields.add(clearSavedSearchField);
        hiddenRow.setFields(hiddenFields);
        rows.add(hiddenRow);

    }
    
    private String prefixLookupParameters(String lookupParameters) {
        StringBuilder newLookupParameters = new StringBuilder(KRADConstants.EMPTY_STRING);
        String[] conversions = StringUtils.split(lookupParameters, KRADConstants.FIELD_CONVERSIONS_SEPARATOR);

        for (int m = 0; m < conversions.length; m++) {
            String conversion = conversions[m];
            String[] conversionPair = StringUtils.split(conversion, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);
            String conversionFrom = conversionPair[0];
            String conversionTo = conversionPair[1];
            conversionFrom = KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + conversionFrom;
            newLookupParameters.append(conversionFrom)
                    .append(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR)
                    .append(conversionTo);

            if (m < conversions.length) {
                newLookupParameters.append(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            }
        }
        return newLookupParameters.toString();
    }
    
    private String prefixFieldConversions(String fieldConversions) {
        StringBuilder newFieldConversions = new StringBuilder(KRADConstants.EMPTY_STRING);
        String[] conversions = StringUtils.split(fieldConversions, KRADConstants.FIELD_CONVERSIONS_SEPARATOR);

        for (int l = 0; l < conversions.length; l++) {
            String conversion = conversions[l];
            //String[] conversionPair = StringUtils.split(conversion, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR);
            String[] conversionPair = StringUtils.split(conversion, KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR, 2);
            String conversionFrom = conversionPair[0];
            String conversionTo = conversionPair[1];
            conversionTo = KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + conversionTo;
            newFieldConversions.append(conversionFrom)
                    .append(KRADConstants.FIELD_CONVERSION_PAIR_SEPARATOR)
                    .append(conversionTo);

            if (l < conversions.length) {
                newFieldConversions.append(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            }
        }

        return newFieldConversions.toString();
    }

}
