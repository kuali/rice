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

package org.kuali.rice.ksb.messaging;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.security.credentials.CredentialsSource;
import org.kuali.rice.core.security.credentials.CredentialsSource.CredentialsType;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.ksb.messaging.exceptionhandling.DefaultMessageExceptionHandler;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.net.URL;


/**
 * The definition of a service on the service bus.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ServiceDefinition implements Serializable {

	private static final Logger LOG = Logger.getLogger(ServiceDefinition.class);
	
	private static final long serialVersionUID = 43631161206712702L;
	
	private transient Object service;
	private String localServiceName;
	private String serviceNameSpaceURI;
	private transient QName serviceName;
	private Boolean queue = Boolean.TRUE;
	private Integer priority;
	private Integer retryAttempts;
	private Long millisToLive;
	private String messageExceptionHandler;
	private String servicePath;
	private URL serviceEndPoint;
	private Boolean busSecurity = Boolean.TRUE;
	private CredentialsType credentialsType;
	private String serviceNamespace;
	
	// if the service is exported from a plugin, we need to ensure it's invoked within the proper classloading context!
	private transient ClassLoader serviceClassLoader;
	
	public ServiceDefinition() {
		serviceClassLoader = ClassLoaderUtils.getDefaultClassLoader();
	}
	
	public ServiceDefinition(final Boolean busSecurity) {
	    Assert.notNull(busSecurity, "busSecurity cannot be null");
	    this.busSecurity = busSecurity;
	}
	
	public Object getService() {
		return this.service;
	}
	public void setService(Object service) {
		this.service = service;
	}
	public String getLocalServiceName() {
		return this.localServiceName;
	}
	public void setLocalServiceName(String serviceName) {
		this.localServiceName = serviceName;
	}
	public String getMessageExceptionHandler() {
		return this.messageExceptionHandler;
	}
	public void setMessageExceptionHandler(String messageExceptionHandler) {
		this.messageExceptionHandler = messageExceptionHandler;
	}
	public Integer getPriority() {
		return this.priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Boolean getQueue() {
		return this.queue;
	}
	public void setQueue(Boolean queue) {
		this.queue = queue;
	}
	public Integer getRetryAttempts() {
		return this.retryAttempts;
	}
	public void setRetryAttempts(Integer retryAttempts) {
		this.retryAttempts = retryAttempts;
	}

	public QName getServiceName() {
		if (this.serviceName == null) {
			if (this.localServiceName == null) {
				int i = 0;
			}
			if (this.serviceNameSpaceURI == null) {
			    this.serviceName = new QName(this.serviceNamespace, this.localServiceName);	
			} else {
			    this.serviceName = new QName(this.serviceNameSpaceURI, this.localServiceName);
			}
			
		}
		return this.serviceName;
	}
	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}
	public URL getServiceEndPoint() {
		return this.serviceEndPoint;
	}
	public void setServiceEndPoint(URL serviceEndPoint) {
		this.serviceEndPoint = serviceEndPoint;
	}
	
	public void setCredentialsType(CredentialsSource.CredentialsType credentialsType) {
	    this.credentialsType = credentialsType;
	}
	
	public CredentialsSource.CredentialsType getCredentialsType() {
	    return this.credentialsType;
	}

	public ClassLoader getServiceClassLoader() {
	    return this.serviceClassLoader;
	}

	public void setServiceClassLoader(ClassLoader serviceClassLoader) {
	    this.serviceClassLoader = serviceClassLoader;
	}
	
	public void validate() {
		
		if (this.serviceName == null && this.localServiceName == null) {
			throw new ConfigurationException("Must give a serviceName or localServiceName");
		}
		
		String serviceNamespace = ConfigContext.getCurrentContextConfig().getServiceNamespace();
		if (serviceNamespace == null) {
			throw new ConfigurationException("Must have a ServiceNamespace");
		}
		this.serviceNamespace = serviceNamespace;
		
//		if (this.serviceName == null) {
//			if (this.serviceNameSpaceURI == null) {
//			    this.serviceName = new QName(ServiceNamespace, this.localServiceName);	
//			} else {
//			    this.serviceName = new QName(this.serviceNameSpaceURI, this.localServiceName);
//			}
//			
//		}
		if (this.serviceName != null && this.localServiceName == null) {
			this.localServiceName = this.getServiceName().getLocalPart();
		}
			
		if (this.servicePath != null){
			if (this.servicePath.endsWith("/")){
				this.servicePath = StringUtils.chop(servicePath);
			}
			if (!this.servicePath.startsWith("/")){
				this.servicePath = "/" + this.servicePath;
			}
		} else {
			this.servicePath = "/";
		}
		
		LOG.debug("Validating service " + this.serviceName);
		
		String endPointURL = ConfigContext.getCurrentContextConfig().getEndPointUrl();
		if (this.serviceEndPoint == null && endPointURL == null) {
			throw new ConfigurationException("Must provide a serviceEndPoint or serviceServletURL");
		} else if (this.serviceEndPoint == null) {
			if (! endPointURL.endsWith("/")) {
				endPointURL += servicePath;
			} else {
				endPointURL = StringUtils.chop(endPointURL) + servicePath;
			}
			try {
				if (servicePath.equals("/")){
					this.serviceEndPoint = new URL(endPointURL + this.getServiceName().getLocalPart());
				} else {
					this.serviceEndPoint = new URL(endPointURL + "/" + this.getServiceName().getLocalPart());
				}
			} catch (Exception e) {
				throw new ConfigurationException("Service Endpoint URL creation failed.", e);
			}
			
		}
		
		if (this.service == null) {
			throw new ConfigurationException("Must provide a service");
		}
		
		if (this.priority == null) {
			setPriority(5);
		}
		
		if (this.retryAttempts == null) {
			setRetryAttempts(0);
		}
		
		if (this.millisToLive == null) {
			setMillisToLive(new Long(-1));
		}
		
		if (getMessageExceptionHandler() == null) {
			setMessageExceptionHandler(DefaultMessageExceptionHandler.class.getName());
		}
	}
	public String getServiceNameSpaceURI() {
		return this.serviceNameSpaceURI;
	}
	public void setServiceNameSpaceURI(String serviceNameSpaceURI) {
		this.serviceNameSpaceURI = serviceNameSpaceURI;
	}
	public Long getMillisToLive() {
		return this.millisToLive;
	}
	public void setMillisToLive(Long millisToLive) {
		this.millisToLive = millisToLive;
	}
	public Boolean getBusSecurity() {
		return this.busSecurity;
	}
	public void setBusSecurity(Boolean busSecurity) {
		this.busSecurity = busSecurity;
	}
	public boolean isSame(ServiceDefinition serviceDefinition) {
		return this.getBusSecurity().equals(serviceDefinition.getBusSecurity()) &&
		this.getMessageExceptionHandler().equals(serviceDefinition.getMessageExceptionHandler()) &&
		this.getMillisToLive().equals(serviceDefinition.getMillisToLive()) &&
		this.getPriority().equals(serviceDefinition.getPriority()) &&
		this.getQueue().equals(serviceDefinition.getQueue()) &&
		this.getRetryAttempts().equals(serviceDefinition.getRetryAttempts()) &&
		this.getServiceEndPoint().equals(serviceDefinition.getServiceEndPoint()) &&
		this.getServiceName().equals(serviceDefinition.getServiceName()) &&
		this.getCredentialsType() == serviceDefinition.getCredentialsType();
	}
	
	public String toString() {
	    return ReflectionToStringBuilder.toString(this);
	}

	/**
	 * @return the servicePath
	 */
	public String getServicePath() {
		return this.servicePath;
	}

	/**
	 * @param servicePath the servicePath to set
	 */
	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}
}
