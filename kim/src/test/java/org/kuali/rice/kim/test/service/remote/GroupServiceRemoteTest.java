/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.test.service.remote;

import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.test.service.GroupServiceTest;
import org.kuali.rice.kim.test.service.ServiceTestUtils;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

/**
 * Test the GroupService via remote calls
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupServiceRemoteTest extends GroupServiceTest {

	public void setUp() throws Exception {
		super.setUp();
		setGroupService((GroupService) ServiceTestUtils.getRemoteServiceProxy(KIMWebServiceConstants.MODULE_TARGET_NAMESPACE, KIMWebServiceConstants.GroupService.WEB_SERVICE_NAME, KIMWebServiceConstants.GroupService.INTERFACE_CLASS));
		setGroupUpdateService((GroupUpdateService) ServiceTestUtils.getRemoteServiceProxy(KIMWebServiceConstants.MODULE_TARGET_NAMESPACE, KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_NAME, KIMWebServiceConstants.GroupUpdateService.INTERFACE_CLASS));
	}

	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = super.getPerTestLifecycles();
		lifecycles.add(getJettyServerLifecycle());
		return lifecycles;
	}

	@Override
	@Test
	public void testPrincipalMembership() {
		super.testPrincipalMembership();
		
		// change group g4 back to inactive; remote service not transactional
		// TODO - fix that
		GroupInfo g4Info = getGroupService().getGroupInfo("g4");
		g4Info.setActive(false);
		getGroupUpdateService().updateGroup("g4", g4Info);
	}

}
