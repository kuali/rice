/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.testharness.data.PerTestUnitTestData;
import org.kuali.rice.testharness.data.UnitTestData;
import org.kuali.rice.testharness.data.UnitTestSql;

/**
 * Basic test to verify we can access the GroupService through the GRL.
 *
 * FIXME: This test causes compile failure in CI because it is relying on a class
 * in KNS module test source, which maven does not expose.  Either the KNS test
 * classes need to be moved into KNS proper, or pushed down into a shared module.
 *
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */

@PerTestUnitTestData(
        @UnitTestData(
            sqlStatements = {
/*            		@UnitTestSql("DELETE  FROM KIM_NAMESPACE_DFLT_ATTRIBS_T WHERE ID=1 "),
            		@UnitTestSql("DELETE  FROM KIM_ROLES_GROUPS_T WHERE ROLE_ID=1 "),
            		@UnitTestSql("DELETE  FROM KIM_ROLES_GROUPS_T WHERE ROLE_ID=2 "),                

            		@UnitTestSql("DELETE  FROM KIM_ROLES_T WHERE ID=1 "),
            		@UnitTestSql("DELETE  FROM KIM_ROLES_T WHERE ID=2 "),
            		            		            		
            		@UnitTestSql("DELETE  FROM KIM_GROUPS_PRINCIPALS_T WHERE GROUP_ID=1 "),
            		@UnitTestSql("DELETE  FROM KIM_GROUPS_PRINCIPALS_T WHERE GROUP_ID=2 "),


            		            		                
            		@UnitTestSql("DELETE  FROM KIM_PRINCIPALS_T WHERE ID=1 "),
            		@UnitTestSql("DELETE  FROM KIM_PRINCIPALS_T WHERE ID=2 "),
            		@UnitTestSql("DELETE  FROM KIM_PRINCIPALS_T WHERE ID=3 "),
            		            		                
            		            		                

            		            		            		
            		@UnitTestSql("DELETE  FROM KIM_GROUP_ATTRIBUTES_T WHERE ID=1"),
            		@UnitTestSql("DELETE  FROM KIM_GROUP_ATTRIBUTES_T WHERE ID=2"),
            		@UnitTestSql("DELETE  FROM KIM_GROUP_ATTRIBUTES_T WHERE ID=3"),
            		            		
            		@UnitTestSql("DELETE  FROM KIM_ATTRIBUTE_TYPES_T WHERE ID=1 "),
            		            		                
            		@UnitTestSql("DELETE  FROM KIM_GROUPS_GROUPS_T WHERE PARENT_GROUP_ID=1"),

            		@UnitTestSql("DELETE  FROM KIM_GROUPS_T WHERE ID=1"),
            		@UnitTestSql("DELETE  FROM KIM_GROUPS_T WHERE ID=2"),
            		@UnitTestSql("DELETE  FROM KIM_GROUPS_T WHERE ID=3"),
            		@UnitTestSql("DELETE  FROM KIM_GROUP_TYPES_T WHERE ID=1"),

            		            		                
            		@UnitTestSql("DELETE  FROM KIM_ENTITYS_T WHERE ID=1"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITYS_T WHERE ID=2"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITYS_T WHERE ID=3"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITY_TYPES_T WHERE ID=1"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITY_TYPES_T WHERE ID=2"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITY_TYPES_T WHERE ID=3"),
            		@UnitTestSql("DELETE  FROM KIM_ENTITY_TYPES_T WHERE ID=4 "),
*/
            		@UnitTestSql("INSERT   INTO  KIM_GROUP_TYPES_T (ID, NAME, DESCRIPTION,WORKFLOW_DOCUMENT_TYPE) VALUES(1, 'TEST_GROUP_TYPE','TEST_GROUP_TYPE', 'WF DOC') "),
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(1, 'KIM Test Group1', 'Test case',1) "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(2, 'KIM Test Group2', 'Test case',1) "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_T (ID, NAME, DESCRIPTION,GROUP_TYPE_ID) VALUES(3, 'KIM Test Group3', 'Test case',1) "),
            		            		                
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_GROUPS_T (PARENT_GROUP_ID, MEMBER_GROUP_ID) VALUES(1, 2) "),
            		            		                
/*            		            		                
            		@UnitTestSql("INSERT   INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (1, 'Person', 'This entity type represents a person.')"),
            		@UnitTestSql("INSERT   INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (2, 'System', 'This entity type represents another system.')"),
            		@UnitTestSql("INSERT   INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (3, 'Service', 'This entity type represents a service.')"),
            		@UnitTestSql("INSERT   INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (4, 'Process', 'This entity type represents a process.')"),
*/
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_ATTRIBUTE_TYPES_T (ID,NAME,DESCRIPTION) VALUES(1, 'TEST_ATTRIBUTE','TEST') "),            		                
            		@UnitTestSql("INSERT   INTO  KIM_GROUP_ATTRIBUTES_T (ID,GROUP_ID,ATTRIBUTE_NAME,ATTRIBUTE_TYPE_ID,ATTRIBUTE_VALUES) VALUES(1, 2,'GROUP_CONTACT_PERSON',1,'JOHN DOE') "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUP_ATTRIBUTES_T (ID,GROUP_ID,ATTRIBUTE_NAME,ATTRIBUTE_TYPE_ID,ATTRIBUTE_VALUES) VALUES(2, 1,'GROUP_PHONE',1,'607-753-0000') "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUP_ATTRIBUTES_T (ID,GROUP_ID,ATTRIBUTE_NAME,ATTRIBUTE_TYPE_ID,ATTRIBUTE_VALUES) VALUES(3, 1,'GROUP_CONTACT_PERSON',1,'JOHN DOE') "),
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_ENTITYS_T (ID,ENTITY_TYPE_ID) VALUES(1, 1) "),
            		@UnitTestSql("INSERT   INTO  KIM_ENTITYS_T (ID,ENTITY_TYPE_ID) VALUES(2, 1) "),
            		@UnitTestSql("INSERT   INTO  KIM_ENTITYS_T (ID,ENTITY_TYPE_ID) VALUES(3, 1) "),
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_PRINCIPALS_T (ID,NAME,ENTITY_ID) VALUES(1, 'ADMIN_PRINCIPAL',1)"),
            		@UnitTestSql("INSERT   INTO  KIM_PRINCIPALS_T (ID,NAME,ENTITY_ID) VALUES(2, 'POWER_USER_PRINCIPAL',2) "),
            		@UnitTestSql("INSERT   INTO  KIM_PRINCIPALS_T (ID,NAME,ENTITY_ID) VALUES(3, 'USER_PRINCIPAL',3) "),
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_PRINCIPALS_T (GROUP_ID,PRINCIPAL_ID) VALUES(1, 1) "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_PRINCIPALS_T (GROUP_ID,PRINCIPAL_ID) VALUES(1, 2) "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_PRINCIPALS_T (GROUP_ID,PRINCIPAL_ID) VALUES(1, 3) "),
            		@UnitTestSql("INSERT   INTO  KIM_GROUPS_PRINCIPALS_T (GROUP_ID,PRINCIPAL_ID) VALUES(2, 3) "),
            		            		                
            		@UnitTestSql("INSERT   INTO  KIM_ROLES_T (ID,NAME,DESCRIPTION) VALUES(1,'KIM_ADMINISTRATOR', 'Administrator of KIM') "),
            		@UnitTestSql("INSERT   INTO  KIM_ROLES_T (ID,NAME,DESCRIPTION) VALUES(2,'KIM_USER', 'User of KIM') "),
            		@UnitTestSql("INSERT   INTO  KIM_ROLES_GROUPS_T (ROLE_ID,GROUP_ID) VALUES(1,1) "),
            		@UnitTestSql("INSERT   INTO  KIM_ROLES_GROUPS_T (ROLE_ID,GROUP_ID) VALUES(1,2) "),
            		@UnitTestSql("INSERT   INTO  KIM_ROLES_GROUPS_T (ROLE_ID,GROUP_ID) VALUES(2,1)")
            }
        )
)
//@Ignore
public class GroupServiceTest extends KIMTestCase {
	
