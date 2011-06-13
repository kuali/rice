package org.kuali.rice.krms.api.repository.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.repository.action.ActionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;

/**
 * Concrete model object implementation of KRMS Repository Rule 
 * immutable. 
 * Instances of Rule can be (un)marshalled to and from XML.
 *
 * @see RuleDefinitionContract
 */
@XmlRootElement(name = RuleDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RuleDefinition.Constants.TYPE_NAME, propOrder = {
		RuleDefinition.Elements.ID,
		RuleDefinition.Elements.NAME,
        RuleDefinition.Elements.NAMESPACE,
        RuleDefinition.Elements.DESCRIPTION,
		RuleDefinition.Elements.TYPE_ID,
		RuleDefinition.Elements.PROPOSITION,
		RuleDefinition.Elements.ACTIONS,
		RuleDefinition.Elements.ATTRIBUTES,
        CoreConstants.CommonElements.VERSION_NUMBER,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RuleDefinition implements RuleDefinitionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true)
	private String id;
    @XmlElement(name = Elements.NAME, required=true)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true)
	private String namespace;
    @XmlElement(name = Elements.DESCRIPTION, required=false)
    private String description;
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	@XmlElement(name = Elements.PROPOSITION, required=true)
	private PropositionDefinition proposition;
	
	@XmlElementWrapper(name = Elements.ACTIONS)
	@XmlElement(name = Elements.ACTION, required=false)
	private List<ActionDefinition> actions;
	
	@XmlElement(name = Elements.ATTRIBUTES, required = false)
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	private final Map<String, String> attributes;
	
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	@XmlTransient
	private String propId;

	/** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private RuleDefinition() {
    	this.id = null;
    	this.name = null;
    	this.namespace = null;
    	this.typeId = null;
    	this.propId = null;
    	this.proposition = null;
    	this.actions = null;
    	this.attributes = null;
        this.versionNumber = null;
    }
    
    /**
	 * Constructs a KRMS Repository Rule object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the Rule
	 */
    private RuleDefinition(Builder builder) {
        this.id = builder.getId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.typeId = builder.getTypeId();
        this.propId = builder.getPropId();
        
        if (builder.getProposition() != null) {
            this.proposition = builder.getProposition().build();
        }
        
        List<ActionDefinition> actionList = new ArrayList<ActionDefinition> ();
        if (builder.getActions() != null){
        	for (ActionDefinition.Builder b : builder.actions){
        		actionList.add(b.build());
        	}
            this.actions = Collections.unmodifiableList(actionList);
        }
        if (builder.attributes != null){
        	this.attributes = Collections.unmodifiableMap(builder.getAttributes());
        } else {
        	this.attributes = null;
        }
        this.versionNumber = builder.getVersionNumber();
    }
    
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

    public String getDescription() {
        return this.description;
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
	public String getPropId(){
		return this.propId;
	}

	@Override
	public PropositionDefinition getProposition(){
		return this.proposition;
	}
	
	@Override
	public List<ActionDefinition> getActions(){
		return this.actions;
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
     * This builder is used to construct instances of KRMS Repository Rule.  It enforces the constraints of the {@link RuleDefinitionContract}.
     */
    public static class Builder implements RuleDefinitionContract, ModelBuilder, Serializable {		
        private static final long serialVersionUID = -7850514191699945347L;
        
		private String id;
        private String name;
        private String description;
        private String namespace;
        private String typeId;
        private String propId;
        private PropositionDefinition.Builder proposition;
        private List<ActionDefinition.Builder> actions;
        private Map<String, String> attributes;
        private Long versionNumber;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String ruleId, String name, String namespace, String typeId, String propId) {
            setId(ruleId);
            setName(name);
            setNamespace(namespace);
            setTypeId(typeId);
            setPropId(propId);
            setAttributes(new HashMap<String, String>());
        }
        
        public static Builder create(String ruleId, String name, String namespace, String typeId, String propId){
        	return new Builder(ruleId, name, namespace, typeId, propId);
        }
        
        /**
         * Creates a builder by populating it with data from the given {@link RuleDefinitionContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(RuleDefinitionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }

        	List <ActionDefinition.Builder> actionList = new ArrayList<ActionDefinition.Builder>();
        	if (contract.getActions() != null){
        		for (ActionDefinitionContract actionContract : contract.getActions()){
        			ActionDefinition.Builder actBuilder = ActionDefinition.Builder.create(actionContract);
        			actionList.add(actBuilder);
        		}
        	}
        	
            Builder builder =  new Builder(contract.getId(), contract.getName(),
            		contract.getNamespace(), contract.getTypeId(), contract.getPropId());
            if (contract.getProposition() != null) {
                builder.setProposition(PropositionDefinition.Builder.create(contract.getProposition()));
            }
        	if (contract.getAttributes() != null){
                builder.setAttributes(new HashMap<String, String>(contract.getAttributes()));
        	}
            builder.setActions(actionList);
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setDescription(contract.getDescription());
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setId(String ruleId) {
            if (ruleId != null && StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("rule ID must be null or else non-blank");
            }
			this.id = ruleId;
		}
     
        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }
            this.name = name;
        }
     
        public void setDescription(String description) {
            if (description != null && StringUtils.isBlank(description)) {
                throw new IllegalArgumentException("description is non-null but is blank");
            }
            this.description = description;
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
		
		public void setPropId(String propId) {
		    if (propId != null && StringUtils.isBlank(propId)) {
		        throw new IllegalArgumentException("propId must be null or non-blank");
		    }
			this.propId = propId;
		}
		
		public void setProposition(PropositionDefinition.Builder prop) {
			this.proposition = prop;
			this.setPropId(prop.getId());
		}
		
		public void setActions(List<ActionDefinition.Builder> actions) {
			if (actions == null){
				this.actions = Collections.unmodifiableList(new ArrayList<ActionDefinition.Builder>());
				return;
			}
			this.actions = Collections.unmodifiableList(actions);
		}
		
		public void setAttributes(Map<String, String> attributes){
			if (attributes == null){
				this.attributes = Collections.emptyMap();
			}
			this.attributes = Collections.unmodifiableMap(attributes);
		}
		
        public void setVersionNumber(Long versionNumber){
            this.versionNumber = versionNumber;
        }
        
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getDescription() {
		    return description;
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
		public String getPropId() {
			return propId;
		}

		@Override
		public PropositionDefinition.Builder getProposition() {
			return proposition;
		}

		@Override
		public List<ActionDefinition.Builder> getActions(){
			return actions;
		}
		@Override
		public Map<String, String> getAttributes() {
			return attributes;
		}

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

		/**
		 * Builds an instance of a Rule based on the current state of the builder.
		 * 
		 * @return the fully-constructed Rule
		 */
        @Override
        public RuleDefinition build() {
            return new RuleDefinition(this);
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
	public static class Constants {
		final static String ROOT_ELEMENT_NAME = "rule";
		final static String TYPE_NAME = "RuleType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
        final static String NAME = "name";
        final static String DESCRIPTION = "description";
		final static String NAMESPACE = "namespace";
		final static String TYPE_ID = "typeId";
		final static String PROPOSITION = "proposition";
		final static String ACTIONS = "actions";
		final static String ACTION = "action";
		final static String ATTRIBUTES = "attributes";
	}

}
