package org.kuali.rice.krms.api.repository.context;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinitionContract;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable representation of a context definition.  A context definition
 * defines information about a context which can be loaded into the rules
 * engine for evaluation.
 * 
 * A context definition includes a list of agendas which are valid within the
 * context.  Typically, during rule engine execution, one or more of these
 * agendas is selected for execution based on a given set of selection criteria.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = ContextDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ContextDefinition.Constants.TYPE_NAME, propOrder = {
		ContextDefinition.Elements.ID,
		ContextDefinition.Elements.NAMESPACE,
		ContextDefinition.Elements.NAME,
        ContextDefinition.Elements.TYPE_ID,
        ContextDefinition.Elements.DESCRIPTION,
		ContextDefinition.Elements.AGENDAS,
		ContextDefinition.Elements.ATTRIBUTES,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ContextDefinition extends AbstractDataTransferObject implements ContextDefinitionContract {
	
	private static final long serialVersionUID = -6639428234851623868L;

	@XmlElement(name = Elements.ID, required = false)
	private final String id;
	
	@XmlElement(name = Elements.NAME, required = true)
    private final String name;
	
	@XmlElement(name = Elements.NAMESPACE, required = true)
    private final String namespace;
	
	@XmlElement(name = Elements.TYPE_ID, required = false)
    private final String typeId;
	
    @XmlElement(name = Elements.DESCRIPTION, required = false)
    private final String description;
    
	@XmlElementWrapper(name = Elements.AGENDAS)
	@XmlElement(name = Elements.AGENDA, required = false)
	private final Set<AgendaDefinition> agendas;
	    
	@XmlElement(name = Elements.ATTRIBUTES, required = false)
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	private final Map<String, String> attributes;
	
	@XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
	
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;

    /**
     * Used only by JAXB.
     */
    private ContextDefinition() {
    	this.id = null;
    	this.name = null;
    	this.namespace = null;
    	this.typeId = null;
    	this.description = null;
    	this.agendas = null;
    	this.versionNumber = null;
    	this.attributes = null;
    }
    
    private ContextDefinition(Builder builder) {
    	this.id = builder.getId();
    	this.name = builder.getName();
    	this.namespace = builder.getNamespace();

    	this.description = builder.getDescription();

    	this.typeId = builder.getTypeId();
    	this.agendas = constructAgendas(builder.getAgendas());
    	this.versionNumber = builder.getVersionNumber();
        if (builder.getAttributes() != null){
        	this.attributes = Collections.unmodifiableMap(new HashMap<String, String>(builder.getAttributes()));
        } else {
        	this.attributes = null;
        }
    }
    
    private static Set<AgendaDefinition> constructAgendas(Set<AgendaDefinition.Builder> agendaBuilders) {
    	Set<AgendaDefinition> agendas = new HashSet<AgendaDefinition>();
    	if (agendaBuilders != null) {
    		for (AgendaDefinition.Builder agendaBuilder : agendaBuilders) {
    			agendas.add(agendaBuilder.build());
    		}
    	}
    	return agendas;
    }
    
    @Override
	public String getId() {
		return id;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTypeId() {
		return typeId;
	}

    @Override
    public String getDescription() {
        return description;
    }
	
	@Override
	public Set<AgendaDefinition> getAgendas() {
		return Collections.unmodifiableSet(this.agendas);
	}
	
	@Override
	public Map<String, String> getAttributes() {
		return this.attributes; 
	}

	@Override
	public Long getVersionNumber() {
		return versionNumber;
	}
	
	/**
	 * A builder which can be used to construct ContextDefinition instances.  Enforces the
	 * constraints of the {@link ContextDefinitionContract}.  This class is the only means
	 * by which a {@link ContextDefinition} object can be constructed.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	public static final class Builder implements ContextDefinitionContract, ModelBuilder, Serializable  {
    	
    	private static final long serialVersionUID = -219369603932108436L;
    	
		private String id;
		private String namespace;
        private String name;
        private String typeId;
        private String description;
        private Set<AgendaDefinition.Builder> agendas;
        private Map<String, String> attributes;
        private Long versionNumber;
        
        private Builder(String namespace, String name) {
        	setNamespace(namespace);
        	setName(name);
        	setAgendas(new HashSet<AgendaDefinition.Builder>());
            setAttributes(new HashMap<String, String>());
        }
        
        /**
         * Creates a context definition builder with the given required values
         * 
         * @param namespace the namespace code of the context definition to create, must not be null or blank
         * @param name the name of the context definition to create, must not be null or blank
         * 
         * @return a builder with the required values already initialized
         * 
         * @throws IllegalArgumentException if the given namespace is null or blank
         * @throws IllegalArgumentException if the given name is null or blank
         */
        public static Builder create(String namespace, String name) {
        	return new Builder(namespace, name);
        }
        
        /**
         * Creates a populates a builder with the data on the given ContextDefinitionContract.
         * This is similar in nature to a "copy constructor" for Style.
         * 
         * @param contract an object implementing the ContextDefinitionContract from which
         * to copy property values
         *  
         * @return a builder with the values from the contract already initialized
         * 
         * @throws IllegalArgumentException if the given contract is null
         */
        public static Builder create(ContextDefinitionContract contract) {
        	if (contract == null) {
        		throw new IllegalArgumentException("contract was null");
        	}
        	Builder builder = create(contract.getNamespace(), contract.getName());
        	builder.setId(contract.getId());
        	builder.setTypeId(contract.getTypeId());
            builder.setDescription(contract.getDescription());
        	builder.setVersionNumber(contract.getVersionNumber());
        	builder.setAgendas(contract.getAgendas());
            if (contract.getAttributes() != null) {
                builder.setAttributes(new HashMap<String, String>(contract.getAttributes()));
            }
        	return builder;
        }
        
        @Override
        public ContextDefinition build() {
        	return new ContextDefinition(this);
        }
        
		@Override
		public Long getVersionNumber() {
			return this.versionNumber;
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public String getNamespace() {
			return this.namespace;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getTypeId() {
			return this.typeId;
		}

        @Override
        public String getDescription() {
            return description;
        }

        @Override
		public Set<AgendaDefinition.Builder> getAgendas() {
			return agendas;
		}

		@Override
		public Map<String, String> getAttributes() {
			return attributes;
		}

		/**
         * Sets the id for the context definition that will be created by this builder.
         * 
         * @param id the id to set
         */
		public void setId(String id) {
			if (id != null){
				if (StringUtils.isBlank(id)){
					throw new IllegalArgumentException("context id is blank");					
				}
			}
			this.id = id;
		}

		/**
		 * Sets the namespace code for the context definition that will be created
		 * by this builder.  The namespace code must not be blank or null.
		 * 
		 * @param namespace the namespace to set on this builder, must not be
		 * null or blank
		 * 
		 * @throws IllegalArgumentException if the given namespace code is null or blank
		 */
		public void setNamespace(String namespace) {
			if (StringUtils.isBlank(namespace)) {
				throw new IllegalArgumentException("namespace is blank");
			}
			this.namespace = namespace;
		}

		/**
		 * Sets the name for the context definition that will be created
		 * by this builder.  The name must not be blank or null.
		 * 
		 * @param name the name to set on this builder, must not be
		 * null or blank
		 * 
		 * @throws IllegalArgumentException if the given name is null or blank
		 */
		public void setName(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("name is blank");
			}
			this.name = name;
		}

		/**
         * Sets the typeId for the context definition that will be created by this builder.
         * 
         * @param typeId the typeId to set
         */
		public void setTypeId(String typeId) {
			if (typeId != null){
				if (StringUtils.isBlank(typeId)){
					throw new IllegalArgumentException("type id is blank");					
				}
			}
			this.typeId = typeId;
		}
		
        /**
         * Sets the typeId for the context definition that will be created by this builder.
         *
         * @param typeId the typeId to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

		public void setAgendas(Set<? extends AgendaDefinitionContract> agendaContracts) {
			this.agendas = new HashSet<AgendaDefinition.Builder>();
			if (agendaContracts != null) for (AgendaDefinitionContract agendaContract : agendaContracts) {
				this.agendas.add(AgendaDefinition.Builder.create(agendaContract));
			}
		}

		public void setAttributes(Map<String, String> attributes){
			if (attributes == null){
				this.attributes = Collections.emptyMap();
			}
			this.attributes = Collections.unmodifiableMap(attributes);
		}
		
		/**
         * Sets the version number for the style that will be returned by this
         * builder.
         * 
         * <p>In general, this value should not be manually set on the builder,
         * but rather copied from an existing {@link ContextDefinitionContract} when
         * invoking {@link Builder#create(ContextDefinitionContract)}.
         * 
         * @param versionNumber the version number to set
         */
		public void setVersionNumber(Long versionNumber) {
			this.versionNumber = versionNumber;
		}
		
    }
	
	/**
     * Defines some internal constants used on this class.
     */
    public static class Constants {
        final static String ROOT_ELEMENT_NAME = "context";
        final static String TYPE_NAME = "ContextDefinitionType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    public static class Elements {
        final static String ID = "id";
        final static String NAMESPACE = "namespace";
        final static String NAME = "name";
        final static String TYPE_ID = "typeId";
        final static String DESCRIPTION = "description";
        final static String AGENDA = "agenda";
        final static String AGENDAS = "agendas";
		final static String ATTRIBUTES = "attributes";
    }
		
	
	
}
