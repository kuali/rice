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

import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.test.service.IdentityServiceTest;
import org.kuali.rice.kim.test.service.ServiceTestUtils;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

/**
 * Test the RoleService via remote calls
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityServiceRemoteTest extends IdentityServiceTest {

	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = super.getPerTestLifecycles();
		lifecycles.add(getJettyServerLifecycle());
		return lifecycles;
	}
	
	@Override
	protected IdentityService findIdSvc() throws Exception {
		return (IdentityService) ServiceTestUtils.getRemoteServiceProxy(KIMWebServiceConstants.MODULE_TARGET_NAMESPACE, KIMWebServiceConstants.IdentityService.WEB_SERVICE_NAME, KIMWebServiceConstants.IdentityService.INTERFACE_CLASS);
	}
}
