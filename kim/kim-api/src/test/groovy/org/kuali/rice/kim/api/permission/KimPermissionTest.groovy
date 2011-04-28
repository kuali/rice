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

class KimPermissionTest {

	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "KimPermissionName"
	private static final String DESCRIPTION = "Some KIM Permission Description"
	private static final String TEMPLATE_ID = "7317791873"
	private static final String OBJECT_ID = UUID.randomUUID()
	private static final Long VERSION_NUMBER = new Long(1) 
	private static final boolean ACTIVE = "true"
	
	private static final String XML = """
		<kimPermission xmlns="http://rice.kuali.org/kim/v2_0">
			<id>${ID}</id>
			<namespaceCode>${NAMESPACE_CODE}</namespaceCode>
			<name>${NAME}</name>
			<description>${DESCRIPTION}</description>
			<templateId>${TEMPLATE_ID}</templateId>
			<active>${ACTIVE}</active>
			<versionNumber>${VERSION_NUMBER}</versionNumber>
        	<objectId>${OBJECT_ID}</objectId>
		</kimPermission>
		"""
	
    @Test
    void happy_path() {
        KimPermission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
    }

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_null() {
		KimPermission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_less_than_1() {
		KimPermission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID).setVersionNumber(-1);
	}
	
	@Test
	void test_copy() {
		def o1b = KimPermission.Builder.create(ID, NAMESPACE_CODE, NAME, TEMPLATE_ID)
		o1b.description = DESCRIPTION

		def o1 = o1b.build()

		def o2 = KimPermission.Builder.create(o1).build()

		Assert.assertEquals(o1, o2)
	}
	
	@Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(KimPermission.class)
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
		return KimPermission.Builder.create(new KimPermissionContract() {
			String id = KimPermissionTest.ID
			String namespaceCode = KimPermissionTest.NAMESPACE_CODE
			String name = KimPermissionTest.NAME
			String description = KimPermissionTest.DESCRIPTION
			String templateId = KimPermissionTest.TEMPLATE_ID
			boolean active = KimPermissionTest.ACTIVE
			Long versionNumber = KimPermissionTest.VERSION_NUMBER
			String objectId = KimPermissionTest.OBJECT_ID
		}).build()
	}
}
