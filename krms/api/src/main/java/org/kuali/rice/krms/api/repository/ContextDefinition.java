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
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.krms.api.Context;
import org.w3c.dom.Element;

/**
 * An immutable representation of a context definition.  A context definition
 * defines information about a context which can be loaded into the rules
 * engine for evaluation.
 * 
 * A context definition includes a list of agendas which are valid within the
 * context.  Typically, during rule engine execution, one or more of these
 * agendas is selected for execution based on a given set of selection criteria.
 *  
 * @see Context 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = ContextDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ContextDefinition.Constants.TYPE_NAME, propOrder = {
		ContextDefinition.Elements.CONTEXT_DEFINITION_ID,
		ContextDefinition.Elements.NAMESPACE_CODE,
		ContextDefinition.Elements.NAME,
		ContextDefinition.Elements.TYPE_ID,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ContextDefinition implements ContextDefinitionContract, ModelObjectComplete {
	
	private static final long serialVersionUID = -6639428234851623868L;

	@XmlElement(name = Elements.CONTEXT_DEFINITION_ID, required = false)
	private final String contextDefinitionId;
	
	@XmlElement(name = Elements.NAME, required = true)
    private final String name;
	
	@XmlElement(name = Elements.NAMESPACE_CODE, required = true)
    private final String namespaceCode;
	
	@XmlElement(name = Elements.TYPE_ID, required = false)
    private final String typeId;
	    
	@XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
	
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Used only by JAXB.
     */
    private ContextDefinition() {
    	this.contextDefinitionId = null;
    	this.name = null;
    	this.namespaceCode = null;
    	this.typeId = null;
    	this.versionNumber = null;
    }
    
    private ContextDefinition(Builder builder) {
    	this.contextDefinitionId = builder.getContextDefinitionId();
    	this.name = builder.getName();
    	this.namespaceCode = builder.getNamespaceCode();
    	this.typeId = builder.getTypeId();
    	this.versionNumber = builder.getVersionNumber();
    }
    
    @Override
	public String getContextDefinitionId() {
		return contextDefinitionId;
	}

	@Override
	public String getNamespaceCode() {
		return namespaceCode;
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
    	
		private String contextDefinitionId;
		private String namespaceCode;
        private String name;
        private String typeId;
        private Long versionNumber;
        
        private Builder(String namespaceCode, String name) {
        	setNamespaceCode(namespaceCode);
        	setName(name);
        }
        
        /**
         * Creates a context definition builder with the given required values
         * 
         * @param namespaceCode the namespace code of the context definition to create, must not be null or blank
         * @param name the name of the context definition to create, must not be null or blank
         * 
         * @return a builder with the required values already initialized
         * 
         * @throws IllegalArgumentException if the given namespaceCode is null or blank
         * @throws IllegalArgumentException if the given name is null or blank
         */
        public static Builder create(String namespaceCode, String name) {
        	return new Builder(namespaceCode, name);
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
        	Builder builder = create(contract.getNamespaceCode(), contract.getName());
        	builder.setContextDefinitionId(contract.getContextDefinitionId());
        	builder.setTypeId(contract.getTypeId());
        	builder.setVersionNumber(contract.getVersionNumber());
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
		public String getContextDefinitionId() {
			return this.contextDefinitionId;
		}

		@Override
		public String getNamespaceCode() {
			return this.namespaceCode;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String getTypeId() {
			return this.typeId;
		}

		/**
         * Sets the contextDefinitionId for the context definition that will be created by this builder.
         * 
         * @param contextDefinitionId the contextDefinitionId to set
         */
		public void setContextDefinitionId(String contextDefinitionId) {
			this.contextDefinitionId = contextDefinitionId;
		}

		/**
		 * Sets the namespace code for the context definition that will be created
		 * by this builder.  The namespace code must not be blank or null.
		 * 
		 * @param namespaceCode the namespaceCode to set on this builder, must not be
		 * null or blank
		 * 
		 * @throws IllegalArgumentException if the given namespace code is null or blank
		 */
		public void setNamespaceCode(String namespaceCode) {
			if (StringUtils.isBlank(namespaceCode)) {
				throw new IllegalArgumentException("namespaceCode is blank");
			}
			this.namespaceCode = namespaceCode;
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
			this.typeId = typeId;
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
        final static String ROOT_ELEMENT_NAME = "contextDefinition";
        final static String TYPE_NAME = "ContextDefinitionType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String CONTEXT_DEFINITION_ID = "contextDefinitionId";
        final static String NAMESPACE_CODE = "namespaceCode";
        final static String NAME = "name";
        final static String TYPE_ID = "typeId";
    }
		
	
	
}
