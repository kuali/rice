package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * abstract base model object for KRMS Attribute immutables. 
 *
 */
@XmlTransient
public abstract class BaseAttribute implements BaseAttributeContract, ModelObjectComplete{	
	private static final long serialVersionUID = -6126133049308968098L;
	
	@XmlElement(name = Elements.ID, required=true)
	private final String id;

	@XmlElement(name = Elements.ATTR_DEFN_ID, required=false)
	private final String attributeDefinitionId;

	@XmlElement(name = Elements.VALUE, required=false)
	private final String value;
	
	@XmlElement(name = Elements.ATTR_DEFN, required=false)
	private final KrmsAttributeDefinition attributeDefinition;
	
    @SuppressWarnings("unused")
	@XmlAnyElement
	private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	 /** 
     * This constructor should only be called by the private default constructor of subclasses,
     * which should only be used by JAXB and never invoked directly.
     */
    protected BaseAttribute() {
    	this.id = null;
    	this.attributeDefinitionId = null;
    	this.value = null;
    	this.attributeDefinition = null;
    }
    
    /**
	 * Constructs a BaseAttribute from the given builder.  
	 * This constructor is protected and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the BaseAttribute
	 */
    protected BaseAttribute(Builder builder) {
        this.id = builder.getId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.value = builder.getValue();
        if (builder.getAttributeDefinition() != null) {
        	this.attributeDefinition = builder.getAttributeDefinition().build();
        } else {
        	this.attributeDefinition = null;
        }
    }
    
	@Override
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getAttributeDefinitionId() {
		return this.attributeDefinitionId;
	}

	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public KrmsAttributeDefinition getAttributeDefinition() {
		return this.attributeDefinition;
	}
	
	/**
     * This builder is used to construct the fields that {@link BaseAttribute} is responsible for.  It is abstract,
     * and intended to be subclassed by extenders of {@link BaseAttribute}.
     */
    public abstract static class Builder implements BaseAttributeContract, ModelBuilder, Serializable {		
		private static final long serialVersionUID = 5799994031051731535L;

		private String id;
        private String attributeDefinitionId;
        private String value;
        private KrmsAttributeDefinition.Builder attributeDefinition;
        
		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        protected Builder(String id, String attributeDefinitionId, String value) {
            setId(id);
            setAttributeDefinitionId(attributeDefinitionId);
            setValue(value);
        }

        protected Builder(BaseAttributeContract attr) {
        	this (attr.getId(), attr.getAttributeDefinitionId(), attr.getValue());
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */
        public void setId(String id) {
            if (StringUtils.isBlank(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

		public void setAttributeDefinitionId(String attributeDefinitionId) {
            if (StringUtils.isBlank(attributeDefinitionId)) {
                throw new IllegalArgumentException("the attribute definition id is blank");
            }
			this.attributeDefinitionId = attributeDefinitionId;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		public void setAttributeDefinition(KrmsAttributeDefinition.Builder attributeDefinition) {
			this.attributeDefinition = attributeDefinition;
		}
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getAttributeDefinitionId() {
			return attributeDefinitionId;
		}
		
		@Override
		public String getValue() {
			return value;
		}

		@Override
		public KrmsAttributeDefinition.Builder getAttributeDefinition() {
			return attributeDefinition;
		}

    }
	
	/**
	 * A protected class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		public final static String ID = "id";
		public final static String ATTR_DEFN_ID = "attributeDefinitionId";
		public final static String VALUE = "value";
		public final static String ATTR_DEFN = "attributeDefinition";
	}
}
