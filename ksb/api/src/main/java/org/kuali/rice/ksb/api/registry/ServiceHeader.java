package org.kuali.rice.ksb.api.registry;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = ServiceHeader.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ServiceHeader.Constants.TYPE_NAME, propOrder = {
    ServiceHeader.Elements.SERVICE_ENDPOINT_ID,
    ServiceHeader.Elements.SERVICE_NAME,
    ServiceHeader.Elements.ENDPOINT_URL,
    ServiceHeader.Elements.ENDPOINT_ALTERNATE_URL,
    ServiceHeader.Elements.APPLICATION_NAMESPACE,
    ServiceHeader.Elements.SERVER_IP_ADDRESS,
    ServiceHeader.Elements.TYPE,
    ServiceHeader.Elements.SERVICE_VERSION,
    ServiceHeader.Elements.CHECKSUM,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ServiceHeader implements ModelObjectComplete, ServiceHeaderContract
{

	private static final long serialVersionUID = 4793306414624564991L;
	
	@XmlElement(name = Elements.SERVICE_ENDPOINT_ID, required = false)
    private final Long serviceEndpointId;
    @XmlElement(name = Elements.SERVICE_NAME, required = true)
    private final QName serviceName;
    @XmlElement(name = Elements.ENDPOINT_URL, required = true)
    private final String endpointUrl;
    @XmlElement(name = Elements.ENDPOINT_ALTERNATE_URL, required = false)
    private final String endpointAlternateUrl;
    @XmlElement(name = Elements.APPLICATION_NAMESPACE, required = true)
    private final String applicationNamespace;
    @XmlElement(name = Elements.SERVER_IP_ADDRESS, required = false)
    private final String serverIpAddress;
    @XmlElement(name = Elements.TYPE, required = true)
    private final String type;
    @XmlElement(name = Elements.SERVICE_VERSION, required = true)
    private final String serviceVersion;
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
    private ServiceHeader() {
        this.serviceEndpointId = null;
        this.serviceName = null;
        this.endpointUrl = null;
        this.endpointAlternateUrl = null;
        this.applicationNamespace = null;
        this.serverIpAddress = null;
        this.type = null;
        this.serviceVersion = null;
        this.checksum = null;
        this.versionNumber = null;
    }

    private ServiceHeader(Builder builder) {
        this.serviceEndpointId = builder.getServiceEndpointId();
        this.serviceName = builder.getServiceName();
        this.endpointUrl = builder.getEndpointUrl();
        this.endpointAlternateUrl = builder.getEndpointAlternateUrl();
        this.applicationNamespace = builder.getApplicationNamespace();
        this.serverIpAddress = builder.getServerIpAddress();
        this.type = builder.getType();
        this.serviceVersion = builder.getServiceVersion();
        this.checksum = builder.getChecksum();
        this.versionNumber = builder.getVersionNumber();
    }

    @Override
    public Long getServiceEndpointId() {
        return this.serviceEndpointId;
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
    public String getEndpointAlternateUrl() {
        return this.endpointAlternateUrl;
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
     * A builder which can be used to construct {@link ServiceHeader} instances.  Enforces the constraints of the {@link ServiceHeaderContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, ServiceHeaderContract
    {

		private static final long serialVersionUID = 4424090938369742940L;

		private Long serviceEndpointId;
        private QName serviceName;
        private String endpointUrl;
        private String endpointAlternateUrl;
        private String applicationNamespace;
        private String serverIpAddress;
        private String type;
        private String serviceVersion;
        private String checksum;
        private Long versionNumber;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(ServiceHeaderContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setServiceEndpointId(contract.getServiceEndpointId());
            builder.setServiceName(contract.getServiceName());
            builder.setEndpointUrl(contract.getEndpointUrl());
            builder.setEndpointAlternateUrl(contract.getEndpointAlternateUrl());
            builder.setApplicationNamespace(contract.getApplicationNamespace());
            builder.setServerIpAddress(contract.getServerIpAddress());
            builder.setType(contract.getType());
            builder.setServiceVersion(contract.getServiceVersion());
            builder.setChecksum(contract.getChecksum());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public ServiceHeader build() {
            return new ServiceHeader(this);
        }

        @Override
        public Long getServiceEndpointId() {
            return this.serviceEndpointId;
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
        public String getEndpointAlternateUrl() {
            return this.endpointAlternateUrl;
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
        public String getChecksum() {
            return this.checksum;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        public void setServiceEndpointId(Long serviceEndpointId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.serviceEndpointId = serviceEndpointId;
        }

        public void setServiceName(QName serviceName) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.serviceName = serviceName;
        }

        public void setEndpointUrl(String endpointUrl) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.endpointUrl = endpointUrl;
        }

        public void setEndpointAlternateUrl(String endpointAlternateUrl) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.endpointAlternateUrl = endpointAlternateUrl;
        }

        public void setApplicationNamespace(String applicationNamespace) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.applicationNamespace = applicationNamespace;
        }

        public void setServerIpAddress(String serverIpAddress) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.serverIpAddress = serverIpAddress;
        }
        
        public void setType(String type) {
        	// TODO add validation of input value if required and throw IllegalArgumentException if needed
        	this.type = type;
        }
        
        public void setServiceVersion(String serviceVersion) {
        	// TODO add validation of input value if required and throw IllegalArgumentException if needed
        	this.serviceVersion = serviceVersion;
        }

        public void setChecksum(String checksum) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.checksum = checksum;
        }

        public void setVersionNumber(Long versionNumber) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.versionNumber = versionNumber;
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

        final static String SERVICE_ENDPOINT_ID = "serviceEndpointId";
        final static String SERVICE_NAME = "serviceName";
        final static String ENDPOINT_URL = "endpointUrl";
        final static String ENDPOINT_ALTERNATE_URL = "endpointAlternateUrl";
        final static String APPLICATION_NAMESPACE = "applicationNamespace";
        final static String SERVER_IP_ADDRESS = "serverIpAddress";
        final static String TYPE = "type";
        final static String SERVICE_VERSION = "serviceVersion";
        final static String CHECKSUM = "checksum";

    }

}

