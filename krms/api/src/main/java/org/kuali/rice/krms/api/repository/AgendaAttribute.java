package org.kuali.rice.krms.api.repository;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AgendaAttribute extends BaseAttribute implements AgendaAttributeContract, ModelObjectComplete {		
	private static final long serialVersionUID = 2988399046412505534L;

	@XmlElement(name = Elements.AGENDA_ID, required=true)
	private String agendaId;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private AgendaAttribute() {
    	super();
    	this.agendaId = null;
    }
    
    /**
	 * Constructs an AgendaAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the AgendaAttribute
	 */
    private AgendaAttribute(Builder builder) {
    	super(builder);
        this.agendaId = builder.getAgendaId();
    }
    
	@Override
	public String getAgendaId() {
		return this.agendaId;
	}

	/**
     * This builder is used to construct instances of AgendaAttribute.  
     */
    public static class Builder extends BaseAttribute.Builder implements AgendaAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = -8619106871420999876L;
		
        private String agendaId;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String agendaId, String attributeDefinitionId, String value) {
        	super(id, attributeDefinitionId, value);
            setAgendaId(agendaId);
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

		public void setAgendaId(String agendaId) {
            if (StringUtils.isBlank(agendaId)) {
                throw new IllegalArgumentException("AgendaId is blank");
            }
			this.agendaId = agendaId;
		}

		@Override
		public String getAgendaId() {
			return agendaId;
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
	 * A class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements extends BaseAttribute.Elements {
		final static String AGENDA_ID = "agendaId";
	}
}
