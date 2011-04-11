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
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of RuleAttribute. 
 * immutable. 
 * Instances of RuleAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = RuleAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RuleAttribute.Constants.TYPE_NAME, propOrder = {
		RuleAttribute.Elements.ID,
		RuleAttribute.Elements.RULE_ID,
		RuleAttribute.Elements.ATTR_DEFN_ID,
		RuleAttribute.Elements.VALUE,
		RuleAttribute.Elements.ATTR_DEFN,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RuleAttribute implements RuleAttributeContract, ModelObjectComplete{		
	private static final long serialVersionUID = 2988399046412505534L;

	@XmlElement(name = Elements.ID, required=true)
	private String id;
	@XmlElement(name = Elements.RULE_ID, required=true)
	private String ruleId;
	@XmlElement(name = Elements.ATTR_DEFN_ID, required=true)
	private String attributeDefinitionId;
	@XmlElement(name = Elements.VALUE, required=true)
	private String value;
	@XmlElement(name = Elements.ATTR_DEFN, required=false)
	private KrmsAttributeDefinition attributeDefinition;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private RuleAttribute() {
    	this.id = null;
    	this.ruleId = null;
    	this.attributeDefinitionId = null;
    	this.value = null;
    	this.attributeDefinition = null;
    }
    
    /**
	 * Constructs an RuleAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the RuleAttribute
	 */
    private RuleAttribute(Builder builder) {
        this.id = builder.getId();
        this.ruleId = builder.getRuleId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.value = builder.getValue();
        this.attributeDefinition = builder.getAttributeDefinition().build();
    }
    
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getRuleId() {
		return this.ruleId;
	}

	@Override
	public String getAttributeDefinitionId() {
		return this.attributeDefinitionId;
	}

	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public KrmsAttributeDefinition getAttributeDefinition() {
		return this.attributeDefinition;
	}
	
	/**
     * This builder is used to construct instances of RuleAttribute.  
     */
    public static class Builder implements RuleAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = -8619106871420999876L;
		
		private String id;
        private String ruleId;
        private String attributeDefinitionId;
        private String value;
        private KrmsAttributeDefinition.Builder attributeDefinition;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String ruleId, String attributeDefinitionId, String value) {
            setId(id);
            setRuleId(ruleId);
            setAttributeDefinitionId(attributeDefinitionId);
            setValue(value);
        }

        public Builder attributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition){
        	setAttributeDefinition(attributeDefinition);
        	return this;
        }
        
        /**
         * Creates a builder from the given parameters.
         * 
         * @param id the RuleAtribute id
         * @param ruleId the Rule Id 
         * @param attributeDefinitionId The attributeDefinitionId
         * @param value 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String ruleId, String attributeDefinitionId, String value) {
            return new Builder(id, ruleId, attributeDefinitionId, value);
        }
        
        public static Builder create(RuleAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getRuleId(),
        			contract.getAttributeDefinitionId(),
        			contract.getValue())
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

		public void setRuleId(String ruleId) {
            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("ruleId is blank");
            }
			this.ruleId = ruleId;
		}

		public void setAttributeDefinitionId(String attributeDefinitionId) {
            if (StringUtils.isBlank(attributeDefinitionId)) {
                throw new IllegalArgumentException("the attribute definition id is blank");
            }
			this.attributeDefinitionId = attributeDefinitionId;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public void setAttributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition) {
			this.attributeDefinition = attributeDefinition;
			//TODO: verify that the attributeDefinitionID field matches the id field in the builder
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getRuleId() {
			return ruleId;
		}

		@Override
		public String getAttributeDefinitionId() {
			return attributeDefinitionId;
		}
		
		@Override
		public String getValue() {
			return value;
		}

		@Override
		public KrmsAttributeDefinition.Builder getAttributeDefinition() {
			return attributeDefinition;
		}

		/**
		 * Builds an instance of a RuleAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed RuleAttribute
		 */
        @Override
        public RuleAttribute build() {
            return new RuleAttribute(this);
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
		final static String ROOT_ELEMENT_NAME = "RuleAttribute";
		final static String TYPE_NAME = "RuleAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String RULE_ID = "ruleId";
		final static String ATTR_DEFN_ID = "attributeDefinitionId";
		final static String VALUE = "value";
		final static String ATTR_DEFN = "attributeDefinition";
	}
}
