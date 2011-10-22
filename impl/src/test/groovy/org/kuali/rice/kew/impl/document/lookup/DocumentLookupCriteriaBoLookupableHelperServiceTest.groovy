package org.kuali.rice.kew.impl.document.lookup

import org.junit.Test
import org.junit.Before
import org.kuali.rice.kew.docsearch.service.impl.DocumentSearchServiceImpl

import org.kuali.rice.kew.api.KEWPropertyConstants;
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals

import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria

/**
 * Tests parsing of document search criteria form
 */
class DocumentLookupCriteriaBoLookupableHelperServiceTest {
    def lookupableHelperService = new DocumentLookupCriteriaBoLookupableHelperService()

    @Before
    void init() {
        lookupableHelperService.setDocumentSearchService(new DocumentSearchServiceImpl() {
            @Override // stub this out
            DocumentSearchCriteria getSavedSearchCriteria(String principalId, String searchName) {
                return null
            }
        });
        lookupableHelperService.setDocumentLookupCriteriaTranslator(new DocumentLookupCriteriaTranslatorImpl())
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
}
