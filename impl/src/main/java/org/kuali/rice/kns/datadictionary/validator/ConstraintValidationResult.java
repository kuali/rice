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
package org.kuali.rice.kns.datadictionary.validator;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;

/**
 * This is a composite class for all the different members that need to be returned when a {@link ConstraintProcessor}
 * processes a {@link Constraint}. 
 * 
 * @author James Renfro, University of Washington 
 */
public class ConstraintValidationResult {

	private Validatable definition;
	private AttributeValueReader attributeValueReader;
	private List<ValidationResultInfo> validationResults;
	
	public ConstraintValidationResult() {
		this.validationResults = new ArrayList<ValidationResultInfo>();
	}
	
	public ConstraintValidationResult(Validatable definition, AttributeValueReader attributeValueReader) {
		this();
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
	}
	
	/**
	 * Adds a single validation result to the list of validation results.
	 * 
	 * @param result
	 */
	public void addValidationResult(ValidationResultInfo result) {
		this.validationResults.add(result);
	}
	
	public void setError(String errorKey, String... errorParameters) {
		ValidationResultInfo result = new ValidationResultInfo(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName());
		result.setError(errorKey, errorParameters);
		addValidationResult(result);
	}
	

	/**
	 * @return the validationResults
	 */
	public List<ValidationResultInfo> getValidationResults() {
		return this.validationResults;
	}

	/**
	 * @return the definition
	 */
	public Validatable getDefinition() {
		return this.definition;
	}

	/**
	 * @return the attributeValueReader
	 */
	public AttributeValueReader getAttributeValueReader() {
		return this.attributeValueReader;
	}

	/**
	 * @param definition the definition to set
	 */
	public void setDefinition(Validatable definition) {
		this.definition = definition;
	}

	/**
	 * @param attributeValueReader the attributeValueReader to set
	 */
	public void setAttributeValueReader(AttributeValueReader attributeValueReader) {
		this.attributeValueReader = attributeValueReader;
	}
	
	
}
