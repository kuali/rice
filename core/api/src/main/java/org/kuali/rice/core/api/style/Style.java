/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.core.api.style;

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
import org.w3c.dom.Element;

/**
 * An immutable representation of a Style.  A style is essentially a block of
 * XML containing and XSL stylesheet. These can be used in various places for
 * the transformation of XML data from one form to another.
 * 
 * @author Eric Westfall
 *
 */
@XmlRootElement(name = Style.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Style.Constants.TYPE_NAME, propOrder = {
		Style.Elements.STYLE_ID,
		Style.Elements.NAME,
		Style.Elements.XML_CONTENT,
		Style.Elements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Style implements StyleContract, ModelObjectComplete {

	private static final long serialVersionUID = -26426318682076660L;
	
	@XmlElement(name = Elements.STYLE_ID, required = false)
	private final Long styleId;
	
	@XmlElement(name = Elements.NAME, required = true)
    private final String name;
	
	@XmlElement(name = Elements.XML_CONTENT, required = false)
    private final String xmlContent;
	
	@XmlElement(name = Elements.ACTIVE, required = true)
    private final boolean active;
    
	@XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
	
	@XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
	private final String objectId;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
    /**
     * Private constructor used only by JAXB.
     */
    private Style() {
    	this.styleId = null;
    	this.name = null;
    	this.xmlContent = null;
    	this.active = false;
    	this.versionNumber = null;
    	this.objectId = null;
    }
    
    private Style(Builder builder) {
    	this.styleId = builder.getStyleId();
    	this.name = builder.getName();
    	this.xmlContent = builder.getXmlContent();
    	this.active = builder.isActive();
    	this.versionNumber = builder.getVersionNumber();
    	this.objectId = builder.getObjectId();
    }

    @Override
    public Long getStyleId() {
		return this.styleId;
	}

    @Override
    public String getName() {
		return this.name;
	}

    @Override
    public String getXmlContent() {
		return this.xmlContent;
	}
	
    @Override
    public boolean isActive() {
		return this.active;
	}
        
	@Override
	public Long getVersionNumber() {
		return this.versionNumber;
	}
	
	@Override
	public String getObjectId() {
		return this.objectId;
	}

	/**
	 * A builder which can be used to construct Style instances.  Enforces the
	 * constraints of the {@link StyleContract}.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	public static final class Builder implements StyleContract, ModelBuilder, Serializable  {
    	
    	private static final long serialVersionUID = -219369603932108436L;
    	
		private Long styleId;
        private String name;
        private String xmlContent;
        private boolean active;
        private Long versionNumber;
        private String objectId;
        
        private Builder(String name) {
        	setName(name);
        	setActive(true);
        }
        
        /**
         * Creates a style builder with the given required values.  This class
         * is the only means by which a {@link Style} object can be created.
         * 
         * <p>Will default the active flag to true.
         * 
         * @param name the name of the style to create, must not be null or blank
         * 
         * @return a builder with the required values already initialized
         * 
         * @throws IllegalArgumentException if the given name is null or blank
         */
        public static Builder create(String name) {
        	return new Builder(name);
        }
        
        /**
         * Creates a populates a builder with the data on the given StyleContract.
         * This is similar in nature to a "copy constructor" for Style.
         * 
         * @param contract an object implementing the StyleContract from which
         * to copy property values
         *  
         * @return a builder with the values from the contract already initialized
         */
        public static Builder create(StyleContract contract) {
        	Builder builder = create(contract.getName());
        	builder.setStyleId(contract.getStyleId());
        	builder.setXmlContent(contract.getXmlContent());
        	builder.setActive(contract.isActive());
        	builder.setVersionNumber(contract.getVersionNumber());
        	builder.setObjectId(contract.getObjectId());
        	return builder;
        }
        
        @Override
        public Style build() {
        	return new Style(this);
        }

        @Override
		public Long getStyleId() {
			return this.styleId;
		}

        /**
         * Sets the styleId for the style that will be returned by this builder.
         * 
         * @param styleId the styleId to set
         */
		public void setStyleId(Long styleId) {
			this.styleId = styleId;
		}

		@Override
		public String getName() {
			return this.name;
		}

		/**
         * Sets the name for the style that will be returned by this builder.
         * The name must not be blank or null.
         * 
         * @param name the name to set on this builder, must not be null or
         * blank
         * 
         * @throws IllegalArgumentException if the given name is null or blank
         */
		public void setName(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("name is blank");
			}
			this.name = name;
		}

		@Override
		public String getXmlContent() {
			return this.xmlContent;
		}

		/**
		 * Sets the XML content for the style that will be returned by this
		 * builder.
		 * 
		 * @param xmlContent the xmlContent to set on this builder
		 */
		public void setXmlContent(String xmlContent) {
			this.xmlContent = xmlContent;
		}

		@Override
		public boolean isActive() {
			return this.active;
		}

		/**
         * Sets the active flag for the style that will be returned by this
         * builder.
         * 
         * @param active the active flag to set
         */
		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public Long getVersionNumber() {
			return this.versionNumber;
		}

		/**
         * Sets the version number for the style that will be returned by this
         * builder.
         * 
         * <p>In general, this value should not be manually set on the builder,
         * but rather copied from an existing {@link StyleContract} when
         * invoking {@link Builder#create(StyleContract)}.
         * 
         * @param versionNumber the version number to set
         */
		public void setVersionNumber(Long versionNumber) {
			this.versionNumber = versionNumber;
		}

		@Override
		public String getObjectId() {
			return objectId;
		}
		
		/**
         * Sets the globally unique object ID for the style that will be
         * returned by this builder.
         * 
         * <p>In general, this value should not be manually set on the builder,
         * but rather copied from an existing {@link StyleContract} when
         * invoking {@link Builder#create(StyleContract)}.
         * 
         * @param objectId the object ID to set
         */
		public void setObjectId(String objectId) {
			this.objectId = objectId;
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
        final static String ROOT_ELEMENT_NAME = "style";
        final static String TYPE_NAME = "StyleType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String STYLE_ID = "styleId";
        final static String NAME = "name";
        final static String XML_CONTENT = "xmlContent";
        final static String ACTIVE = "active";
    }

}
