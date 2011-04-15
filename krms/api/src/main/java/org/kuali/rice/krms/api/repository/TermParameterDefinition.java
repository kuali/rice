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

/**
 * Immutable DTO for TermParameters.  An instance represents a single parameter on a Term. 
 * Construction must be done via the {@link Builder} inner class.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = TermParameterDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RuleDefinition.Constants.TYPE_NAME, propOrder = {
		TermParameterDefinition.Elements.ID,
		TermParameterDefinition.Elements.NAME,
		TermParameterDefinition.Elements.VALUE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class TermParameterDefinition implements TermParameterDefinitionContract, ModelObjectComplete {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = Elements.ID, required=true)
	private final String termParameterId;
	
	@XmlElement(name = Elements.NAME, required=true)
	private final String name;
	
	@XmlElement(name = Elements.VALUE)
	private final String value;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	// For JAXB use only, shouldn't be invoked directly
	private TermParameterDefinition() {
		termParameterId = null;
		name = null;
		value = null;
	}
	
	private TermParameterDefinition(Builder builder) {
		termParameterId = builder.getTermParameterId(); 
		name = builder.getName();
		value = builder.getValue();
	}
	
	public static class Builder implements TermParameterDefinitionContract, ModelBuilder, Serializable {

		private static final long serialVersionUID = 1L;
		
		private String termParameterId;
		private String name;
		private String value;
		
		private static final String NON_NULL_NON_EMPTY_ERROR =  
			" must be non-null and must contain non-whitespace chars";
		
		private Builder(String termParameterId, String name, String value) {
			setTermParameterId(termParameterId);
			setName(name);
			setValue(value);
		}
		
		public static Builder create(String termParameterId, String name, String value) {
			return new Builder(termParameterId, name, value);
		}
		
		public static Builder create(TermParameterDefinitionContract termParameterDefinition) {
			return new Builder(termParameterDefinition.getTermParameterId(), 
					termParameterDefinition.getName(), 
					termParameterDefinition.getValue());
		}
		
		// Setters:
		
		/**
		 * @param termParameterId the termParameterId to set.  for {@link TermParameterDefinition}s used in creational 
		 * service methods, it must be null.  Otherwise, it must be non-null and contain non-whitespace chars.
		 * @throws IllegalArgumentException if termParameterId is all whitespace chars
		 */
		public void setTermParameterId(String termParameterId) {
			if (termParameterId != null && StringUtils.isBlank(termParameterId)) {
				throw new IllegalArgumentException("termParameterId" + NON_NULL_NON_EMPTY_ERROR);
			}
			this.termParameterId = termParameterId;
		}
		
		/**
		 * @param name the name to set.  Must be non-null and contain non-whitespace chars.
		 * @throws IllegalArgumentException if name is null or is all whitespace chars
		 */
		public void setName(String name) {
			if (StringUtils.isBlank(name)) {
				throw new IllegalArgumentException("name" + NON_NULL_NON_EMPTY_ERROR);
			}
			this.name = name;
		}
		
		/**
		 * @param value the value to set.  May be null or empty.
		 */
		public void setValue(String value) {
			this.value = value;
		}
		
		// Getters:
		
		/**
		 * @return the termParameterId
		 */
		public String getTermParameterId() {
			return this.termParameterId;
		}
		
		/**
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}		
		
		/**
		 * return a {@link TermParameterDefinition} instance constructed from this {@link Builder} 
		 * @see org.kuali.rice.core.api.mo.ModelBuilder#build()
		 */
		@Override
		public TermParameterDefinition build() {
			return new TermParameterDefinition(this);
		}
		
	}
	
	
	/**
	 * @return the termParameterId
	 */
	public String getTermParameterId() {
		return this.termParameterId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	static class Constants {
		public static final String ROOT_ELEMENT_NAME = "TermParameterDefinition";
		public static final String TYPE_NAME = "TermParameterDefinitionType";
	}
	
	static class Elements {
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String VALUE = "value";
	}
}
