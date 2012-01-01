/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.api.repository.type;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;

/**
 * Concrete model object implementation of KRMS KrmsTypeAttribute. 
 * immutable. 
 * Instances of KrmsTypeAttribute can be (un)marshalled to and from XML.
 *
 */
@XmlRootElement(name = KrmsTypeAttribute.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = KrmsTypeAttribute.Constants.TYPE_NAME, propOrder = {
		KrmsTypeAttribute.Elements.ID,
		KrmsTypeAttribute.Elements.TYPE_ID,
		KrmsTypeAttribute.Elements.ATTR_DEFN_ID,
		KrmsTypeAttribute.Elements.SEQ_NO,
		KrmsTypeAttribute.Elements.ACTIVE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class KrmsTypeAttribute extends AbstractDataTransferObject implements KrmsTypeAttributeContract {
	private static final long serialVersionUID = -304265575559412478L;
	
	@XmlElement(name = Elements.ID, required = false)
	private String id;
	@XmlElement(name = Elements.TYPE_ID, required = true)
	private String typeId;
	@XmlElement(name = Elements.ATTR_DEFN_ID, required = true)
	private String attributeDefinitionId;
	@XmlElement(name = Elements.SEQ_NO, required = true)
	private Integer sequenceNumber;
	@XmlElement(name = Elements.ACTIVE, required = false)
	private boolean active;

	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private KrmsTypeAttribute() {
    	this.id = null;
    	this.typeId = null;
    	this.attributeDefinitionId = null;
    	this.sequenceNumber = null;
    	this.active = true;
    }
    
    /**
	 * Constructs a KRMS KrmsTypeAttribute from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the KrmsTypeAttribute
	 */
    private KrmsTypeAttribute(Builder builder) {
        this.id = builder.getId();
        this.typeId = builder.getTypeId();
        this.attributeDefinitionId = builder.getAttributeDefinitionId();
        this.sequenceNumber = builder.getSequenceNumber();
        this.active = builder.isActive();
    }
    
	public String getId() {
		return this.id;
	}
	
	public String getTypeId() {
		return this.typeId;
	}

	public String getAttributeDefinitionId() {
		return this.attributeDefinitionId;
	}

	public Integer getSequenceNumber() {
		return this.sequenceNumber;
	}
	
	public boolean isActive() {
		return this.active; 
	}

	/**
     * This builder is used to construct instances of KrmsTypeAttribute.  
     */
    public static class Builder implements KrmsTypeAttributeContract, ModelBuilder, Serializable {
		private static final long serialVersionUID = 2729964674427296346L;

		private String id;
        private String typeId;
        private String attributeDefinitionId;
        private Integer sequenceNumber;
        private boolean active;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String typeId, String attributeDefinitionId, Integer sequenceNumber) {
            setTypeId(typeId);
            setAttributeDefinitionId(attributeDefinitionId);
            setSequenceNumber(sequenceNumber);
			setActive(true);
        }

        /**
         * Creates a builder from the given parameters.
         * 
         * @param typeId the KrmsType Id
         * @param attributeDefinitionId The attributeDefinitionId
         * @param sequenceNumber 
         * @return an instance of the builder with the fields already populated
         * @throws IllegalArgumentException if the either the id, name or namespace is null or blank
         */
        public static Builder create(String typeId, String attributeDefinitionId, Integer sequenceNumber) {
            return new Builder(typeId, attributeDefinitionId, sequenceNumber);
        }
        
        public static Builder create(KrmsTypeAttributeContract contract){
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	Builder builder = new Builder(contract.getTypeId(),
        			contract.getAttributeDefinitionId(),
        			contract.getSequenceNumber());
            builder.setId(contract.getId());
        	return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set
		 */
        public void setId(String id) {
            this.id = id;
        }

		public void setTypeId(String typeId) {
            if (null != typeId && StringUtils.isBlank(typeId)) {
                throw new IllegalArgumentException("typeId must be null or non-blank");
            }
			this.typeId = typeId;
		}

		public void setAttributeDefinitionId(String attributeDefinitionId) {
            if (StringUtils.isBlank(attributeDefinitionId)) {
                throw new IllegalArgumentException("the attribute definition id is blank");
            }
			this.attributeDefinitionId = attributeDefinitionId;
		}
		
		public void setSequenceNumber(Integer sequenceNumber) {
			if (sequenceNumber == null){
				 throw new IllegalArgumentException("the sequence number is null");
			}
			this.sequenceNumber = sequenceNumber;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getId() {
			return id;
		}

		public String getTypeId() {
			return typeId;
		}

		public String getAttributeDefinitionId() {
			return attributeDefinitionId;
		}
		
		public Integer getSequenceNumber() {
			return sequenceNumber;
		}

		public boolean isActive() {
			return active;
		}

		/**
		 * Builds an instance of a KrmsTypeAttribute based on the current state of the builder.
		 * 
		 * @return the fully-constructed KrmsTypeAttribute
		 */
        @Override
        public KrmsTypeAttribute build() {
            return new KrmsTypeAttribute(this);
        }
		
    }

	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "krmsTypeAttribute";
		final static String TYPE_NAME = "KrmsTypeAttributeType";
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String TYPE_ID = "typeId";
		final static String ATTR_DEFN_ID = "attributeDefinitionId";
		public final static String SEQ_NO = "sequenceNumber";
		final static String ACTIVE = "active";
	}
}
