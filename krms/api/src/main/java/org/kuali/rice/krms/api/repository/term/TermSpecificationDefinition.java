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
import org.kuali.rice.krms.api.repository.BuilderUtils;
import org.kuali.rice.krms.api.repository.BuilderUtils.Transformer;

/**
 * Immutable DTO for TermSpecifications.  Construction must be done via the {@link Builder} inner class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = TermSpecificationDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = TermSpecificationDefinition.Constants.TYPE_NAME, propOrder = {
		TermSpecificationDefinition.Elements.ID,
		TermSpecificationDefinition.Elements.CONTEXT_ID,
		TermSpecificationDefinition.Elements.NAME,
		TermSpecificationDefinition.Elements.TYPE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class TermSpecificationDefinition implements TermSpecificationDefinitionContract, ModelObjectComplete {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = Elements.ID, required=false)
	private final String id;
	@XmlElement(name = Elements.CONTEXT_ID, required=true)
	private final String contextId;
	@XmlElement(name = Elements.NAME, required=true)
	private final String name;
	@XmlElement(name = Elements.TYPE, required=true)
	private final String type;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	/**
	 * For JAXB use only, shouldn't ever be invoked directly
	 */
	private TermSpecificationDefinition() {
		id = null;
		contextId = null;
		name = null;
		type = null;
	}
	
	/**
	 * Private constructor enforces use of Builder
	 * 
	 * @param b the builder to use
	 */
	private TermSpecificationDefinition(Builder b) {
		id = b.getId();
		contextId = b.getContextId();
		name = b.getName();
		type = b.getType();
	}
	
	/**
	 * Builder for the {@link TermSpecificationDefinition} immutable DTO.  Instantiate using static factory method(s).
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	public static class Builder implements TermSpecificationDefinitionContract, ModelBuilder, Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String termSpecificationId;
		private String contextId;
		private String name;
		private String type;
		
		private static final String NON_NULL_NON_EMPTY_ERROR =  " must be non-null and must contain non-whitespace chars"; 

		/**
		 * {@link Transformer} to ease converting lists to Builder types
		 */
		public static final Transformer<TermSpecificationDefinitionContract, TermSpecificationDefinition.Builder>
		toBuilder = new BuilderUtils.Transformer<TermSpecificationDefinitionContract, TermSpecificationDefinition.Builder>() {
			public TermSpecificationDefinition.Builder transform(TermSpecificationDefinitionContract input) {
				return TermSpecificationDefinition.Builder.create(input);
			}
		};
		
		private Builder(String termSpecificationId, String contextId, String name, String type) {
			// weird to use setters in constructor .. oh well.
			setId(termSpecificationId);
			setContextId(contextId);
			setName(name);
			setType(type);
		}
		
		/**
		 * This method constructs a {@link Builder} from a complete set of field values for this object.
		 * 
		 * @param termSpecificationId the primary key field.  Must be null for service methods that 
		 * create {@link TermSpecificationDefinitionContract}s, and must be non-null & contain non-whitespace 
		 * chars otherwise.
		 * @param contextId key relates the {@link TermSpecificationDefinition} to a context.
		 * @param name the name for the {@link TermSpecificationDefinition}
		 * @param type the type for the {@link TermSpecificationDefinition}
		 * @return a {@link Builder} object
		 */
		public static Builder create(String termSpecificationId, String contextId, String name, String type) {
			return new Builder(termSpecificationId, contextId, name, type);
		}
		
		public static Builder create(TermSpecificationDefinitionContract termSpecification) {
			if (termSpecification == null) throw new IllegalArgumentException("termSpecification must be non-null");
			return new Builder(termSpecification.getId(), 
					termSpecification.getContextId(), 
					termSpecification.getName(), 
					termSpecification.getType());
		}
		
		// Setters
		
		/**
		 * @param termSpecificationId the key for this {@link TermSpecificationDefinition}.  Must be null for
		 * service methods that create {@link TermSpecificationDefinitionContract}s, and otherwise must be non-null & contain 
		 * non-whitespace chars.
		 */
		public void setId(String termSpecificationId) {
			if (termSpecificationId != null && StringUtils.isBlank(termSpecificationId))
				throw new IllegalArgumentException("termSpecificationId must contain non-whitespace chars");
			this.termSpecificationId = termSpecificationId;
		}
		
		/**
		 * @param contextId the contextId to set.  Must be non-null and contain non-whitespace chars;
		 */
		public void setContextId(String contextId) {
			if (StringUtils.isBlank(contextId)) {
				throw new IllegalArgumentException("contextId" + NON_NULL_NON_EMPTY_ERROR);
			}
			this.contextId = contextId;
		}
		
		/**
		 * @param name the name to set.  Must be non-null and contain non-whitespace chars;
		 */
		public void setName(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("name" + NON_NULL_NON_EMPTY_ERROR);
			}
			this.name = name;
		}
		/**
		 * @param type the type to set. Must be non-null and contain non-whitespace chars;
		 */
		public void setType(String type) {
			if (StringUtils.isBlank(type)) {
				throw new IllegalArgumentException("type" + NON_NULL_NON_EMPTY_ERROR);
			}
			this.type = type;
		}
		
		// Getters
		
		/**
		 * @return the termSpecificationId
		 */
		@Override
		public String getId() {
			return this.termSpecificationId;
		}

		/**
		 * @return the contextId
		 */
		@Override
		public String getContextId() {
			return this.contextId;
		}

		/**
		 * @return the name
		 */
		@Override
		public String getName() {
			return this.name;
		}

		/**
		 * @return the type
		 */
		@Override
		public String getType() {
			return this.type;
		}

		/**
		 * Constructs a {@link TermSpecificationDefinition}
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 */
		@Override
		public TermSpecificationDefinition build() {
			return new TermSpecificationDefinition(this);
		}
	}

	/**
	 * This value will be non-null for persisted  
	 * @see org.kuali.rice.krms.api.repository.term.TermSpecificationDefinitionContract#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krms.api.repository.term.TermSpecificationDefinitionContract#getContextId()
	 */
	@Override
	public String getContextId() {
		return contextId;
	}

	/**
	 * @see org.kuali.rice.krms.api.repository.term.TermSpecificationDefinitionContract#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @see org.kuali.rice.krms.api.repository.term.TermSpecificationDefinitionContract#getType()
	 */
	@Override
	public String getType() {
		return type;
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
	* @see java.lang.Object#hashCode()
	*/
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
	}
	
	/**
	* @see java.lang.Object#toString()
	*/
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "TermSpecification";
		final static String TYPE_NAME = "TermSpecificationType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { CoreConstants.CommonElements.FUTURE_ELEMENTS };
	}
	
	static class Elements {
		public static final String ID = "id";
		public static final String CONTEXT_ID = "contextId";
		public static final String NAME = "name";
		public static final String TYPE = "type";
	}
	

}
