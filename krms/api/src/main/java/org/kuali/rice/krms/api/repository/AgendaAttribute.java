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
 * Concrete model object implementation of AgendaAttribute. 
 * immutable. 
 * Instances of AgendaAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = AgendaAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaAttribute.Constants.TYPE_NAME, propOrder = {
		AgendaAttribute.Elements.ID,
		AgendaAttribute.Elements.AGENDA_ID,
		AgendaAttribute.Elements.ATTR_DEFN_ID,
		AgendaAttribute.Elements.VALUE,
		AgendaAttribute.Elements.ATTR_DEFN,
		"_elements"
})
public final class AgendaAttribute implements AgendaAttributeContract, ModelObjectComplete{		
	private static final long serialVersionUID = 2988399046412505534L;

	@XmlElement(name = Elements.ID, required=true)
	private String id;
	@XmlElement(name = Elements.AGENDA_ID, required=true)
	private String agendaId;
	@XmlElement(name = Elements.ATTR_DEFN_ID, required=true)
	private String attributeDefinitionId;
	@XmlElement(name = Elements.VALUE, required=true)
	private String value;
	@XmlElement(name = Elements.ATTR_DEFN, required=false)
	private KrmsAttributeDefinition attributeDefinition;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private AgendaAttribute() {
    	this.id = null;
    	this.agendaId = null;
    	this.attributeDefinitionId = null;
    	this.value = null;
    	this.attributeDefinition = null;
    }
    
    /**
	 * Constructs an AgendaAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the AgendaAttribute
	 */
    private AgendaAttribute(Builder builder) {
        this.id = builder.getId();
        this.agendaId = builder.getAgendaId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.value = builder.getValue();
        this.attributeDefinition = builder.getAttributeDefinition().build();
    }
    
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getAgendaId() {
		return this.agendaId;
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
     * This builder is used to construct instances of AgendaAttribute.  
     */
    public static class Builder implements AgendaAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = -8619106871420999876L;
		
		private String id;
        private String agendaId;
        private String attributeDefinitionId;
        private String value;
        private KrmsAttributeDefinition.Builder attributeDefinition;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String agendaId, String attributeDefinitionId, String value) {
            setId(id);
            setAgendaId(agendaId);
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
         * @param id the AgendaAtribute id
         * @param AgendaId the Agenda Id 
         * @param attributeDefinitionId The attributeDefinitionId
         * @param value 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String id, String agendaId, String attributeDefinitionId, String value) {
            return new Builder(id, agendaId, attributeDefinitionId, value);
        }
        
        public static Builder create(AgendaAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getAgendaId(),
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

		public void setAgendaId(String agendaId) {
            if (StringUtils.isBlank(agendaId)) {
                throw new IllegalArgumentException("AgendaId is blank");
            }
			this.agendaId = agendaId;
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
		public String getAgendaId() {
			return agendaId;
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
		 * Builds an instance of a AgendaAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed AgendaAttribute
		 */
        @Override
        public AgendaAttribute build() {
            return new AgendaAttribute(this);
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
		final static String ROOT_ELEMENT_NAME = "AgendaAttribute";
		final static String TYPE_NAME = "AgendaAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String AGENDA_ID = "agendaId";
		final static String ATTR_DEFN_ID = "attributeDefinitionId";
		final static String VALUE = "value";
		final static String ATTR_DEFN = "attributeDefinition";
	}
}
