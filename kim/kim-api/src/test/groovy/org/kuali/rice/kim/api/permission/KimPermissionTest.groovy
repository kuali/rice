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

/**
 * This is a description of what this class does - eldavid don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class KimPermissionTest {

	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "KimPermissionName"
	private static final String OBJECT_ID = "hgw4yreydsheye65"
	private static final Long VERSION_NUMBER = "1"
	private static final boolean ACTIVE = "true"
	
	private static final String XML = """
		<kimPermission xmlns="http://rice.kuali.org/kim/v2_0">
			<id>${ID}</id>
			<name>${NAME}</name>
			<namespaceCode>${NAMESPACE_CODE}</namespaceCode>
			<active>${ACTIVE}</active>
			<versionNumber>${VERSION_NUMBER}</versionNumber>
        	<objectId>${OBJECT_ID}</objectId>
		</kimPermission>
		"""
	
    @Test
    void happy_path() {
        KimPermission.Builder.create()
    }

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_null() {
		KimPermission.Builder.create().setVersionNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_less_than_1() {
		KimPermission.Builder.create().setVersionNumber(-1);
	}
	
	@Test
	void test_copy() {
		def o1b = KimPermission.Builder.create()
		o1b.id = "the_id"
        o1b.namespaceCode = "a"

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
			boolean active = KimPermissionTest.ACTIVE
			Long versionNumber = KimPermissionTest.VERSION_NUMBER
			String objectId = KimPermissionTest.OBJECT_ID
		}).build()
	}
}
