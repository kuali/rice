package org.kuali.rice.ksb.api.bus.support;

import java.io.Serializable;
import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.api.security.credentials.CredentialsSource.CredentialsType;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;

public abstract class AbstractServiceConfiguration implements ServiceConfiguration {

	private static final long serialVersionUID = 2681595879406587302L;

	private final QName serviceName;
	private final URL endpointUrl;
	private final String applicationNamespace;
	private final String serviceVersion;
	private final String type;
	private final boolean queue;
	private final Integer priority;
	private final Integer retryAttempts;
	private final Long millisToLive;
	private final String messageExceptionHandler;
	private final Boolean busSecurity;
	private final CredentialsType credentialsType;
	
	protected AbstractServiceConfiguration(Builder<?> builder) {
		this.serviceName = builder.getServiceName();
		this.endpointUrl = builder.getEndpointUrl();
		this.applicationNamespace = builder.getApplicationNamespace();
		this.serviceVersion = builder.getServiceVersion();
		this.type = builder.getType();
		this.queue = builder.isQueue();
		this.priority = builder.getPriority();
		this.retryAttempts = builder.getRetryAttempts();
		this.millisToLive = builder.getMillisToLive();
		this.messageExceptionHandler = builder.getMessageExceptionHandler();
		this.busSecurity = builder.getBusSecurity();
		this.credentialsType = builder.getCredentialsType();
	}
	
	@Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
	
	public QName getServiceName() {
		return serviceName;
	}

	public URL getEndpointUrl() {
		return endpointUrl;
	}
	
	public String getApplicationNamespace() {
		return applicationNamespace;
	}
	
	public String getServiceVersion() {
		return serviceVersion;
	}
	
	public String getType() {
		return type;
	}

	public boolean isQueue() {
		return queue;
	}

	public Integer getPriority() {
		return priority;
	}

	public Integer getRetryAttempts() {
		return retryAttempts;
	}

	public Long getMillisToLive() {
		return millisToLive;
	}

	public String getMessageExceptionHandler() {
		return messageExceptionHandler;
	}

	public Boolean getBusSecurity() {
		return busSecurity;
	}

	public CredentialsType getCredentialsType() {
		return credentialsType;
	}
		
	protected static abstract class Builder<T> implements Serializable {
		
		private static final long serialVersionUID = -3002495884401672488L;

		private QName serviceName;
		private URL endpointUrl;
		private String applicationNamespace;
		private String serviceVersion;
		private String type;
		private boolean queue;
		private Integer priority;
		private Integer retryAttempts;
		private Long millisToLive;
		private String messageExceptionHandler;
		private Boolean busSecurity;
		private CredentialsType credentialsType;
		
		public abstract T build();
		
		protected void copyServiceDefinitionProperties(ServiceDefinition serviceDefinition) {
			setServiceName(serviceDefinition.getServiceName());
			setEndpointUrl(serviceDefinition.getEndpointUrl());
			setApplicationNamespace(serviceDefinition.getApplicationNamespace());
			setServiceVersion(serviceDefinition.getServiceVersion());
			setType(serviceDefinition.getType());
			setQueue(serviceDefinition.isQueue());
			setPriority(serviceDefinition.getPriority());
			setRetryAttempts(serviceDefinition.getRetryAttempts());
			setMillisToLive(serviceDefinition.getMillisToLive());
			setMessageExceptionHandler(serviceDefinition.getMessageExceptionHandler());
			setBusSecurity(serviceDefinition.getBusSecurity());
			setCredentialsType(serviceDefinition.getCredentialsType());
		}
		
		public QName getServiceName() {
			return serviceName;
		}
		public void setServiceName(QName serviceName) {
			this.serviceName = serviceName;
		}
		public URL getEndpointUrl() {
			return endpointUrl;
		}
		public void setEndpointUrl(URL endpointUrl) {
			this.endpointUrl = endpointUrl;
		}
		public String getApplicationNamespace() {
			return applicationNamespace;
		}
		public void setApplicationNamespace(String applicationNamespace) {
			this.applicationNamespace = applicationNamespace;
		}
		public String getServiceVersion() {
			return serviceVersion;
		}
		public void setServiceVersion(String serviceVersion) {
			this.serviceVersion = serviceVersion;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public boolean isQueue() {
			return queue;
		}
		public void setQueue(boolean queue) {
			this.queue = queue;
		}
		public Integer getPriority() {
			return priority;
		}
		public void setPriority(Integer priority) {
			this.priority = priority;
		}
		public Integer getRetryAttempts() {
			return retryAttempts;
		}
		public void setRetryAttempts(Integer retryAttempts) {
			this.retryAttempts = retryAttempts;
		}
		public Long getMillisToLive() {
			return millisToLive;
		}
		public void setMillisToLive(Long millisToLive) {
			this.millisToLive = millisToLive;
		}
		public String getMessageExceptionHandler() {
			return messageExceptionHandler;
		}
		public void setMessageExceptionHandler(String messageExceptionHandler) {
			this.messageExceptionHandler = messageExceptionHandler;
		}
		public Boolean getBusSecurity() {
			return busSecurity;
		}
		public void setBusSecurity(Boolean busSecurity) {
			this.busSecurity = busSecurity;
		}
		public CredentialsType getCredentialsType() {
			return credentialsType;
		}
		public void setCredentialsType(CredentialsType credentialsType) {
			this.credentialsType = credentialsType;
		}
		
	}
	
}
