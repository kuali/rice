/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.clientapp;

import org.junit.Test;
import org.kuali.workflow.test.WorkflowTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Full Name (email at address dot com)
 *
 */
public class WorkflowInfoTest extends WorkflowTestCase {

    /**
     * Tests the loading of a RouteHeaderVO using the WorkflowInfo.
     *
     * Verifies that an NPE no longer occurrs as mentioned in KULRICE-765.
     */
    @Test
    public void testGetRouteHeader() throws Exception {
	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "TestDocumentType");
	Long documentId = document.getRouteHeaderId();
	assertNotNull(documentId);

	RouteHeaderVO routeHeaderVO = new WorkflowInfo().getRouteHeader(documentId);
	assertNotNull(routeHeaderVO);

	assertEquals(documentId, routeHeaderVO.getRouteHeaderId());
	assertEquals(EdenConstants.ROUTE_HEADER_INITIATED_CD, routeHeaderVO.getDocRouteStatus());
    }

}
