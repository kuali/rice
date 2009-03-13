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
package org.kuali.rice.kew.messaging.exceptionhandling;

import org.junit.Test;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;

/**
 * This is a unit test for testing the functionality of the ExceptionRoutingService. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
/*
@PerTestUnitTestData(
		@UnitTestData(sqlStatements = {
    		@UnitTestSql("INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND) VALUES('13', '5B4F09743F4DEF33ED404F8189D44F24', '2', null, null, 'KR-SYS', 'Y')"),
    		@UnitTestSql("INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND) VALUES('999', '5DF45238F5528FD6E0404F8189D840B8', 1, '63', '13', 'Y')"),
    		@UnitTestSql("INSERT INTO KRIM_RSP_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('11', '5D8B0E3E634E96A3E02F4F8189D8468D', 1, '13', '54', '13', 'TestFinalApproverDocumentType')"),
    	})
	)
*/
public class ExceptionRoutingServiceTest extends KEWTestCase {
	
	/**
	 * Checks to make sure that the KIM routing is working.
	 * Based upon the test method org.kuali.rice.kew.doctype.DocumentTypeTest.testFinalApproverRouting()
	 */
	@Test public void testKimExceptionRouting() throws Exception {
		loadXmlFile("RouteExceptionTestDoc.xml");
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("admin"), "TestFinalApproverDocumentType");
        document.setTitle("");
        document.routeDocument("");
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        try {
            document.approve("");
            fail("document should have thrown routing exception");
        } catch (Exception e) {
            //deal with single transaction issue in test.
        	TestUtilities.getExceptionThreader().join();
        	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
            assertTrue("Document should be in exception routing", document.stateIsException());
        }
	}
}
