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

package org.kuali.rice.kim.impl.group

import groovy.mock.interceptor.MockFor
import java.sql.Timestamp
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kim.api.group.Group
import org.kuali.rice.kim.api.group.GroupMember
import org.kuali.rice.kim.api.group.GroupService
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.kim.util.KimConstants
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo


class GroupServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    static Map<String, GroupBo> sampleGroups = new HashMap<String, GroupBo>()
    static Map<String, GroupBo> sampleGroupsByName = new HashMap<String, GroupBo>()
    static GroupBo memberGroup
    static GroupMemberBo memberGroupMember
    static List<GroupMemberBo> group1Members = new ArrayList<GroupMemberBo>()
    static List<GroupMemberBo> group2Members = new ArrayList<GroupMemberBo>()

    MockFor businessObjectServiceMockFor
    BusinessObjectService bos

    MockFor groupDaoMockFor
    GroupDao groupDao

    GroupServiceImpl groupServiceImpl
    GroupService groupService

    @BeforeClass
    static void createSampleGroupBOs() {
        Calendar calendarFrom = Calendar.getInstance();

        //Doing setup in a static context since bring up and down a server is an expensive operation
        GroupMemberBo member1 = new GroupMemberBo(id: "1", memberId: "234", groupId: "1",
                typeCode: "P", activeFromDate: new Timestamp(new Long(1262402288000)),
                activeToDate: new Timestamp(new Long(4102543088000)))
        GroupMemberBo member2 = new GroupMemberBo(id: "2", memberId: "240", groupId: "1",
                typeCode: "P", activeFromDate: new Timestamp(new Long(1262402288000)),
                activeToDate: new Timestamp(new Long(4102543088000)))
        GroupMemberBo member3 = new GroupMemberBo(id: "3", memberId: "114", groupId: "1",
                typeCode: "G", activeFromDate: new Timestamp(new Long(1262402288000)),
                activeToDate: new Timestamp(new Long(4102543088000)))

        GroupMemberBo member4 = new GroupMemberBo(id: "4", memberId: "250", groupId: "2",
                typeCode: "P", activeFromDate: new Timestamp(new Long(1262402288000)),
                activeToDate: new Timestamp(new Long(4102543088000)))
        GroupMemberBo member5 = new GroupMemberBo(id: "5", memberId: "114", groupId: "2",
                typeCode: "G", activeFromDate: new Timestamp(new Long(1262402288000)),
                activeToDate: new Timestamp(new Long(4102543088000)))

        for (bo in [member1, member2, member3 ]) {
            group1Members.add(bo)
        }

        for (bo in [member4, member5 ]) {
            group2Members.add(bo)
        }

        KimTypeBo kimTypeBo = new KimTypeBo(id: "2", serviceName: "service", namespaceCode: "KUALI", name: "thisType",
                active: true)

        GroupAttributeBo attribute1 = new GroupAttributeBo(id: "1", assignedToId: "2", kimAttributeId: "1",
                         attributeValue: "Y", kimTypeId: "2", kimType: kimTypeBo)


        List<GroupAttributeBo> attributesForOtherGroup = new ArrayList<GroupAttributeBo>()
        attributesForOtherGroup.add(attribute1)

        memberGroup = new GroupBo(active: true, id: "114", namespaceCode: "PUNK",
                name: "membergroup", description: "this is some member group", kimTypeId: "1")
        memberGroupMember = member3
        GroupBo someGroup = new GroupBo(active: true, id: "1", namespaceCode: "PUNK",
                name: "somegroup", description: "this is some group", kimTypeId: "1", members: group1Members)
        GroupBo otherGroup = new GroupBo(active: true, id: "2", namespaceCode: "ROCK",
                name: "othergroup", description: "this is some other group", kimTypeId: "2", members: group2Members,
                attributes: attributesForOtherGroup)
        GroupBo thirdGroup = new GroupBo(active: true, id: "114", namespaceCode: "SOMETHING",
                name: "HMMM", description: "this is some weird group", kimTypeId: "1")
        for (bo in [someGroup, otherGroup, thirdGroup]) {
            sampleGroups.put(bo.id, bo)
            sampleGroupsByName.put(bo.namespaceCode + ";" + bo.name, bo)
        }
    }

    @Before
    void setupMockContext() {
        businessObjectServiceMockFor = new MockFor(BusinessObjectService.class)
        groupDaoMockFor = new MockFor(GroupDao.class)

    }

    @Before
    void setupServiceUnderTest() {
        groupServiceImpl = new GroupServiceImpl()
        groupService = groupServiceImpl    //assign Interface type to implementation reference for unit test only
    }

    void injectBusinessObjectServiceIntoGroupService() {
        bos =  businessObjectServiceMockFor.proxyDelegateInstance()
        groupServiceImpl.setBusinessObjectService(bos)
    }

    void injectGroupDaoIntoGroupService() {
        groupDao = groupDaoMockFor.proxyDelegateInstance()
        groupServiceImpl.setGroupDao(groupDao)
    }

    @Test
    public void test_getGroup() {
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1..sampleGroups.size()) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }
        injectBusinessObjectServiceIntoGroupService()
        for (String id : sampleGroups.keySet()) {
            Group group = groupService.getGroup(id)
            Assert.assertEquals(GroupBo.to(sampleGroups.get(id)), group)
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_getGroupNonExistent() {
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, Object primaryKey -> return null
        }
        injectBusinessObjectServiceIntoGroupService()
        Group group = groupService.getGroup("badId")
        Assert.assertNull(group)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_getGroupByName() {
        businessObjectServiceMockFor.demand.findMatching(1..sampleGroupsByName.size()) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroupsByName.get(map.get(KimConstants.UniqueKeyConstants.NAMESPACE_CODE) + ";" + map.get(KimConstants.UniqueKeyConstants.GROUP_NAME)))
        }
        injectBusinessObjectServiceIntoGroupService()

        for (String name : sampleGroupsByName.keySet()) {
            GroupBo tempGroup = sampleGroupsByName.get(name)
            Group group = groupService.getGroupByName(tempGroup.namespaceCode, tempGroup.name)
            Assert.assertEquals(GroupBo.to(sampleGroupsByName.get(name)), group)
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_getGroupByNameNonExistent() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        Group group = groupService.getGroupByName("badNamespace", "noname")
        Assert.assertNull(group)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_isMemberOfGroup() {
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> group1Members
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> group2Members
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        Assert.assertTrue("Principal '240' should be a member of 'somegroup'", groupService.isMemberOfGroup("240", "1"))

        //Should not be member
        Assert.assertFalse("Principal '240' should not be a member of 'othergroup'", groupService.isMemberOfGroup("240", "2"))

        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_getGroupsForPrincipal() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        List<Group> groups = groupService.getGroupsForPrincipal("240");
        Assert.assertEquals("Pricipal should only be a member of 1 group", 1, groups.size())
        List<Group> expectedGroups = new ArrayList<Group>();
        expectedGroups.add(GroupBo.to(sampleGroups.get("1")))
        Assert.assertEquals(expectedGroups, groups)
        businessObjectServiceMockFor.verify(bos)
    }

        @Test
    public void test_getGroupsForPrincipalByNamespace() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        List<Group> groups = groupService.getGroupsForPrincipalByNamespace("240", "PUNK")
        Assert.assertEquals("Pricipal should only be a member of 1 group", 1, groups.size())
        List<Group> expectedGroups = new ArrayList<Group>();
        expectedGroups.add(GroupBo.to(sampleGroups.get("1")))
        Assert.assertEquals(expectedGroups, groups)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    public void test_lookupGroups() {
        groupDaoMockFor.demand.getGroups(1) {
            Map map -> return new ArrayList<GroupBo>(sampleGroups.values())
        }
        injectGroupDaoIntoGroupService()
        List<String> expectedIds = new ArrayList<String>()
        expectedIds.addAll(sampleGroups.keySet())

        List<String> groupIds = groupService.lookupGroupIds(Collections.singletonMap("active", "true"))

        Assert.assertEquals(groupIds.size(), sampleGroups.size())
        Assert.assertEquals(expectedIds, groupIds)
        groupDaoMockFor.verify(groupDao)
    }

    @Test
    public void test_lookupGroupIds() {
        groupDaoMockFor.demand.getGroups(1) {
            Map map -> return new ArrayList<GroupBo>(sampleGroups.values())
        }
        injectGroupDaoIntoGroupService()
        List<Group> expectedGroups = new ArrayList<Group>()
        for (GroupBo groupBo : sampleGroups.values()) {
            expectedGroups.add(GroupBo.to(groupBo))
        }

        List<Group> groups = groupService.lookupGroups(Collections.singletonMap("active", "true"))

        Assert.assertEquals(groups.size(), sampleGroups.size())
        Assert.assertEquals(expectedGroups, groups)
        groupDaoMockFor.verify(groupDao)
    }

    @Test
    public void test_isDirectMemberOfGroup() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        Assert.assertTrue("Principal '240' should be a direct member of 'somegroup'", groupService.isDirectMemberOfGroup("240", "1"))

        //Should not be member
        Assert.assertFalse("Principal '240' should not be a member of 'othergroup'", groupService.isDirectMemberOfGroup("240", "2"))

        businessObjectServiceMockFor.verify(bos)
    }

    @Test void test_getGroupIdsForPrincipal() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        List<String> groupIds = groupService.getGroupIdsForPrincipal("240");
        Assert.assertEquals("PricipalId 240 should only be a member of 1 group", 1, groupIds.size())
        List<String> expectedGroupIds = Collections.singletonList("1");
        Assert.assertEquals(expectedGroupIds, groupIds)
        businessObjectServiceMockFor.verify(bos)

    }

    @Test
    void test_getGroupIdsForPrincipalByNamespace() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        List<String> groupIds = groupService.getGroupIdsForPrincipalByNamespace("240", "PUNK");
        Assert.assertEquals("PricipalId 240 should only be a member of 1 group", 1, groupIds.size())
        List<String> expectedGroupIds = Collections.singletonList("1");
        Assert.assertEquals(expectedGroupIds, groupIds)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getDirectGroupIdsForPrincipal() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(sampleGroups.get("1").getMembers().get(0))
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, String groupId -> return sampleGroups.get(groupId)
        }
        injectBusinessObjectServiceIntoGroupService()

        // Should be member
        List<String> groupIds = groupService.getDirectGroupIdsForPrincipal("240");
        Assert.assertEquals("PricipalId 240 should only be a member of 1 group", 1, groupIds.size())
        List<String> expectedGroupIds = Collections.singletonList("1");
        Assert.assertEquals(expectedGroupIds, groupIds)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getMemberPrincipalIds() {
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(0..3) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }

        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = new ArrayList<String>()
        for (GroupMemberBo memberBo : sampleGroups.get("1").getMembers()){
            if (memberBo.typeCode.equals("P")) {
                expectedIds.add(memberBo.memberId)
            }
        }
        List<String> actualIds = groupService.getMemberPrincipalIds("1")
        Assert.assertEquals("Should be " + expectedIds.size() + "Ids returned", expectedIds.size(), actualIds.size())
        for (String id : actualIds) {
            Assert.assertTrue(id + "should be in List", expectedIds.contains(id) )
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getDirectMemberPrincipalIds() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return group1Members
        }
        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = new ArrayList<String>()
        for (GroupMemberBo memberBo : sampleGroups.get("1").getMembers()){
            if (memberBo.typeCode.equals("P")) {
                expectedIds.add(memberBo.memberId)
            }
        }
        List<String> actualIds = groupService.getDirectMemberPrincipalIds("1")
        Assert.assertEquals("Should be " + expectedIds.size() + "Ids returned", expectedIds.size(), actualIds.size())
        for (String id : actualIds) {
            Assert.assertTrue(id + "should be in List", expectedIds.contains(id) )
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getMemberGroupIds() {
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(0..4) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }
        /*businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }
        *//*businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return group1Members
        }*//*
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }*/
        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = new ArrayList<String>()
        for (GroupMemberBo memberBo : sampleGroups.get("1").getMembers()){
            if (memberBo.typeCode.equals("G")) {
                expectedIds.add(memberBo.memberId)
            }
        }
        List<String> actualIds = groupService.getMemberGroupIds("1")
        Assert.assertEquals("Should be " + expectedIds.size() + " Ids returned", expectedIds.size(), actualIds.size())
        for (String id : actualIds) {
            Assert.assertTrue(id + " should be in List", expectedIds.contains(id) )
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getDirectMemberGroupIds() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return group1Members
        }
        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = new ArrayList<String>()
        for (GroupMemberBo memberBo : sampleGroups.get("1").getMembers()){
            if (memberBo.typeCode.equals("G")) {
                expectedIds.add(memberBo.memberId)
            }
        }
        List<String> actualIds = groupService.getDirectMemberGroupIds("1")
        Assert.assertEquals("Should be " + expectedIds.size() + "Ids returned", expectedIds.size(), actualIds.size())
        for (String id : actualIds) {
            Assert.assertTrue(id + "should be in List", expectedIds.contains(id) )
        }
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getParentGroupIds() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(memberGroupMember)
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1..3) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.emptyList()
        }
        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = Collections.singletonList(sampleGroups.get("1").id)
        List<String> actualIds = groupService.getParentGroupIds(memberGroupMember.memberId)
        Assert.assertEquals("Should be " + expectedIds.size() + "Ids returned", expectedIds.size(), actualIds.size())
        Assert.assertEquals(expectedIds, actualIds)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getDirectParentGroupIds() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return Collections.singletonList(memberGroupMember)
        }
        businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1..3) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }
        injectBusinessObjectServiceIntoGroupService()

        List<String> expectedIds = Collections.singletonList(sampleGroups.get("1").id)
        List<String> actualIds = groupService.getDirectParentGroupIds(memberGroupMember.memberId)
        Assert.assertEquals("Should be " + expectedIds.size() + "Ids returned", expectedIds.size(), actualIds.size())
        Assert.assertEquals(expectedIds, actualIds)
        businessObjectServiceMockFor.verify(bos)
    }

    @Test
    void test_getMembers() {
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return group1Members
        }
        businessObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, Map map -> return group2Members
        }
        /*businessObjectServiceMockFor.demand.findBySinglePrimaryKey(1..sampleGroups.size()) {
            Class clazz, Object primaryKey -> return sampleGroups.get(primaryKey)
        }*/
        injectBusinessObjectServiceIntoGroupService()

        List<GroupMember> expected = new ArrayList<GroupMember>();
        for (GroupBo groupBo : sampleGroups.values()) {
            for (GroupMemberBo memberBo : groupBo.members) {
                expected.add(GroupMemberBo.to(memberBo))
            }
        }
        List<String> groupIds = ["1", "2"]
        List<GroupMember> actual = groupService.getMembers(groupIds)
        Assert.assertEquals("Should be " + expected.size() + "Ids returned", expected.size(), actual.size())
        for (GroupMember gm : actual) {
            Assert.assertTrue(gm.getId() + "should be in List", expected.contains(gm) )
        }
        businessObjectServiceMockFor.verify(bos)
    }
}
