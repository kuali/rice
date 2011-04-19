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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import org.springframework.util.CollectionUtils;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = TermDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = TermDefinition.Constants.TYPE_NAME, propOrder = {
		TermDefinition.Elements.ID,
		TermDefinition.Elements.SPECIFICATION,
		TermDefinition.Elements.PARAMETERS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class TermDefinition implements TermDefinitionContract, ModelObjectComplete {
	
	// TODO: javadocs here are horribly incomplete
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = Elements.ID, required=false)
	private final String id;
	@XmlElement(name = Elements.SPECIFICATION, required=true)
	private final TermSpecificationDefinition specification;
	@XmlElement(name = Elements.PARAMETERS, required=false)
	private final Set<TermParameterDefinition> parameters;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
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
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return EqualsBuilder.reflectionEquals(this, obj, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class Constants {
		public static final String ROOT_ELEMENT_NAME = "TermDefinition";
		public static final String TYPE_NAME = "TermDefinitionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}

	public static class Elements {
		public static final String ID = "id";
		public static final String SPECIFICATION = "specification";
		public static final String PARAMETERS = "parameters";
	}
	
	
}
