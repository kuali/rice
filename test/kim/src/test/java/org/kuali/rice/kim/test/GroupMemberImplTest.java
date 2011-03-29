/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kim.test;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.test.KIMTestCase;


/**
 * This is test base that should be used for all KIM unit tests. All non-web unit tests for KIM should extend this base
 * class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Ignore
 public class GroupMemberImplTest extends KIMTestCase {
	 /**
	private GroupMemberImpl groupMember;
	
	@Override
	public void setUp() {
		super.setUp();
		groupMember = new GroupMemberImpl;
	}
	
	@Override
	public void tearDown() {
		super.tearDown();
		groupMember = null();
	}
				
	@Test
	public void testGroupMemberId(String groupMemberId) {
		String groupMemberId = groupMember.setGroupMemberId("g1111");
		assertEquals("g1111", groupMemberId.getGroupMemberId());
	}
	
	@Test
	public void testGroupId(String groupId) {
		String groupIds = groupService.getGroupId("g222");
		assertEquals("g222", groupMember.getGroupMemberId());
	}
	**/
}
	
