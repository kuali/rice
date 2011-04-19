package org.kuali.rice.krms.api.repository.rule;

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
import org.kuali.rice.krms.api.repository.BaseAttribute;
import org.kuali.rice.krms.api.repository.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.BaseAttribute.Builder;
import org.kuali.rice.krms.api.repository.BaseAttribute.Elements;

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
public final class RuleAttribute extends BaseAttribute implements RuleAttributeContract, ModelObjectComplete{		
	private static final long serialVersionUID = 2988399046412505534L;

	@XmlElement(name = Elements.RULE_ID, required=true)
	private String ruleId;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private RuleAttribute() {
    	super();
    	this.ruleId = null;
    }
    
    /**
	 * Constructs an RuleAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the RuleAttribute
	 */
    private RuleAttribute(Builder builder) {
    	super(builder);
        this.ruleId = builder.getRuleId();
    }
    
	@Override
	public String getRuleId() {
		return this.ruleId;
	}

	/**
     * This builder is used to construct instances of RuleAttribute.  
     */
    public static class Builder extends BaseAttribute.Builder implements RuleAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = -8619106871420999876L;
		
        private String ruleId;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String ruleId, String attributeDefinitionId, String value) {
        	super(id, attributeDefinitionId, value);
            setRuleId(ruleId);
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

		public void setRuleId(String ruleId) {
            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("ruleId is blank");
            }
			this.ruleId = ruleId;
		}

		@Override
		public String getRuleId() {
			return ruleId;
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
	public static class Elements extends BaseAttribute.Elements {
		final static String RULE_ID = "ruleId";
	}
}
