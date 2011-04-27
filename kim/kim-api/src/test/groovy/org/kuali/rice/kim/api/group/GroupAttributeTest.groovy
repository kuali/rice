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

package org.kuali.rice.kim.api.group

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Assert
import org.junit.Test
import org.kuali.rice.kim.api.attribute.KimAttribute
import org.kuali.rice.kim.api.type.KimType


class GroupAttributeTest {
    private static final String ID = "1"
    private static final String GROUP_ID = "50"
    private static final KimType KIM_TYPE

    static {
        KimType.Builder builder = KimType.Builder.create()
        builder.setId("1")
        KIM_TYPE = builder.build()
    }

    private static final KimAttribute KIM_ATTRIBUTE

    static {
        KimAttribute.Builder builder = KimAttribute.Builder.create("the_comp", "the_attr", "the_ns")
        builder.setId("1")
        KIM_ATTRIBUTE = builder.build()
    }

    private static final String ATTRIBUTE_VALUE = "X"
    private static final Long VER_NBR = new Long(1)
    private static final String OBJ_ID = UUID.randomUUID()

    private static final String XML

    static {
        XML  = """
        <groupAttribute xmlns="http://rice.kuali.org/kim/v2_0">
            <id>${ID}</id>
            <groupId>${GROUP_ID}</groupId>
            <kimType>
                <id>${KIM_TYPE.id}</id>
                <versionNumber>${VER_NBR}</versionNumber>
            </kimType>
            <kimAttribute>
                <id>${KIM_ATTRIBUTE.id}</id>
                <componentName>${KIM_ATTRIBUTE.componentName}</componentName>
                <attributeName>${KIM_ATTRIBUTE.attributeName}</attributeName>
                <namespaceCode>${KIM_ATTRIBUTE.namespaceCode}</namespaceCode>
                <versionNumber>${VER_NBR}</versionNumber>
            </kimAttribute>
            <attributeValue>${ATTRIBUTE_VALUE}</attributeValue>
            <versionNumber>${VER_NBR}</versionNumber>
            <objectId>${OBJ_ID}</objectId>
        </groupAttribute>
        """
    }

    @Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(GroupAttribute.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  GroupAttribute groupAttribute = createGroupAttributeFromPassedInContract()
	  marshaller.marshal(groupAttribute,sw)
	  String xml = sw.toString()

	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}

    private GroupAttribute createGroupAttributeFromPassedInContract() {
		GroupAttribute groupAttribute =  GroupAttribute.Builder.create(new GroupAttributeContract() {
            String getId() {GroupAttributeTest.ID}
            String getGroupId() {GroupAttributeTest.GROUP_ID}
            KimType getKimType() {GroupAttributeTest.KIM_TYPE}
            KimAttribute getKimAttribute() {GroupAttributeTest.KIM_ATTRIBUTE}
            String getAttributeValue() {GroupAttributeTest.ATTRIBUTE_VALUE}
            Long getVersionNumber() { GroupAttributeTest.VER_NBR }
            String getObjectId() { GroupAttributeTest.OBJ_ID }
		  }).build()

        return groupAttribute
	}

    @Test
	public void testXmlUnmarshal() {
        JAXBContext jc = JAXBContext.newInstance(Group.class)
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GroupAttribute groupAttribute = (GroupAttribute) unmarshaller.unmarshal(new StringReader(XML))

        Assert.assertEquals(ID, groupAttribute.id)
        Assert.assertEquals(GROUP_ID, groupAttribute.groupId)
        Assert.assertEquals(KIM_TYPE, groupAttribute.kimType)
        Assert.assertEquals(KIM_ATTRIBUTE, groupAttribute.kimAttribute)
        Assert.assertEquals(ATTRIBUTE_VALUE, groupAttribute.attributeValue)
        Assert.assertEquals(OBJ_ID, groupAttribute.objectId)
        Assert.assertEquals(VER_NBR, groupAttribute.versionNumber)

	}
}
