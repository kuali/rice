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
package org.kuali.rice.kns.datadictionary.validation.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.PrerequisiteConstrained;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.ProcessorResult;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class PrerequisiteConstraintProcessor extends BasePrerequisiteConstraintProcessor<PrerequisiteConstrained> {
	
	private static final String CONSTRAINT_NAME = "prerequisite constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, PrerequisiteConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		if (ValidatorUtils.isNullOrEmpty(value))
			return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));
		
		List<ConstraintValidationResult> prerequisiteConstraintResults = new ArrayList<ConstraintValidationResult>();
		
		// A single definition that is prerequisite constrained actually has a list of prerequisite constraints on it. 
		// Each of these constraints needs to be validated independently and the result needs to be added the DictionaryValidationResult object
		// as well as being returned as part of the list of constraint validation results being returned by this method
		List<PrerequisiteConstraint> prerequisiteConstraints = definition.getPrerequisiteConstraints();
		if (null != prerequisiteConstraints && prerequisiteConstraints.size() > 0) {
			for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
				ConstraintValidationResult constraintValidationResult = processPrerequisiteConstraint(prerequisiteConstraint, attributeValueReader);
				result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
				prerequisiteConstraintResults.add(constraintValidationResult);
			}
		}
		
		if (prerequisiteConstraintResults.size() > 0)
			return new ProcessorResult(prerequisiteConstraintResults);
		
		// If no actual prerequisite constraints exist then this is a case of not having a constraint
		return new ProcessorResult(result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME));
	}

	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#getType()
	 */
	@Override
	public Class<PrerequisiteConstrained> getType() {
		return PrerequisiteConstrained.class;
	}

}
