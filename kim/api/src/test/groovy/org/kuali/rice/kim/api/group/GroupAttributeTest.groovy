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

import java.sql.Timestamp
import java.text.SimpleDateFormat
import org.junit.Test
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Assert
/**
 * Created by IntelliJ IDEA.
 * User: jjhanso
 * Date: 4/7/11
 * Time: 9:59 AM
 * To change this template use File | Settings | File Templates.
 */
class GroupAttributeTest {
    private static final String ID = "1"
    private static final String GROUP_ID = "50"
    private static final String KIM_TYPE_ID = "1"
    private static final String ATTRIBUTE_ID = "1"
    private static final String VALUE = "X"
    private static final Long VER_NBR = new Long(1)
    private static final String OBJ_ID = UUID.randomUUID()

    private static final String XML = """
    <groupAttribute xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <groupId>${GROUP_ID}</groupId>
        <kimTypeId>${KIM_TYPE_ID}</kimTypeId>
        <attributeId>${ATTRIBUTE_ID}</attributeId>
        <value>${VALUE}</value>
        <versionNumber>${VER_NBR}</versionNumber>
        <objectId>${OBJ_ID}</objectId>
    </groupAttribute>
    """

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
            String getKimTypeId() {GroupAttributeTest.KIM_TYPE_ID}
            String getAttributeId() {GroupAttributeTest.ATTRIBUTE_ID}
            String getValue() {GroupAttributeTest.VALUE}
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
        Assert.assertEquals(KIM_TYPE_ID, groupAttribute.kimTypeId)
        Assert.assertEquals(ATTRIBUTE_ID, groupAttribute.attributeId)
        Assert.assertEquals(VALUE, groupAttribute.value)
        Assert.assertEquals(OBJ_ID, groupAttribute.objectId)
        Assert.assertEquals(VER_NBR, groupAttribute.versionNumber)

	}
}
