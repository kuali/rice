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
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeXmlParserTest extends KEWTestCase {

	private WorkgroupTypeService service;

	@Override
	protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		this.service = KEWServiceLocator.getWorkgroupTypeService();
	}

	/**
	 * Tests that an existing WorkgroupType can be updated via the XML.
	 */
	@Test
	public void testUpdateWorkgroupType() throws Exception {
		loadXmlFile("WorkgroupTypeDocTypeConfig.xml");
		loadXmlFile("WorkgroupTypeConfig.xml");

		WorkgroupType wgt2 = service.findByName("WorkgroupType2");
		assertNotNull(wgt2);
		assertEquals(2, wgt2.getAttributes().size());
		assertEquals(2, wgt2.getActiveAttributes().size());
		WorkgroupTypeAttribute attribute1 = wgt2.getAttributes().get(0);
		WorkgroupTypeAttribute attribute2 = wgt2.getAttributes().get(1);
		assertEquals("WorkgroupTypeAttribute1", attribute1.getAttribute().getName());
		assertEquals("WorkgroupTypeAttribute2", attribute2.getAttribute().getName());

		// update WorkgroupType2 to remove WorkgroupTypeAttribute1
		loadXmlFile("WorkgroupTypeConfigUpdate1.xml");
		wgt2 = service.findByName("WorkgroupType2");
		assertNotNull(wgt2);
		assertEquals(2, wgt2.getAttributes().size());
		assertEquals(1, wgt2.getActiveAttributes().size());
		assertEquals("attribute id should be same as before.", attribute2.getWorkgroupTypeAttributeId(), wgt2.getActiveAttributes().get(0).getWorkgroupTypeAttributeId());

		// now let's reactive WorkgroupTypeAttribute1 and add a new one
		loadXmlFile("WorkgroupTypeConfigUpdate2.xml");
		wgt2 = service.findByName("WorkgroupType2");
		assertNotNull(wgt2);
		assertEquals(3, wgt2.getAttributes().size());
		assertEquals(3, wgt2.getActiveAttributes().size());
		WorkgroupTypeAttribute attribute3New = wgt2.getAttributes().get(0);
		WorkgroupTypeAttribute attribute1New = wgt2.getAttributes().get(1);
		WorkgroupTypeAttribute attribute2New = wgt2.getAttributes().get(2);
		assertEquals("WorkgroupTypeAttribute3", attribute3New.getAttribute().getName());
		assertEquals("WorkgroupTypeAttribute1", attribute1New.getAttribute().getName());
		assertEquals("WorkgroupTypeAttribute2", attribute2New.getAttribute().getName());
		assertEquals("attribute 1 id should be same as before.", attribute1.getWorkgroupTypeAttributeId(), attribute1New.getWorkgroupTypeAttributeId());
		assertEquals("attribute 2 id should be same as before.", attribute2.getWorkgroupTypeAttributeId(), attribute2New.getWorkgroupTypeAttributeId());
		assertEquals(0, attribute3New.getOrderIndex());
		assertEquals(1, attribute1New.getOrderIndex());
		assertEquals(2, attribute2New.getOrderIndex());

		// rearrange indexes and verify things are retrieved from the db correclty
		attribute1New.setOrderIndex(2);
		attribute2New.setOrderIndex(1);
		service.save(wgt2);
		wgt2 = service.findByName("WorkgroupType2");
		assertEquals("WorkgroupTypeAttribute2", wgt2.getAttributes().get(1).getAttribute().getName());
		assertEquals("WorkgroupTypeAttribute1", wgt2.getAttributes().get(2).getAttribute().getName());

	}

	@Test
	public void testParseWorkgroupType() throws Exception {
		loadXmlFile("WorkgroupTypeDocTypeConfig.xml");
		loadXmlFile("WorkgroupTypeConfig.xml");

		// check WorkgroupType1
		WorkgroupType workgroupType1 = service.findByName("WorkgroupType1");
		assertNotNull("WorkgroupType1 should have been found.", workgroupType1);
		assertNotNull("WorkgroupType should have an ID", workgroupType1.getWorkgroupTypeId());
		assertEquals("WorkgroupType1", workgroupType1.getName());
		assertEquals("Should be one attribute.", 1, workgroupType1.getAttributes().size());
		WorkgroupTypeAttribute workgroupTypeAttribute1 = workgroupType1.getAttributes().get(0);
		assertEquals(workgroupType1, workgroupTypeAttribute1.getWorkgroupType());
		assertNotNull(workgroupTypeAttribute1.getWorkgroupTypeAttributeId());
		assertNotNull(workgroupTypeAttribute1.getAttribute());
		assertEquals("Wrong attribute", "WorkgroupTypeAttribute1", workgroupTypeAttribute1.getAttribute().getName());

		// check WorkgroupType2
		WorkgroupType workgroupType2 = service.findByName("WorkgroupType2");
		assertNotNull("WorkgroupType2 should have been found.", workgroupType2);
		assertNotNull("WorkgroupType should have an ID", workgroupType2.getWorkgroupTypeId());
		assertEquals("WorkgroupType2", workgroupType2.getName());
		assertEquals("Workgroup Type 2", workgroupType2.getLabel());
		assertEquals("The Second Workgroup Type", workgroupType2.getDescription());
		assertEquals("Should be two attributes.", 2, workgroupType2.getAttributes().size());
		workgroupTypeAttribute1 = workgroupType2.getAttributes().get(0);
		WorkgroupTypeAttribute workgroupTypeAttribute2 = workgroupType2.getAttributes().get(1);
		assertEquals(workgroupType2, workgroupTypeAttribute1.getWorkgroupType());
		assertEquals(workgroupType2, workgroupTypeAttribute2.getWorkgroupType());
		assertNotNull(workgroupTypeAttribute1.getWorkgroupTypeAttributeId());
		assertNotNull(workgroupTypeAttribute2.getWorkgroupTypeAttributeId());
		assertNotNull(workgroupTypeAttribute1.getAttribute());
		assertNotNull(workgroupTypeAttribute2.getAttribute());
		assertEquals("Wrong attribute", "WorkgroupTypeAttribute1", workgroupTypeAttribute1.getAttribute().getName());
		assertEquals("Wrong attribute", "WorkgroupTypeAttribute2", workgroupTypeAttribute2.getAttribute().getName());

		// check WorkgroupType3
		WorkgroupType workgroupType3 = service.findByName("WorkgroupType3");
		assertNotNull("WorkgroupType3 should have been found.", workgroupType3);
		assertNotNull("WorkgroupType should have an ID", workgroupType3.getWorkgroupTypeId());
		assertEquals("WorkgroupType3", workgroupType3.getName());
		assertEquals("WorkgroupType3", workgroupType3.getLabel());
		assertNull(workgroupType3.getDescription());
		assertEquals("WorkgroupType3DocType", workgroupType3.getDocumentTypeName());
		DocumentType docType = getDocumentTypeService().findByName(workgroupType3.getDocumentTypeName());
		assertNotNull(docType);
	}

	private DocumentTypeService getDocumentTypeService() {
		return KEWServiceLocator.getDocumentTypeService();
	}

}
