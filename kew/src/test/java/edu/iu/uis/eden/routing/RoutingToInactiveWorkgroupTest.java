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
package edu.iu.uis.eden.routing;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.test.TestUtilities;

public class RoutingToInactiveWorkgroupTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("RoutingConfig.xml");
    }
    
    @Test public void testRoutingToInactiveWorkgroup() throws Exception {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), "InactiveWorkgroupDocType");
        try {
            doc.routeDocument("");
            fail("document should have thrown routing exception");
        } catch (Exception e) {
            
        }
        TestUtilities.getExceptionThreader().join();//wait for doc to go into exception routing
        doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), doc.getRouteHeaderId());
        assertTrue("Document should be in exception routing because workgroup is inactive", doc.stateIsException());

        try {
            doc.routeDocument("routing a document that is in exception routing");
            fail("Succeeded in routing document that is in exception routing");
        } catch (InvalidActionTakenException iate) {
            log.info("Expected exception occurred: " + iate);
        } catch (WorkflowException we) {
            fail("Attempt at routing document in exception routing succeeded, when it should have been an InvalidActionTakenException");
        }
    }
}