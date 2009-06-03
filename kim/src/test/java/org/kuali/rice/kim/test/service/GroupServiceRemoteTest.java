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

import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;

/**
 * Test the GroupService via remote calls
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupServiceRemoteTest extends GroupServiceTest {

	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
		return new BaseLifecycle() {
			public void start() throws Exception { 
				new JettyServerLifecycle(ServiceTestUtils.getConfigIntProp("kim.test.port"), "/" + ServiceTestUtils.getConfigProp("app.context.name"), "/../kim/src/test/webapp").start();
				super.start();
			}
		};
	}
	
	/**
	 * This method tries to get a client proxy for the specified KIM service
	 * 
	 * @param  svcName - name of the KIM service desired
	 * @return the proxy object
	 * @throws Exception 
	 */
	@Override
	protected Object getKimService(String svcNamespace, String... svcNames) throws Exception {
		return ServiceTestUtils.getRemoteServiceProxy(svcNamespace, svcNames[0], svcNames[1]);
	}
}