    private static final String TEST_GROUP1 = "KIM Test Group1";
    private static final String TEST_GROUP2 = "KIM Test Group2";
    private static final String TEST_GROUP3 = "KIM Test Group3";

	@Test
    public void testGetAllGroupNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> groupNames = groupService.getAllGroupNames();
        assertEquals(3, groupNames.size());
        for(String name:groupNames){
        	assertTrue(name.equals(TEST_GROUP1)||name.equals(TEST_GROUP2)||name.equals(TEST_GROUP3));
        }

    }


    @Test
    public void testGetAllGroupNames_SyncSOAP() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<String> groupNames = groupService.getAllGroupNames();
        assertEquals(3, groupNames.size());
        for(String name:groupNames){
        	assertTrue(name.equals(TEST_GROUP1)||name.equals(TEST_GROUP2)||name.equals(TEST_GROUP3));
        }
   }
    
    @Test
    public void testGetAllGroups_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<GroupDTO> groupDtos = groupService.getAllGroups();
        assertEquals(3, groupDtos.size());
        for(GroupDTO groupDto:groupDtos){
        	assertTrue(groupDto.getName().equals(TEST_GROUP1)||groupDto.getName().equals(TEST_GROUP2)||groupDto.getName().equals(TEST_GROUP3));
        }
   }
    
    @Test
    public void testGetAllGroups_SyncSOAP() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupSoapService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<GroupDTO> groupDtos = groupSoapService.getAllGroups();
        assertEquals(3, groupDtos.size());
        for(GroupDTO groupDto:groupDtos){
        	assertTrue(groupDto.getName().equals(TEST_GROUP1)||groupDto.getName().equals(TEST_GROUP2)||groupDto.getName().equals(TEST_GROUP3));
        }
   }

    @Test    
    public void testGetGroupMembers_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <GroupDTO> memberGroups = groupService.getGroupMembers(this.TEST_GROUP1);
        assertTrue(memberGroups.size()==1);
        for(GroupDTO mg:memberGroups){
        	assertTrue(mg.getName().equals(this.TEST_GROUP2));
        }
    }

    @Test    
    public void testGetGroupMembers_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <GroupDTO> memberGroups = groupService.getGroupMembers(this.TEST_GROUP1);
        assertTrue(memberGroups.size()==1);
        for(GroupDTO mg:memberGroups){
        	assertTrue(mg.getName().equals(this.TEST_GROUP2));
        }
    }

    @Test    
    public void testGetGroupMemberNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <String> memberGroupNames = groupService.getGroupMemberNames(this.TEST_GROUP1);
        assertTrue(memberGroupNames.size()==1);
        for(String name:memberGroupNames){
        	assertTrue(name.equals(this.TEST_GROUP2));
        }
    }


    @Test    
    public void testGetGroupMemberNames_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <String> memberGroupNames = groupService.getGroupMemberNames(this.TEST_GROUP1);
        assertTrue(memberGroupNames.size()==1);
        for(String name:memberGroupNames){
        	assertTrue(name.equals(this.TEST_GROUP2));
        }
    }
    
    
    
    @Test    
    public void testGetGroupParents_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <GroupDTO> parentGroups = groupService.getGroupParents(this.TEST_GROUP2);
        assertTrue(parentGroups.size()==1);
        for(GroupDTO pg:parentGroups){
        	assertTrue(pg.getName().equals(this.TEST_GROUP1));
        }
    }

    @Test    
    public void testGetGroupParents_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <GroupDTO> parentGroups = groupService.getGroupParents(this.TEST_GROUP2);
        assertTrue(parentGroups.size()==1);
        for(GroupDTO pg:parentGroups){
        	assertTrue(pg.getName().equals(this.TEST_GROUP1));
        }
    }

    @Test    
    public void testGetGroupParentNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <String> parentGroupNames = groupService.getGroupParentNames(this.TEST_GROUP2);
        assertTrue(parentGroupNames.size()==1);
        for(String name:parentGroupNames){
        	assertTrue(name.equals(this.TEST_GROUP1));
        }
    }


    @Test    
    public void testGetGroupParentNames_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List <String> parentGroupNames = groupService.getGroupParentNames(this.TEST_GROUP2);
        assertTrue(parentGroupNames.size()==1);
        for(String name:parentGroupNames){
        	assertTrue(name.equals(this.TEST_GROUP1));
        }
    }
    

    
    
    
    
    @Test    
    public void testGetGroupNamesWithAttributes_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        List <String> groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==2);


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==1);
        assertTrue(groupNamesWithAttributes.get(0).equals(this.TEST_GROUP1));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==1);
        assertTrue(groupNamesWithAttributes.get(0).equals(this.TEST_GROUP1));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0001");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==0);
        

        
    }

    @Test    
    public void testGetGroupNamesWithAttributes_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        List <String> groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==2);


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==1);
        assertTrue(groupNamesWithAttributes.get(0).equals(this.TEST_GROUP1));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==1);
        assertTrue(groupNamesWithAttributes.get(0).equals(this.TEST_GROUP1));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0001");
        
        groupNamesWithAttributes = groupService.getGroupNamesWithAttributes(groupAttributes);
        assertTrue(groupNamesWithAttributes.size()==0);
        

        
    }

    

    @Test    
    public void testGetGroupsWithAttributes_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        List <GroupDTO> groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==2);


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==1);
        assertTrue(groupsWithAttributes.get(0).getName().equals(this.TEST_GROUP1));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==1);
        assertTrue(groupsWithAttributes.get(0).getName().equals(this.TEST_GROUP1));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0001");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==0);
        

        
    }

    
    @Test    
    public void testGetGroupsWithAttributes_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        List <GroupDTO> groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==2);


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==1);
        assertTrue(groupsWithAttributes.get(0).getName().equals(this.TEST_GROUP1));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==1);
        assertTrue(groupsWithAttributes.get(0).getName().equals(this.TEST_GROUP1));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0001");
        
        groupsWithAttributes = groupService.getGroupsWithAttributes(groupAttributes);
        assertTrue(groupsWithAttributes.size()==0);
        

        
    }

    
    @Test  
    public void testGetPrincipalMembers_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<PrincipalDTO> principals = groupService.getPrincipalMembers(this.TEST_GROUP1);
        assertTrue(principals.size()==3);

        principals = groupService.getPrincipalMembers(this.TEST_GROUP2);
        assertTrue(principals.size()==1);
        assertTrue(principals.get(0).getName().equals("USER_PRINCIPAL"));

        principals = groupService.getPrincipalMembers(this.TEST_GROUP3);
        assertTrue(principals.size()==0);
        

        
    }
    
    @Test  
    public void testGetPrincipalMembers_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<PrincipalDTO> principals = groupService.getPrincipalMembers(this.TEST_GROUP1);
        assertTrue(principals.size()==3);

        principals = groupService.getPrincipalMembers(this.TEST_GROUP2);
        assertTrue(principals.size()==1);
        assertTrue(principals.get(0).getName().equals("USER_PRINCIPAL"));

        principals = groupService.getPrincipalMembers(this.TEST_GROUP3);
        assertTrue(principals.size()==0);
        

        
    }
    
    
    @Test  
    public void testGetPrincipalMemberNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> principals = groupService.getPrincipalMemberNames(this.TEST_GROUP1);
        assertTrue(principals.size()==3);

        principals = groupService.getPrincipalMemberNames(this.TEST_GROUP2);
        assertTrue(principals.size()==1);
        assertTrue(principals.get(0).equals("USER_PRINCIPAL"));

        principals = groupService.getPrincipalMemberNames(this.TEST_GROUP3);
        assertTrue(principals.size()==0);
    }


    @Test  
    public void testGetPrincipalMemberNames_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> principals = groupService.getPrincipalMemberNames(this.TEST_GROUP1);
        assertTrue(principals.size()==3);

        principals = groupService.getPrincipalMemberNames(this.TEST_GROUP2);
        assertTrue(principals.size()==1);
        assertTrue(principals.get(0).equals("USER_PRINCIPAL"));

        principals = groupService.getPrincipalMemberNames(this.TEST_GROUP3);
        assertTrue(principals.size()==0);
    }



    @Test  
    public void testGetGroupRoleNames_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> roleNames = groupService.getRoleNamesForGroup(this.TEST_GROUP1);
        assertTrue(roleNames.size()==2);
        
        roleNames = groupService.getRoleNamesForGroup(this.TEST_GROUP2);
        assertTrue(roleNames.size()==1);
        assertTrue(roleNames.get(0).equals("KIM_ADMINISTRATOR"));
    }
    @Test  
    public void testGetGroupRoleNames_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<String> roleNames = groupService.getRoleNamesForGroup(this.TEST_GROUP1);
        assertTrue(roleNames.size()==2);
        
        roleNames = groupService.getRoleNamesForGroup(this.TEST_GROUP2);
        assertTrue(roleNames.size()==1);
        assertTrue(roleNames.get(0).equals("KIM_ADMINISTRATOR"));
    }

    
    
    @Test  
    public void testGetGroupRoles_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<RoleDTO> roles = groupService.getRolesForGroup(this.TEST_GROUP1);
        assertTrue(roles.size()==2);
        
        roles = groupService.getRolesForGroup(this.TEST_GROUP2);
        assertTrue(roles.size()==1);
        assertTrue(roles.get(0).getName().equals("KIM_ADMINISTRATOR"));
    }


    @Test  
    public void testGetGroupRoles_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);

        List<RoleDTO> roles = groupService.getRolesForGroup(this.TEST_GROUP1);
        assertTrue(roles.size()==2);
        
        roles = groupService.getRolesForGroup(this.TEST_GROUP2);
        assertTrue(roles.size()==1);
        assertTrue(roles.get(0).getName().equals("KIM_ADMINISTRATOR"));
    }

    @Test    
    public void testHasAttributes_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP1, groupAttributes));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP1, groupAttributes));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP2, groupAttributes));

        
        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        assertTrue(!groupService.hasAttributes(this.TEST_GROUP2, groupAttributes));
        
        
    }

    
    @Test    
    public void testHasAttributes_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        HashMap<String,String> groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP1, groupAttributes));
        

        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP1, groupAttributes));


        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_CONTACT_PERSON","JOHN DOE");
        assertTrue(groupService.hasAttributes(this.TEST_GROUP2, groupAttributes));

        
        groupAttributes = new HashMap();
        groupAttributes.put("GROUP_PHONE","607-753-0000");
        assertTrue(!groupService.hasAttributes(this.TEST_GROUP2, groupAttributes));
        
        
    }
    
    

    

    @Test    
    public void testGetEntityIds_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<Long> entityIds = groupService.getEntityMemberIds(this.TEST_GROUP1);
        assertTrue(entityIds.size()==3);
        
        entityIds = groupService.getEntityMemberIds(this.TEST_GROUP2);
        assertTrue(entityIds.size()==1);
        assertTrue(entityIds.get(0)==3);
        
    }
    @Test
    public void testGetEntityIds_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<Long> entityIds = groupService.getEntityMemberIds(this.TEST_GROUP1);
        assertTrue(entityIds.size()==3);
        
        entityIds = groupService.getEntityMemberIds(this.TEST_GROUP2);
        assertTrue(entityIds.size()==1);
        assertTrue(entityIds.get(0)==3);
        
    }


    @Test    
    public void testGetEntityMembers_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<EntityDTO> entityDtos = groupService.getEntityMembers(this.TEST_GROUP1);
        assertTrue(entityDtos.size()==3);
        
        entityDtos = groupService.getEntityMembers(this.TEST_GROUP2);
        assertTrue(entityDtos.size()==1);
        assertTrue(entityDtos.get(0).getId()==3);
        assertTrue(entityDtos.get(0).getEntityTypeId()==1);
        
    }


    @Test    
    public void testGetEntityMembers_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<EntityDTO> entityDtos = groupService.getEntityMembers(this.TEST_GROUP1);
        assertTrue(entityDtos.size()==3);
        
        entityDtos = groupService.getEntityMembers(this.TEST_GROUP2);
        assertTrue(entityDtos.size()==1);
        assertTrue(entityDtos.get(0).getId()==3);
        assertTrue(entityDtos.get(0).getEntityTypeId()==1);
        
    }

