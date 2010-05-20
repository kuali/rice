/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.workgroup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.xml.GroupXmlJAXBParser;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.xml.GroupMembershipXmlDto;
import org.kuali.rice.kim.xml.GroupXmlDto;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupXmlImportJAXBTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(GroupXmlImportJAXBTest.class);
    private static final String INDENT = "  ";

   /**
     *
     * Verify that a workgroup with a bad user in the xml is not going to be put in the db.
     *
     * @throws Exception
     */

    @Test public void testGroupImportXml() throws Exception {
//    	loadXmlFile("GroupXmlImportTest.xml");
    	InputStream xmlFile = getClass().getResourceAsStream("GroupXmlImportJAXBTest.xml");
    	
    	GroupXmlJAXBParser parser = new GroupXmlJAXBParser();
    	GroupXmlDto groupInfo = parser.parse(xmlFile);

    	assertNotNull(groupInfo);
    	assertTrue(groupInfo.getGroupName().equals("TestUserGroup"));
    	assertTrue(groupInfo.getGroupDescription().equals("Group for test user"));
    	
//        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
//        //verify that the group was ingested
//        Group group = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestUserGroup");
//
//        assertNotNull(group);
//        List<String> members = identityManagementService.getGroupMemberPrincipalIds(group.getGroupId());
//        List<String> groups = identityManagementService.getMemberGroupIds(group.getGroupId());
//        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("ewestfal").getPrincipalId(), group.getGroupId()));
//        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("rkirkend").getPrincipalId(), group.getGroupId()));
//        assertTrue(identityManagementService.isMemberOfGroup("2015", group.getGroupId()));
//        assertTrue(KIMServiceLocator.getGroupService().isGroupMemberOfGroup(identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestWorkgroup").getGroupId(), group.getGroupId()));
    }
    
    @Test public void testGroupExportXml() throws Exception {
        JAXBContext context = JAXBContext.newInstance(GroupXmlDto.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        GroupXmlDto grp = new GroupXmlDto(
            "MyGroup", "Dan's unit test", true);
        List<GroupMembershipXmlDto> mbrs = new ArrayList<GroupMembershipXmlDto>();
        
        GroupMembershipXmlDto mbr1 = new GroupMembershipXmlDto();
        mbr1.setMemberId("CHynde");
        mbr1.setMemberTypeCode("P");
        mbrs.add(mbr1);
        
        GroupMembershipXmlDto mbr2 = new GroupMembershipXmlDto();
        mbr2.setMemberId("BMarley");
        mbr2.setMemberTypeCode("P");
        mbrs.add(mbr2);
        
        GroupMembershipXmlDto mbr3 = new GroupMembershipXmlDto();
        mbr3.setMemberId("Pretenders");
        mbr3.setMemberTypeCode("G");
        mbrs.add(mbr3);
        
        grp.setMembers(mbrs);
        
        AttributeSet attrs = new AttributeSet();
        attrs.put("documentTypeName","Doc");
        attrs.put("routeNodeName","P");
        attrs.put("required","false");
        attrs.put("actionDetailsAtRoleMemberLevel","false");
        grp.setAttributes(attrs);
        marshaller.marshal(grp, new FileWriter(".\\GroupXmlExportJAXBResults.xml"));       
    }
 
}
