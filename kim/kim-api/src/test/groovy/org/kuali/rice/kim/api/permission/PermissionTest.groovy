/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.permission

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test;

class PermissionTest {

	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "PermissionName"
	private static final String DESCRIPTION = "Some KIM Permission Description"
	private static final String TEMPLATE_ID = "7317791873"
	private static final String OBJECT_ID = UUID.randomUUID()
	private static final Long VERSION_NUMBER = new Long(1) 
	private static final boolean ACTIVE = "true"
	
	private static final String XML = """
		<permission xmlns="http://rice.kuali.org/kim/v2_0">
			<id>${ID}</id>
			<namespaceCode>${NAMESPACE_CODE}</namespaceCode>
			<name>${NAME}</name>
			<description>${DESCRIPTION}</description>
			<templateId>${TEMPLATE_ID}</templateId>
			<active>${ACTIVE}</active>
			<versionNumber>${VERSION_NUMBER}</versionNumber>
        	<objectId>${OBJECT_ID}</objectId>
		</permission>
		"""
	
    @Test
    void happy_path() {
        Permission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
    }

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_null() {
		Permission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_less_than_1() {
		Permission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(-1);
	}
	
	@Test
	void test_copy() {
		def o1b = Permission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
		o1b.description = DESCRIPTION

		def o1 = o1b.build()

		def o2 = Permission.Builder.create(o1).build()

		Assert.assertEquals(o1, o2)
	}
	
	@Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(Permission.class)
	  def marshaller = jc.createMarshaller()
	  def sw = new StringWriter()

	  def param = this.create()
	  marshaller.marshal(param,sw)

	  def unmarshaller = jc.createUnmarshaller()
	  def actual = unmarshaller.unmarshal(new StringReader(sw.toString()))
	  def expected = unmarshaller.unmarshal(new StringReader(XML))

	  Assert.assertEquals(expected,actual)
	}
	
	private create() {
		return Permission.Builder.create(new PermissionContract() {
			String id = PermissionTest.ID
			String namespaceCode = PermissionTest.NAMESPACE_CODE
			String name = PermissionTest.NAME
			String description = PermissionTest.DESCRIPTION
			String templateId = PermissionTest.TEMPLATE_ID
			boolean active = PermissionTest.ACTIVE
			Long versionNumber = PermissionTest.VERSION_NUMBER
			String objectId = PermissionTest.OBJECT_ID
		}).build()
	}
}
