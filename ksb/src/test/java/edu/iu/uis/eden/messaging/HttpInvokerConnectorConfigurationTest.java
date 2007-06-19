/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.junit.Test;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.serviceconnectors.HttpInvokerConnector;

/**
 * A test which tests the RemoteResourceServiceLocatorImpl class.
 *
 * @author ewestfal
 */
public class HttpInvokerConnectorConfigurationTest extends TestCase {

	
	/**
	 * Tests the configuration and initialization of the HttpClient which is
	 * used for the invocation of remote service calls.
	 */
	@Test public void testHttpClientConfiguration() throws Exception {
		// test the default configuration
		Core.init(new SimpleConfig());
		HttpInvokerConnector httpConnector = new HttpInvokerConnector(new ServiceInfo());
		HttpClient httpClient = httpConnector.getHttpClient();
		
		HttpConnectionManager connectionManager = httpClient.getHttpConnectionManager();
		assertTrue("Should be a multi-threaded connection manager.", connectionManager instanceof MultiThreadedHttpConnectionManager);
		int defaultMaxConnectionsPerHost = connectionManager.getParams().getDefaultMaxConnectionsPerHost();
		assertEquals(20, defaultMaxConnectionsPerHost);
		assertEquals(20, connectionManager.getParams().getMaxTotalConnections());
		assertEquals(10000, connectionManager.getParams().getConnectionTimeout());
		assertEquals(10000, httpClient.getParams().getConnectionManagerTimeout());
		assertEquals(CookiePolicy.RFC_2109, httpClient.getParams().getCookiePolicy());
		
		// re-init the core with some of these paramters changed
		SimpleConfig config = new SimpleConfig();
		Properties properties = config.getProperties();
		properties.put("http.connection-manager.max-total", "500");
		properties.put("http.connection-manager.timeout", "5000");
		properties.put("http.connection.timeout", "15000");
		properties.put("http.somerandomproperty", "thisismyproperty");
		properties.put("http.authentication.preemptive", "false");
		Core.init(config);
		
		httpConnector = new HttpInvokerConnector(new ServiceInfo());
		httpClient = httpConnector.getHttpClient();
		
		connectionManager = httpClient.getHttpConnectionManager();
		assertTrue("Should be a multi-threaded connection manager.", connectionManager instanceof MultiThreadedHttpConnectionManager);
		defaultMaxConnectionsPerHost = connectionManager.getParams().getDefaultMaxConnectionsPerHost();
		assertEquals(20, defaultMaxConnectionsPerHost);
		assertEquals(500, connectionManager.getParams().getMaxTotalConnections());
		assertEquals(15000, connectionManager.getParams().getConnectionTimeout());
		assertEquals(5000, httpClient.getParams().getConnectionManagerTimeout());
		assertEquals(CookiePolicy.RFC_2109, httpClient.getParams().getCookiePolicy());
		assertFalse(httpClient.getParams().isAuthenticationPreemptive());
		
		// do another one that checks that booleans are working properly
		config = new SimpleConfig();
		properties = config.getProperties();
		properties.put("http.authentication.preemptive", "true");
		Core.init(config);
		
		httpConnector = new HttpInvokerConnector(new ServiceInfo());
		httpClient = httpConnector.getHttpClient();
		
		assertTrue(httpClient.getParams().isAuthenticationPreemptive());
		
		// check setting the classname of the connection manager
		config = new SimpleConfig();
		properties = config.getProperties();
		properties.put("http.connection-manager.class", MyHttpConnectionManager.class.getName());
		Core.init(config);
		
		httpConnector = new HttpInvokerConnector(new ServiceInfo());
		httpClient = httpConnector.getHttpClient();
		
		connectionManager = httpClient.getHttpConnectionManager();
		assertTrue("Should be a MyHttpConnectionManager.", connectionManager instanceof MyHttpConnectionManager);
		
		// now try setting the retry handler which expects an object that we can't pipe through our
		// String-based configuration.  This is an illegal parameter to configure and we should
		// recieve a WorkflowRuntimeException
		config = new SimpleConfig();
		properties = config.getProperties();
		properties.put("http.method.retry-handler", "badparm");
		Core.init(config);
		
		try {
			httpConnector = new HttpInvokerConnector(new ServiceInfo());
			fail("An exception should have been thrown because the retry handler is an illegal parameter.");
		} catch (Exception e) {
		    //nothing to do here
		}
		
	}
	
	public static class MyHttpConnectionManager extends SimpleHttpConnectionManager {
	    // nothing extra needed
	}
	
}
