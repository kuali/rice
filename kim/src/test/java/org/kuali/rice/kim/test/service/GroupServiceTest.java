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
package org.kuali.rice.kim.test.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Test the GroupService 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupServiceTest extends KIMTestCase {

	protected GroupService groupService;
	protected GroupUpdateService groupUpdateService;

	public void setUp() throws Exception {
		super.setUp();
		groupService = (GroupService) getKimService(ServiceTestUtils.getConfigProp("kim.test.namespace.group"),
													  ServiceTestUtils.getConfigProp("kim.test.servicename.group"),
													  ServiceTestUtils.getConfigProp("kim.test.serviceclass.group"));
		groupUpdateService = (GroupUpdateService) getKimService(ServiceTestUtils.getConfigProp("kim.test.namespace.group"),
														  ServiceTestUtils.getConfigProp("kim.test.servicename.groupupdate"),
														  ServiceTestUtils.getConfigProp("kim.test.serviceclass.groupupdate"));
	}
	
	@Test
	public void testGetDirectMemberGroupIds() {
		List<String> groupIds = groupService.getDirectMemberGroupIds("g1");

		assertTrue( "g1 must contain group g2", groupIds.contains( "g2" ) );
		assertFalse( "g1 must not contain group g3", groupIds.contains( "g3" ) );

		groupIds = groupService.getDirectMemberGroupIds("g2");
		
		assertTrue( "g2 must contain group g3", groupIds.contains( "g3" ) );
		assertFalse( "g2 must not contain group g4 (inactive)", groupIds.contains( "g4" ) );
		
	}
	
	@Test
	public void testGetMemberGroupIds() {
		List<String> groupIds = groupService.getMemberGroupIds("g1");

		assertTrue( "g1 must contain group g2", groupIds.contains( "g2" ) );
		assertTrue( "g1 must contain group g3", groupIds.contains( "g3" ) );
		assertFalse( "g1 must not contain group g4 (inactive)", groupIds.contains( "g4" ) );

		groupIds = groupService.getMemberGroupIds("g2");

		assertTrue( "g2 must contain group g3", groupIds.contains( "g3" ) );
		assertFalse( "g2 must not contain group g1", groupIds.contains( "g1" ) );
	}
	
	// test principal membership
	@Test
	public void testPrincipalMembership() {
		assertTrue( "p1 must be in g2", groupService.isMemberOfGroup("p1", "g2") );
		assertTrue( "p1 must be direct member of g2", groupService.isDirectMemberOfGroup("p1", "g2") );
		assertTrue( "p3 must be in g2", groupService.isMemberOfGroup("p3", "g2") );
		assertFalse( "p3 should not be a direct member of g2", groupService.isDirectMemberOfGroup("p3", "g2") );
		assertFalse( "p4 should not be reported as a member of g2 (g4 is inactive)", groupService.isMemberOfGroup("p4", "g2") );
		
		// re-activate group 4
		GroupInfo g4Info = groupService.getGroupInfo("g4");
		g4Info.setActive(true);
		groupUpdateService.updateGroup("g4", g4Info);

		assertTrue( "p4 should be reported as a member of g2 (now that g4 is active)", groupService.isMemberOfGroup("p4", "g2") );
	}
	
	/**
	 * This method tries to get a client proxy for the specified KIM service
	 * 
	 * @param  svcName - name of the KIM service desired
	 * @return the proxy object
	 * @throws Exception 
	 */
	protected Object getKimService(String svcNamespace, String... svcNames) throws Exception {
		// TODO: local namespace should be a valid, non-partial namespace (unlike 'KIM')
		return GlobalResourceLoader.getService(new QName("KIM", svcNames[0]));
	}
}
