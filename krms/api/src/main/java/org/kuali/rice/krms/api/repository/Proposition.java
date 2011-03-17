package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * Concrete model object implementation of KRMS Proposition 
 * immutable. 
 * Instances of PropositionParameter can be (un)marshalled to and from XML.
 *
 * @see PropositionContract
 */
@XmlRootElement(name = Proposition.Constants.ROOT_ELEMENT_NAME, namespace = KrmsType.Constants.KRMSNAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Proposition.Constants.TYPE_NAME, propOrder = {
		Proposition.Elements.PROP_ID,
		Proposition.Elements.DESC,
		Proposition.Elements.TYPE_ID,
		Proposition.Elements.PROP_TYPE_CODE,
		"_elements"
})
public final class Proposition implements PropositionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.PROP_ID, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String propId;
	@XmlElement(name = Elements.DESC, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String description;
	@XmlElement(name = Elements.TYPE_ID, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String typeId;
	@XmlElement(name = Elements.PROP_TYPE_CODE, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
	private String propositionTypeCode;
//	@XmlElement(name = Elements.PARAMETER, required=true, namespace = KrmsType.Constants.KRMSNAMESPACE)
//	private List<PropositionParameter> parameters;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private Proposition() {
    	this.propId = null;
    	this.description = null;
    	this.typeId = null;
    	this.propositionTypeCode = null;
//    	this.parameters = null;
    }
    
    /**
	 * Constructs a KRMS KrmsType from the given builder.  This constructor is private and should only
	 * ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KRMS type
	 */
    private Proposition(Builder builder) {
        this.propId = builder.getPropId();
        this.description = builder.getDescription();
        this.typeId = builder.getTypeId();
        this.propositionTypeCode = builder.getPropositionTypeCode();
//        this.parameters = builder.getParameters();
    }
    
	@Override
	public String getPropId() {
		return this.propId;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getTypeId() {
		return this.typeId;
	}

	@Override
	public String getPropositionTypeCode() {
		return this.propositionTypeCode; 
	}

//	@Override
//	public List<PropositionParameter> getParameters() {
//		return this.parameters; 
//	}

	/**
     * This builder is used to construct instances of KRMS KrmsType.  It enforces the constraints of the {@link KrmsTypeContract}.
     */
    public static class Builder implements PropositionContract, ModelBuilder, Serializable {
    	private static final long serialVersionUID = -6889320709850568900L;
		
        private String propId;
        private String description;
        private String typeId;
        private String propositionTypeCode;
//        private List<PropositionParameter.Builder> parameters;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String propId, String desc, String typeId, String propTypeCode) {
            setPropId(propId);
            setDescription(desc);
            setTypeId(typeId);
			setPropositionTypeCode(propTypeCode);
//			setParameters(list);
        }


        /**
         * Creates a builder by populating it with data from the given {@link KrmsTypeContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(PropositionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder =  new Builder(contract.getPropId(), contract.getDescription(), contract.getTypeId(), contract.getPropositionTypeCode());
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setPropId(String propId) {
            if (StringUtils.isBlank(propId)) {
                throw new IllegalArgumentException("propId is blank");
            }
			this.propId = propId;
		}

		public void setDescription(String desc) {
            if (StringUtils.isBlank(desc)) {
                throw new IllegalArgumentException("description is blank");
            }
			this.description = desc;
		}
		
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
	                throw new IllegalArgumentException("KRMS type id is blank");
			}
			// TODO: check against valid values
			this.typeId = typeId;
		}
		
		public void setPropositionTypeCode(String propTypeCode) {
			if (StringUtils.isBlank(propTypeCode)) {
                throw new IllegalArgumentException("proposition type code is blank");
			}
			this.propositionTypeCode = propTypeCode;
		}
		
//		public void setParameters(List<PropositionParameter.Builder> parameters){
//			if (parameters == null || parameters.isEmpty()){
//				throw new IllegalArgumentException("no parameters are specified");
//			}
//
//		}

		@Override
		public String getPropId() {
			return propId;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getTypeId() {
			return typeId;
		}

		@Override
		public String getPropositionTypeCode() {
			return propositionTypeCode;
		}
		
//		@Override
//		public List<PropositionParameter.Builder> getParameters() {
//			return parameters;
//		}

		/**
		 * Builds an instance of a CampusType based on the current state of the builder.
		 * 
		 * @return the fully-constructed CampusType
		 */
        @Override
        public Proposition build() {
            return new Proposition(this);
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
		final static String ROOT_ELEMENT_NAME = "Proposition";
		final static String TYPE_NAME = "PropositionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String PROP_ID = "propId";
		final static String DESC = "description";
		final static String TYPE_ID = "typeId";
		final static String PROP_TYPE_CODE = "propositionType";
		final static String PARAMETER = "parameter";
	}
}
