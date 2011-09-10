/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCriteriaConfiguration;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.service.DataDictionaryService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class adapts the RemotableAttributeField instances from the various attributes
 * associated with a document type and combines with the "default" rows for the search,
 * returning the final List of Row objects to render for the document search.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentLookupCriteriaProcessorKEWAdapter implements DocumentLookupCriteriaProcessor {

    /**
     * Indicates where document attributes should be placed inside search criteria
     */
    private static final String DOCUMENT_ATTRIBUTE_FIELD_MARKER = "DOCUMENT_ATTRIBUTE_FIELD_MARKER";

    private static final String APPLICATION_DOCUMENT_STATUS_CODE = "applicationDocumentStatusCode";
    private static final String ROUTE_NODE_NAME = "routeNodeName";
    private static final String ROUTE_NODE_LOGIC = "routeNodeLogic";

    private static final String[] BASIC_FIELD_NAMES = {
            "documentTypeName",
            "initiatorPrincipalName",
            "documentId",
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
            APPLICATION_DOCUMENT_STATUS_CODE,
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

	private DataDictionaryService dataDictionaryService;

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

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

    protected void loadRowsWithFields(List<Row> rowsToLoad, List<Row> defaultRows, String[] fieldNames,
            DocumentType documentType) {
        for (String fieldName : fieldNames) {
            if (fieldName.equals(DOCUMENT_ATTRIBUTE_FIELD_MARKER) && documentType != null) {
                rowsToLoad.addAll(getDocumentAttributeRows(documentType));
            }
            else {
                for (Row defaultRow : defaultRows) {
                    for (Field defaultField : defaultRow.getFields()) {
                        // dp "endsWith" here because lower bounds properties come
                        // across like "rangeLowerBoundKeyPrefix_dateCreated"
                        if (defaultField.getPropertyName().endsWith(fieldName)) {
                            // don't show the following fields if there is no document type
                            if (fieldName.equals(APPLICATION_DOCUMENT_STATUS_CODE) ||
                                    fieldName.equals(ROUTE_NODE_NAME) ||
                                    fieldName.equals(ROUTE_NODE_LOGIC)) {
                                if (documentType == null) {
                                    continue;
                                }
                            }
                            rowsToLoad.add(defaultRow);
                        }
                    }
                }
            }
        }
    }

    protected List<Row> getDocumentAttributeRows(DocumentType documentType) {
        List<Row> documentAttributeRows = new ArrayList<Row>();
        DocumentLookupCriteriaConfiguration configuration =
                KEWServiceLocator.getDocumentLookupCustomizationMediator().
                        getDocumentLookupCriteriaConfiguration(documentType);
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
                field.setPropertyName(KEWConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + field.getPropertyName());
			}
            fixedDocumentAttributeRows.add(row);
        }

		// TODO - Rice 2.0 - need to add back in the building of application document status row, commented out for now because this code is weird!
		// If Application Document Status policy is in effect for this document type,
		// add search attributes for document status, and transition dates.
		// Note: document status field is a drop down if valid statuses are defined,
		//       a text input field otherwise.
		// fixedDocumentAttributeRows.addAll( buildAppDocStatusRows(documentType) );

		return fixedDocumentAttributeRows;
	}

    protected void addHiddenFields(List<Row> rows, boolean advancedSearch, boolean superUserSearch) {
		Row hiddenRow = new Row();
		hiddenRow.setHidden(true);

		Field detailedField = new Field();
		detailedField.setPropertyName("isAdvancedSearch");
		detailedField.setPropertyValue(advancedSearch ? "YES" : "NO");
		detailedField.setFieldType(Field.HIDDEN);

		Field superUserSearchField = new Field();
		superUserSearchField.setPropertyName("superUserSearch");
		superUserSearchField.setPropertyValue(superUserSearch ? "YES" : "NO");
		superUserSearchField.setFieldType(Field.HIDDEN);
        
		List<Field> hiddenFields = new ArrayList<Field>();
		hiddenFields.add(detailedField);
		hiddenFields.add(superUserSearchField);
		hiddenRow.setFields(hiddenFields);
		rows.add(hiddenRow);

    }

}
