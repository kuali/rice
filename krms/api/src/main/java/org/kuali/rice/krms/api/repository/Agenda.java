package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of KRMS Repository Agenda 
 * immutable. 
 * Instances of Agenda can be (un)marshalled to and from XML.
 *
 * @see AgendaContract
 */
@XmlRootElement(name = Agenda.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Agenda.Constants.TYPE_NAME, propOrder = {
		Agenda.Elements.AGENDA_ID,
		Agenda.Elements.NAME,
		Agenda.Elements.NAMESPACE,
		Agenda.Elements.TYPE_ID,
		Agenda.Elements.CONTEXT_ID,
		Agenda.Elements.FIRST_ITEM_ID, 
		Agenda.Elements.ATTRIBUTES,
		"_elements"
})
public final class Agenda implements AgendaContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.AGENDA_ID, required=true)
	private String agendaId;
	@XmlElement(name = Elements.NAME, required=true)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true)
	private String namespace;
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	@XmlElement(name = Elements.CONTEXT_ID, required=true)
	private String contextId;
	@XmlElement(name = Elements.FIRST_ITEM_ID, required=true)
	private String firstItemId;
	@XmlElement(name = Elements.ATTRIBUTES, required=false)
	private List<AgendaAttribute> attributes;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	/** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private Agenda() {
    	this.agendaId = null;
    	this.name = null;
    	this.namespace = null;
    	this.typeId = null;
    	this.contextId = null;
    	this.firstItemId = null;
    	this.attributes = null;
    }
    
    /**
	 * Constructs a KRMS Repository Agenda object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the Agenda
	 */
    private Agenda(Builder builder) {
        this.agendaId = builder.getAgendaId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.typeId = builder.getTypeId();
        this.contextId = builder.getContextId();
        this.firstItemId = builder.getFirstItemId();
        List<AgendaAttribute> attrList = new ArrayList<AgendaAttribute>();
        for (AgendaAttribute.Builder b : builder.attributes){
        	attrList.add(b.build());
        }
        this.attributes = Collections.unmodifiableList(attrList);
    }
    
	@Override
	public String getAgendaId() {
		return this.agendaId;
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
	public String getTypeId() {
		return this.typeId;
	}

	@Override
	public String getContextId(){
		return this.contextId;
	}

	@Override
	public String getFirstItemId(){
		return this.firstItemId;
	}
	
	@Override
	public List<AgendaAttribute> getAttributes() {
		return this.attributes; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository Agenda.  It enforces the constraints of the {@link AgendaContract}.
     */
    public static class Builder implements AgendaContract, ModelBuilder, Serializable {
		
        private String agendaId;
        private String name;
        private String namespace;
        private String typeId;
        private String contextId;
        private String firstItemId;
        private List<AgendaAttribute.Builder> attributes;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String agendaId, String name, String namespace, String typeId, String contextId) {
            setAgendaId(agendaId);
            setName(name);
            setNamespace(namespace);
            setTypeId(typeId);
            setContextId(contextId);
        }
        
        public static Builder create(String agendaId, String name, String namespace, String typeId, String contextId){
        	return new Builder(agendaId, name, namespace, typeId, contextId);
        }
        /**
         * Creates a builder by populating it with data from the given {@link AgendaContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(AgendaContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	List <AgendaAttribute.Builder> attrBuilderList = new ArrayList<AgendaAttribute.Builder>();
        	if (contract.getAttributes() != null){
        		for (AgendaAttributeContract attrContract : contract.getAttributes()){
        			AgendaAttribute.Builder myBuilder = AgendaAttribute.Builder.create(attrContract);
        			attrBuilderList.add(myBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getAgendaId(), contract.getName(),
            		contract.getNamespace(), contract.getTypeId(), contract.getContextId());
            builder.setFirstItemId( contract.getFirstItemId() );
            builder.setAttributes( attrBuilderList );
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setAgendaId(String agendaId) {
            if (StringUtils.isBlank(agendaId)) {
                throw new IllegalArgumentException("agendaId is blank");
            }
			this.agendaId = agendaId;
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
     
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
	                throw new IllegalArgumentException("KRMS type id is blank");
			}
			this.typeId = typeId;
		}
		
		public void setContextId(String contextId) {
			this.contextId = contextId;
		}
		
		
		public void setFirstItemId(String firstItemId) {
			this.firstItemId = firstItemId;
		}
		
		public void setAttributes(List<AgendaAttribute.Builder> attributes){
			if (attributes == null){
				this.attributes = Collections.unmodifiableList(new ArrayList<AgendaAttribute.Builder>());
				return;
			}
			this.attributes = Collections.unmodifiableList(attributes);
		}
		
		@Override
		public String getAgendaId() {
			return agendaId;
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
		public String getTypeId() {
			return typeId;
		}

		@Override
		public String getContextId() {
			return contextId;
		}

		@Override
		public String getFirstItemId() {
			return firstItemId;
		}

		@Override
		public List<AgendaAttribute.Builder> getAttributes() {
			return attributes;
		}

		/**
		 * Builds an instance of a Agenda based on the current state of the builder.
		 * 
		 * @return the fully-constructed Agenda
		 */
        @Override
        public Agenda build() {
            return new Agenda(this);
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
		final static String ROOT_ELEMENT_NAME = "Agenda";
		final static String TYPE_NAME = "AgendaType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String AGENDA_ID = "agendaId";
		final static String NAME = "name";
		final static String NAMESPACE = "namespace";
		final static String TYPE_ID = "typeId";
		final static String CONTEXT_ID = "contextId";
		final static String FIRST_ITEM_ID = "firstItemId";
		final static String ATTRIBUTES = "attribute";
	}

}
