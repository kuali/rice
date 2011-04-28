/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.api.permission;

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
 * An immutable representation of a {@link PermissionContract}.
 *
 * <p>To construct an instance of a KimPermission, use the {@link Permission.Builder} class.<p/>
 *
 * @see PermissionContract
 */
@XmlRootElement(name = Permission.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Permission.Constants.TYPE_NAME, propOrder = {
		Permission.Elements.ID,
		Permission.Elements.NAMESPACE_CODE,
		Permission.Elements.NAME,
		Permission.Elements.DESCRIPTION,
		Permission.Elements.TEMPLATE_ID,
        Permission.Elements.ACTIVE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.OBJECT_ID,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Permission implements PermissionContract, ModelObjectComplete{

	private static final long serialVersionUID = 1L;
	
    @XmlElement(name = Permission.Elements.ID, required = true)
    private final String id;

    @XmlElement(name = Permission.Elements.NAMESPACE_CODE, required = true)
    private final String namespaceCode;

    @XmlElement(name = Permission.Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Permission.Elements.DESCRIPTION, required = false)
    private final String description;

    @XmlElement(name = Permission.Elements.TEMPLATE_ID, required = true)
    private final String templateId;
    
    @XmlElement(name = Permission.Elements.ACTIVE, required = false)
    private final boolean active;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
    
    /**
	 *  A constructor to be used only by JAXB unmarshalling.
	 *  
	 */
	private Permission() {
		this.id = null;
        this.namespaceCode = null;
        this.name = null;
        this.description = null;
        this.templateId = null;
        this.active = false;
        this.versionNumber = Long.valueOf(1L);
        this.objectId = null;
	}
	
    /**
	 * A constructor using the Builder.
	 * 
	 * @param builder
	 */
	private Permission(Builder builder) {
		this.id = builder.getId();
        this.namespaceCode = builder.getNamespaceCode();
        this.name = builder.getName();
        this.description = builder.getDescription();
        this.templateId = builder.getTemplateId();        
        this.active = builder.isActive();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#getNamespaceCode()
	 */
	@Override
	public String getNamespaceCode() {
		return namespaceCode;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#getTemplateId()
	 */
	@Override
	public String getTemplateId() {
		return templateId;
	}
	
	/**
	 * @see org.kuali.rice.kim.api.permission.PermissionContract#isActive()
	 */
	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @see org.kuali.rice.core.api.mo.Versioned#getVersionNumber()
	 */
	@Override
	public Long getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @see org.kuali.rice.core.api.mo.GloballyUnique#getObjectId()
	 */
	@Override
	public String getObjectId() {
		return objectId;
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
     * This builder constructs a KimPermission enforcing the constraints of the {@link PermissionContract}.
     */
    public static final class Builder implements PermissionContract, ModelBuilder, Serializable {
        private String id;
        private String namespaceCode;
        private String name;
        private String description;
        private String templateId;
        private Long versionNumber = 1L;
        private String objectId;
        private boolean active;
        
        private Builder(String id, String namespaceCode, String name, String templateId) {
            setId(id);
            setNamespaceCode(namespaceCode);
            setName(name);
            setTemplateId(templateId);
        }

        /**
         * Creates a KimPermission with the required fields.
         */
        public static Builder create(String id, String namespaceCode, String name, String templateId) {
            return new Builder(id, namespaceCode, name, templateId);
        }

        /**
         * Creates a KimPermission from an existing {@link PermissionContract}.
         */
        public static Builder create(PermissionContract contract) {
            Builder builder = new Builder(contract.getId(), contract.getNamespaceCode(), contract.getName(), contract.getTemplateId());
            builder.setDescription(contract.getDescription());
            
            builder.setActive(contract.isActive());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());

            return builder;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(final String id) {
        	if (StringUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id is blank");
            }
        	this.id = id;
        }
        
        @Override
        public String getNamespaceCode() {
            return namespaceCode;
        }

        public void setNamespaceCode(final String namespaceCode) {
        	if (StringUtils.isEmpty(namespaceCode)) {
                throw new IllegalArgumentException("namespaceCode is blank");
            }
        	this.namespaceCode = namespaceCode;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(final String name) {
        	if (StringUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name is blank");
            }
        	this.name = name;
        }

		@Override
		public String getDescription() {
			return description;
		}
		
		public void setDescription(final String description) {
			this.description = description;
		}

		@Override
		public String getTemplateId() {
			if (StringUtils.isEmpty(templateId)) {
                throw new IllegalArgumentException("templateId is blank");
            }
			return templateId;
		}
		
		public void setTemplateId(final String templateId) {
			this.templateId = templateId;
		}
		
		@Override
		public boolean isActive() {
			return active;
		}
		
		public void setActive(final boolean active) {
            this.active = active;
        }

		@Override
		public Long getVersionNumber() {
			return versionNumber;
		}

		public void setVersionNumber(final Long versionNumber) {
			if (versionNumber == null || versionNumber <= 0) {
	            throw new IllegalArgumentException("versionNumber is invalid");
	        }
			this.versionNumber = versionNumber;
	    }
		 
		@Override
		public String getObjectId() {
			return objectId;
		}

        public void setObjectId(final String objectId) {
            this.objectId = objectId;
        }
        
        @Override
        public Permission build() {
            return new Permission(this);
        }
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        static final String ROOT_ELEMENT_NAME = "permission";
        static final String TYPE_NAME = "PermissionType";
        static final String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        static final String ID = "id";
        static final String NAMESPACE_CODE = "namespaceCode";
        static final String NAME = "name";
        static final String DESCRIPTION = "description";
        static final String TEMPLATE_ID = "templateId";        
        static final String ACTIVE = "active";
    }
}
