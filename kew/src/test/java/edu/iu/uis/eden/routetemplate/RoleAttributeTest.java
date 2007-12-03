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
package edu.iu.uis.eden.routetemplate;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

/**
 * Tests the generation of Action Requests from RoleAttributes
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleAttributeTest extends KEWTestCase {

	@Test public void testWorkgroupRoleAttribute() throws Exception {
		loadXmlFile("WorkgroupRoleAttributeTestConfig.xml");
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "WorkgroupRoleAttributeDocument");
		document.routeDocument("");

		assertTrue("document should be enroute.", document.stateIsEnroute());
	}

	/**
	 * Tests that if you return a non-null Id object with a null id value inside, that the role action request
	 * generation handles it properly.
	 */
	@Test public void testBadWorkgroupRoleAttribute() throws Exception {
		loadXmlFile("BadWorkgroupRoleAttributeTestConfig.xml");
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "BadWorkgroupRoleAttributeDocument");

		try {
			document.routeDocument("");
			fail("Should have thrown an error because we had some bad ids.");
		} catch (Exception e) {}
	}

}