//
    
    @Test    
    public void testGetPersonMembers_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<EntityDTO> entityDtos = groupService.getEntityMembers(this.TEST_GROUP1);
        assertTrue(entityDtos.size()==3);
        
        
        entityDtos = groupService.getEntityMembers(this.TEST_GROUP2);
        assertTrue(entityDtos.size()==1);
        assertTrue(entityDtos.get(0).getId()==3);
        
        
    }
    
    @Test    
    public void testGetPersonMembers_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<EntityDTO> entityDtos = groupService.getEntityMembers(this.TEST_GROUP1);
        assertTrue(entityDtos.size()==3);
        
        
        entityDtos = groupService.getEntityMembers(this.TEST_GROUP2);
        assertTrue(entityDtos.size()==1);
        assertTrue(entityDtos.get(0).getId()==3);
    }

    @Test    
    public void testGetPersonMemberIds_SyncJava() throws Exception {
        QName serviceName = new QName("KIM", "groupService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<Long> entityIds = groupService.getEntityMemberIds(this.TEST_GROUP1);
        assertTrue(entityIds.size()==3);
        
        
        entityIds = groupService.getEntityMemberIds(this.TEST_GROUP2);
        assertTrue(entityIds.size()==1);
        assertTrue(entityIds.get(0)==3);
    }


    @Test    
    public void testGetPersonMemberIds_SyncSoap() throws Exception {
        QName serviceName = new QName("KIM", "groupSoapService");
        GroupService groupService = (GroupService) GlobalResourceLoader.getService(serviceName);
        List<Long> entityIds = groupService.getEntityMemberIds(this.TEST_GROUP1);
        assertTrue(entityIds.size()==3);
        
        
        entityIds = groupService.getEntityMemberIds(this.TEST_GROUP2);
        assertTrue(entityIds.size()==1);
        assertTrue(entityIds.get(0)==3);
    }

    
}
