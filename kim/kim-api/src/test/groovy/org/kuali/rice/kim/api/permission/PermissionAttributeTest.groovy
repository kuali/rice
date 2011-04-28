/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.kim.api.permission

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Assert
import org.junit.Test
import org.kuali.rice.kim.api.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimType

class PermissionAttributeTest {

    private static final String ID = "1";
	private static final String PERMISSION_ID = "93121771551017"
	private static final KimType KIM_TYPE 
	private static final KIM_TYPE_BUILDER
	
	static {
		KimType.Builder builder = KimType.Builder.create()
		builder.setId("1")
		KIM_TYPE_BUILDER = builder
		KIM_TYPE = builder.build()
	}

	private static final KimAttribute KIM_ATTRIBUTE

	static {
		KimAttribute.Builder builder = KimAttribute.Builder.create("the_component", "the_attribute", "the_namespacecode")
    	builder.setId("1")
		KIM_ATTRIBUTE = builder.build()
	}
	
	private static final String ATTRIBUTE_VALUE = "Some Attribute Value"
	private static final String OBJECT_ID = UUID.randomUUID()
	private static final Long VERSION_NUMBER = new Long(1)
	
	private static final String XML

    static {
		XML = """
	    <permissionAttribute xmlns="http://rice.kuali.org/kim/v2_0">
	        <id>${ID}</id>
	        <permissionId>${PERMISSION_ID}</permissionId>
	        <kimType>
	        	<id>${KIM_TYPE.id}</id>
	            <versionNumber>${VERSION_NUMBER}</versionNumber>
	        </kimType>
	        <kimAttribute>
	        	<id>${KIM_ATTRIBUTE.id}</id>
	            <componentName>${KIM_ATTRIBUTE.componentName}</componentName>
	            <attributeName>${KIM_ATTRIBUTE.attributeName}</attributeName>
	            <namespaceCode>${KIM_ATTRIBUTE.namespaceCode}</namespaceCode>
	            <versionNumber>${VERSION_NUMBER}</versionNumber>
	        </kimAttribute>        
	        <attributeValue>${ATTRIBUTE_VALUE}</attributeValue>
	        <versionNumber>${VERSION_NUMBER}</versionNumber>
	        <objectId>${OBJECT_ID}</objectId>
	    </permissionAttribute>
	    """
	}
	
	private PermissionAttribute create() {
		PermissionAttribute permissionAttribute =  PermissionAttribute.Builder.create(new PermissionAttributeContract() {
			String getId() {PermissionAttributeTest.ID}
			String getPermissionId() {PermissionAttributeTest.PERMISSION_ID}
			KimType getKimType() {PermissionAttributeTest.KIM_TYPE}
			KimAttribute getKimAttribute() {PermissionAttributeTest.KIM_ATTRIBUTE}
			String getAttributeValue() {PermissionAttributeTest.ATTRIBUTE_VALUE}
			Long getVersionNumber() {PermissionAttributeTest.VERSION_NUMBER }
			String getObjectId() {PermissionAttributeTest.OBJECT_ID }
		}).build()
		
		return permissionAttribute
	}

	@Test
	void happy_path() {
		PermissionAttribute.Builder.create(KIM_TYPE_BUILDER)
	}
	
	@Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_id_is_blank() {
        PermissionAttribute.Builder.create(KIM_TYPE_BUILDER).setId("");
    }

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_null() {
		PermissionAttribute.Builder.create(KIM_TYPE_BUILDER).setVersionNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	void test_Builder_fail_ver_num_less_than_1() {
		PermissionAttribute.Builder.create(KIM_TYPE_BUILDER).setVersionNumber(-1);
	}
	
    @Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(PermissionAttribute.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  PermissionAttribute permissionAttribute = create()
	  marshaller.marshal(permissionAttribute,sw)
	  String xml = sw.toString()

	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}

	@Test
	public void testXmlUnmarshal() {
		JAXBContext jc = JAXBContext.newInstance(PermissionAttribute.class)
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		PermissionAttribute permissionAttribute = (PermissionAttribute) unmarshaller.unmarshal(new StringReader(XML))

		Assert.assertEquals(ID, permissionAttribute.id)
		Assert.assertEquals(PERMISSION_ID, permissionAttribute.permissionId)
		Assert.assertEquals(KIM_TYPE, permissionAttribute.kimType)
		Assert.assertEquals(KIM_ATTRIBUTE, permissionAttribute.kimAttribute)
		Assert.assertEquals(ATTRIBUTE_VALUE, permissionAttribute.attributeValue)
		Assert.assertEquals(OBJECT_ID, permissionAttribute.objectId)
		Assert.assertEquals(VERSION_NUMBER, permissionAttribute.versionNumber)
	}
}
