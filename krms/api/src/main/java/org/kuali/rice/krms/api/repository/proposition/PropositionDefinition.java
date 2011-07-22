package org.kuali.rice.krms.api.repository.proposition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition.Elements;


/**
 * Concrete model object implementation of KRMS Proposition. 
 * Immutable. 
 * Instances of Proposition can be (un)marshalled to and from XML.
 *
 * There are three main types of Propositions:
 *    1. Compound Propositions - a proposition consisting of other propositions
 *    	 and a boolean algebra operator (AND, OR) defining how to evaluate those propositions.
 *    2. Parameterized Propositions - a proposition which is parameterized by some set of values, 
 *    	 evaluation logic is implemented by hand and returns true or false
 *    3. Simple Propositions - a proposition of the form lhs op rhs where 
 *    	lhs=left-hand side, rhs=right-hand side, and op=operator
 * Propositions are reference by a rule or another proposition (in the case of compound propositions).
 * Propositions are never re-used across multiple rules.
 * Each proposition can have zero or more parameters. The proposition parameter is the primary 
 * data element used to define the proposition.  (@see PropositionParameter)
 * 
 * @see PropositonContract
 * @see PropositionParameterContract
 */
@XmlRootElement(name = PropositionDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PropositionDefinition.Constants.TYPE_NAME, propOrder = {
		PropositionDefinition.Elements.ID,
		PropositionDefinition.Elements.DESC,
        PropositionDefinition.Elements.RULE_ID,
        PropositionDefinition.Elements.TYPE_ID,
		PropositionDefinition.Elements.PROP_TYPE_CODE,
		PropositionDefinition.Elements.PARAMETERS,									// xml element name differs from class property name
		PropositionDefinition.Elements.CMPND_OP_CODE,
		PropositionDefinition.Elements.CMPND_COMPONENTS,
        CoreConstants.CommonElements.VERSION_NUMBER,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class PropositionDefinition extends AbstractJaxbModelObject implements PropositionDefinitionContract {
	private static final long serialVersionUID = 2783959459503209577L;

	// TODO: change this to field name to id
	@XmlElement(name = Elements.ID, required=true)
	private String id;
	
	@XmlElement(name = Elements.DESC, required=true)
	private String description;
	
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	
    @XmlElement(name = Elements.RULE_ID, required=true)
    private String ruleId;
    
    @XmlElement(name = Elements.PROP_TYPE_CODE, required=true)
	private String propositionTypeCode;

	@XmlElementWrapper(name = Elements.PARAMETERS)
	@XmlElement(name = Elements.PARAMETER, required=false)
	private List<PropositionParameter> parameters;
	
	@XmlElement(name = Elements.CMPND_OP_CODE, required=false)
	private String compoundOpCode;
	
	@XmlElementWrapper(name = Elements.CMPND_COMPONENTS, required=false)
	@XmlElement(name = Elements.CMPND_COMPONENT, required=false)
	private List<PropositionDefinition> compoundComponents;
	
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private PropositionDefinition() {
    	this.id = null;
    	this.description = null;
    	this.typeId = null;
    	this.propositionTypeCode = null;
    	this.parameters = null;
    	this.compoundOpCode = null;
    	this.compoundComponents = null;
        this.versionNumber = null;
    }
    
    /**
	 * Constructs a KRMS Proposition from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KRMS Proposition
	 */
    private PropositionDefinition(Builder builder) {
        this.id = builder.getId();
        this.description = builder.getDescription();
        this.ruleId = builder.getRuleId();
        this.typeId = builder.getTypeId();
        this.propositionTypeCode = builder.getPropositionTypeCode();
        
        // Build parameter list
        List<PropositionParameter> paramList = new ArrayList<PropositionParameter>();
        for (PropositionParameter.Builder b : builder.parameters){
        	paramList.add(b.build());
        }
        this.parameters = Collections.unmodifiableList(paramList);
        
        // Build Compound Proposition properties
        this.compoundOpCode = builder.getCompoundOpCode();
        List <PropositionDefinition> componentList = new ArrayList<PropositionDefinition>();
        if (builder.compoundComponents != null){
        	for (PropositionDefinition.Builder b : builder.compoundComponents){
        		componentList.add(b.build());
        	}
            this.compoundComponents = Collections.unmodifiableList(componentList);
        }
        this.versionNumber = builder.getVersionNumber();
    }
    
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

    /**
     * @return the ruleId
     */
    public String getRuleId() {
        return this.ruleId;
    }

	@Override
	public String getTypeId() {
		return this.typeId;
	}

	@Override
	public String getPropositionTypeCode() {
		return this.propositionTypeCode; 
	}

	@Override
	public List<PropositionParameter> getParameters() {
		return this.parameters; 
	}

	@Override
	public String getCompoundOpCode() {
		return this.compoundOpCode; 
	}

	@Override
	public List<PropositionDefinition> getCompoundComponents() {
		return this.compoundComponents; 
	}

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }
        
	/**
     * This builder is used to construct instances of KRMS Proposition.  It enforces the constraints of the {@link PropositionDefinitionContract}.
     */
    public static class Builder implements PropositionDefinitionContract, ModelBuilder, Serializable {
    	private static final long serialVersionUID = -6889320709850568900L;
		
        private String id;
        private String description;
        private String ruleId;
        private String typeId;
        private String propositionTypeCode;
        private List<PropositionParameter.Builder> parameters;
        private String compoundOpCode;
        private List<PropositionDefinition.Builder> compoundComponents;
        private RuleDefinition.Builder rule;
        private Long versionNumber;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 * @param typeId TODO
		 */
        private Builder(String propId, String propTypeCode, String ruleId, String typeId, List<PropositionParameter.Builder> parameters) {
        	setId(propId);
			setPropositionTypeCode(propTypeCode);
			setRuleId(ruleId);
			setTypeId(typeId);
			setParameters(parameters);
        }
        
        public Builder compoundOpCode(String opCode){
        	setCompoundOpCode(opCode);
        	return this;
        }
        
        public Builder compoundComponents (List<PropositionDefinition.Builder> components){
        	setCompoundComponents(components);
        	return this;
        }
 
        public static Builder create(String propId, String propTypeCode, String ruleId, String typeId, List<PropositionParameter.Builder> parameters){
        	return new Builder(propId, propTypeCode, ruleId, typeId, parameters);
        }
        
        /**
         * Creates a builder by populating it with data from the given {@link PropositionDefinitionContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(PropositionDefinitionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	List <PropositionParameter.Builder> paramBuilderList = new ArrayList<PropositionParameter.Builder>();
        	if (contract.getParameters() != null){
        		for (PropositionParameterContract paramContract : contract.getParameters()){
        			PropositionParameter.Builder myBuilder = PropositionParameter.Builder.create(paramContract);
        			paramBuilderList.add(myBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getId(), contract.getPropositionTypeCode(), contract.getRuleId(), contract.getTypeId(), paramBuilderList);
            
        	List <PropositionDefinition.Builder> componentBuilderList = new ArrayList<PropositionDefinition.Builder>();
        	if (contract.getCompoundComponents() != null) {
        		for (PropositionDefinitionContract cContract : contract.getCompoundComponents()){
        			PropositionDefinition.Builder pBuilder = PropositionDefinition.Builder.create(cContract);
        			componentBuilderList.add(pBuilder);
        		}
                builder.setCompoundComponents(componentBuilderList);
        	}
        	builder.setCompoundOpCode(contract.getCompoundOpCode());
            builder.setDescription(contract.getDescription());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set
		 */

        public void setId(String propId) {
            if (propId != null && StringUtils.isBlank(propId)) {
                throw new IllegalArgumentException("proposition id must not be blank");            	
            }
			this.id = propId;
		}

		public void setDescription(String desc) {
			this.description = desc;
		}
		
        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }
        
        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }
        
        public void setRule(RuleDefinition.Builder rule) {
            if (rule != null && !StringUtils.isBlank(rule.getId())) {
                setRuleId(rule.getId());
            }
            this.rule = rule;
        }
        
		public void setPropositionTypeCode(String propTypeCode) {
			if (StringUtils.isBlank(propTypeCode)) {
                throw new IllegalArgumentException("proposition type code is blank");
			}
			if (!PropositionType.VALID_TYPE_CODES.contains(propTypeCode)) {
                throw new IllegalArgumentException("invalid proposition type code");
			}
			this.propositionTypeCode = propTypeCode;
		}
		
		public void setParameters(List<PropositionParameter.Builder> parameters) {
			// compound propositions have empty parameter lists
			// Simple propositions must have a non-empty parameter list
			if (parameters == null || parameters.isEmpty()){
				this.parameters = Collections.unmodifiableList(new ArrayList<PropositionParameter.Builder>());
			} else {
			    this.parameters = Collections.unmodifiableList(parameters);
			}
		}
		
		public void setCompoundOpCode(String opCode){
			if (StringUtils.isBlank(opCode)){ return; }
			if (!LogicalOperator.OP_CODES.contains(opCode)){
				throw new IllegalArgumentException("invalid opCode value");
			}
			this.compoundOpCode = opCode;
		}

		public void setCompoundComponents(List<PropositionDefinition.Builder> components){
			if (components == null || components.isEmpty()){
				this.compoundComponents = Collections.unmodifiableList(new ArrayList<PropositionDefinition.Builder>());
				return;
			}
			this.compoundComponents = Collections.unmodifiableList(components);
		}
		
        public void setVersionNumber(Long versionNumber){
            this.versionNumber = versionNumber;
        }
        
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
		@Override
		public String getRuleId() {
		    return ruleId;
		}

		@Override
		public String getTypeId() {
			return typeId;
		}

		@Override
		public String getPropositionTypeCode() {
			return propositionTypeCode;
		}
		
		@Override
		public List<PropositionParameter.Builder> getParameters() {
			return parameters;
		}

		@Override
		public String getCompoundOpCode() {
			return compoundOpCode;
		}
		
		@Override
		public List<PropositionDefinition.Builder> getCompoundComponents() {
			return compoundComponents;
		}

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

		/**
		 * Builds an instance of a Proposition based on the current state of the builder.
		 * 
		 * @return the fully-constructed Proposition
		 */
        @Override
        public PropositionDefinition build() {
            return new PropositionDefinition(this);
        }
		
    }
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "proposition";
		final static String TYPE_NAME = "PropositionType";
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String DESC = "description";
        final static String RULE_ID = "ruleId";
		final static String TYPE_ID = "typeId";
		final static String PROP_TYPE_CODE = "propositionTypeCode";
		final static String PARAMETER = "parameter";
		final static String PARAMETERS = "parameters";
		final static String CMPND_OP_CODE = "compoundOpCode";
		final static String CMPND_COMPONENTS = "compoundComponents";
		final static String CMPND_COMPONENT = "proposition";
	}

	
}
