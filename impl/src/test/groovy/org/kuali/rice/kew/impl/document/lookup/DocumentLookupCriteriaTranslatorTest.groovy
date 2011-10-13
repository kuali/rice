package org.kuali.rice.kew.impl.document.lookup

import org.junit.Test
import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals

/**
 *
 */
class DocumentLookupCriteriaTranslatorTest {
    def documentLookupCriteriaTranslator = new DocumentLookupCriteriaTranslatorImpl()

    /**
     * Tests that the doc statuses selected on the document search form are properly parsed into
     * the DocumentLookupCriteria
     */
    @Test
    void testLoadCriteriaDocStatuses() {
        // form fields
        def fields = new HashMap<String, String>()
        fields.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                   [ DocumentStatus.INITIATED.code,
                     DocumentStatus.PROCESSED.code,
                     DocumentStatus.FINAL.code,
                     "category:" + DocumentStatusCategory.SUCCESSFUL.getCode(),
                     "category:" + DocumentStatusCategory.UNSUCCESSFUL.getCode()].join(','))

        def crit = documentLookupCriteriaTranslator.translateFieldsToCriteria(fields)
        assertNotNull(crit)

        assertEquals([ DocumentStatus.INITIATED, DocumentStatus.PROCESSED, DocumentStatus.FINAL ], crit.getDocumentStatuses())
        assertEquals([ DocumentStatusCategory.SUCCESSFUL, DocumentStatusCategory.UNSUCCESSFUL ], crit.getDocumentStatusCategories())
    }
}
