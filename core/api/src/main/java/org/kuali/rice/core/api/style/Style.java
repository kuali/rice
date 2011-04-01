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

@XmlRootElement(name = Style.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Style.Constants.TYPE_NAME, propOrder = {
		Style.Elements.STYLE_ID,
		Style.Elements.NAME,
		Style.Elements.XML_CONTENT,
		Style.Elements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
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
    
	@XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = true)
    private final Long versionNumber;
	
	@XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
	private final String objectId;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
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
        
        public static Builder create(String name) {
        	return new Builder(name);
        }
        
        public static Builder create(StyleContract contract) {
        	Builder builder = create(contract.getName());
        	builder.setStyleId(contract.getStyleId());
        	builder.setXmlContent(contract.getXmlContent());
        	builder.setActive(contract.isActive());
        	builder.setVersionNumber(contract.getVersionNumber());
        	builder.setObjectId(contract.getObjectId());
        	return builder;
        }
        
        public Style build() {
        	return new Style(this);
        }

        @Override
		public Long getStyleId() {
			return this.styleId;
		}

		public void setStyleId(Long styleId) {
			this.styleId = styleId;
		}

		@Override
		public String getName() {
			return this.name;
		}

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

		public void setXmlContent(String xmlContent) {
			this.xmlContent = xmlContent;
		}

		@Override
		public boolean isActive() {
			return this.active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public Long getVersionNumber() {
			return this.versionNumber;
		}

		public void setVersionNumber(Long versionNumber) {
			this.versionNumber = versionNumber;
		}

		@Override
		public String getObjectId() {
			return objectId;
		}
		
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
