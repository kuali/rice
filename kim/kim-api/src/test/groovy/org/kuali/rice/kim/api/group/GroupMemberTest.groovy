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
import org.junit.Assert
import org.junit.Test

class GroupMemberTest {

    private static final String ID = "1"
    private static final String GROUP_ID = "50"
    private static final String MEMBER_ID = "1"
    private static final String TYPE_CD = "P"
    private static final Long VER_NBR = new Long(1)
    private static final String OBJ_ID = UUID.randomUUID()
    private static final String ACTIVE_FROM_STRING = "2011-01-01 12:00:00.0"
    private static final Timestamp ACTIVE_FROM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ACTIVE_FROM_STRING).toTimestamp()
    private static final String ACTIVE_TO_STRING = "2012-01-01 12:00:00.0"
    private static final Timestamp ACTIVE_TO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ACTIVE_TO_STRING).toTimestamp()

        private static final String XML = """
    <groupMember xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <groupId>${GROUP_ID}</groupId>
        <memberId>${MEMBER_ID}</memberId>
        <typeCode>${TYPE_CD}</typeCode>
        <activeFromDate>${ACTIVE_FROM.getTime()}</activeFromDate>
        <activeToDate>${ACTIVE_TO.getTime()}</activeToDate>
        <versionNumber>${VER_NBR}</versionNumber>
        <objectId>${OBJ_ID}</objectId>
    </groupMember>
    """

        @Test
	public void testXmlMarshaling() {
	  JAXBContext jc = JAXBContext.newInstance(GroupMember.class)
	  Marshaller marshaller = jc.createMarshaller()
	  StringWriter sw = new StringWriter()

	  GroupMember groupMember = createGroupMemberFromPassedInContract()
	  marshaller.marshal(groupMember,sw)
	  String xml = sw.toString()

	  Unmarshaller unmarshaller = jc.createUnmarshaller();
	  Object actual = unmarshaller.unmarshal(new StringReader(xml))
	  Object expected = unmarshaller.unmarshal(new StringReader(XML))
	  Assert.assertEquals(expected,actual)
	}

    private GroupMember createGroupMemberFromPassedInContract() {
		GroupMember groupMember =  GroupMember.Builder.create(new GroupMemberContract() {
            String getId() {GroupMemberTest.ID}
            String getGroupId() {GroupMemberTest.GROUP_ID}
            String getMemberId() {GroupMemberTest.MEMBER_ID}
            String getTypeCode() {GroupMemberTest.TYPE_CD}
            Timestamp getActiveFromDate() {GroupMemberTest.ACTIVE_FROM}
            Timestamp getActiveToDate() {GroupMemberTest.ACTIVE_TO}
            Long getVersionNumber() { GroupMemberTest.VER_NBR }
            String getObjectId() { GroupMemberTest.OBJ_ID }
            boolean isActive() { this.isActive() }
            boolean isActive(Timestamp t) { this.isActive(t) }
		}).build()

        return groupMember
	}

    @Test
	public void testXmlUnmarshal() {
        JAXBContext jc = JAXBContext.newInstance(GroupMember.class)
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GroupMember groupMember = (GroupMember) unmarshaller.unmarshal(new StringReader(XML))

        Assert.assertEquals(ID, groupMember.id)
        Assert.assertEquals(GROUP_ID, groupMember.groupId)
        Assert.assertEquals(MEMBER_ID, groupMember.memberId)
        Assert.assertEquals(TYPE_CD, groupMember.typeCode)
        Assert.assertEquals(ACTIVE_FROM, groupMember.activeFromDate)
        Assert.assertEquals(ACTIVE_TO, groupMember.activeToDate)
        Assert.assertEquals(OBJ_ID, groupMember.objectId)
        Assert.assertEquals(VER_NBR, groupMember.versionNumber)
	}
}
