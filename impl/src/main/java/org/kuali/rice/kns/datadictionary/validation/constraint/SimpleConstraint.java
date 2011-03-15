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
package org.kuali.rice.kns.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleConstraint extends BaseConstraint {
    
	@XmlElement
	private Boolean required;
	
	@XmlElement
	private Integer maxLength;
	
	@XmlElement
	private Integer minLength;
	
	@XmlElement
	protected String exclusiveMin;
	
	@XmlElement
	protected String inclusiveMax;
	
	//Don't know if we will support min/max occurs at this time
	@XmlElement
	private Integer minOccurs;
	
	@XmlElement
	private Integer maxOccurs;

	/**
	 * @return the required
	 */
	public Boolean getRequired() {
		return this.required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * @return the maxLength
	 */
	public Integer getMaxLength() {
		return this.maxLength;
	}

	/**
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return the minLength
	 */
	public Integer getMinLength() {
		return this.minLength;
	}

	/**
	 * @param minLength the minLength to set
	 */
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	/**
	 * @return the exclusiveMin
	 */
	public String getExclusiveMin() {
		return this.exclusiveMin;
	}

	/**
	 * @param exclusiveMin the exclusiveMin to set
	 */
	public void setExclusiveMin(String exclusiveMin) {
		this.exclusiveMin = exclusiveMin;
	}

	/**
	 * @return the inclusiveMax
	 */
	public String getInclusiveMax() {
		return this.inclusiveMax;
	}

	/**
	 * @param inclusiveMax the inclusiveMax to set
	 */
	public void setInclusiveMax(String inclusiveMax) {
		this.inclusiveMax = inclusiveMax;
	}

	/**
	 * @return the minOccurs
	 */
	public Integer getMinOccurs() {
		return this.minOccurs;
	}

	/**
	 * @param minOccurs the minOccurs to set
	 */
	public void setMinOccurs(Integer minOccurs) {
		this.minOccurs = minOccurs;
	}

	/**
	 * @return the maxOccurs
	 */
	public Integer getMaxOccurs() {
		return this.maxOccurs;
	}

	/**
	 * @param maxOccurs the maxOccurs to set
	 */
	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}
	
	
}

