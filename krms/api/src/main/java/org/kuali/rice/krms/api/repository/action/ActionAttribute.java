package org.kuali.rice.krms.api.repository.action;

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
 * Concrete model object implementation of ActionAttribute. 
 * immutable. 
 * Instances of ActionAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = ActionAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ActionAttribute.Constants.TYPE_NAME, propOrder = {
		ActionAttribute.Elements.ID,
		ActionAttribute.Elements.ACTION_ID,
		ActionAttribute.Elements.ATTR_DEFN_ID,
		ActionAttribute.Elements.VALUE,
		ActionAttribute.Elements.ACTION_TYPE_ID,
		ActionAttribute.Elements.ATTR_DEFN,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ActionAttribute extends BaseAttribute implements ActionAttributeContract, ModelObjectComplete {	
	private static final long serialVersionUID = -6126133049308968098L;
	
	@XmlElement(name = Elements.ACTION_ID, required=true)
	private final String actionId;
	@XmlElement(name = Elements.ACTION_TYPE_ID, required=true)
	private final String actionTypeId;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private ActionAttribute() {
    	super();
    	this.actionId = null;
    	this.actionTypeId = null;
    }
    
    /**
	 * Constructs an ActionAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the ActionAttribute
	 */
    private ActionAttribute(Builder builder) {
    	super(builder);
        this.actionId = builder.getActionId();
        this.actionTypeId = builder.getActionTypeId();
    }
    
	@Override
	public String getActionId() {
		return this.actionId;
	}

	@Override
	public String getActionTypeId() {
		return this.actionTypeId;
	}
	
	/**
     * This builder is used to construct instances of ActionAttribute.  
     */
    public static class Builder extends BaseAttribute.Builder implements ActionAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = 5799994031051731535L;

        private String actionId;
        private String actionTypeId;

        /**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String actionId, String attributeDefinitionId, String actionTypeId, String value) {
        	super(id, attributeDefinitionId, value);
            setActionId(actionId);
            setValue(value);
        }

        public Builder attributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition){
        	setAttributeDefinition(attributeDefinition);
        	return this;
        }
        
        /**
         * Creates a builder from the given parameters.
         * 
         * @param id the ActionAtribute id
         * @param actionId the Action Id 
         * @param attributeDefinitionId The attributeDefinitionId
         * @param value 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String actionId, String attributeDefinitionId, String actionTypeId, String value) {
            return new Builder(id, actionId, attributeDefinitionId, actionTypeId, value);
        }
        
        public static Builder create(ActionAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getActionId(),
        			contract.getAttributeDefinitionId(),
        			contract.getValue(),
        			contract.getActionTypeId())
        			.attributeDefinition(KrmsAttributeDefinition.Builder
        					.create(contract.getAttributeDefinition()));
        	return builder;
        }

		public void setActionId(String actionId) {
            if (StringUtils.isBlank(actionId)) {
                throw new IllegalArgumentException("actionId is blank");
            }
			this.actionId = actionId;
		}

		public void setActionTypeId(String actionTypeId) {
			this.actionTypeId = actionTypeId;
		}
		
		@Override
		public String getActionId() {
			return actionId;
		}

		@Override
		public String getActionTypeId() {
			return actionTypeId;
		}

		/**
		 * Builds an instance of a ActionAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed ActionAttribute
		 */
        @Override
        public ActionAttribute build() {
            return new ActionAttribute(this);
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
		final static String ROOT_ELEMENT_NAME = "ActionAttribute";
		final static String TYPE_NAME = "ActionAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements extends BaseAttribute.Elements {
		final static String ACTION_ID = "actionId";
		final static String ACTION_TYPE_ID = "actionTypeId";
	}
}
