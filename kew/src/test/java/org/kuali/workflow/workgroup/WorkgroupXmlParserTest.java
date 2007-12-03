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

import java.util.List;

import org.junit.Test;
import org.kuali.workflow.attribute.Extension;
import org.kuali.workflow.attribute.ExtensionData;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Tests parsing of the XML for Workgroups.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupXmlParserTest extends KEWTestCase {

	@Test
	public void testParseWorkgroupWithExtensionData() throws Exception {
		loadXmlFile("WorkgroupConfig.xml");
		Workgroup workgroup = getWorkgroupService().getWorkgroup(new GroupNameId("WGWithExtData"));
		assertNotNull("Workgroup should exist.", workgroup);
		assertEquals("WGWithExtData", workgroup.getDisplayName());
		assertEquals("Incorrect workgroup type.", "ChartOrgWorkgroup", workgroup.getWorkgroupType());
		WorkgroupType workgroupType = getWorkgroupTypeService().findByName(workgroup.getWorkgroupType());
		assertNotNull("Failed to find workgroup type 'ChartOrgWorkgroup'", workgroupType);
		assertEquals("Should have 1 attribute.", 1, workgroupType.getAttributes().size());
		WorkgroupTypeAttribute attribute = workgroupType.getAttributes().get(0);
		assertEquals("ChartOrgDataAttribute", attribute.getAttribute().getName());

		List<Extension> extensions = workgroup.getExtensions();
		assertEquals("Workgroup should have 1 extension.", 1, extensions.size());

		Extension extension = extensions.get(0);
		assertEquals("ChartOrgDataAttribute", extension.getAttributeName());
		assertEquals(2, extension.getData().size());

		ExtensionData data1 = extension.getData().get(0);
		ExtensionData data2 = extension.getData().get(1);
		assertEquals("chart", data1.getKey());
		assertEquals("BL", data1.getValue());
		assertEquals("org", data2.getKey());
		assertEquals("BUS", data2.getValue());

		assertEquals("BL", extension.getDataValue("chart"));
		assertEquals("BUS", extension.getDataValue("org"));
	}

	private WorkgroupService getWorkgroupService() {
		return KEWServiceLocator.getWorkgroupService();
	}

	private WorkgroupTypeService getWorkgroupTypeService() {
		return KEWServiceLocator.getWorkgroupTypeService();
	}

}
