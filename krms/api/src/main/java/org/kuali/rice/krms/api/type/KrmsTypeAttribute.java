package org.kuali.rice.krms.api.type;

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
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition.Builder;

/**
 * Concrete model object implementation of KRMS KrmsTypeAttribute. 
 * immutable. 
 * Instances of KrmsTypeAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = KrmsTypeAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = KrmsTypeAttribute.Constants.TYPE_NAME, propOrder = {
		KrmsTypeAttribute.Elements.ID,
		KrmsTypeAttribute.Elements.TYPE_ID,
		KrmsTypeAttribute.Elements.ATTR_DEFN_ID,
		KrmsTypeAttribute.Elements.SEQ_NO,
		KrmsTypeAttribute.Elements.ACTIVE,
		KrmsTypeAttribute.Elements.ATTR_DEFN,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class KrmsTypeAttribute implements KrmsTypeAttributeContract, ModelObjectComplete{
	private static final long serialVersionUID = -304265575559412478L;
	
	@XmlElement(name = Elements.ID, required=true)
	private String id;
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	@XmlElement(name = Elements.ATTR_DEFN_ID, required=true)
	private String attributeDefinitionId;
	@XmlElement(name = Elements.SEQ_NO, required=true)
	private Integer sequenceNumber;
	@XmlElement(name = Elements.ACTIVE, required=false)
	private boolean active;
	@XmlElement(name = Elements.ATTR_DEFN, required=false)
	private KrmsAttributeDefinition attributeDefinition;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private KrmsTypeAttribute() {
    	this.id = null;
    	this.typeId = null;
    	this.attributeDefinitionId = null;
    	this.sequenceNumber = null;
    	this.active = false;
    	this.attributeDefinition = null;
    }
    
    /**
	 * Constructs a KRMS KrmsTypeAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KrmsTypeAttribute
	 */
    private KrmsTypeAttribute(Builder builder) {
        this.id = builder.getId();
        this.typeId = builder.getTypeId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.sequenceNumber = builder.getSequenceNumber();
        this.active = builder.isActive();
        if (builder.getAttributeDefinition() != null) {
        	this.attributeDefinition = builder.getAttributeDefinition().build();
        }
    }
    
	public String getId() {
		return this.id;
	}
	
	public String getTypeId() {
		return this.typeId;
	}

	public String getAttributeDefinitionId() {
		return this.attributeDefinitionId;
	}

	public Integer getSequenceNumber() {
		return this.sequenceNumber;
	}
	
	public boolean isActive() {
		return this.active; 
	}

	public KrmsAttributeDefinition getAttributeDefinition() {
		return this.attributeDefinition;
	}
	
	/**
     * This builder is used to construct instances of KrmsTypeAttribute.  
     */
    public static class Builder implements KrmsTypeAttributeContract, ModelBuilder, Serializable {
		private static final long serialVersionUID = 2729964674427296346L;

		private String id;
        private String typeId;
        private String attributeDefinitionId;
        private Integer sequenceNumber;
        private boolean active;
        private KrmsAttributeDefinition.Builder attributeDefinition;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String typeId, String attributeDefinitionId, Integer sequenceNumber) {
            setId(id);
            setTypeId(typeId);
            setAttributeDefinitionId(attributeDefinitionId);
            setSequenceNumber(sequenceNumber);
			setActive(true);
        }

        public Builder attributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition){
        	setAttributeDefinition(attributeDefinition);
        	return this;
        }
        
        /**
         * Creates a builder from the given parameters.
         * 
         * @param id the KrmsTypeAtribute id
         * @param typeId the KrmsType Id 
         * @param attributeDefinitionId The attributeDefinitionId
         * @param sequenceNumber 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String typeId, String attributeDefinitionId, Integer sequenceNumber) {
            return new Builder(id, typeId, attributeDefinitionId, sequenceNumber);
        }
        
        public static Builder create(KrmsTypeAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getTypeId(),
        			contract.getAttributeDefinitionId(),
        			contract.getSequenceNumber())
        			.attributeDefinition(KrmsAttributeDefinition.Builder
        					.create(contract.getAttributeDefinition()));
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

		public void setTypeId(String typeId) {
            if (StringUtils.isBlank(typeId)) {
                throw new IllegalArgumentException("typeId is blank");
            }
			this.typeId = typeId;
		}

		public void setAttributeDefinitionId(String attributeDefinitionId) {
            if (StringUtils.isBlank(attributeDefinitionId)) {
                throw new IllegalArgumentException("the attribute definition id is blank");
            }
			this.attributeDefinitionId = attributeDefinitionId;
		}
		
		public void setSequenceNumber(Integer sequenceNumber) {
			if (sequenceNumber == null){
				 throw new IllegalArgumentException("the sequence number is null");
			}
			this.sequenceNumber = sequenceNumber;
		}
		
		public void setAttributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition) {
			this.attributeDefinition = attributeDefinition;
			//TODO: verify that the attributeDefinitionID field matches the id field in the builder
		}
		
		public void setActive(boolean active) {
			this.active = active;
		}

		public String getId() {
			return id;
		}

		public String getTypeId() {
			return typeId;
		}

		public String getAttributeDefinitionId() {
			return attributeDefinitionId;
		}
		
		public Integer getSequenceNumber() {
			return sequenceNumber;
		}

		public KrmsAttributeDefinition.Builder getAttributeDefinition() {
			return attributeDefinition;
		}


		public boolean isActive() {
			return active;
		}

		/**
		 * Builds an instance of a KrmsTypeAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed KrmsTypeAttribute
		 */
        @Override
        public KrmsTypeAttribute build() {
            return new KrmsTypeAttribute(this);
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
		final static String ROOT_ELEMENT_NAME = "KrmsTypeAttribute";
		final static String TYPE_NAME = "KrmsTypeAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String TYPE_ID = "typeId";
		final static String ATTR_DEFN_ID = "attributeDefinitionId";
		final static String SEQ_NO = "sequenceNumber";
		final static String ACTIVE = "active";
		final static String ATTR_DEFN = "attributeDefinition";
	}
}
