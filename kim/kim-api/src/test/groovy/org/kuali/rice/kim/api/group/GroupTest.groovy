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
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import junit.framework.Assert
import org.junit.Test
import org.kuali.rice.kim.api.common.attribute.KimAttribute
import org.kuali.rice.kim.api.common.attribute.KimAttributeData
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract
import org.kuali.rice.kim.api.type.KimType

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

    private static final KimType KIM_TYPE_1

    static {
        KimType.Builder builder = KimType.Builder.create()
        builder.setId("1")
        KIM_TYPE_1 = builder.build()
    }

    private static final KimAttribute KIM_ATTRIBUTE_1

    static {
        KimAttribute.Builder builder = KimAttribute.Builder.create("the_comp_1", "the_attr_1", "the_ns_1")
        builder.setId("1")
        KIM_ATTRIBUTE_1 = builder.build()
    }

    private static final KimType KIM_TYPE_2

    static {
        KimType.Builder builder = KimType.Builder.create()
        builder.setId("2")
        KIM_TYPE_2 = builder.build()
    }

    private static final KimAttribute KIM_ATTRIBUTE_2

    static {
        KimAttribute.Builder builder = KimAttribute.Builder.create("the_comp_2", "the_attr_2", "the_ns_2")
        builder.setId("2")
        KIM_ATTRIBUTE_2 = builder.build()
    }


    private static final String ATTRIBUTES_1_ID = "1"
    private static final String ATTRIBUTES_1_GROUP_ID = "50"
    private static final String ATTRIBUTES_1_VALUE = "X"
    private static final Long ATTRIBUTES_1_VER_NBR = new Long(1)
    private static final String ATTRIBUTES_1_OBJ_ID = UUID.randomUUID()

    private static final String ATTRIBUTES_2_ID = "2"
    private static final String ATTRIBUTES_2_GROUP_ID = "50"
    private static final String ATTRIBUTES_2_VALUE = "Y"
    private static final Long ATTRIBUTES_2_VER_NBR = new Long(1)
    private static final String ATTRIBUTES_2_OBJ_ID = UUID.randomUUID()

	private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Long(1)
	private static final String OBJECT_ID = UUID.randomUUID()

    private static final String XML;
    static {
        XML = """
        <group xmlns="http://rice.kuali.org/kim/v2_0">
          <id>${ID}</id>
          <namespaceCode>${NAMESPACE}</namespaceCode>
          <name>${NAME}</name>
          <description>${DESCRIPTION}</description>
          <kimTypeId>${KIM_TYPE_ID}</kimTypeId>
          <attributes>
              <attribute>
                <id>${ATTRIBUTES_1_ID}</id>
                <assignedToId>${ATTRIBUTES_1_GROUP_ID}</assignedToId>
                <kimTypeId>${KIM_TYPE_1.id}</kimTypeId>
                <kimType>
                    <id>${KIM_TYPE_1.id}</id>
                    <versionNumber>${VERSION_NUMBER}</versionNumber>
                </kimType>
                <kimAttribute>
                    <id>${KIM_ATTRIBUTE_1.id}</id>
                    <componentName>${KIM_ATTRIBUTE_1.componentName}</componentName>
                    <attributeName>${KIM_ATTRIBUTE_1.attributeName}</attributeName>
                    <namespaceCode>${KIM_ATTRIBUTE_1.namespaceCode}</namespaceCode>
                    <versionNumber>${VERSION_NUMBER}</versionNumber>
                </kimAttribute>
                <attributeValue>${ATTRIBUTES_1_VALUE}</attributeValue>
                <versionNumber>${ATTRIBUTES_1_VER_NBR}</versionNumber>
                <objectId>${ATTRIBUTES_1_OBJ_ID}</objectId>
              </attribute>
              <attribute>
                <id>${ATTRIBUTES_2_ID}</id>
                <assignedToId>${ATTRIBUTES_2_GROUP_ID}</assignedToId>
                <kimTypeId>${KIM_TYPE_2.id}</kimTypeId>
                <kimType>
                    <id>${KIM_TYPE_2.id}</id>
                    <versionNumber>${VERSION_NUMBER}</versionNumber>
                </kimType>
                <kimAttribute>
                    <id>${KIM_ATTRIBUTE_2.id}</id>
                    <componentName>${KIM_ATTRIBUTE_2.componentName}</componentName>
                    <attributeName>${KIM_ATTRIBUTE_2.attributeName}</attributeName>
                    <namespaceCode>${KIM_ATTRIBUTE_2.namespaceCode}</namespaceCode>
                    <versionNumber>${VERSION_NUMBER}</versionNumber>
                </kimAttribute>
                <attributeValue>${ATTRIBUTES_2_VALUE}</attributeValue>
                <versionNumber>${ATTRIBUTES_2_VER_NBR}</versionNumber>
                <objectId>${ATTRIBUTES_2_OBJ_ID}</objectId>
              </attribute>
          </attributes>
          <active>${ACTIVE}</active>
          <versionNumber>${VERSION_NUMBER}</versionNumber>
          <objectId>${OBJECT_ID}</objectId>
        </group>
        """
    }

    /*<members>
            <id>${MEMBER_1_ID}</id>
            <assignedToId>${MEMBER_1_GROUP_ID}</assignedToId>
            <memberId>${MEMBER_1_MEMBER_ID}</memberId>
            <typeCode>${MEMBER_1_TYPE_CD}</typeCode>
            <activeFromDate>${MEMBER_1_ACTIVE_FROM.getTime()}</activeFromDate>
            <activeToDate>${MEMBER_1_ACTIVE_TO.getTime()}</activeToDate>
            <versionNumber>${MEMBER_1_VER_NBR}</versionNumber>
            <objectId>${MEMBER_1_OBJ_ID}</objectId>
          </members>
          <members>
            <id>${MEMBER_2_ID}</id>
            <assignedToId>${MEMBER_2_GROUP_ID}</assignedToId>
            <memberId>${MEMBER_2_MEMBER_ID}</memberId>
            <typeCode>${MEMBER_2_TYPE_CD}</typeCode>
            <versionNumber>${MEMBER_2_VER_NBR}</versionNumber>
            <objectId>${MEMBER_2_OBJ_ID}</objectId>
          </members>*/

    @Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(Group.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  Group group = this.createGroupFromPassedInContract()
	  marshaller.marshal(group,sw)
	  String xml = sw.toString()
      println(xml)
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
			/*List<GroupMember> getMembers() {[
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
                        String getObjectId() { GroupTest.MEMBER_2_OBJ_ID }}).build() ]}*/
            List<KimAttributeData> getAttributes() {[
                    KimAttributeData.Builder.create(new KimAttributeDataContract() {
				        String getId() {GroupTest.ATTRIBUTES_1_ID}
                        String getAssignedToId() {GroupTest.ATTRIBUTES_1_GROUP_ID}
                        String getKimTypeId() {GroupTest.KIM_TYPE_1.id}
                        KimType getKimType() {GroupTest.KIM_TYPE_1}
                        KimAttribute getKimAttribute() {GroupTest.KIM_ATTRIBUTE_1}
                        String getAttributeValue() {GroupTest.ATTRIBUTES_1_VALUE}
                        Long getVersionNumber() { GroupTest.ATTRIBUTES_1_VER_NBR }
                        String getObjectId() { GroupTest.ATTRIBUTES_1_OBJ_ID }}).build(),
                    KimAttributeData.Builder.create(new KimAttributeDataContract() {
				        String getId() {GroupTest.ATTRIBUTES_2_ID}
                        String getAssignedToId() {GroupTest.ATTRIBUTES_2_GROUP_ID}
                        String getKimTypeId() {GroupTest.KIM_TYPE_2.id}
                        KimType getKimType() {GroupTest.KIM_TYPE_2}
                        KimAttribute getKimAttribute() {GroupTest.KIM_ATTRIBUTE_2}
                        String getAttributeValue() {GroupTest.ATTRIBUTES_2_VALUE}
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
	    /*Assert.assertEquals(2, group.members.size())
        Assert.assertEquals(MEMBER_1_ID, group.members[0].id)
        Assert.assertEquals(MEMBER_1_GROUP_ID, group.members[0].assignedToId)
        Assert.assertEquals(MEMBER_1_MEMBER_ID, group.members[0].memberId)
        Assert.assertEquals(MEMBER_1_TYPE_CD, group.members[0].typeCode)
        Assert.assertEquals(MEMBER_1_ACTIVE_FROM, group.members[0].activeFromDate)
        Assert.assertEquals(MEMBER_1_ACTIVE_TO, group.members[0].activeToDate)
        Assert.assertEquals(MEMBER_1_OBJ_ID, group.members[0].objectId)
        Assert.assertEquals(MEMBER_1_VER_NBR, group.members[0].versionNumber)

        Assert.assertEquals(MEMBER_2_ID, group.members[1].id)
        Assert.assertEquals(MEMBER_2_GROUP_ID, group.members[1].assignedToId)
        Assert.assertEquals(MEMBER_2_MEMBER_ID, group.members[1].memberId)
        Assert.assertEquals(MEMBER_2_TYPE_CD, group.members[1].typeCode)
        Assert.assertEquals(null, group.members[1].activeFromDate)
        Assert.assertEquals(null, group.members[1].activeToDate)
        Assert.assertEquals(MEMBER_2_OBJ_ID, group.members[1].objectId)
        Assert.assertEquals(MEMBER_2_VER_NBR, group.members[1].versionNumber)*/

	    Assert.assertEquals(2, group.attributes.size())
        Assert.assertEquals(ATTRIBUTES_1_ID, group.attributes[0].id)
        Assert.assertEquals(ATTRIBUTES_1_GROUP_ID, group.attributes[0].assignedToId)
        Assert.assertEquals(KIM_TYPE_1, group.attributes[0].kimType)
        Assert.assertEquals(KIM_ATTRIBUTE_1, group.attributes[0].kimAttribute)
        Assert.assertEquals(ATTRIBUTES_1_VALUE, group.attributes[0].attributeValue)
        Assert.assertEquals(ATTRIBUTES_1_OBJ_ID, group.attributes[0].objectId)
        Assert.assertEquals(ATTRIBUTES_1_VER_NBR, group.attributes[0].versionNumber)

        Assert.assertEquals(ATTRIBUTES_2_ID, group.attributes[1].id)
        Assert.assertEquals(ATTRIBUTES_2_GROUP_ID, group.attributes[1].assignedToId)
        Assert.assertEquals(KIM_TYPE_2, group.attributes[1].kimType)
        Assert.assertEquals(KIM_ATTRIBUTE_2, group.attributes[1].kimAttribute)
        Assert.assertEquals(ATTRIBUTES_2_VALUE, group.attributes[1].attributeValue)
        Assert.assertEquals(ATTRIBUTES_2_OBJ_ID, group.attributes[1].objectId)
        Assert.assertEquals(ATTRIBUTES_2_VER_NBR, group.attributes[1].versionNumber)


        Assert.assertEquals(VERSION_NUMBER, group.versionNumber)
	    Assert.assertEquals(OBJECT_ID, group.objectId)
	}
}
