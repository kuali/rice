package org.kuali.rice.kew.engine;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.doctype.ProcessDefinition;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.framework.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.postprocessor.DefaultPostProcessor;
import org.kuali.rice.kew.test.KEWTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        PostProcessor.clear();

        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), DOCUMENT_TYPE_NAME);
        document.route("test");
        assertNotNull(document.getDocumentId());
        assertTrue(document.isFinal());

        // verify that the PostProcessor invoked the proper status transitions
        // Initiated -> Enroute -> Processed -> Final
        assertEquals(3, PostProcessor.statusChanges.size());
        DocumentRouteStatusChange iToR = PostProcessor.statusChanges.get(0);
        DocumentRouteStatusChange rToP = PostProcessor.statusChanges.get(1);
        DocumentRouteStatusChange pToF = PostProcessor.statusChanges.get(2);
        assertEquals(DocumentStatus.INITIATED.getCode(), iToR.getOldRouteStatus());
        assertEquals(DocumentStatus.ENROUTE.getCode(), iToR.getNewRouteStatus());
        assertEquals(DocumentStatus.ENROUTE.getCode(), rToP.getOldRouteStatus());
        assertEquals(DocumentStatus.PROCESSED.getCode(), rToP.getNewRouteStatus());
        assertEquals(DocumentStatus.PROCESSED.getCode(), pToF.getOldRouteStatus());
        assertEquals(DocumentStatus.FINAL.getCode(), pToF.getNewRouteStatus());
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

    public static class PostProcessor extends DefaultPostProcessor {

        private static List<DocumentRouteStatusChange> statusChanges = new ArrayList<DocumentRouteStatusChange>();

        public static void clear() {
            statusChanges.clear();
        }

        @Override
        public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
            statusChanges.add(statusChangeEvent);
            return super.doRouteStatusChange(statusChangeEvent);
        }
    }

}