package org.kuali.rice.ksb.api.registry;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = ServiceInfo.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ServiceInfo.Constants.TYPE_NAME, propOrder = {
    ServiceInfo.Elements.SERVICE_ID,
    ServiceInfo.Elements.SERVICE_NAME,
    ServiceInfo.Elements.ENDPOINT_URL,
    ServiceInfo.Elements.INSTANCE_ID,
    ServiceInfo.Elements.APPLICATION_NAMESPACE,
    ServiceInfo.Elements.SERVER_IP_ADDRESS,
    ServiceInfo.Elements.TYPE,
    ServiceInfo.Elements.SERVICE_VERSION,
    ServiceInfo.Elements.STATUS,
    ServiceInfo.Elements.SERVICE_DESCRIPTOR_ID,
    ServiceInfo.Elements.CHECKSUM,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ServiceInfo implements ModelObjectComplete, ServiceInfoContract
{

	private static final long serialVersionUID = 4793306414624564991L;
	
	@XmlElement(name = Elements.SERVICE_ID, required = false)
    private final String serviceId;
	
    @XmlElement(name = Elements.SERVICE_NAME, required = true)
    private final QName serviceName;
    
    @XmlElement(name = Elements.ENDPOINT_URL, required = true)
    private final String endpointUrl;
    
    @XmlElement(name = Elements.INSTANCE_ID, required = true)
    private final String instanceId;
    
    @XmlElement(name = Elements.APPLICATION_NAMESPACE, required = true)
    private final String applicationNamespace;
    
    @XmlElement(name = Elements.SERVER_IP_ADDRESS, required = true)
    private final String serverIpAddress;
    
    @XmlElement(name = Elements.TYPE, required = true)
    private final String type;
    
    @XmlElement(name = Elements.SERVICE_VERSION, required = true)
    private final String serviceVersion;
    
    @XmlJavaTypeAdapter(ServiceEndpointStatus.Adapter.class)
    @XmlElement(name = Elements.STATUS, required = true)
    private final String status;
    
    @XmlElement(name = Elements.SERVICE_DESCRIPTOR_ID, required = false)
    private final String serviceDescriptorId;
    
    @XmlElement(name = Elements.CHECKSUM, required = true)
    private final String checksum;
    
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private ServiceInfo() {
        this.serviceId = null;
        this.serviceName = null;
        this.endpointUrl = null;
        this.instanceId = null;
        this.applicationNamespace = null;
        this.serverIpAddress = null;
        this.type = null;
        this.serviceVersion = null;
        this.status = null;
        this.serviceDescriptorId = null;
        this.checksum = null;
        this.versionNumber = null;
    }

    private ServiceInfo(Builder builder) {
        this.serviceId = builder.getServiceId();
        this.serviceName = builder.getServiceName();
        this.endpointUrl = builder.getEndpointUrl();
        this.instanceId = builder.getInstanceId();
        this.applicationNamespace = builder.getApplicationNamespace();
        this.serverIpAddress = builder.getServerIpAddress();
        this.type = builder.getType();
        this.serviceVersion = builder.getServiceVersion();
        ServiceEndpointStatus builderStatus = builder.getStatus();
        this.status = builderStatus == null ? null : builderStatus.getCode();
        this.serviceDescriptorId = builder.getServiceDescriptorId();
        this.checksum = builder.getChecksum();
        this.versionNumber = builder.getVersionNumber();
    }

    @Override
    public String getServiceId() {
        return this.serviceId;
    }

    @Override
    public QName getServiceName() {
        return this.serviceName;
    }

    @Override
    public String getEndpointUrl() {
        return this.endpointUrl;
    }
    
    @Override
    public String getInstanceId() {
        return this.instanceId;
    }

    @Override
    public String getApplicationNamespace() {
        return this.applicationNamespace;
    }

    @Override
    public String getServerIpAddress() {
        return this.serverIpAddress;
    }
    
    @Override
    public String getType() {
    	return this.type;
    }
    
    @Override
    public String getServiceVersion() {
    	return this.serviceVersion;
    }
    
    @Override
    public ServiceEndpointStatus getStatus() {
    	return ServiceEndpointStatus.fromCode(this.status);
    }

    @Override
    public String getServiceDescriptorId() {
        return this.serviceDescriptorId;
    }
    
    @Override
    public String getChecksum() {
        return this.checksum;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * A builder which can be used to construct {@link ServiceInfo} instances.  Enforces the constraints of the {@link ServiceInfoContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, ServiceInfoContract
    {

		private static final long serialVersionUID = 4424090938369742940L;

		private String serviceId;
        private QName serviceName;
        private String endpointUrl;
        private String instanceId;
        private String applicationNamespace;
        private String serverIpAddress;
        private String type;
        private String serviceVersion;
        private ServiceEndpointStatus status;
        private String serviceDescriptorId;
        private String checksum;
        private Long versionNumber;

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(ServiceInfoContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setServiceId(contract.getServiceId());
            builder.setServiceName(contract.getServiceName());
            builder.setEndpointUrl(contract.getEndpointUrl());
            builder.setInstanceId(contract.getInstanceId());
            builder.setApplicationNamespace(contract.getApplicationNamespace());
            builder.setServerIpAddress(contract.getServerIpAddress());
            builder.setType(contract.getType());
            builder.setServiceVersion(contract.getServiceVersion());
            builder.setStatus(contract.getStatus());
            builder.setServiceDescriptorId(contract.getServiceDescriptorId());
            builder.setChecksum(contract.getChecksum());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public ServiceInfo build() {
        	validateAll();
            return new ServiceInfo(this);
        }

        @Override
        public String getServiceId() {
            return this.serviceId;
        }

        @Override
        public QName getServiceName() {
            return this.serviceName;
        }

        @Override
        public String getEndpointUrl() {
            return this.endpointUrl;
        }
        
        @Override
        public String getInstanceId() {
            return this.instanceId;
        }

        @Override
        public String getApplicationNamespace() {
            return this.applicationNamespace;
        }

        @Override
        public String getServerIpAddress() {
            return this.serverIpAddress;
        }
        
        @Override
        public String getType() {
        	return this.type;
        }
        
        @Override
        public String getServiceVersion() {
        	return this.serviceVersion;
        }

        @Override
        public ServiceEndpointStatus getStatus() {
        	return this.status;
        }
        
        @Override
        public String getServiceDescriptorId() {
            return this.serviceDescriptorId;
        }
        
        @Override
        public String getChecksum() {
            return this.checksum;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public void setServiceName(QName serviceName) {
            validateServiceName(serviceName);
            this.serviceName = serviceName;
        }

        public void setEndpointUrl(String endpointUrl) {
            validateEndpointUrl(endpointUrl);
            this.endpointUrl = endpointUrl;
        }
        
        public void setInstanceId(String instanceId) {
            validateInstanceId(instanceId);
            this.instanceId = instanceId;
        }

        public void setApplicationNamespace(String applicationNamespace) {
            validateApplicationNamespace(applicationNamespace);
            this.applicationNamespace = applicationNamespace;
        }

        public void setServerIpAddress(String serverIpAddress) {
        	validateServerIpAddress(serverIpAddress);
            this.serverIpAddress = serverIpAddress;
        }
        
        public void setType(String type) {
        	validateType(type);
        	this.type = type;
        }
        
        public void setServiceVersion(String serviceVersion) {
        	validateServiceVersion(serviceVersion);
        	this.serviceVersion = serviceVersion;
        }
        
        public void setStatus(ServiceEndpointStatus status) {
        	validateStatus(status);
        	this.status = status;
        }

        public void setServiceDescriptorId(String serviceDescriptorId) {
            this.serviceDescriptorId = serviceDescriptorId;
        }
        
        public void setChecksum(String checksum) {
            validateChecksum(checksum);
            this.checksum = checksum;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }
        
        private void assertNotNull(String name, Object object) {
        	if (object == null) {
        		throw new IllegalArgumentException(name + " was null");
        	}
        }
        
        private void assertNotBlank(String name, String value) {
        	assertNotNull(name, value);
        	if (StringUtils.isBlank(value)) {
        		throw new IllegalArgumentException(name + " was blank");
        	}
        }
        
        private void validateServiceName(QName serviceName) {
        	assertNotNull("serviceName", serviceName);
        }
        
        private void validateEndpointUrl(String endpointUrl) {
        	assertNotBlank("endpointUrl", endpointUrl);
        }
        
        private void validateInstanceId(String instanceId) {
        	assertNotBlank("instanceId", instanceId);
        }
        
        private void validateApplicationNamespace(String applicationNamespace) {
        	assertNotBlank("applicationNamespace", applicationNamespace);
        }
        
        private void validateServerIpAddress(String serverIpAddress) {
        	assertNotBlank("serverIpAddress", serverIpAddress);
        }
        
        private void validateType(String type) {
        	assertNotBlank("type", type);
        }

        private void validateServiceVersion(String serviceVersion) {
        	assertNotBlank("serviceVersion", serviceVersion);
        }
        
        private void validateStatus(ServiceEndpointStatus status) {
        	assertNotNull("status", status);
        }
        
        private void validateChecksum(String checksum) {
        	assertNotBlank("checksum", checksum);
        }
        
        private void validateAll() {
        	validateServiceName(serviceName);
            validateEndpointUrl(endpointUrl);
            validateInstanceId(instanceId);
            validateApplicationNamespace(applicationNamespace);
            validateServerIpAddress(serverIpAddress);
            validateType(type);
            validateServiceVersion(serviceVersion);
            validateStatus(status);
            validateChecksum(checksum);
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "serviceHeader";
        final static String TYPE_NAME = "ServiceHeaderType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String SERVICE_ID = "serviceId";
        final static String SERVICE_NAME = "serviceName";
        final static String ENDPOINT_URL = "endpointUrl";
        final static String INSTANCE_ID = "instanceId";
        final static String APPLICATION_NAMESPACE = "applicationNamespace";
        final static String SERVER_IP_ADDRESS = "serverIpAddress";
        final static String TYPE = "type";
        final static String SERVICE_VERSION = "serviceVersion";
        final static String STATUS = "status";
        final static String SERVICE_DESCRIPTOR_ID = "serviceDescriptorId";
        final static String CHECKSUM = "checksum";

    }

}

