/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.config;


import java.io.IOException;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.CoreConfigHelper;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.ksb.messaging.HttpClientHelper;
import org.kuali.rice.ksb.messaging.KSBHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;


/**
 * Initializes and loads webservice resources for the Embedded plugin.
 * Currently, the only 2 services which are exposed are the utility service and
 * the document actions service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ThinClientResourceLoader extends BaseResourceLoader {
		private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ThinClientResourceLoader.class);

    	private static final String DEFAULT_MAX_CONNECTIONS = "40";
    	private static final String DEFAULT_CONNECTION_TIMEOUT = "60000";
    	private static final String DEFAULT_CONNECTION_MANAGER_TIMEOUT = "60000";
    	public static final String MAX_CONNECTIONS = "kew." + HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS; // kew.http.connection-manager.max-total
    	public static final String CONNECTION_TIMEOUT = "kew." + HttpConnectionManagerParams.CONNECTION_TIMEOUT; // kew.http.connection.timeout
    	public static final String CONNECTION_MANAGER_TIMEOUT = "kew." + HttpClientParams.CONNECTION_MANAGER_TIMEOUT; // kew.http.connection-manager.timeout
    	public static final String DOCUMENT_ENDPOINT = "workflowdocument.javaservice.endpoint";
    	public static final String SECURE_DOCUMENT_ENDPOINT = "secure.workflowdocument.javaservice.endpoint";
    	public static final String UTILITY_ENDPOINT = "workflowutility.javaservice.endpoint";
    	public static final String SECURE_UTILITY_ENDPOINT = "secure.workflowutility.javaservice.endpoint";
    	public static final String IDENTITY_ENDPOINT = "identity.javaservice.endpoint"; 
    	public static final String SECURE_IDENTITY_ENDPOINT = "secure.identity.javaservice.endpoint"; 
    	public static final String GROUP_ENDPOINT = "group.javaservice.endpoint"; 
    	public static final String SECURE_GROUP_ENDPOINT = "secure.group.javaservice.endpoint"; 
    	
    	
    	private static final String IDLE_CONNECTION_THREAD_INTERVAL_PROPERTY = "ksb.thinClient.idleConnectionThreadInterval";
    	private static final String IDLE_CONNECTION_TIMEOUT_PROPERTY = "ksb.thinClient.idleConnectionTimeout";
    	private static final String DEFAULT_IDLE_CONNECTION_THREAD_INTERVAL = "7500";
    	private static final String DEFAULT_IDLE_CONNECTION_TIMEOUT = "5000";
    	private static final String RETRY_SOCKET_EXCEPTION_PROPERTY = "ksb.thinClient.retrySocketException";
    	
    	private Map<String, Object> services = Collections.synchronizedMap(new HashMap<String, Object>());

    	private IdleConnectionTimeoutThread ictt;
    	
	public ThinClientResourceLoader() {
		super(new QName(CoreConfigHelper.getApplicationId(), "ThinClientResourceLoader"));
		ictt = new IdleConnectionTimeoutThread();
	}

	@Override
	public void start() throws Exception {
		super.start();
		initializeHttpClientParams();
		runIdleConnectionTimeout();
		//springLifecycle.start();
	}



	@Override
	public void stop() throws Exception {
		super.stop();
		if (ictt != null) {
		    ictt.shutdown();
		}
		//springLifecycle.stop();
	}

	public Object getService(QName serviceQName) {
	    String serviceName = serviceQName.getLocalPart();
	    	Object cachedService = services.get(serviceName);
	    	if (cachedService != null) {
	    	    return cachedService;
	    	}
		if (serviceName.equals(KewApiConstants.WORKFLOW_UTILITY_SERVICE)) {
		    throw new UnsupportedOperationException("Reimplement me! - see KULRICE-5061");
//			WorkflowUtility utility = getWorkflowUtility();
//			services.put(serviceName, utility);
//			return utility;
//		} else if (serviceName.equals(KewApiConstants.WORKFLOW_DOCUMENT_ACTIONS_SERVICE)) {
//			WorkflowDocumentActions documentActions = getWorkflowDocument();
//			services.put(serviceName, documentActions);
//			return documentActions;
		} else if (serviceName.equals(KimApiServiceLocator.KIM_IDENTITY_SERVICE)) {
			IdentityService identityService = getIdentityService();
			services.put(serviceName, identityService);
			return identityService;
		} else if (serviceName.equals(KimApiServiceLocator.KIM_GROUP_SERVICE)) {
			GroupService groupService = getGroupService();
			services.put(serviceName, groupService);
			return groupService;
		}
	    return null;
	}

//	public WorkflowUtility getWorkflowUtility() {
//	    return (WorkflowUtility)getServiceProxy(WorkflowUtility.class, UTILITY_ENDPOINT, SECURE_UTILITY_ENDPOINT);
//	}
//
//	public WorkflowDocumentActions getWorkflowDocument() {
//	    return (WorkflowDocumentActions)getServiceProxy(WorkflowDocumentActions.class, DOCUMENT_ENDPOINT, SECURE_DOCUMENT_ENDPOINT);
//	}
	
	public IdentityService getIdentityService() {
	    return (IdentityService)getServiceProxy(IdentityService.class, IDENTITY_ENDPOINT, SECURE_IDENTITY_ENDPOINT);
	}

	public GroupService getGroupService() {
	    return (GroupService)getServiceProxy(GroupService.class, GROUP_ENDPOINT, SECURE_GROUP_ENDPOINT);
	}

	protected Object getServiceProxy(Class serviceInterface, String endpointParam, String secureEndpointParam) {
	    HttpInvokerProxyFactoryBean proxyFactory = new HttpInvokerProxyFactoryBean();
	    String serviceUrl = ConfigContext.getCurrentContextConfig().getProperty(endpointParam);
	    if (StringUtils.isEmpty(serviceUrl)) {
		throw new IllegalArgumentException("The " + endpointParam + " configuration parameter was not defined but is required.");
	    }
	    proxyFactory.setServiceUrl(serviceUrl);
	    proxyFactory.setServiceInterface(serviceInterface);
	    String secureProp = ConfigContext.getCurrentContextConfig().getProperty(secureEndpointParam);
	    Boolean secureIt = null;
        secureIt = secureProp == null || Boolean.valueOf(secureProp);
	    KSBHttpInvokerRequestExecutor executor = new KSBHttpInvokerRequestExecutor(getHttpClient());
	    executor.setSecure(secureIt);
	    proxyFactory.setHttpInvokerRequestExecutor(executor);
	    proxyFactory.afterPropertiesSet();
	    return proxyFactory.getObject();
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
		Properties configProps = ConfigContext.getCurrentContextConfig().getProperties();
		for (Iterator iterator = configProps.keySet().iterator(); iterator.hasNext();) {
			String paramName = (String) iterator.next();
			if (paramName.startsWith("http.")) {
				HttpClientHelper.setParameter(httpClientParams, paramName, (String) configProps.get(paramName));
			}
		}

		String maxConnectionsValue = configProps.getProperty(MAX_CONNECTIONS);
		if (!StringUtils.isEmpty(maxConnectionsValue)) {
		    Integer maxConnections = new Integer(maxConnectionsValue);
		    Map<HostConfiguration, Integer> maxHostConnectionsMap = new HashMap<HostConfiguration, Integer>();
		    maxHostConnectionsMap.put(HostConfiguration.ANY_HOST_CONFIGURATION, maxConnections);
		    httpClientParams.setParameter(HttpConnectionManagerParams.MAX_HOST_CONNECTIONS, maxHostConnectionsMap);
		    httpClientParams.setIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, maxConnections);
		}

		String connectionManagerTimeoutValue = configProps.getProperty(CONNECTION_MANAGER_TIMEOUT);
		if (!StringUtils.isEmpty(connectionManagerTimeoutValue)) {
		    httpClientParams.setLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, new Long(connectionManagerTimeoutValue));
		}

		String connectionTimeoutValue = configProps.getProperty(CONNECTION_TIMEOUT);
		if (!StringUtils.isEmpty(connectionTimeoutValue)) {
		    httpClientParams.setIntParameter(HttpConnectionManagerParams.CONNECTION_TIMEOUT, new Integer(connectionTimeoutValue));
		}
	}

	protected void configureDefaultHttpClientParams(HttpParams params) {
		params.setParameter(HttpClientParams.CONNECTION_MANAGER_CLASS, MultiThreadedHttpConnectionManager.class);
		params.setParameter(HttpMethodParams.COOKIE_POLICY, CookiePolicy.RFC_2109);
		params.setLongParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, new Long(DEFAULT_CONNECTION_MANAGER_TIMEOUT));
		Map<HostConfiguration, Integer> maxHostConnectionsMap = new HashMap<HostConfiguration, Integer>();
		maxHostConnectionsMap.put(HostConfiguration.ANY_HOST_CONFIGURATION, new Integer(DEFAULT_MAX_CONNECTIONS));
		params.setParameter(HttpConnectionManagerParams.MAX_HOST_CONNECTIONS, maxHostConnectionsMap);
		params.setIntParameter(HttpConnectionManagerParams.MAX_TOTAL_CONNECTIONS, new Integer(DEFAULT_MAX_CONNECTIONS));
		params.setIntParameter(HttpConnectionManagerParams.CONNECTION_TIMEOUT, new Integer(DEFAULT_CONNECTION_TIMEOUT));
	
		boolean retrySocketException = new Boolean(ConfigContext.getCurrentContextConfig().getProperty(RETRY_SOCKET_EXCEPTION_PROPERTY));
		if (retrySocketException) {
		    LOG.info("Installing custom HTTP retry handler to retry requests in face of SocketExceptions");
		    params.setParameter(HttpMethodParams.RETRY_HANDLER, new CustomHttpMethodRetryHandler());
		}
	}
	
		/**
	 * Idle connection timeout thread added as a part of the fix for ensuring that 
	 * threads that timed out need to be cleaned or and send back to the pool so that 
	 * other clients can use it.
	 *
	 */
	private void runIdleConnectionTimeout() {
	    if (ictt != null) {
		    String timeoutInterval = ConfigContext.getCurrentContextConfig().getProperty(IDLE_CONNECTION_THREAD_INTERVAL_PROPERTY);
		    if (StringUtils.isBlank(timeoutInterval)) {
			timeoutInterval = DEFAULT_IDLE_CONNECTION_THREAD_INTERVAL;
		    }
		    String connectionTimeout = ConfigContext.getCurrentContextConfig().getProperty(IDLE_CONNECTION_TIMEOUT_PROPERTY);
		    if (StringUtils.isBlank(connectionTimeout)) {
			connectionTimeout = DEFAULT_IDLE_CONNECTION_TIMEOUT;
		    }
		    
		    ictt.addConnectionManager(getHttpClient().getHttpConnectionManager());
		    ictt.setTimeoutInterval(new Integer(timeoutInterval));
		    ictt.setConnectionTimeout(new Integer(connectionTimeout));
		    //start the thread
		    ictt.start();
	    }
	}
	
	private static final class CustomHttpMethodRetryHandler extends DefaultHttpMethodRetryHandler {

	    @Override
	    public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
		boolean shouldRetry = super.retryMethod(method, exception, executionCount);
		if (!shouldRetry && exception instanceof SocketException) {
		    LOG.warn("Retrying request because of SocketException!", exception);
		    shouldRetry = true;
		}
		return shouldRetry;
	    }
	    
	}

}
