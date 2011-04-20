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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jdom.IllegalAddException;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.krms.api.repository.BuilderUtils;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition.Builder;
import org.springframework.util.CollectionUtils;

/**
 * Immutable DTO for Terms.  Construction must be done via the {@link Builder} inner class.
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
public final class TermDefinition implements TermDefinitionContract, ModelObjectComplete {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = Elements.ID, required=false)
	private final String id;
	@XmlElement(name = Elements.SPECIFICATION, required=true)
	private final TermSpecificationDefinition specification;
	@XmlElementWrapper(name = Elements.PARAMETERS, required=false)
	@XmlElement(name = "parameter", required=false)
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
		specification = builder.getSpecification().build();
		parameters = BuilderUtils.convertFromBuilderSet(builder.getParameters());
	}
	
	/**
	 * {@link ModelBuilder} for {@link TermDefinition}s.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	public static class Builder implements TermDefinitionContract, ModelBuilder, Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String id;
		private TermSpecificationDefinition.Builder specification;
		private Set<TermParameterDefinition.Builder> parameters;
		
		private Builder(String id, TermSpecificationDefinition.Builder termSpecificationDefinition, 
				Set<TermParameterDefinition.Builder> termParameters) {
			setId(id);
			setSpecification(termSpecificationDefinition);
			setParameters(termParameters);
		}

		/**
		 * static factory for creating a {@link Builder}.
		 * 
		 * @param id may be null.
		 * @param termSpecificationDefinition must not be null.
		 * @param termParameters may be null.
		 */
		public static Builder create(String id, TermSpecificationDefinition.Builder termSpecificationDefinition, 
				Set<TermParameterDefinition.Builder> termParameters) {
			return new Builder(id, termSpecificationDefinition, termParameters);
		}
		
		/**
		 * static factory for creating a {@link Builder} from a {@link TermDefinitionContract}.
		 * 
		 * @param term must be non-null.
		 */
		public static Builder create(TermDefinitionContract term) {
			if (term == null) throw new IllegalAddException("term may not be null");
			
			// Convert TermParameterDefinitionContract to TermParameterDefinition:
			Set<TermParameterDefinition.Builder> outParams = 
				BuilderUtils.transform(term.getParameters(), TermParameterDefinition.Builder.toBuilder);

			return create(term.getId(), 
					// doing my TermSpecificationDefinitionContract conversion inline:
					TermSpecificationDefinition.Builder.create(term.getSpecification()),
					// this is made immutable in the setter
					outParams 
					);
		}
		
		// Builder setters:
		
		/**
		 * @param id the id to set.  Should be null to build {@link TermDefinition}s for creation operations.
		 * @throws IllegalArgumentException if the id is non-null and only contains whitespace
		 */
		public void setId(String id) {
			if (id != null && StringUtils.isBlank(id)) {
				throw new IllegalArgumentException("id must contain non-whitespace chars");
			}
			this.id = id;
		}
		
		/**
		 * @param termSpecification the termSpecification to set
		 * @throws IllegalArgumentException if termSpecification is null
		 */
		public void setSpecification(TermSpecificationDefinition.Builder termSpecification) {
			if (termSpecification == null) {
				throw new IllegalArgumentException("termSpecification must not be null");
			}
			this.specification = termSpecification;
		}
		
		/**
		 * @param parameters the termParameters to set.  May be null, or empty.
		 */
		public void setParameters(Set<TermParameterDefinition.Builder> parameters) {
			this.parameters = parameters;
		}

		// Builder getters:
		
		/**
		 * @return the id
		 */
		@Override
		public String getId() {
			return id;
		}

		/**
		 * @return the termSpecification
		 */
		@Override
		public TermSpecificationDefinition.Builder getSpecification() {
			return specification;
		}
		
		

		/**
		 * @return the termParameters
		 */
		@Override
		public Set<TermParameterDefinition.Builder> getParameters() {
			return parameters;
		}
		
		/**
		 * Builds the {@link TermDefinition}, or dies trying.
		 * 
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 * @throws IllegalStateException if builder validation fails
		 */
		@Override
		public TermDefinition build() {
			return new TermDefinition(this);
		}
	}
	
	/**
	 * @return the termId.  May be null if this {@link TermDefinition} hasn't been persisted.
	 */
	@Override
	public String getId() {
		return this.id;
	}
	/**
	 * @return the specification.  Will never be null.
	 */
	@Override
	public TermSpecificationDefinition getSpecification() {
		return this.specification;
	}
	/**
	 * @return the parameters.  May be empty, but will never be null.
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

	static class Constants {
		public static final String ROOT_ELEMENT_NAME = "TermDefinition";
		public static final String TYPE_NAME = "TermDefinitionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}

	static class Elements {
		public static final String ID = "id";
		public static final String SPECIFICATION = "specification";
		public static final String PARAMETERS = "parameters";
	}
	
	
}
