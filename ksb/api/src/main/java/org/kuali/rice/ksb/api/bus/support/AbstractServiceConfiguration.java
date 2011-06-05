package org.kuali.rice.ksb.api.bus.support;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.security.credentials.CredentialsType;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;
import org.kuali.rice.core.util.jaxb.QNameAsStringAdapter;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractServiceConfiguration.Constants.TYPE_NAME, propOrder = {
		AbstractServiceConfiguration.Elements.SERVICE_NAME,
		AbstractServiceConfiguration.Elements.ENDPOINT_URL,
		AbstractServiceConfiguration.Elements.APPLICATION_ID,
		AbstractServiceConfiguration.Elements.SERVICE_VERSION,
		AbstractServiceConfiguration.Elements.TYPE,
		AbstractServiceConfiguration.Elements.QUEUE,
		AbstractServiceConfiguration.Elements.PRIORITY,
		AbstractServiceConfiguration.Elements.RETRY_ATTEMPTS,
		AbstractServiceConfiguration.Elements.MILLIS_TO_LIVE,
		AbstractServiceConfiguration.Elements.MESSAGE_EXCEPTION_HANDLER,
		AbstractServiceConfiguration.Elements.BUS_SECURITY,
		AbstractServiceConfiguration.Elements.CREDENTIALS_TYPE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public abstract class AbstractServiceConfiguration implements ServiceConfiguration {

	private static final long serialVersionUID = 2681595879406587302L;

	@XmlJavaTypeAdapter(QNameAsStringAdapter.class)
	@XmlElement(name = Elements.SERVICE_NAME, required = true)
	private final QName serviceName;
	
	@XmlElement(name = Elements.ENDPOINT_URL, required = true)
	private final URL endpointUrl;
	
	@XmlElement(name = Elements.APPLICATION_ID, required = true)
	private final String applicationId;
	
	@XmlElement(name = Elements.SERVICE_VERSION, required = true)
	private final String serviceVersion;
	
	@XmlElement(name = Elements.TYPE, required = true)
	private final String type;
	
	@XmlElement(name = Elements.QUEUE, required = false)
	private final boolean queue;
	
	@XmlElement(name = Elements.PRIORITY, required = false)
	private final Integer priority;
	
	@XmlElement(name = Elements.RETRY_ATTEMPTS, required = false)
	private final Integer retryAttempts;
	
	@XmlElement(name = Elements.MILLIS_TO_LIVE, required = false)
	private final Long millisToLive;
	
	@XmlElement(name = Elements.MESSAGE_EXCEPTION_HANDLER, required = false)
	private final String messageExceptionHandler;
	
	@XmlElement(name = Elements.BUS_SECURITY, required = false)
	private final Boolean busSecurity;
	
	@XmlJavaTypeAdapter(CredentialsTypeAdapter.class)
	@XmlElement(name = Elements.CREDENTIALS_TYPE, required = false)
	private final String credentialsType;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	/**
     * Constructor intended for use only by JAXB.
     */
	protected AbstractServiceConfiguration() {
		this.serviceName = null;
		this.endpointUrl = null;
		this.applicationId = null;
		this.serviceVersion = null;
		this.type = null;
		this.queue = false;
		this.priority = null;
		this.retryAttempts = null;
		this.millisToLive = null;
		this.messageExceptionHandler = null;
		this.busSecurity = null;
		this.credentialsType = null;
	}
	
	protected AbstractServiceConfiguration(Builder<?> builder) {
		this.serviceName = builder.getServiceName();
		this.endpointUrl = builder.getEndpointUrl();
		this.applicationId = builder.getApplicationId();
		this.serviceVersion = builder.getServiceVersion();
		this.type = builder.getType();
		this.queue = builder.isQueue();
		this.priority = builder.getPriority();
		this.retryAttempts = builder.getRetryAttempts();
		this.millisToLive = builder.getMillisToLive();
		this.messageExceptionHandler = builder.getMessageExceptionHandler();
		this.busSecurity = builder.getBusSecurity();
		CredentialsType cred = builder.getCredentialsType();
		this.credentialsType = cred == null ? null : cred.name();
	}
	
	/**
	 * Compares this configuration with another service configuration to determine if
	 * they are equal.  This method utilizes reflection to automatically compares all
	 * fields of the service configuration (with the exception of the internal
	 * {@link #_futureElements} field) so determine equality.  This includes private
	 * fields declared on the subclass.
	 * 
	 * <p>Subclasses should generally not need to override this provided the default
	 * reflection-based comparison will produce an accurate result for additional
	 * fields that are introduced in the subclass.
	 */
	@Override
    public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

	/**
	 * Produces a hash code value for this service configuration.  This method utilizes
	 * reflection to automatically generate a hashcode using fields from the service
	 * configuration.  This includes all fields (with the exception of the internal
	 * {@link #_futureElements} field) including private fields declared on the subclass.
	 * 
	 * <p>Subclasses should generally not need to override this provded the default
	 * reflect-based comparison will produce an accurate result for additional
	 * fields that are introduced in the subclass.
	 */
	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }
	
	public QName getServiceName() {
		return serviceName;
	}

	public URL getEndpointUrl() {
		return endpointUrl;
	}
	
	public String getApplicationId() {
		return applicationId;
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
		if (credentialsType == null) {
			return null;
		}
		return CredentialsType.valueOf(credentialsType);
	}
		
	protected static abstract class Builder<T> implements Serializable {
		
		private static final long serialVersionUID = -3002495884401672488L;

		private QName serviceName;
		private URL endpointUrl;
		private String applicationId;
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
			setApplicationId(serviceDefinition.getApplicationId());
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
		public String getApplicationId() {
			return applicationId;
		}
		public void setApplicationId(String applicationId) {
			this.applicationId = applicationId;
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
	
    /**
     * Defines some internal constants used on this class.
     */
    protected static class Constants {
        protected final static String TYPE_NAME = "ServiceConfigurationType";
        private final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    protected static class Elements {
    	protected final static String SERVICE_NAME = "serviceName";
    	protected final static String ENDPOINT_URL = "endpointUrl";
    	protected final static String APPLICATION_ID = "applicationId";
    	protected final static String SERVICE_VERSION = "serviceVersion";
    	protected final static String TYPE = "type";
    	protected final static String QUEUE = "queue";
    	protected final static String PRIORITY = "priority";
    	protected final static String RETRY_ATTEMPTS = "retryAttempts";
    	protected final static String MILLIS_TO_LIVE = "millisToLive";
    	protected final static String MESSAGE_EXCEPTION_HANDLER = "messageExceptionHandler";
    	protected final static String BUS_SECURITY = "busSecurity";
    	protected final static String CREDENTIALS_TYPE = "credentialsType";
    }
    
    static final class CredentialsTypeAdapter extends EnumStringAdapter<CredentialsType> {
		
		protected Class<CredentialsType> getEnumClass() {
			return CredentialsType.class;
		}
		
	}
	
}
