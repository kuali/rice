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
package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class TermResolverDefinition implements TermResolverDefinitionContract, ModelObjectComplete {
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final String namespaceCode;
	private final String name;
	private final String typeId;
	private final TermSpecificationDefinition output;
	private final List<TermSpecificationDefinition> prerequisites;
	private final List<TermResolverAttribute> attributes;
	private final List<String> parameterNames;
	
	/**
	 * This private constructor is for JAXB use only, don't invoke directly.
	 */
	private TermResolverDefinition() {
		id = null;
		namespaceCode = null;
		name = null;
		typeId = null;
		output = null;
		prerequisites = null;
		attributes = null;
		parameterNames = null;
	}
	
	private TermResolverDefinition(Builder builder) {
		this.id = builder.getId();
		this.namespaceCode = builder.getNamespaceCode();
		this.name = builder.getName();
		this.typeId = builder.getTypeId();
		this.output = builder.getOutput().build();
		this.prerequisites = BuilderUtils.convertFromBuilderList(builder.getPrerequisites());
		this.attributes = BuilderUtils.convertFromBuilderList(builder.getAttributes());
		this.parameterNames = Collections.unmodifiableList(builder.getParameterNames());
	}
	
	public static class Builder implements TermResolverDefinitionContract, ModelBuilder, 
		Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String id;
		private String namespaceCode;
		private String name;
		private String typeId;
		private TermSpecificationDefinition.Builder output;
		private List<TermSpecificationDefinition.Builder> prerequisites;
		private List<TermResolverAttribute.Builder> attributes;
		private List<String> parameterNames;
		
		public static final Builder dummy = new Builder(); 
		
		private Builder() {
		}
		
		private Builder(String id,
				String namespaceCode,
				String name,
				String typeId,
				TermSpecificationDefinition.Builder output,
				List<TermSpecificationDefinition.Builder> prerequisites,
				List<TermResolverAttribute.Builder> attributes,
				List<String> parameterNames) {
			setNamespaceCode(namespaceCode);
			setName(name);
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
			setTypeId(termResolver.getTypeId());
			setOutput(TermSpecificationDefinition.Builder.create(termResolver.getOutput()));
			setPrerequisites(BuilderUtils.transform(termResolver.getPrerequisites(), TermSpecificationDefinition.Builder.toBuilder));
			setAttributes(BuilderUtils.transform(termResolver.getAttributes(), TermResolverAttribute.Builder.toBuilder));
			this.setParameterNames(termResolver.getParameterNames());
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
				List<TermSpecificationDefinition.Builder> prerequisites) {
			this.prerequisites = prerequisites;
		}

		/**
		 * @param attributes the attributes to set
		 */
		public void setAttributes(List<TermResolverAttribute.Builder> attributes) {
			this.attributes = attributes;
		}

		/**
		 * @param parameterNames the parameterNames to set
		 */
		public void setParameterNames(List<String> parameterNames) {
			this.parameterNames = parameterNames;
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
		public List<TermSpecificationDefinition.Builder> getPrerequisites() {
			return this.prerequisites;
		}
		/**
		 * @return the attributes
		 */
		public List<TermResolverAttribute.Builder> getAttributes() {
			return this.attributes;
		}
		/**
		 * @return the parameterNames
		 */
		public List<String> getParameterNames() {
			return this.parameterNames;
		}
		
		/**
		 * This overridden method ...
		 * 
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 */
		@Override
		public TermResolverDefinition build() {
			return new TermResolverDefinition();
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
	public List<TermSpecificationDefinition> getPrerequisites() {
		return this.prerequisites;
	}
	/**
	 * @return the attributes
	 */
	@Override
	public List<TermResolverAttribute> getAttributes() {
		return this.attributes;
	}

	/**
	 * @return the parameterNames
	 */
	@Override
	public List<String> getParameterNames() {
		return this.parameterNames;
	}
	
	
}
