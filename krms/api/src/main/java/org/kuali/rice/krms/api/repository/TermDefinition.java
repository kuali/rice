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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	private final String termId;
	private final TermSpecificationDefinition specification;
	private final List<TermParameterDefinition> parameters;
	
	/**
	 * This constructor is for JAXB only.  Do not invoke directly.
	 */
	private TermDefinition() {
		termId = null;
		specification = null;
		parameters = null;
	}
	
	private TermDefinition(Builder builder) {
		termId = builder.getTermId();
		specification = builder.getSpecification();
		parameters = builder.getParameters();
	}
	
	public static class Builder implements TermDefinitionContract, ModelBuilder, Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String termId;
		private TermSpecificationDefinition specification;
		private List<TermParameterDefinition> parameters;
		
		private Builder(String termId, TermSpecificationDefinition termSpecificationDefinition, 
				List<TermParameterDefinition> termParameters) {
			setTermId(termId);
			setSpecification(termSpecificationDefinition);
			setParameters(termParameters);
		}

		public static Builder create(String termId, TermSpecificationDefinition termSpecificationDefinition, 
				List<TermParameterDefinition> termParameters) {
			return new Builder(termId, termSpecificationDefinition, termParameters);
		}
		
		public static Builder create(TermDefinitionContract term) {
			// Convert TermParameterDefinitionContract to TermParameterDefinition:
			List<? extends TermParameterDefinitionContract> paramContracts = term.getParameters();
			List<TermParameterDefinition> outParams;
			if (CollectionUtils.isEmpty(paramContracts)) {
				outParams = Collections.emptyList();
			} else {
				outParams = new ArrayList<TermParameterDefinition>(paramContracts.size());
				
				for (TermParameterDefinitionContract paramContract : paramContracts) {
					outParams.add(TermParameterDefinition.Builder.create(paramContract).build());
				}
			}
			return create(term.getTermId(), 
					// doing my TermSpecificationDefinitionContract conversion inline:
					TermSpecificationDefinition.Builder.create(term.getSpecification()).build(),
					// this is made immutable in the setter
					outParams 
					);
		}
		
		// Builder setters:
		
		// TODO: javadoc the validation rules
		
		/**
		 * @param termId the termId to set
		 */
		public void setTermId(String termId) {
			if (termId != null && StringUtils.isBlank(termId)) {
				throw new IllegalArgumentException("termId must contain non-whitespace chars");
			}
			this.termId = termId;
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
		public void setParameters(List<TermParameterDefinition> parameters) {
			if (CollectionUtils.isEmpty(parameters)) {
				this.parameters = Collections.emptyList();
			} else {
				this.parameters = Collections.unmodifiableList(parameters);
			}
		}

		// Builder getters:
		
		/**
		 * @return the termId
		 */
		@Override
		public String getTermId() {
			return this.termId;
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
		public List<TermParameterDefinition> getParameters() {
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
	public String getTermId() {
		return this.termId;
	}
	/**
	 * @return the specification
	 */
	public TermSpecificationDefinition getSpecification() {
		return this.specification;
	}
	/**
	 * @return the parameters
	 */
	public List<TermParameterDefinition> getParameters() {
		return this.parameters;
	}

	
}
