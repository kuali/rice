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
package edu.iu.uis.eden.messaging.serviceconnectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.log4j.Logger;
import org.kuali.bus.security.httpinvoker.AuthenticationCommonsHttpInvokerRequestExecutor;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.HttpClientHelper;
import edu.iu.uis.eden.messaging.KEWHttpInvokerProxyFactoryBean;
import edu.iu.uis.eden.messaging.KEWHttpInvokerRequestExecutor;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @version $Revision: 1.3 $ $Date: 2007-12-03 02:51:29 $
 * @since 0.9
 */
public class HttpInvokerConnector extends AbstractServiceConnector {

	private static final Logger LOG = Logger.getLogger(HttpInvokerConnector.class);

	private HttpClientParams httpClientParams;

	private boolean httpClientInitialized = false;

	public HttpInvokerConnector(final ServiceInfo serviceInfo) {
		super(serviceInfo);
		initializeHttpClientParams();
	}
	
	public Object getService() throws Exception {
	    LOG.debug("Getting connector for endpoint " + this.getServiceInfo().getEndpointUrl());
		KEWHttpInvokerProxyFactoryBean client = new KEWHttpInvokerProxyFactoryBean();
		client.setServiceUrl(this.getServiceInfo().getEndpointUrl());
		client.setServiceInfo(this.getServiceInfo());
		
		KEWHttpInvokerRequestExecutor executor;
		
		if (getCredentialsSource() != null) {
		    executor = new AuthenticationCommonsHttpInvokerRequestExecutor(getHttpClient(), getCredentialsSource(), getServiceInfo());
		} else {
		    executor = new KEWHttpInvokerRequestExecutor(getHttpClient());
		}
		executor.setSecure(this.getServiceInfo().getServiceDefinition().getBusSecurity());
		client.setHttpInvokerRequestExecutor(executor);	
		client.afterPropertiesSet();
		return getServiceProxyWithFailureMode(client.getObject(), this.getServiceInfo());
	}

	/**
	 * Creates a commons HttpClient for service invocation. Config parameters
	 * that start with http.* are used to configure the client.
	 * 
	 * TODO we need to add support for other invocation protocols and
	 * implementations, but for now...
	 */
	public HttpClient getHttpClient() {
		return new HttpClient(this.httpClientParams);
	}

	protected void initializeHttpClientParams() {
		if (! this.httpClientInitialized) {
		    this.httpClientParams = new HttpClientParams();
			configureDefaultHttpClientParams(this.httpClientParams);
			Properties configProps = Core.getCurrentContextConfig().getProperties();
			for (Iterator iterator = configProps.keySet().iterator(); iterator.hasNext();) {
				String paramName = (String) iterator.next();
				if (paramName.startsWith("http.")) {
					HttpClientHelper.setParameter(this.httpClientParams, paramName, (String) configProps.get(paramName));
				}
			}
			this.httpClientInitialized = true;
		}
	}

	protected void configureDefaultHttpClientParams(HttpParams params) {
		params.setParameter(HttpClientParams.CONNECTION_MANAGER_CLASS, MultiThreadedHttpConnectionManager.class);
		params.setParameter(HttpMethodParams.COOKIE_POLICY, CookiePolicy.RFC_2109);
		params.setLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, 10000);
		Map<HostConfiguration, Integer> maxHostConnectionsMap = new HashMap<HostConfiguration, Integer>();
		maxHostConnectionsMap.put(HostConfiguration.ANY_HOST_CONFIGURATION, new Integer(20));
		params.setParameter(HttpConnectionManagerParams.MAX_HOST_CONNECTIONS, maxHostConnectionsMap);
		params.setIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, 20);
		params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
	}
}