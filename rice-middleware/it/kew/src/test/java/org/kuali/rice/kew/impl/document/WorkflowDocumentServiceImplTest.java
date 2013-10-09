package org.kuali.rice.kew.impl.document;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkflowDocumentServiceImplTest extends KEWTestCase {

    private WorkflowDocumentService workflowDocumentService;

    @Before
    public void setUpService() {
        this.workflowDocumentService = KewApiServiceLocator.getWorkflowDocumentService();
    }

    @Test
    public void testGetDocumentTypeName() {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("admin"), "TestDocumentType");
        assertNotNull(document.getDocumentId());
        assertEquals("TestDocumentType", document.getDocumentTypeName());
        assertEquals("TestDocumentType", workflowDocumentService.getDocumentTypeName(document.getDocumentId()));
    }

}
