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
import junit.framework.Assert

/**
 * Created by IntelliJ IDEA.
 * User: jjhanso
 * Date: 4/6/11
 * Time: 7:47 AM
 * To change this template use File | Settings | File Templates.
 */
class GroupTest {

    private static final String ID = "50"
    private static final String NAMESPACE = "KUALI"
    private static final String NAME = "grouptest"
    private static final String DESCRIPTION = "A super awesome test group";
    private static final String KIM_TYPE_ID = "1";

    private static final String MEMBER_1_ID = "1"
    private static final String MEMBER_1_GROUP_ID = "50"
    private static final String MEMBER_1_MEMBER_ID = "1"
    private static final String MEMBER_1_TYPE_CD = "P"
    private static final Long MEMBER_1_VER_NBR = new Long(1)
    private static final String MEMBER_1_OBJ_ID = UUID.randomUUID()
    private static final String MEMBER_1_ACTIVE_FROM_STRING = "2011-01-01 12:00:00.0"
    private static final Timestamp MEMBER_1_ACTIVE_FROM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MEMBER_1_ACTIVE_FROM_STRING).toTimestamp()
    private static final String MEMBER_1_ACTIVE_TO_STRING = "2012-01-01 12:00:00.0"
    private static final Timestamp MEMBER_1_ACTIVE_TO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(MEMBER_1_ACTIVE_TO_STRING).toTimestamp()


    private static final String MEMBER_2_ID = "2"
    private static final String MEMBER_2_GROUP_ID = "50"
    private static final String MEMBER_2_MEMBER_ID = "2"
    private static final String MEMBER_2_TYPE_CD = "G"
    private static final Long MEMBER_2_VER_NBR = new Long(1)
    private static final String MEMBER_2_OBJ_ID = UUID.randomUUID()

    private static final String ATTRIBUTES_1_ID = "1"
    private static final String ATTRIBUTES_1_GROUP_ID = "50"
    private static final String ATTRIBUTES_1_KIM_TYPE_ID = "1"
    private static final String ATTRIBUTES_1_ATTRIBUTE_ID = "1"
    private static final String ATTRIBUTES_1_VALUE = "X"
    private static final Long ATTRIBUTES_1_VER_NBR = new Long(1)
    private static final String ATTRIBUTES_1_OBJ_ID = UUID.randomUUID()

    private static final String ATTRIBUTES_2_ID = "2"
    private static final String ATTRIBUTES_2_GROUP_ID = "50"
    private static final String ATTRIBUTES_2_KIM_TYPE_ID = "2"
    private static final String ATTRIBUTES_2_ATTRIBUTE_ID = "2"
    private static final String ATTRIBUTES_2_VALUE = "Y"
    private static final Long ATTRIBUTES_2_VER_NBR = new Long(1)
    private static final String ATTRIBUTES_2_OBJ_ID = UUID.randomUUID()

	private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Long(1)
	private static final String OBJECT_ID = UUID.randomUUID()

    private static final String XML = """
    <group xmlns="http://rice.kuali.org/kim/v2_0">
      <id>${ID}</id>
      <namespaceCode>${NAMESPACE}</namespaceCode>
      <name>${NAME}</name>
      <description>${DESCRIPTION}</description>
      <kimTypeId>${KIM_TYPE_ID}</kimTypeId>
      <members>
        <id>${MEMBER_1_ID}</id>
        <groupId>${MEMBER_1_GROUP_ID}</groupId>
        <memberId>${MEMBER_1_MEMBER_ID}</memberId>
        <typeCode>${MEMBER_1_TYPE_CD}</typeCode>
        <activeFromDate>${MEMBER_1_ACTIVE_FROM.getTime()}</activeFromDate>
        <activeToDate>${MEMBER_1_ACTIVE_TO.getTime()}</activeToDate>
        <versionNumber>${MEMBER_1_VER_NBR}</versionNumber>
        <objectId>${MEMBER_1_OBJ_ID}</objectId>
      </members>
      <members>
        <id>${MEMBER_2_ID}</id>
        <groupId>${MEMBER_2_GROUP_ID}</groupId>
        <memberId>${MEMBER_2_MEMBER_ID}</memberId>
        <typeCode>${MEMBER_2_TYPE_CD}</typeCode>
        <versionNumber>${MEMBER_2_VER_NBR}</versionNumber>
        <objectId>${MEMBER_2_OBJ_ID}</objectId>
      </members>
      <attributes>
        <id>${ATTRIBUTES_1_ID}</id>
        <groupId>${ATTRIBUTES_1_GROUP_ID}</groupId>
        <kimTypeId>${ATTRIBUTES_1_KIM_TYPE_ID}</kimTypeId>
        <attributeId>${ATTRIBUTES_1_ATTRIBUTE_ID}</attributeId>
        <value>${ATTRIBUTES_1_VALUE}</value>
        <versionNumber>${ATTRIBUTES_1_VER_NBR}</versionNumber>
        <objectId>${ATTRIBUTES_1_OBJ_ID}</objectId>
      </attributes>
      <attributes>
        <id>${ATTRIBUTES_2_ID}</id>
        <groupId>${ATTRIBUTES_2_GROUP_ID}</groupId>
        <kimTypeId>${ATTRIBUTES_2_KIM_TYPE_ID}</kimTypeId>
        <attributeId>${ATTRIBUTES_2_ATTRIBUTE_ID}</attributeId>
        <value>${ATTRIBUTES_2_VALUE}</value>
        <versionNumber>${ATTRIBUTES_2_VER_NBR}</versionNumber>
        <objectId>${ATTRIBUTES_2_OBJ_ID}</objectId>
      </attributes>
      <active>${ACTIVE}</active>
      <versionNumber>${VERSION_NUMBER}</versionNumber>
      <objectId>${OBJECT_ID}</objectId>
    </group>
    """

    @Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(Group.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  Group group = this.createGroupFromPassedInContract()
	  marshaller.marshal(group,sw)
	  String xml = sw.toString()

	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}

    private Group createGroupFromPassedInContract() {
		Group group =  Group.Builder.create(new GroupContract() {
			String getId() {GroupTest.ID}
            String getNamespaceCode() {GroupTest.NAMESPACE}
			String getName() {GroupTest.NAME}
            String getDescription() {GroupTest.DESCRIPTION}
            String getKimTypeId() {GroupTest.KIM_TYPE_ID}
			List<GroupMember> getMembers() {[
                    GroupMember.Builder.create(new GroupMemberContract() {
				        String getId() {GroupTest.MEMBER_1_ID}
                        String getGroupId() {GroupTest.MEMBER_1_GROUP_ID}
                        String getMemberId() {GroupTest.MEMBER_1_MEMBER_ID}
                        String getTypeCode() {GroupTest.MEMBER_1_TYPE_CD}
                        Timestamp getActiveFromDate() {GroupTest.MEMBER_1_ACTIVE_FROM}
                        Timestamp getActiveToDate() {GroupTest.MEMBER_1_ACTIVE_TO}
                        Long getVersionNumber() { GroupTest.MEMBER_1_VER_NBR }
                        String getObjectId() { GroupTest.MEMBER_1_OBJ_ID }}).build(),
                    GroupMember.Builder.create(new GroupMemberContract() {
				        String getId() {GroupTest.MEMBER_2_ID}
                        String getGroupId() {GroupTest.MEMBER_2_GROUP_ID}
                        String getMemberId() {GroupTest.MEMBER_2_MEMBER_ID}
                        String getTypeCode() {GroupTest.MEMBER_2_TYPE_CD}
                        Timestamp getActiveFromDate() {null}
                        Timestamp getActiveToDate() {null}
                        Long getVersionNumber() { GroupTest.MEMBER_2_VER_NBR }
                        String getObjectId() { GroupTest.MEMBER_2_OBJ_ID }}).build() ]}
            List<GroupAttribute> getAttributes() {[
                    GroupAttribute.Builder.create(new GroupAttributeContract() {
				        String getId() {GroupTest.ATTRIBUTES_1_ID}
                        String getGroupId() {GroupTest.ATTRIBUTES_1_GROUP_ID}
                        String getKimTypeId() {GroupTest.ATTRIBUTES_1_KIM_TYPE_ID}
                        String getAttributeId() {GroupTest.ATTRIBUTES_1_ATTRIBUTE_ID}
                        String getValue() {GroupTest.ATTRIBUTES_1_VALUE}
                        Long getVersionNumber() { GroupTest.ATTRIBUTES_1_VER_NBR }
                        String getObjectId() { GroupTest.ATTRIBUTES_1_OBJ_ID }}).build(),
                    GroupAttribute.Builder.create(new GroupAttributeContract() {
				        String getId() {GroupTest.ATTRIBUTES_2_ID}
                        String getGroupId() {GroupTest.ATTRIBUTES_2_GROUP_ID}
                        String getKimTypeId() {GroupTest.ATTRIBUTES_2_KIM_TYPE_ID}
                        String getAttributeId() {GroupTest.ATTRIBUTES_2_ATTRIBUTE_ID}
                        String getValue() {GroupTest.ATTRIBUTES_2_VALUE}
                        Long getVersionNumber() { GroupTest.ATTRIBUTES_2_VER_NBR }
                        String getObjectId() { GroupTest.ATTRIBUTES_2_OBJ_ID }}).build() ]}
			boolean isActive() { GroupTest.ACTIVE.toBoolean() }
            Long getVersionNumber() { GroupTest.VERSION_NUMBER }
			String getObjectId() { GroupTest.OBJECT_ID }
		  }).build()

        return group
	}

    @Test
	public void testXmlUnmarshal() {
        JAXBContext jc = JAXBContext.newInstance(Group.class)
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Group group = (Group) unmarshaller.unmarshal(new StringReader(XML))
        Assert.assertEquals(ID,group.id)
        Assert.assertEquals(NAME,group.name)
        Assert.assertEquals(NAMESPACE,group.namespaceCode)
        Assert.assertEquals(new Boolean(ACTIVE).booleanValue(),group.active)
	    Assert.assertEquals(2, group.members.size())
        Assert.assertEquals(MEMBER_1_ID, group.members[0].id)
        Assert.assertEquals(MEMBER_1_GROUP_ID, group.members[0].groupId)
        Assert.assertEquals(MEMBER_1_MEMBER_ID, group.members[0].memberId)
        Assert.assertEquals(MEMBER_1_TYPE_CD, group.members[0].typeCode)
        Assert.assertEquals(MEMBER_1_ACTIVE_FROM, group.members[0].activeFromDate)
        Assert.assertEquals(MEMBER_1_ACTIVE_TO, group.members[0].activeToDate)
        Assert.assertEquals(MEMBER_1_OBJ_ID, group.members[0].objectId)
        Assert.assertEquals(MEMBER_1_VER_NBR, group.members[0].versionNumber)

        Assert.assertEquals(MEMBER_2_ID, group.members[1].id)
        Assert.assertEquals(MEMBER_2_GROUP_ID, group.members[1].groupId)
        Assert.assertEquals(MEMBER_2_MEMBER_ID, group.members[1].memberId)
        Assert.assertEquals(MEMBER_2_TYPE_CD, group.members[1].typeCode)
        Assert.assertEquals(null, group.members[1].activeFromDate)
        Assert.assertEquals(null, group.members[1].activeToDate)
        Assert.assertEquals(MEMBER_2_OBJ_ID, group.members[1].objectId)
        Assert.assertEquals(MEMBER_2_VER_NBR, group.members[1].versionNumber)

	    Assert.assertEquals(2, group.attributes.size())
        Assert.assertEquals(ATTRIBUTES_1_ID, group.attributes[0].id)
        Assert.assertEquals(ATTRIBUTES_1_GROUP_ID, group.attributes[0].groupId)
        Assert.assertEquals(ATTRIBUTES_1_KIM_TYPE_ID, group.attributes[0].kimTypeId)
        Assert.assertEquals(ATTRIBUTES_1_ATTRIBUTE_ID, group.attributes[0].attributeId)
        Assert.assertEquals(ATTRIBUTES_1_VALUE, group.attributes[0].value)
        Assert.assertEquals(ATTRIBUTES_1_OBJ_ID, group.attributes[0].objectId)
        Assert.assertEquals(ATTRIBUTES_1_VER_NBR, group.attributes[0].versionNumber)

        Assert.assertEquals(ATTRIBUTES_2_ID, group.attributes[1].id)
        Assert.assertEquals(ATTRIBUTES_2_GROUP_ID, group.attributes[1].groupId)
        Assert.assertEquals(ATTRIBUTES_2_KIM_TYPE_ID, group.attributes[1].kimTypeId)
        Assert.assertEquals(ATTRIBUTES_2_ATTRIBUTE_ID, group.attributes[1].attributeId)
        Assert.assertEquals(ATTRIBUTES_2_VALUE, group.attributes[1].value)
        Assert.assertEquals(ATTRIBUTES_2_OBJ_ID, group.attributes[1].objectId)
        Assert.assertEquals(ATTRIBUTES_2_VER_NBR, group.attributes[1].versionNumber)


        Assert.assertEquals(VERSION_NUMBER, group.versionNumber)
	    Assert.assertEquals(OBJECT_ID, group.objectId)
	}
}
