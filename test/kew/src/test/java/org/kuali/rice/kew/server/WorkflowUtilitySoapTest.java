/*
 * Copyright 2005-2009 The Kuali Foundation
 *
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
package org.kuali.rice.kew.server;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.util.KEWWebServiceConstants;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;

public class WorkflowUtilitySoapTest extends WorkflowUtilityTest {

    @Override
	public void setUp() throws Exception {
		super.setUp();
		setWorkflowUtility((WorkflowUtility) getRemoteServiceProxy(KEWWebServiceConstants.MODULE_TARGET_NAMESPACE, KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_NAME, KEWWebServiceConstants.WorkflowUtility.INTERFACE_CLASS));
	}

	@Override
	protected void verifyEmptyArray(String qualifier, Object[] array) {
    	assertNull("Number of " + qualifier + "s Returned Should be 0 so return object should be null", array);
	}

	protected Object getRemoteServiceProxy(String svcNamespace, String svcName, String svcClassName) throws Exception {
		Class<?> serviceClass = Class.forName(svcClassName);
		
		String svcAddr = generateServiceAddress(svcName);
		ClientProxyFactoryBean clientFactory = new JaxWsProxyFactoryBean();
		clientFactory.setServiceClass(serviceClass);
		clientFactory.setAddress(svcAddr);
		
		ServiceInfo svcInfo = new ServiceInfo();
		svcInfo.setEndpointUrl(svcAddr);
		svcInfo.setServiceDefinition(new SOAPServiceDefinition());
		//Set logging (not currently) and security interceptors
		// clientFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		clientFactory.getOutInterceptors().add(new CXFWSS4JOutInterceptor(svcInfo));
		// clientFactory.getInInterceptors().add(new LoggingInInterceptor());
		clientFactory.getInInterceptors().add(new CXFWSS4JInInterceptor(svcInfo));
		
		return clientFactory.create();		
	}

	protected String generateServiceAddress(String svcName) {
//		return "http://localhost:" + getExtraJettyServerPort() +  getExtraJettyServerContextName() + "/remoting/" + svcName;
		return "http://localhost:" + getJettyServerPort() +  getJettyServerContextName() + "/remoting/" + svcName;
	}

//	@Override
//	protected List<Lifecycle> getPerTestLifecycles() {
//		List<Lifecycle> lifecycles = super.getPerTestLifecycles();
//		lifecycles.add(buildJettyServer(getExtraJettyServerPort(), getExtraJettyServerContextName(), getJettyServerRelativeWebappRoot()));
//		return lifecycles;
//	}

	protected int getExtraJettyServerPort() {
		return getJettyServerPort() + 1;
	}

	protected String getExtraJettyServerContextName() {
		return getJettyServerContextName() + "2";
	}

}
