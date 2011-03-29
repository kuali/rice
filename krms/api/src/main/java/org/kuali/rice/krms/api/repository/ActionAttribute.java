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
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of ActionAttribute. 
 * immutable. 
 * Instances of ActionAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = ActionAttribute.Constants.ROOT_ELEMENT_NAME, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ActionAttribute.Constants.TYPE_NAME, propOrder = {
		ActionAttribute.Elements.ID,
		ActionAttribute.Elements.ACTION_ID,
		ActionAttribute.Elements.ATTR_DEFN_ID,
		ActionAttribute.Elements.VALUE,
		ActionAttribute.Elements.ATTR_DEFN,
		"_elements"
})
public final class ActionAttribute implements ActionAttributeContract, ModelObjectComplete{	
	private static final long serialVersionUID = 3861710013942660403L;
	
	@XmlElement(name = Elements.ID, required=true, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
	private String id;
	@XmlElement(name = Elements.ACTION_ID, required=true, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
	private String actionId;
	@XmlElement(name = Elements.ATTR_DEFN_ID, required=true, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
	private String attributeDefinitionId;
	@XmlElement(name = Elements.VALUE, required=true, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
	private String value;
	@XmlElement(name = Elements.ATTR_DEFN, required=false, namespace = ActionAttribute.Constants.KRMSNAMESPACE)
	private KrmsAttributeDefinition attributeDefinition;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private ActionAttribute() {
    	this.id = null;
    	this.actionId = null;
    	this.attributeDefinitionId = null;
    	this.value = null;
    	this.attributeDefinition = null;
    }
    
    /**
	 * Constructs an ActionAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the ActionAttribute
	 */
    private ActionAttribute(Builder builder) {
        this.id = builder.getId();
        this.actionId = builder.getActionId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.value = builder.getValue();
        this.attributeDefinition = builder.getAttributeDefinition().build();
    }
    
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getActionId() {
		return this.actionId;
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
     * This builder is used to construct instances of ActionAttribute.  
     */
    public static class Builder implements ActionAttributeContract, ModelBuilder, Serializable {
		private static final long serialVersionUID = 7460424268906859210L;
		
		private String id;
        private String actionId;
        private String attributeDefinitionId;
        private String value;
        private KrmsAttributeDefinition.Builder attributeDefinition;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String actionId, String attributeDefinitionId, String value) {
            setId(id);
            setActionId(actionId);
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
         * @param id the ActionAtribute id
         * @param actionId the Action Id 
         * @param attributeDefinitionId The attributeDefinitionId
         * @param value 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String actionId, String attributeDefinitionId, String value) {
            return new Builder(id, actionId, attributeDefinitionId, value);
        }
        
        public static Builder create(ActionAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getActionId(),
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

		public void setActionId(String actionId) {
            if (StringUtils.isBlank(actionId)) {
                throw new IllegalArgumentException("actionId is blank");
            }
			this.actionId = actionId;
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
		public String getActionId() {
			return actionId;
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
		final static String KRMSNAMESPACE = "http://rice.kuali.org/schema/krms";		
		final static String ROOT_ELEMENT_NAME = "ActionAttribute";
		final static String TYPE_NAME = "ActionAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String ACTION_ID = "actionId";
		final static String ATTR_DEFN_ID = "attributeDefinitionId";
		final static String VALUE = "value";
		final static String ATTR_DEFN = "attributeDefinition";
	}
}
