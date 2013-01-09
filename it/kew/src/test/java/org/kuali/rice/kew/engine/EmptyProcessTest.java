package org.kuali.rice.kew.engine;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.doctype.ProcessDefinition;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test cases for documents with empty processes
 */
public class EmptyProcessTest extends KEWTestCase {

    private static final String DOCUMENT_TYPE_NAME = "EmptyProcessDocType";

    protected void loadTestData() throws Exception {
        loadXmlFile("EngineConfig.xml");
    }

    /**
     * creates a new doc of the given type and routes it, asserting that it goes final
     * @throws Exception
     */
    @Test public void testEmptyProcess() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), DOCUMENT_TYPE_NAME);
        document.route("test");
        assertNotNull(document.getDocumentId());
        assertTrue(document.isFinal());
    }

    /**
     * Tests scenario for KULRICE-7235: Document Type use of Null routePaths causes IllegalArgumentException:
     * contract was null
     * @throws Exception
     */
    @Test public void testGetRoutePathForDocumentTypeName() throws Exception {
        RoutePath routePath =
                KewApiServiceLocator.getDocumentTypeService().getRoutePathForDocumentTypeName(DOCUMENT_TYPE_NAME);
        assertNotNull(routePath);
        ProcessDefinition processDefinition = routePath.getPrimaryProcess();
        assertNotNull(processDefinition);
        assertNull("The initial route node *should* be null since this is an empty process", processDefinition.getInitialRouteNode());
    }

}