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
package org.kuali.rice.kim.test.service;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.RemotedServiceHolder;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.messaging.serviceconnectors.SOAPConnector;

import javax.xml.namespace.QName;
import java.util.List;

import static org.junit.Assert.fail;

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

	/*@Override
	protected Lifecycle getLoadApplicationLifecycle() {
		return getJettyServerLifecycle();
	} */
	
	private int getConfigIntProp(String intPropKey) {
		return Integer.parseInt(getConfigProp(intPropKey));
	}

	private String getConfigProp(String propKey) {
		return ConfigContext.getCurrentContextConfig().getProperty(propKey);
	}
	
	/**
	 * This method tries to get a client proxy for the specified KIM service
	 * 
	 * @param  svcName - name of the KIM service desired
	 * @return the proxy object
	 * @throws Exception 
	 */
	protected Object getKimService(String svcName) throws Exception {
		RemoteResourceServiceLocator rrl = KSBResourceLoaderFactory.getRemoteResourceLocator();
		List<RemotedServiceHolder> svcHolders = rrl.getAllServices(new QName("KIM", svcName));
		if (svcHolders.size() > 1) {
			fail("Found more than one RemotedServiceHolder for " + svcName);
		}
		ServiceInfo svcInfo = svcHolders.get(0).getServiceInfo();
		SOAPConnector connector = new SOAPConnector(svcInfo);
		return connector.getService();
	}
}
