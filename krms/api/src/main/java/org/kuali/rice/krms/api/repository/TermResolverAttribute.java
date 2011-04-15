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
import org.kuali.rice.krms.api.repository.BuilderUtils.Transformer;

/**
 * Concrete model object implementation of {@link TermResolverAttribute}. 
 * immutable. 
 * Instances of {@link TermResolverAttribute} can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = TermResolverAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = TermResolverAttribute.Constants.TYPE_NAME, propOrder = {
		TermResolverAttribute.Elements.ID,
		TermResolverAttribute.Elements.TERM_RESOLVER_ID,
		TermResolverAttribute.Elements.ATTR_DEFN_ID,
		TermResolverAttribute.Elements.VALUE,
		TermResolverAttribute.Elements.ATTR_DEFN,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class TermResolverAttribute extends BaseAttribute implements TermResolverAttributeContract, ModelObjectComplete {	
	private static final long serialVersionUID = -6126133049308968098L;
	
	@XmlElement(name = Elements.TERM_RESOLVER_ID, required=true)
	private final String termResolverId;

	/** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private TermResolverAttribute() {
    	super();
    	this.termResolverId = null;
    }
    
    /**
	 * Constructs a TermResolverAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the {@link TermResolverAttribute}
	 */
    private TermResolverAttribute(Builder builder) {
    	super(builder);
        this.termResolverId = builder.getTermResolverId();
    }
    
	@Override
	public String getTermResolverId() {
		return this.termResolverId;
	}

	/**
     * This builder is used to construct instances of {@link TermResolverAttribute}.  
     */
    public static class Builder extends BaseAttribute.Builder implements TermResolverAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = 5799994031051731535L;

        private String termResolverId;

		/**
		 * {@link Transformer} to ease converting lists of contracts to Builders
		 */
		public static final Transformer<TermResolverAttributeContract, TermResolverAttribute.Builder>
		toBuilder = new Transformer<TermResolverAttributeContract, TermResolverAttribute.Builder>() {
			public org.kuali.rice.krms.api.repository.TermResolverAttribute.Builder transform(
					TermResolverAttributeContract input) {
				return TermResolverAttribute.Builder.create(input);
			}
		};
        
        /**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String termResolverId, String attributeDefinitionId, String value) {
        	super(id, attributeDefinitionId, value);
            setTermResolverId(termResolverId);
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
        public static Builder create(String id, String termResolverId, String attributeDefinitionId, String value) {
            return new Builder(id, termResolverId, attributeDefinitionId, value);
        }
        
        public static Builder create(TermResolverAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getId(), 
        			contract.getTermResolverId(),
        			contract.getAttributeDefinitionId(),
        			contract.getValue()).attributeDefinition(KrmsAttributeDefinition.Builder
        					.create(contract.getAttributeDefinition()));
        	return builder;
        }

		public void setTermResolverId(String termResolverId) {
            if (StringUtils.isBlank(termResolverId)) {
                throw new IllegalArgumentException("termResolverId is blank");
            }
			this.termResolverId = termResolverId;
		}
		
		@Override
		public String getTermResolverId() {
			return termResolverId;
		}

		/**
		 * Builds an instance of a ActionAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed ActionAttribute
		 */
        @Override
        public TermResolverAttribute build() {
            return new TermResolverAttribute(this);
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
		final static String ROOT_ELEMENT_NAME = "TermResolverAttribute";
		final static String TYPE_NAME = "TermResolverAttributeType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements extends BaseAttribute.Elements {
		final static String TERM_RESOLVER_ID = "termResolverId";
	}
}
