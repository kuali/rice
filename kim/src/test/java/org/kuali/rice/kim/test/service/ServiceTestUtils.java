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
package org.kuali.rice.kim.test.service;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;

/**
 * This is a description of what this class does - jimt don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ServiceTestUtils {
	static	String serverHostStr = getConfigProp("kim.test.host");
	static	String serverPortStr = getConfigProp("kim.test.remote.port");
	static	String appContext = getConfigProp("app.context.name");
	
	public static int getConfigIntProp(String intPropKey) {
		return Integer.parseInt(getConfigProp(intPropKey));
	}

	public static String getConfigProp(String propKey) {
		return ConfigContext.getCurrentContextConfig().getProperty(propKey);
	}
	
	/**
	 * This method tries to get a client proxy for the specified 
	 * remote KIM service
	 * 
	 * @param  clazz - service's class
	 * @return the proxy object
	 * @throws Exception 
	 */
	public static Object getRemoteServiceProxy(String svcNamespace, String svcName, String svcClassName) throws Exception {
		Class<?> serviceClass = Class.forName(svcClassName);
		
		// protocol will probably be configured eventually as well
		String svcAddr = "http://" + serverHostStr + 
							(null != serverPortStr ? ":" + Integer.parseInt(serverPortStr) : "") + "/" +
							appContext + "/" + "remoting/" + svcName;

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
}
