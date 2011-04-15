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
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.springframework.util.CollectionUtils;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
// TODO: Where are the JAXB annotations?
public class TermDefinition implements TermDefinitionContract, ModelObjectComplete {
	
	// TODO: javadocs here are horribly incomplete
	
	private static final long serialVersionUID = 1L;
	
	private final String id;
	private final TermSpecificationDefinition specification;
	private final Set<TermParameterDefinition> parameters;
	
	/**
	 * This constructor is for JAXB only.  Do not invoke directly.
	 */
	private TermDefinition() {
		id = null;
		specification = null;
		parameters = null;
	}
	
	private TermDefinition(Builder builder) {
		id = builder.getId();
		specification = builder.getSpecification();
		parameters = builder.getParameters();
	}
	
	public static class Builder implements TermDefinitionContract, ModelBuilder, Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String id;
		private TermSpecificationDefinition specification;
		private Set<TermParameterDefinition> parameters;
		
		private Builder(String id, TermSpecificationDefinition termSpecificationDefinition, 
				Set<TermParameterDefinition> termParameters) {
			setId(id);
			setSpecification(termSpecificationDefinition);
			setParameters(termParameters);
		}

		public static Builder create(String id, TermSpecificationDefinition termSpecificationDefinition, 
				Set<TermParameterDefinition> termParameters) {
			return new Builder(id, termSpecificationDefinition, termParameters);
		}
		
		public static Builder create(TermDefinitionContract term) {
			// Convert TermParameterDefinitionContract to TermParameterDefinition:
			Set<? extends TermParameterDefinitionContract> paramContracts = term.getParameters();
			Set<TermParameterDefinition> outParams;
			if (CollectionUtils.isEmpty(paramContracts)) {
				outParams = Collections.emptySet();
			} else {
				outParams = new HashSet<TermParameterDefinition>(paramContracts.size());
				
				for (TermParameterDefinitionContract paramContract : paramContracts) {
					outParams.add(TermParameterDefinition.Builder.create(paramContract).build());
				}
			}
			return create(term.getId(), 
					// doing my TermSpecificationDefinitionContract conversion inline:
					TermSpecificationDefinition.Builder.create(term.getSpecification()).build(),
					// this is made immutable in the setter
					outParams 
					);
		}
		
		// Builder setters:
		
		// TODO: javadoc the validation rules
		
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			if (id != null && StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("id must contain non-whitespace chars");
			}
			this.id = id;
		}
		
		/**
		 * @param termSpecification the termSpecification to set
		 */
		public void setSpecification(TermSpecificationDefinition termSpecification) {
			if (termSpecification == null) {
				throw new IllegalArgumentException("termSpecification must not be null");
			}
			this.specification = termSpecification;
		}
		
		/**
		 * @param parameters the termParameters to set
		 */
		public void setParameters(Set<TermParameterDefinition> parameters) {
			if (CollectionUtils.isEmpty(parameters)) {
				this.parameters = Collections.emptySet();
			} else {
				this.parameters = Collections.unmodifiableSet(parameters);
			}
		}

		// Builder getters:
		
		/**
		 * @return the id
		 */
		@Override
		public String getId() {
			return this.id;
		}

		/**
		 * @return the termSpecification
		 */
		@Override
		public TermSpecificationDefinition getSpecification() {
			return this.specification;
		}
		
		

		/**
		 * @return the termParameters
		 */
		@Override
		public Set<TermParameterDefinition> getParameters() {
			return this.parameters;
		}
		
		/**
		 * This overridden method ...
		 * 
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 */
		@Override
		public TermDefinition build() {
			return new TermDefinition();
		}
	}
	
	/**
	 * @return the termId
	 */
	@Override
	public String getId() {
		return this.id;
	}
	/**
	 * @return the specification
	 */
	@Override
	public TermSpecificationDefinition getSpecification() {
		return this.specification;
	}
	/**
	 * @return the parameters
	 */
	@Override
	public Set<TermParameterDefinition> getParameters() {
		return this.parameters;
	}

	
}
