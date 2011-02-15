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
package org.kuali.rice.kns.datadictionary.validation.result;

import java.util.Collections;
import java.util.List;

import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProcessorResult {

	private final transient Validatable definition;
	private final transient AttributeValueReader attributeValueReader;
	
	private final List<ConstraintValidationResult> constraintValidationResults;
	
	public ProcessorResult(ConstraintValidationResult constraintValidationResult) {
		this(constraintValidationResult, null, null);
	}
	
	public ProcessorResult(ConstraintValidationResult constraintValidationResult, Validatable definition, AttributeValueReader attributeValueReader) {
		this.constraintValidationResults = Collections.singletonList(constraintValidationResult);
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
	}
	
	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults) {
		this(constraintValidationResults, null, null);
	}
	
	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults, Validatable definition, AttributeValueReader attributeValueReader) {
		this.constraintValidationResults = constraintValidationResults;
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
	}
	
	public boolean isSingleConstraintResult() {
		return this.constraintValidationResults.size() == 1;
	}

	public boolean isDefinitionProvided() {
		return definition != null;
	}
	
	public boolean isAttributeValueReaderProvided() {
		return attributeValueReader != null;
	}
	
	public ConstraintValidationResult getFirstConstraintValidationResult() {
		return this.constraintValidationResults.isEmpty() ? null : this.constraintValidationResults.get(0);
	}
	
	/**
	 * @return the constraintValidationResults
	 */
	public List<ConstraintValidationResult> getConstraintValidationResults() {
		return this.constraintValidationResults;
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
		
}
