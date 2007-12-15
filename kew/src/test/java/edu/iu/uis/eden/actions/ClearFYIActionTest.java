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
package edu.iu.uis.eden.actions;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;

/**
 *
 * @author delyea
 */
public class ClearFYIActionTest extends KEWTestCase {
    
    private String getSavedStatusDisplayValue() {
        return (String) EdenConstants.DOCUMENT_STATUSES.get(EdenConstants.ROUTE_HEADER_SAVED_CD);
    }
    
    @Test public void testSavedDocumentAdhocRequest() throws Exception {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        doc.saveDocument("");
        UserIdVO user = new NetworkIdVO("dewey");
        doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, "annotation1", user, "respDesc1", false);
        doc = new WorkflowDocument(user, doc.getRouteHeaderId());
        assertTrue("FYI should be requested of user " + user, doc.isFYIRequested());
        try {
            doc.clearFYI();
        } catch (Exception e) {
            fail("A non-initator with an FYI request should be allowed to take the FYI action on a " + getSavedStatusDisplayValue() + " document");
        }
        assertTrue("Document should be " + getSavedStatusDisplayValue(), doc.stateIsSaved());
        
        WorkgroupIdVO workgroup = new WorkgroupNameIdVO("NonSIT");
        UserIdVO workgroupUser = new NetworkIdVO("dewey");
        doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        doc.saveDocument("");
        doc.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_FYI_REQ, "annotation1", workgroup, "respDesc1", false);
        doc = new WorkflowDocument(workgroupUser, doc.getRouteHeaderId());
        assertTrue("FYI should be requested of user " + workgroupUser, doc.isFYIRequested());
        try {
            doc.clearFYI();
        } catch (Exception e) {
            fail("A non-initator with an FYI request should be allowed to take the FYI action on a " + getSavedStatusDisplayValue() + " document");
        }
        assertTrue("Document should be " + getSavedStatusDisplayValue(), doc.stateIsSaved());
    }
}
