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
package org.kuali.workflow.workgroup;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routetemplate.RuleAttribute;

/**
 * Tests interaction with the default WorkgroupTypeService implementation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeServiceTest extends KEWTestCase {

	private WorkgroupTypeService service;

	@Override
	protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		service = KEWServiceLocator.getWorkgroupTypeService();
	}

	@Test
	public void testSave() throws Exception {
		WorkgroupType workgroupType = new WorkgroupType();
		workgroupType.setName("MyName");
		workgroupType.setLabel("MyLabel");
		workgroupType.setDescription("MyDescription");
		workgroupType.setActive(true);

		WorkgroupTypeAttribute attribute1 = new WorkgroupTypeAttribute();
		attribute1.setWorkgroupType(workgroupType);
		RuleAttribute ruleAttribute1 = KEWServiceLocator.getRuleAttributeService().findByName("TestRuleAttribute");
		assertNotNull("Could not locate the 'TestRuleAttribute'", ruleAttribute1);
		attribute1.setAttribute(ruleAttribute1);
		workgroupType.getAttributes().add(attribute1);

		service.save(workgroupType);

		assertNotNull(workgroupType.getWorkgroupTypeId());
		assertNotNull(workgroupType.getAttributes().get(0).getWorkgroupTypeAttributeId());

	}

}
