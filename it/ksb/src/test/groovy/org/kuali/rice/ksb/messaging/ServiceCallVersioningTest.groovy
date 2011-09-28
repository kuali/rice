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
package org.kuali.rice.ksb.messaging

import org.junit.Test
import org.kuali.rice.ksb.messaging.remotedservices.EchoService
import javax.xml.namespace.QName
import org.kuali.rice.ksb.messaging.remotedservices.SOAPService
import org.kuali.rice.ksb.messaging.remotedservices.JaxWsEchoService
import org.apache.cxf.frontend.ClientProxyFactoryBean
import org.apache.cxf.aegis.databinding.AegisDatabinding
import org.apache.cxf.interceptor.LoggingInInterceptor
import org.apache.cxf.interceptor.LoggingOutInterceptor
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory
import org.apache.cxf.endpoint.Client

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.URI;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.remotedservices.EchoService;
import org.kuali.rice.ksb.messaging.remotedservices.JaxWsEchoService;
import org.kuali.rice.ksb.messaging.remotedservices.SOAPService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase
import org.kuali.rice.core.cxf.interceptors.ServiceCallVersioningOutInterceptor
import org.kuali.rice.ksb.messaging.remotedservices.ServiceCallInformationHolder;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class ServiceCallVersioningTest extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}

	private String getClient1Port() {
		return ConfigContext.getCurrentContextConfig().getProperty("ksb.client1.port")
	}

	@Test void testSimpleSOAPService() throws Exception{
		EchoService echoService = (EchoService)GlobalResourceLoader.getService(new QName("TestCl1", "soap-echoService"))
        echoService.captureHeaders()
        assertHeadersCaptured()
	}

	/*@Test void testJaxWsSOAPService(){
		JaxWsEchoService jaxwsEchoService = (JaxWsEchoService) GlobalResourceLoader.getService(new QName("TestCl1", "jaxwsEchoService"))
		jaxwsEchoService.captureHeaders();
	}*/

    def void assertHeadersCaptured() {
        Map<String, List<String>> headers = ServiceCallInformationHolder.stuff.get("capturedHeaders")
        System.out.println("HEADERS");
        System.out.println(headers);
		assertTrue(headers.get(ServiceCallVersioningOutInterceptor.KUALI_RICE_ENVIRONMENT_HEADER).contains("dev"))
        assertTrue(headers.get(ServiceCallVersioningOutInterceptor.KUALI_RICE_VERSION_HEADER).any { it =~ /2\.0.*/ })
        assertTrue(headers.get(ServiceCallVersioningOutInterceptor.KUALI_APP_NAME_HEADER).contains("ServiceCallVersioningTest"))
        assertTrue(headers.get(ServiceCallVersioningOutInterceptor.KUALI_APP_VERSION_HEADER).contains("1.0.0"))
    }
}