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
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

import org.kuali.rice.krms.engine.LogicalOperator;

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
		PropositionDefinition.Elements.PROP_ID,
		PropositionDefinition.Elements.DESC,
		PropositionDefinition.Elements.TYPE_ID,
		PropositionDefinition.Elements.PROP_TYPE_CODE,
		"parameters",									// xml element name differs from class property name
		PropositionDefinition.Elements.CMPND_OP_CODE,
		"compoundComponents",							// xml element name differs from class property name
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class PropositionDefinition implements PropositionDefinitionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.PROP_ID, required=true)
	private String propId;
	
	@XmlElement(name = Elements.DESC, required=true)
	private String description;
	
	@XmlElement(name = Elements.TYPE_ID, required=true)
	private String typeId;
	
	@XmlElement(name = Elements.PROP_TYPE_CODE, required=true)
	private String propositionTypeCode;
	
	@XmlElement(name = Elements.PARAMETERS, required=false)
	private List<PropositionParameter> parameters;
	
	@XmlElement(name = Elements.CMPND_OP_CODE, required=false)
	private String compoundOpCode;
	
	@XmlElement(name = Elements.CMPND_COMPONENTS, required=false)
	private List<PropositionDefinition> compoundComponents;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private PropositionDefinition() {
    	this.propId = null;
    	this.description = null;
    	this.typeId = null;
    	this.propositionTypeCode = null;
    	this.parameters = null;
    	this.compoundOpCode = null;
    	this.compoundComponents = null;
    }
    
    /**
	 * Constructs a KRMS Proposition from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KRMS Proposition
	 */
    private PropositionDefinition(Builder builder) {
        this.propId = builder.getPropId();
        this.description = builder.getDescription();
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
        }
        this.compoundComponents = Collections.unmodifiableList(componentList);
    }
    
	@Override
	public String getPropId() {
		return this.propId;
	}

	@Override
	public String getDescription() {
		return this.description;
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

	/**
     * This builder is used to construct instances of KRMS Proposition.  It enforces the constraints of the {@link PropositionDefinitionContract}.
     */
    public static class Builder implements PropositionDefinitionContract, ModelBuilder, Serializable {
    	private static final long serialVersionUID = -6889320709850568900L;
		
        private String propId;
        private String description;
        private String typeId;
        private String propositionTypeCode;
        private List<PropositionParameter.Builder> parameters;
        private String compoundOpCode;
        private List<PropositionDefinition.Builder> compoundComponents;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String propId, String desc, String typeId, String propTypeCode, List<PropositionParameter.Builder> parameters) {
            setPropId(propId);
            setDescription(desc);
            setTypeId(typeId);
			setPropositionTypeCode(propTypeCode);
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
 
        public static Builder create(String propId, String desc, String typeId, String propTypeCode, List<PropositionParameter.Builder> parameters){
        	return new Builder(propId, desc, typeId, propTypeCode, parameters);
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
        	List <PropositionDefinition.Builder> componentBuilderList = new ArrayList<PropositionDefinition.Builder>();
        	if (contract.getCompoundComponents() != null) {
        		for (PropositionDefinitionContract cContract : contract.getCompoundComponents()){
        			PropositionDefinition.Builder pBuilder = PropositionDefinition.Builder.create(cContract);
        			componentBuilderList.add(pBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getPropId(), contract.getDescription(), 
            			contract.getTypeId(), contract.getPropositionTypeCode(), paramBuilderList)
            			.compoundOpCode(contract.getCompoundOpCode())
            			.compoundComponents(componentBuilderList);
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setPropId(String propId) {
            if (StringUtils.isBlank(propId)) {
                throw new IllegalArgumentException("propId is blank");
            }
			this.propId = propId;
		}

		public void setDescription(String desc) {
            if (StringUtils.isBlank(desc)) {
                throw new IllegalArgumentException("description is blank");
            }
			this.description = desc;
		}
		
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
	                throw new IllegalArgumentException("KRMS type id is blank");
			}
			// TODO: check against valid values ?
			this.typeId = typeId;
		}
		
		public void setPropositionTypeCode(String propTypeCode) {
			if (StringUtils.isBlank(propTypeCode)) {
                throw new IllegalArgumentException("proposition type code is blank");
			}
			if (!PropositionTypes.VALID_TYPE_CODES.contains(propTypeCode)) {
                throw new IllegalArgumentException("invalid proposition type code");
			}
			this.propositionTypeCode = propTypeCode;
		}
		
		public void setParameters(List<PropositionParameter.Builder> parameters){
			// compound propositions have empty parameter lists
			// Simple propositions must have a non-empty parameter list
			if (parameters == null || parameters.isEmpty()){
				if (PropositionTypes.COMPOUND.code().equals( this.propositionTypeCode)){
					this.parameters = Collections.unmodifiableList(new ArrayList<PropositionParameter.Builder>());
					return;
				} else {
					throw new IllegalArgumentException("no proposition parameters are specified");
				}
			}
			this.parameters = Collections.unmodifiableList(parameters);
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
		

		@Override
		public String getPropId() {
			return propId;
		}

		@Override
		public String getDescription() {
			return description;
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
		final static String ROOT_ELEMENT_NAME = "Proposition";
		final static String TYPE_NAME = "PropositionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String PROP_ID = "propId";
		final static String DESC = "description";
		final static String TYPE_ID = "typeId";
		final static String PROP_TYPE_CODE = "propositionTypeCode";
		final static String PARAMETERS = "parameter";
		final static String CMPND_OP_CODE = "compoundOpCode";
		final static String CMPND_COMPONENTS = "proposition";
	}

	/**
	 * This enumeration identifies the valid Proposition type codes
	 * The PropositionType enumeration lists the appropriate types of propositions
	 * The valid types are:
     *    Compound Propositions - a proposition consisting of other propositions and a boolean algebra operator (AND, OR) defining how to evaluate those propositions
     *    Parameterized Propositions - a proposition which is parameterized by some set of values, evaluation logic is implemented by hand and returns true or false
	 *    Simple Propositions - a proposition of the form lhs op rhs where lhs=left-hand side, rhs=right-hand side, and op=operator
	 */
	public enum PropositionTypes {
		SIMPLE("S"),
		PARAMETERIZED("P"),
		COMPOUND("C");
		
		private final String code;
		private PropositionTypes(String code){
			this.code = code;
		}
		public static final Collection<PropositionDefinition.PropositionTypes> VALID_TYPES =
			Collections.unmodifiableCollection(Arrays.asList(SIMPLE, PARAMETERIZED, COMPOUND));
			
		public static final Collection<String> VALID_TYPE_CODES =
			Collections.unmodifiableCollection(Arrays.asList(SIMPLE.code(), PARAMETERIZED.code(), COMPOUND.code()));
			
		public String code(){
			return code;
		}
		@Override
		public String toString() {
			return code;
		}		
	}
}
