/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.engine;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

/**
 * Tests a new document being spawned from the post processing of an existing document
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PostProcessorSpawnedDocumentTest extends KEWTestCase {
	
	private static final String DOCUMENT_TYPE_THAT_SPAWNS = "SpawnNewDocumentType";

    protected void loadTestData() throws Exception {
        loadXmlFile("PostProcessorSpawnedDocConfig.xml");
    }

    @Test public void testSpawnDocument() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("jitrue"), DOCUMENT_TYPE_THAT_SPAWNS);
    	document.saveRoutingData();
    	assertNotNull(document.getRouteHeaderId());
    	assertTrue("Document should be initiatied", document.stateIsInitiated());
    	document.routeDocument("Route");

        // should have generated a request to "bmcgough"
    	document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Document should be enroute", document.stateIsEnroute());
        assertEquals("Document should be enroute.", EdenConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        assertTrue(document.isApprovalRequested());
        document.approve("Test approve by bmcgough");
        Long originalRouteHeaderId = document.getRouteHeaderId();
    	
    	// get spawned document (should be next document id)
    	document = new WorkflowDocument(new NetworkIdVO("jhopf"), Long.valueOf(originalRouteHeaderId.longValue() + 1));
        assertEquals("Document should be final.", EdenConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());

    	// get original document
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), originalRouteHeaderId);
        assertEquals("Document should be final.", EdenConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());
    }
}
