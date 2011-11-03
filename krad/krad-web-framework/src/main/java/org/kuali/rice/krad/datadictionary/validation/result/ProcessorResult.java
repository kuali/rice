/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.datadictionary.validation.result;

import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProcessorResult {

	private final transient List<Constraint> constraints;
	private final transient Constrainable definition;
	private final transient AttributeValueReader attributeValueReader;
	
	private final List<ConstraintValidationResult> constraintValidationResults;
	
	public ProcessorResult(ConstraintValidationResult constraintValidationResult) {
		this(constraintValidationResult, null, null);
	}
	
	public ProcessorResult(ConstraintValidationResult constraintValidationResult, Constrainable definition, AttributeValueReader attributeValueReader, Constraint... constraints) {
		this.constraintValidationResults = Collections.singletonList(constraintValidationResult);
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
		this.constraints = Arrays.asList(constraints);
	}
	
	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults) {
		this(constraintValidationResults, null, null);
	}
	
	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults, Constrainable definition, AttributeValueReader attributeValueReader, Constraint... constraints) {
		this.constraintValidationResults = constraintValidationResults;
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
		this.constraints = Arrays.asList(constraints);
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
	public Constrainable getDefinition() {
		return this.definition;
	}

	/**
	 * @return the attributeValueReader
	 */
	public AttributeValueReader getAttributeValueReader() {
		return this.attributeValueReader;
	}

	/**
	 * @return the constraints
	 */
	public List<Constraint> getConstraints() {
		return this.constraints;
	}
		
}
