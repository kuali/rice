package org.kuali.rice.krms.api.repository.agenda;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.jaxb.MapStringStringAdapter;

/**
 * Concrete model object implementation of KRMS Repository Agenda 
 * immutable. 
 * Instances of Agenda can be (un)marshalled to and from XML.
 *
 * @see AgendaDefinitionContract
 */
@XmlRootElement(name = AgendaDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaDefinition.Constants.TYPE_NAME, propOrder = {
		AgendaDefinition.Elements.AGENDA_ID,
		AgendaDefinition.Elements.NAME,
		AgendaDefinition.Elements.NAMESPACE_CODE,
		AgendaDefinition.Elements.TYPE_ID,
		AgendaDefinition.Elements.CONTEXT_ID,
		AgendaDefinition.Elements.FIRST_ITEM_ID,
		AgendaDefinition.Elements.ATTRIBUTES,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AgendaDefinition implements AgendaDefinitionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.AGENDA_ID, required = false)
	private final String agendaId;
	
	@XmlElement(name = Elements.NAME, required = true)
	private final String name;
	
	@XmlElement(name = Elements.NAMESPACE_CODE, required = true)
	private final String namespaceCode;
	
	@XmlElement(name = Elements.TYPE_ID, required = false)
	private final String typeId;
	
	@XmlElement(name = Elements.CONTEXT_ID, required = true)
	private final String contextId;
	
	@XmlElement(name = Elements.FIRST_ITEM_ID, required = false)
	private final String firstItemId;
	
	@XmlElement(name = Elements.ATTRIBUTES, required = false)
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	private final Map<String, String> attributes;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	/** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private AgendaDefinition() {
    	this.agendaId = null;
    	this.name = null;
    	this.namespaceCode = null;
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
    private AgendaDefinition(Builder builder) {
        this.agendaId = builder.getAgendaId();
        this.name = builder.getName();
        this.namespaceCode = builder.getNamespaceCode();
        this.typeId = builder.getTypeId();
        this.contextId = builder.getContextId();
        this.firstItemId = builder.getFirstItemId();
        this.attributes = Collections.unmodifiableMap(new HashMap<String, String>(builder.getAttributes()));
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
	public String getNamespaceCode() {
		return this.namespaceCode;
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
	public Map<String, String> getAttributes() {
		return this.attributes; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository Agenda.  It enforces the constraints of the {@link AgendaDefinitionContract}.
     */
    public static class Builder implements AgendaDefinitionContract, ModelBuilder, Serializable {
		
        private static final long serialVersionUID = -8862851720709537839L;
        
		private String agendaId;
        private String name;
        private String namespaceCode;
        private String typeId;
        private String contextId;
        private String firstItemId;
        private Map<String, String> attributes;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String name, String namespaceCode, String typeId, String contextId, String eventName) {
            setName(name);
            setNamespaceCode(namespaceCode);
            setTypeId(typeId);
            setContextId(contextId);
            setAttributes(new HashMap<String, String>());
        }
        
        public static Builder create(String name, String namespaceCode, String typeId, String contextId, String eventName){
        	return new Builder(name, namespaceCode, typeId, contextId, eventName);
        }
        /**
         * Creates a builder by populating it with data from the given {@link AgendaDefinitionContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(AgendaDefinitionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder =  new Builder(contract.getAgendaId(), contract.getName(),
            		contract.getNamespaceCode(), contract.getTypeId(), contract.getContextId());
            builder.setFirstItemId( contract.getFirstItemId() );
            builder.setAttributes(new HashMap<String, String>(contract.getAttributes()));
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
     
        public void setNamespaceCode(String namespaceCode) {
            if (StringUtils.isBlank(namespaceCode)) {
                throw new IllegalArgumentException("namespace is blank");
            }
			this.namespaceCode = namespaceCode;
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
		
		public void setAttributes(Map<String, String> attributes){
			if (attributes == null) {
				throw new IllegalArgumentException("attributes was null, consider passing an empty Map instead");
			}
			this.attributes = attributes;
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
		public String getNamespaceCode() {
			return namespaceCode;
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
		public Map<String, String> getAttributes() {
			return attributes;
		}

		/**
		 * Builds an instance of a Agenda based on the current state of the builder.
		 * 
		 * @return the fully-constructed Agenda
		 */
        @Override
        public AgendaDefinition build() {
            return new AgendaDefinition(this);
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
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_furutreElements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String AGENDA_ID = "agendaId";
		final static String NAME = "name";
		final static String NAMESPACE_CODE = "namespaceCode";
		final static String TYPE_ID = "typeId";
		final static String CONTEXT_ID = "contextId";
		final static String FIRST_ITEM_ID = "firstItemId";
		final static String ATTRIBUTES = "attributes";
		final static String ATTRIBUTE = "attribute";
	}

}
