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
 * Concrete model object implementation of KRMS Repository Rule 
 * immutable. 
 * Instances of Rule can be (un)marshalled to and from XML.
 *
 * @see RuleContract
 */
@XmlRootElement(name = Rule.Constants.ROOT_ELEMENT_NAME, namespace = Rule.Constants.KRMSNAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Rule.Constants.TYPE_NAME, propOrder = {
		Rule.Elements.ID,
		Rule.Elements.NAME,
		Rule.Elements.NAMESPACE,
		Rule.Elements.TYPE_ID,
		Rule.Elements.PROPOSITION,
		Rule.Elements.ACTION, 
		Rule.Elements.ATTRIBUTES,
		"_elements"
})
public final class Rule implements RuleContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true, namespace = Rule.Constants.KRMSNAMESPACE)
	private String ruleId;
	@XmlElement(name = Elements.NAME, required=true, namespace = Rule.Constants.KRMSNAMESPACE)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true, namespace = Rule.Constants.KRMSNAMESPACE)
	private String namespace;
	@XmlElement(name = Elements.TYPE_ID, required=true, namespace = Rule.Constants.KRMSNAMESPACE)
	private String typeId;
	@XmlElement(name = Elements.PROPOSITION, required=true, namespace = Rule.Constants.KRMSNAMESPACE)
	private Proposition proposition;
	@XmlElement(name = Elements.ACTION, required=false, namespace = Rule.Constants.KRMSNAMESPACE)
	private Action action;
	@XmlElement(name = Elements.ATTRIBUTES, required=false, namespace = Rule.Constants.KRMSNAMESPACE)
	private List<RuleAttribute> attributes;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	@XmlTransient
	private String propId;
	@XmlTransient
	private String actionId;

	/** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private Rule() {
    	this.ruleId = null;
    	this.name = null;
    	this.namespace = null;
    	this.typeId = null;
    	this.propId = null;
    	this.actionId = null;
    	this.proposition = null;
    	this.action = null;
    	this.attributes = null;
    }
    
    /**
	 * Constructs a KRMS Repository Rule object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the Rule
	 */
    private Rule(Builder builder) {
        this.ruleId = builder.getRuleId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.typeId = builder.getTypeId();
        this.propId = builder.getPropId();
        this.proposition = builder.getProposition().build();
        this.actionId = builder.getActionId();
        this.action = builder.getAction().build();
        List<RuleAttribute> attrList = new ArrayList<RuleAttribute>();
        for (RuleAttribute.Builder b : builder.attributes){
        	attrList.add(b.build());
        }
        this.attributes = Collections.unmodifiableList(attrList);
    }
    
	@Override
	public String getRuleId() {
		return this.actionId;
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
	public String getPropId(){
		return this.propId;
	}

	@Override
	public Proposition getProposition(){
		return this.proposition;
	}
	
	@Override
	public String getActionId(){
		return this.actionId;
	}
	
	@Override
	public Action getAction(){
		return this.action;
	}
	
	
	@Override
	public List<RuleAttribute> getAttributes() {
		return this.attributes; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository Rule.  It enforces the constraints of the {@link RuleContract}.
     */
    public static class Builder implements RuleContract, ModelBuilder, Serializable {
		
        private String ruleId;
        private String name;
        private String namespace;
        private String typeId;
        private String propId;
        private String actionId;
        private Proposition.Builder proposition;
        private Action.Builder action;
        private List<RuleAttribute.Builder> attributes;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String ruleId, String name, String namespace, String typeId, String propId) {
            setRuleId(ruleId);
            setName(name);
            setNamespace(namespace);
            setTypeId(typeId);
            setPropId(propId);
        }
        
        public Builder actionId (String actionId){
        	setActionId(actionId);
        	return this;
        }
        
        public Builder action (Action.Builder action){
        	setAction(action);
        	return this;
        }
        
        public Builder proposition (Proposition.Builder proposition){
        	setProposition(proposition);
        	return this;
        }
        
        public Builder attributes (List<RuleAttribute.Builder> attributes){
        	setAttributes(attributes);
        	return this;
        }
 
        public static Builder create(String ruleId, String name, String namespace, String typeId, String propId){
        	return new Builder(ruleId, name, namespace, typeId, propId);
        }
        /**
         * Creates a builder by populating it with data from the given {@link RuleContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(RuleContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	List <RuleAttribute.Builder> attrBuilderList = new ArrayList<RuleAttribute.Builder>();
        	if (contract.getAttributes() != null){
        		for (RuleAttributeContract attrContract : contract.getAttributes()){
        			RuleAttribute.Builder myBuilder = RuleAttribute.Builder.create(attrContract);
        			attrBuilderList.add(myBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getRuleId(), contract.getName(),
            		contract.getNamespace(), contract.getTypeId(), contract.getPropId())
            			.proposition(Proposition.Builder.create(contract.getProposition()))
            			.actionId(contract.getActionId())
            			.action(Action.Builder.create(contract.getAction()))
            			.attributes(attrBuilderList);
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setRuleId(String ruleId) {
            if (StringUtils.isBlank(ruleId)) {
                throw new IllegalArgumentException("ruleId is blank");
            }
			this.ruleId = ruleId;
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
		
		public void setPropId(String propId) {
			this.propId = propId;
		}
		
		public void setProposition(Proposition.Builder prop) {
			this.proposition = prop;
			this.setPropId(prop.getPropId());
		}
		
		public void setActionId(String actionId) {
			this.actionId = actionId;
		}
		
		public void setAction(Action.Builder action) {
			this.action = action;
			setActionId(action.getActionId());
		}
		
		public void setAttributes(List<RuleAttribute.Builder> attributes){
			if (attributes == null){
				this.attributes = Collections.unmodifiableList(new ArrayList<RuleAttribute.Builder>());
				return;
			}
			this.attributes = Collections.unmodifiableList(attributes);
		}
		
		@Override
		public String getRuleId() {
			return actionId;
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
		public String getPropId() {
			return propId;
		}

		@Override
		public String getActionId() {
			return actionId;
		}

		@Override
		public Proposition.Builder getProposition() {
			return proposition;
		}

		@Override
		public Action.Builder getAction(){
			return action;
		}
		@Override
		public List<RuleAttribute.Builder> getAttributes() {
			return attributes;
		}

		/**
		 * Builds an instance of a Rule based on the current state of the builder.
		 * 
		 * @return the fully-constructed Rule
		 */
        @Override
        public Rule build() {
            return new Rule(this);
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
		final static String ROOT_ELEMENT_NAME = "Rule";
		final static String TYPE_NAME = "RuleType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "ruleId";
		final static String NAME = "name";
		final static String NAMESPACE = "namespace";
		final static String TYPE_ID = "typeId";
		final static String PROPOSITION = "proposition";
		final static String ACTION = "action";
		final static String ATTRIBUTES = "attribute";
	}

}
