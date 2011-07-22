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
package org.kuali.rice.krms.api.repository.term;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.krms.api.repository.BuilderUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = TermResolverDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = TermResolverDefinition.Constants.TYPE_NAME, propOrder = {
		TermResolverDefinition.Elements.ID,
		TermResolverDefinition.Elements.NAME,
        TermResolverDefinition.Elements.NAMESPACE_CODE,
        TermResolverDefinition.Elements.CONTEXT_ID,
		TermResolverDefinition.Elements.TYPE_ID,
		TermResolverDefinition.Elements.OUTPUT,
		TermResolverDefinition.Elements.PREREQUISITES,
		TermResolverDefinition.Elements.ATTRIBUTES,
		TermResolverDefinition.Elements.PARAMETER_NAMES,
        CoreConstants.CommonElements.VERSION_NUMBER,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class TermResolverDefinition extends AbstractJaxbModelObject implements TermResolverDefinitionContract {
	
    private static final long serialVersionUID = 1L;
	
	@XmlElement(name = Elements.ID, required=false)
	private final String id;
	@XmlElement(name = Elements.NAMESPACE_CODE, required=true)
	private final String namespaceCode;
	@XmlElement(name = Elements.NAME, required=true)
	private final String name;
    @XmlElement(name = Elements.CONTEXT_ID, required=false)
    private final String contextId;
    @XmlElement(name = Elements.TYPE_ID, required=true)
    private final String typeId;
	@XmlElement(name = Elements.OUTPUT, required=false)
	private final TermSpecificationDefinition output;

	@XmlElement(name = "termSpecificationDefinition", required=false)
	@XmlElementWrapper(name = Elements.PREREQUISITES, required=false)
	private final Set<TermSpecificationDefinition> prerequisites;

	@XmlElement(name = Elements.ATTRIBUTES, required = false)
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	private final Map<String, String> attributes;

	@XmlElementWrapper(name = Elements.PARAMETER_NAMES, required=false)
	@XmlElement(name = "parameterName")
	private final Set<String> parameterNames;

	@XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;	
	
    @SuppressWarnings("unused")
	@XmlAnyElement
	private final Collection<org.w3c.dom.Element> _futureElements = null;
    
	/**
	 * This private constructor is for JAXB use only, don't invoke directly.
	 */
	private TermResolverDefinition() {
		id = null;
		namespaceCode = null;
		name = null;
		contextId = null;
		typeId = null;
		output = null;
		prerequisites = null;
		attributes = null;
		parameterNames = null;
        versionNumber = null;
	}
	
	private TermResolverDefinition(Builder builder) {
		this.id = builder.getId();
		this.namespaceCode = builder.getNamespaceCode();
		this.name = builder.getName();
		this.contextId = builder.getContextId();
		this.typeId = builder.getTypeId();
		this.output = builder.getOutput().build();
		this.prerequisites = BuilderUtils.convertFromBuilderSet(builder.getPrerequisites());
		this.parameterNames = Collections.unmodifiableSet(builder.getParameterNames());
		this.versionNumber = builder.getVersionNumber();
        if (builder.attributes != null){
        	this.attributes = Collections.unmodifiableMap(builder.getAttributes());
        } else {
        	this.attributes = null;
        }
	}
	
	public static class Builder implements TermResolverDefinitionContract, ModelBuilder, 
		Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String id;
		private String namespaceCode;
		private String name;
        private String contextId;
        private String typeId;
		private TermSpecificationDefinition.Builder output;
		private Set<TermSpecificationDefinition.Builder> prerequisites;
        private Map<String, String> attributes;
		private Set<String> parameterNames;
        private Long versionNumber;
		
		private Builder(String id,
				String namespaceCode,
				String name,
                String contextId,
                String typeId,
				TermSpecificationDefinition.Builder output,
				Set<TermSpecificationDefinition.Builder> prerequisites,
				Map<String, String> attributes,
				Set<String> parameterNames) {
			setId(id);
			setNamespaceCode(namespaceCode);
			setName(name);
			setContextId(contextId);
			setTypeId(typeId);
			setOutput(output);
			setPrerequisites(prerequisites);
			setAttributes(attributes);
			setParameterNames(parameterNames);
		}
		
		
		
		private Builder(TermResolverDefinitionContract termResolver) {
			setId(termResolver.getId());
			setNamespaceCode(termResolver.getNamespaceCode());
			setName(termResolver.getName());
			setContextId(termResolver.getContextId());
			setTypeId(termResolver.getTypeId());
			setOutput(TermSpecificationDefinition.Builder.create(termResolver.getOutput()));
			setPrerequisites(BuilderUtils.transform(termResolver.getPrerequisites(), TermSpecificationDefinition.Builder.toBuilder));
			setAttributes(termResolver.getAttributes());
			this.setParameterNames(termResolver.getParameterNames());
			this.setVersionNumber(termResolver.getVersionNumber());
		}
		
		public static Builder create(TermResolverDefinitionContract termResolver) {
			return new Builder(termResolver);
		}
		
		public static Builder create(String id,
				String namespaceCode,
				String name,
				String contextId,
				String typeId,
				TermSpecificationDefinition.Builder output,
				Set<TermSpecificationDefinition.Builder> prerequisites,
				Map<String, String> attributes,
				Set<String> parameterNames) {
			return new Builder(id, namespaceCode, name, contextId, typeId, output, prerequisites, attributes, parameterNames); 
		}

		// Builder setters:
		// TODO: proper validation & javadocs
		
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			if (id != null && StringUtils.isBlank(id)) {
				throw new IllegalArgumentException(/* TODO */);
			}
			this.id = id;
		}

		/**
		 * @param namespaceCode the namespaceCode to set
		 */
		public void setNamespaceCode(String namespaceCode) {
			if (StringUtils.isBlank(namespaceCode)) {
				throw new IllegalArgumentException(/* TODO */);
			}
			this.namespaceCode = namespaceCode;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException(/* TODO */);
			}
			this.name = name;
		}

        /**
         * @param contextId the contextId to set
         */
        public void setContextId(String contextId) {
            this.contextId = contextId;
        }
		
		/**
		 * @param typeId the typeId to set
		 */
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
				throw new IllegalArgumentException(/* TODO */);
			}
			this.typeId = typeId;
		}

		/**
		 * @param output the output to set
		 */
		public void setOutput(TermSpecificationDefinition.Builder output) {
			if (output == null) {
				throw new IllegalArgumentException(/* TODO */);
			}
			this.output = output;
		}

		/**
		 * @param prerequisites the prerequisites to set
		 */
		public void setPrerequisites(
				Set<TermSpecificationDefinition.Builder> prerequisites) {
			this.prerequisites = prerequisites;
		}

		/**
		 * @param attributes the attributes to set
		 */
		public void setAttributes(Map<String, String> attributes){
			if (attributes == null){
				this.attributes = Collections.emptyMap();
			} else {
				this.attributes = Collections.unmodifiableMap(attributes);
			}
		}

		/**
		 * @param parameterNames the parameterNames to set
		 */
		public void setParameterNames(Set<String> parameterNames) {
			this.parameterNames = parameterNames;
		}		
		
		/**
		 * @param versionNumber the versionNumber to set.  May be null.
		 */
        public void setVersionNumber(Long versionNumber){
            this.versionNumber = versionNumber;
        }
        
		// Builder getters:

		/**
		 * @return the id
		 */
		public String getId() {
			return this.id;
		}
		
		/**
		 * @return the namespaceCode
		 */
		public String getNamespaceCode() {
			return this.namespaceCode;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}
        /**
         * @return the contextId
         */
        public String getContextId() {
            return this.contextId;
        }
		/**
		 * @return the typeId
		 */
		public String getTypeId() {
			return this.typeId;
		}
		/**
		 * @return the output
		 */
		public TermSpecificationDefinition.Builder getOutput() {
			return this.output;
		}
		/**
		 * @return the prerequisites
		 */
		public Set<TermSpecificationDefinition.Builder> getPrerequisites() {
			return this.prerequisites;
		}
		/**
		 * @return the attributes
		 */
		public Map<String, String> getAttributes() {
			return this.attributes;
		}
		/**
		 * @return the parameterNames
		 */
		public Set<String> getParameterNames() {
			return (parameterNames == null) ? Collections.<String>emptySet() : parameterNames;
		}
		
		/**
		 * @return the version number
		 */
        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

		/**
		 * This overridden method ...
		 * 
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 */
		@Override
		public TermResolverDefinition build() {
			return new TermResolverDefinition(this);
		}
		
	}
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}
	
	/**
	 * @return the namespaceCode
	 */
	@Override
	public String getNamespaceCode() {
		return this.namespaceCode;
	}
	
	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
    /**
     * @return the contextId
     */
    public String getContextId() {
        return this.contextId;
    }

	/**
	 * @return the typeId
	 */
	@Override
	public String getTypeId() {
		return this.typeId;
	}
	
	/**
	 * @return the specification
	 */
	@Override
	public TermSpecificationDefinition getOutput() {
		return this.output;
	}
	
	/**
	 * @return the prerequisites
	 */
	@Override
	public Set<TermSpecificationDefinition> getPrerequisites() {
		return this.prerequisites;
	}
	/**
	 * @return the attributes
	 */
	@Override
	public Map<String, String> getAttributes() {
		return this.attributes;
	}

	/**
	 * @return the parameterNames
	 */
	@Override
	public Set<String> getParameterNames() {
		return this.parameterNames;
	}
	
	/**
	 * @see org.kuali.rice.core.api.mo.common.Versioned#getVersionNumber()
	 */
    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "termResolverDefinition";
		final static String TYPE_NAME = "termResolverDefinitionType";
	}
	
	static class Elements {
		public static final String ID = "id";
		public static final String NAMESPACE_CODE = "namespaceCode";
		public static final String NAME = "name";
        public static final String CONTEXT_ID = "contextId";
        public static final String TYPE_ID = "typeId";
		public static final String OUTPUT = "output";
		public static final String PREREQUISITES = "prerequisites";
		public static final String ATTRIBUTES = "attributes";
		public static final String PARAMETER_NAMES = "parameterNames";
	}
}