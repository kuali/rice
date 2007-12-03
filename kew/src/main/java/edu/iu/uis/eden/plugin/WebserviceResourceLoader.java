/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.plugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.BaseResourceLoader;
import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.SpringLoader;
import edu.iu.uis.eden.core.dependencylifecycles.SpringLifeCycle;
import edu.iu.uis.eden.messaging.HttpClientHelper;
import edu.iu.uis.eden.messaging.KEWHttpInvokerRequestExecutor;
import edu.iu.uis.eden.server.WorkflowDocumentActions;
import edu.iu.uis.eden.server.WorkflowUtility;

/**
 * Initializes and loads webservice resources for the Embedded plugin.
 * Currently, the only 2 services which are exposed are the utility service and
 * the document actions service.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebserviceResourceLoader extends BaseResourceLoader {
	
	private Lifecycle springLifecycle = new SpringLifeCycle("WebClientBeans.xml");
	
	public WebserviceResourceLoader() {
		super(new QName(Core.getCurrentContextConfig().getMessageEntity(), ResourceLoader.WEB_SERVICE_PLUGIN));
	}

	
	
	@Override
	public void start() throws Exception {
		super.start();
		springLifecycle.start();
	}



	@Override
	public void stop() throws Exception {
		super.stop();
		springLifecycle.stop();
	}

	public Object getService(QName serviceName) {
		return ContextClassLoaderProxy.wrap(getServiceInternal(serviceName));
	}

	protected Object getServiceInternal(QName serviceName) {
		if (serviceName.getLocalPart().equals(KEWServiceLocator.WORKFLOW_UTILITY_SERVICE)) {
			return getWorkflowUtility();
		} else if (serviceName.getLocalPart().equals(KEWServiceLocator.WORKFLOW_DOCUMENT_ACTIONS_SERVICE)) {
			return getWorkflowDoucment();
		}
		return SpringLoader.getInstance().getService(serviceName);
	}

	public WorkflowUtility getWorkflowUtility() {
		HttpInvokerProxyFactoryBean proxyFactory = new HttpInvokerProxyFactoryBean();
		proxyFactory.setServiceUrl(Core.getCurrentContextConfig().getProperty("workflowutility.javaservice.endpoint"));
		
		proxyFactory.setServiceInterface(WorkflowUtility.class);
		String secureProp = Core.getCurrentContextConfig().getProperty("secure.workflowutility.javaservice.endpoint");
		Boolean secureIt = null;
		if (secureProp == null) {
			secureIt = true;
		} else {
			secureIt = new Boolean(secureProp);
		}
		
		KEWHttpInvokerRequestExecutor executor = new KEWHttpInvokerRequestExecutor(secureIt); // new
																						// KEWHttpInvokerRequestExecutor(getHttpClient());
		proxyFactory.setHttpInvokerRequestExecutor(executor);
		proxyFactory.afterPropertiesSet();
		return (WorkflowUtility) proxyFactory.getObject();
	}

	public WorkflowDocumentActions getWorkflowDoucment() {
		HttpInvokerProxyFactoryBean proxyFactory = new HttpInvokerProxyFactoryBean();
		proxyFactory.setServiceUrl(Core.getCurrentContextConfig().getProperty("workflowdocument.javaservice.endpoint"));
		proxyFactory.setServiceInterface(WorkflowDocumentActions.class);
		String secureProp = Core.getCurrentContextConfig().getProperty("secure.workflowdocument.javaservice.endpoint");
		Boolean secureIt = null;
		if (secureProp == null) {
			secureIt = true;
		} else {
			secureIt = new Boolean(secureProp);
		}
		
		KEWHttpInvokerRequestExecutor executor = new KEWHttpInvokerRequestExecutor(secureIt); // new
																						// KEWHttpInvokerRequestExecutor(getHttpClient());
		proxyFactory.setHttpInvokerRequestExecutor(executor);
		proxyFactory.afterPropertiesSet();
		return (WorkflowDocumentActions) proxyFactory.getObject();
	}

	/*
	 * the below code copied from RemoteResourceServiceLocator
	 */

	private HttpClientParams httpClientParams;

	/**
	 * Creates a commons HttpClient for service invocation. Config parameters
	 * that start with http.* are used to configure the client.
	 * 
	 * TODO we need to add support for other invocation protocols and
	 * implementations, but for now...
	 */
	protected HttpClient getHttpClient() {
		return new HttpClient(httpClientParams);
	}

	protected void initializeHttpClientParams() {
		httpClientParams = new HttpClientParams();
		configureDefaultHttpClientParams(httpClientParams);
		Properties configProps = Core.getCurrentContextConfig().getProperties();
		for (Iterator iterator = configProps.keySet().iterator(); iterator.hasNext();) {
			String paramName = (String) iterator.next();
			if (paramName.startsWith("http.")) {
				HttpClientHelper.setParameter(httpClientParams, paramName, (String) configProps.get(paramName));
			}
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
		params.setIntParameter(HttpConnectionManagerParams.CONNECTION_TIMEOUT, 10000);
	}

}
