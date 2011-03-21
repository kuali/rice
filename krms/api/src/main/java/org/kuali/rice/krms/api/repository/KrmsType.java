package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.mo.ModelBuilder;
import org.kuali.rice.core.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of KRMS KrmsType. 
 * immutable. 
 * Instances of KrmsType can be (un)marshalled to and from XML.
 *
 * @see KrmsTypeContract
 */
@XmlRootElement(name = KrmsType.Constants.ROOT_ELEMENT_NAME, namespace = KrmsType.Constants.KRMSNAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = KrmsType.Constants.TYPE_NAME, propOrder = {
		KrmsType.Elements.ID,
		KrmsType.Elements.NAME,
		KrmsType.Elements.NAMESPACE,
		KrmsType.Elements.SERVICENAME,
		KrmsType.Elements.ACTIVE,
		"_elements"
})
public final class KrmsType implements KrmsTypeContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String id;
	@XmlElement(name = Elements.NAME, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String namespace;
	@XmlElement(name = Elements.SERVICENAME, required=false, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String serviceName;
	@XmlElement(name = Elements.ACTIVE, required=false, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private boolean active;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private KrmsType() {
    	this.id = null;
    	this.name = null;
    	this.namespace = null;
    	this.serviceName = null;
    	this.active = false;
    }
    
    /**
	 * Constructs a KRMS KrmsType from the given builder.  This constructor is private and should only
	 * ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KRMS type
	 */
    private KrmsType(Builder builder) {
        this.id = builder.getId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.serviceName = builder.getServiceName();
        this.active = builder.isActive();
    }
    
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String getServiceName() {
		return this.serviceName;
	}
	@Override
	public boolean isActive() {
		return this.active; 
	}

	/**
     * This builder is used to construct instances of KRMS KrmsType.  It enforces the constraints of the {@link KrmsTypeContract}.
     */
    public static class Builder implements KrmsTypeContract, ModelBuilder, Serializable {
    	private static final long serialVersionUID = -6889320709850568900L;
		
		private String id;
        private String name;
        private String namespace;
        private String serviceName = "";
        private boolean active;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String name, String namespace) {
            setId(id);
            setName(name);
            setNamespace(namespace);
//            setServiceName(serviceName);
			setActive(true);
        }

        public Builder serviceName(String serviceName){
        	this.serviceName = serviceName;
        	return this;
        }
        /**
         * Creates a builder from the given parameters.
         * 
         * @param id the KRMS type id
         * @param name the KRMS type name
         * @param namespace the KRMS type namespace
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String name, String namespace) {
            return new Builder(id, name, namespace);
        }

        /**
         * Creates a builder by populating it with data from the given {@link KrmsTypeContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(KrmsTypeContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder =  new Builder(contract.getId(), contract.getName(), contract.getNamespace());
            builder.setNamespace(contract.getNamespace());
            builder.setActive(contract.isActive());
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */
        public void setId(String id) {
            if (StringUtils.isBlank(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

		public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }
			this.name = name;
		}

		public void setNamespace(String namespace) {
            if (StringUtils.isBlank(namespace)) {
                throw new IllegalArgumentException("namespace is blank");
            }
			this.namespace = namespace;
		}
		
		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}
		
		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getNamespace() {
			return namespace;
		}

		@Override
		public String getServiceName() {
			return serviceName;
		}

		@Override
		public boolean isActive() {
			return active;
		}

		/**
		 * Builds an instance of a CampusType based on the current state of the builder.
		 * 
		 * @return the fully-constructed CampusType
		 */
        @Override
        public KrmsType build() {
            return new KrmsType(this);
        }
		
    }
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String KRMSNAMESPACE = "http://rice.kuali.org/schema/krms";		
		final static String ROOT_ELEMENT_NAME = "KRMSType";
		final static String TYPE_NAME = "KRMSTypeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String NAME = "name";
		final static String NAMESPACE = "namespace";
		final static String SERVICENAME = "serviceName";
		final static String ACTIVE = "active";
	}
}
