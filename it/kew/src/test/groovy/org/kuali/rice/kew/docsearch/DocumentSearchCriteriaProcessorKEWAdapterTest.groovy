/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.docsearch

import org.junit.Test
import org.kuali.rice.kew.impl.document.search.DocumentSearchCriteriaBo
import org.kuali.rice.kew.service.KEWServiceLocator
import org.kuali.rice.kew.test.KEWTestCase
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl
import org.kuali.rice.kns.lookup.LookupableHelperService
import org.kuali.rice.kns.web.ui.Row
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import org.junit.Ignore

/**
 * Tests the DocumentSearchCriteriaProcessorKEWAdapter
 */
class DocumentSearchCriteriaProcessorKEWAdapterTest extends KEWTestCase {
     private static def BASIC_FIELD_NAMES = [
            "documentTypeName",
            "initiatorPrincipalName",
            "documentId",
            "dateCreated",
            "saveName"
     ]

     private static def ADVANCED_FIELD_NAMES = [
            "documentTypeName",
            "initiatorPrincipalName",
            "approverPrincipalName",
            "viewerPrincipalName",
            "groupViewerName",
            "groupViewerId",
            "documentId",
            "applicationDocumentId",
            "statusCode",
            "applicationDocumentStatusCode",
            "routeNodeName",
            "routeNodeLogic",
            "dateCreated",
            "dateApproved",
            "dateLastModified",
            "dateFinalized",
            "title",
            "saveName"
    ]

    private static def DEFAULT_ROW_VALUES = [
        [ propertyName: "documentTypeName", fieldLabel: "Document Type", fieldType: "text" ],
        [ propertyName: "initiatorPrincipalName", fieldLabel: "Initiator", fieldType: "text" ],
        [ propertyName: "approverPrincipalName", fieldLabel: "Approver", fieldType: "text" ],
        [ propertyName: "viewerPrincipalName", fieldLabel: "Viewer", fieldType: "text" ],
        [ propertyName: "groupViewerName", fieldLabel: "Group Viewer", fieldType: "text" ],
        [ propertyName: "documentId", fieldLabel: "Document Id", fieldType: "text" ],
        [ propertyName: "applicationDocumentId", fieldLabel: "Application Document Id", fieldType: "text" ],
        [ propertyName: "statusCode", fieldLabel: "Document Status", fieldType: "multiselect" ],
        [ propertyName: "applicationDocumentStatus", fieldLabel: "Application Document Status", fieldType: "text" ],
        [ propertyName: "routeNodeName", fieldLabel: "Route Node", fieldType: "text" ],
        [ propertyName: "routeNodeLogic", fieldLabel: "Route Node Logic", fieldType: "text" ],
        [ propertyName: "rangeLowerBoundKeyPrefix_dateCreated", fieldLabel: "Date Created From", fieldType: "text" ],
        [ propertyName: "dateCreated", fieldLabel: "Date Created To", fieldType: "text" ],
        [ propertyName: "rangeLowerBoundKeyPrefix_dateApproved", fieldLabel: "Date Approved From", fieldType: "text" ],
        [ propertyName: "dateApproved", fieldLabel: "Date Approved To", fieldType: "text" ],
        [ propertyName: "rangeLowerBoundKeyPrefix_dateLastModified", fieldLabel: "Date Last Modified From", fieldType: "text" ],
        [ propertyName: "dateLastModified", fieldLabel: "Date Last Modified To", fieldType: "text" ],
        [ propertyName: "rangeLowerBoundKeyPrefix_dateFinalized", fieldLabel: "Date Finalized From", fieldType: "text" ],
        [ propertyName: "dateFinalized", fieldLabel: "Date Finalized To", fieldType: "text" ],
        [ propertyName: "title", fieldLabel: "Title", fieldType: "text" ],
        [ propertyName: "saveName", fieldLabel: "Name this search (optional)", fieldType: "text" ],
        [ propertyName: "groupViewerId", fieldLabel: "Group Viewer Id", fieldType: "hidden" ]
    ]

    private static def SEARCHABLE_FIELD_NAMES = [
        "documentAttribute.givenname", "documentAttribute.testLongKey", "documentAttribute.testFloatKey", "documentAttribute.testDateTimeKey",
        "rangeLowerBoundKeyPrefix_dateCreated",
        "isAdvancedSearch",
        "resetSavedSearch",
        "superUserSearch"
        // TODO - KULRICE-5635 - need to add back in the building of application document status row
    ]

    @Override
    protected void loadTestData() throws Exception {
        loadXmlFile("SearchAttributeConfig.xml");
    }

    protected List<Row> getDefaultRows() {
        LookupableHelperService lhs = new KualiLookupableHelperServiceImpl()
        lhs.setBusinessObjectClass(DocumentSearchCriteriaBo.class)
        lhs.getRows()
    }

    /* this test should probably go in a KualiLookupableHelperService(Impl) integration test */
    @Test
    void testDocumentSearchCriteriaDefaultRows() {
        // simulate helper service initialization from lookup form
        def rows = getDefaultRows()
        displayRows(rows)
        assertRows(rows, DEFAULT_ROW_VALUES)
    }

    @Test
    void testBasicRows() {
        DocumentSearchCriteriaProcessor dscp = new DocumentSearchCriteriaProcessorKEWAdapter()
        def defaultRows = getDefaultRows()
        def dscpRows = dscp.getRows(KEWServiceLocator.getDocumentTypeService().findByName("SearchDocType"), defaultRows, false, false)
        displayRows(dscpRows)
        assertRowPresence(dscpRows, BASIC_FIELD_NAMES + SEARCHABLE_FIELD_NAMES)
    }

    @Test @Ignore("WIP")
    void testAdvancedRows() {
        DocumentSearchCriteriaProcessor dscp = new DocumentSearchCriteriaProcessorKEWAdapter()
        def defaultRows = getDefaultRows()
        def dscpRows = dscp.getRows(KEWServiceLocator.getDocumentTypeService().findByName("SearchDocType"), defaultRows, true, false)
        displayRows(dscpRows)
        assertRowPresence(dscpRows, ADVANCED_FIELD_NAMES + SEARCHABLE_FIELD_NAMES)
    }
    
    protected assertRowPresence(List<Row> row, List<String> names) {
        def row_fields = row.collect { it.fields }.flatten().collect { it.propertyName }
        assertEquals(names.toSet(), row_fields.toSet())
    }

    protected assertRows(List<Row> rows, test_values) {
        rows.eachWithIndex {
            it, i ->
                assertEquals(it.fields[0].propertyName, test_values[i]["propertyName"])
                assertEquals(it.fields[0].fieldLabel, test_values[i]["fieldLabel"])
                assertEquals(it.fields[0].fieldType, test_values[i]["fieldType"])
        }
    }

    protected displayRows(List<Row> rows) {
        rows.each {
            it.fields.each {
                println it.propertyName
                println it.fieldLabel
                println it.fieldType
            }
        }
    }
}
