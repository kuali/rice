/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.impl.document.search

import org.junit.Test
import org.junit.Before
import org.kuali.rice.kew.docsearch.service.impl.DocumentSearchServiceImpl

import org.kuali.rice.kew.api.KEWPropertyConstants;
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals

import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria
import org.kuali.rice.kew.doctype.bo.DocumentType
import org.kuali.rice.kns.web.ui.Row
import org.kuali.rice.kew.docsearch.DocumentSearchCriteriaProcessor

/**
 * Tests parsing of document search criteria form
 */
class DocumentSearchCriteriaBoLookupableHelperServiceTest {
    def lookupableHelperService = new DocumentSearchCriteriaBoLookupableHelperService()

    @Before
    void init() {
        lookupableHelperService.setDocumentSearchService(new DocumentSearchServiceImpl() {
            @Override // stub this out
            DocumentSearchCriteria getSavedSearchCriteria(String principalId, String searchName) {
                return null
            }
        });
        lookupableHelperService.setDocumentSearchCriteriaTranslator(new DocumentSearchCriteriaTranslatorImpl())
    }

    /**
     * Tests that the doc statuses selected on the document search form are properly parsed into
     * the DocumentSearchCriteria
     */
    @Test
    void testLoadCriteriaDocStatuses() {
        // form fields
        def fields = new HashMap<String, String>()
        // parameters not captured by form fields (?)
        def params = new HashMap<String, String[]>()
        params.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                   [ DocumentStatus.INITIATED.code,
                     DocumentStatus.PROCESSED.code,
                     DocumentStatus.FINAL.code,
                     "category:" + DocumentStatusCategory.SUCCESSFUL.getCode(),
                     "category:" + DocumentStatusCategory.UNSUCCESSFUL.getCode()] as String[])

        lookupableHelperService.setParameters(params)
        def crit = lookupableHelperService.loadCriteria(fields)
        assertNotNull(crit)

        assertEquals([ DocumentStatus.INITIATED, DocumentStatus.PROCESSED, DocumentStatus.FINAL ], crit.getDocumentStatuses())
        assertEquals([ DocumentStatusCategory.SUCCESSFUL, DocumentStatusCategory.UNSUCCESSFUL ], crit.getDocumentStatusCategories())
    }

    @Test
    void testCheckForAdditionalFieldsSetsRows() {
        def DOC_TYPE = "DOC TYPE"
        def setRowsCalledWith = ""
        new DocumentSearchCriteriaBoLookupableHelperService() {
            protected void setRows(String doctype) {
                setRowsCalledWith = doctype
            }
        }.checkForAdditionalFields([documentTypeName: DOC_TYPE])
        assertEquals("checkForAdditionalFields did not initialize rows for document type argument: $DOC_TYPE", DOC_TYPE, setRowsCalledWith)
    }
}