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

import java.util.List;

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test;

import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.kuali.rice.kim.api.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimType;

class PermissionTest {

	private static final String OBJECT_ID = UUID.randomUUID()
	private static final Long VERSION_NUMBER = new Long(1)
	private static final boolean ACTIVE = "true"
	
	private static final String ID = "50"
	private static final String NAMESPACE_CODE = "KUALI"
	private static final String NAME = "PermissionName"
	private static final String DESCRIPTION = "Some Permission Description"
	private static final String TEMPLATE_ID = "7317791873"

	private static final String ATTRIBUTES_1_ID = "1"
	private static final String ATTRIBUTES_1_PERMISSION_ID = "50"
	private static final String ATTRIBUTES_1_VALUE = "Some Attribute Value 1"
	private static final Long ATTRIBUTES_1_VER_NBR = new Long(1)
	private static final String ATTRIBUTES_1_OBJ_ID = UUID.randomUUID()
	
	private static final KimType KIM_TYPE_1
	private static final String KIM_TYPE_1_ID = "1"
	private static final String KIM_TYPE_1_OBJ_ID = UUID.randomUUID()
	static {
		KimType.Builder builder = KimType.Builder.create()
		builder.setId(KIM_TYPE_1_ID)
		builder.setNamespaceCode(NAMESPACE_CODE)
		builder.setActive(ACTIVE)
		builder.setVersionNumber(VERSION_NUMBER)
		builder.setObjectId(KIM_TYPE_1_OBJ_ID)
		KIM_TYPE_1 = builder.build()
	}
		
	private static final KimAttribute KIM_ATTRIBUTE_1
	private static final String KIM_ATTRIBUTE_1_ID = "1"
	private static final String KIM_ATTRIBUTE_1_COMPONENT_NAME = "the_component1"
	private static final String KIM_ATTRIBUTE_1_NAME = "the_attribute1"
	static {
		KimAttribute.Builder builder = KimAttribute.Builder.create(KIM_ATTRIBUTE_1_COMPONENT_NAME, KIM_ATTRIBUTE_1_NAME, NAMESPACE_CODE)
		builder.setId(KIM_ATTRIBUTE_1_ID)
		KIM_ATTRIBUTE_1 = builder.build()
	}
	
	private static final String XML = """
		<permission xmlns="http://rice.kuali.org/kim/v2_0">
			<id>${ID}</id>
			<namespaceCode>${NAMESPACE_CODE}</namespaceCode>
			<name>${NAME}</name>
			<description>${DESCRIPTION}</description>
			<templateId>${TEMPLATE_ID}</templateId>
			<attributes>
			    <attribute>
	                <id>${ATTRIBUTES_1_ID}</id>
	                <attributeValue>${ATTRIBUTES_1_VALUE}</attributeValue>
	                <permissionId>${ATTRIBUTES_1_PERMISSION_ID}</permissionId>
	                <versionNumber>${VERSION_NUMBER}</versionNumber>
	                <objectId>${ATTRIBUTES_1_OBJ_ID}</objectId>
	                <kimType>
	                    <id>${KIM_TYPE_1_ID}</id>
	                    <namespaceCode>${NAMESPACE_CODE}</namespaceCode>
	                    <active>${ACTIVE}</active>
	                    <versionNumber>${VERSION_NUMBER}</versionNumber>
	                    <objectId>${KIM_TYPE_1_OBJ_ID}</objectId>
	                </kimType>
	                <kimAttribute>
                    	<id>${KIM_ATTRIBUTE_1_ID}</id>
                    	<componentName>${KIM_ATTRIBUTE_1_COMPONENT_NAME}</componentName>
						<attributeName>${KIM_ATTRIBUTE_1_NAME}</attributeName>
						<namespaceCode>${NAMESPACE_CODE}</namespaceCode>
						<versionNumber>${VERSION_NUMBER}</versionNumber>
					</kimAttribute>					
				</attribute>  
			</attributes>
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
	  JAXBContext jc = JAXBContext.newInstance(Permission.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  Permission permission = this.create()
	  marshaller.marshal(permission,sw)
	  String xml = sw.toString()

	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}
	
	private create() {
		Permission permission = Permission.Builder.create(new PermissionContract() {
			String getId() {PermissionTest.ID}
			String getNamespaceCode() {PermissionTest.NAMESPACE_CODE}
			String getName() {PermissionTest.NAME}
			String getDescription() {PermissionTest.DESCRIPTION}
			String getTemplateId() {PermissionTest.TEMPLATE_ID}
			List<PermissionAttribute> getAttributes() {[
				PermissionAttribute.Builder.create(new PermissionAttributeContract() {
					 String getId() {PermissionTest.ATTRIBUTES_1_ID}
					 String getAttributeValue() {PermissionTest.ATTRIBUTES_1_VALUE}
                     String getPermissionId() {PermissionTest.ATTRIBUTES_1_PERMISSION_ID}
					 Long getVersionNumber() {PermissionTest.ATTRIBUTES_1_VER_NBR}
					 KimType getKimType() {PermissionTest.KIM_TYPE_1}
                     KimAttribute getKimAttribute() {PermissionTest.KIM_ATTRIBUTE_1}
                     String getObjectId() {PermissionTest.ATTRIBUTES_1_OBJ_ID}}).build() ]}			
			boolean isActive() {PermissionTest.ACTIVE.toBoolean()}
			Long getVersionNumber() {PermissionTest.VERSION_NUMBER}
			String getObjectId() {PermissionTest.OBJECT_ID}
		}).build()
		
		return permission
	}
}
