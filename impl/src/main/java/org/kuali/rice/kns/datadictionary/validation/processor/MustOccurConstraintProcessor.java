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
import org.kuali.rice.kns.datadictionary.validation.capability.ErrorLevel;
import org.kuali.rice.kns.datadictionary.validation.capability.MustOccurConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MustOccurConstraintProcessor extends BasePrerequisiteConstraintProcessor<MustOccurConstrained> {

	private static final String CONSTRAINT_NAME = "must occur constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result,
			Object value, MustOccurConstrained definition, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		if (ValidatorUtils.isNullOrEmpty(value))
			return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));
		
		List<ConstraintValidationResult> constraintValidationResults = new ArrayList<ConstraintValidationResult>();
		
		List<MustOccurConstraint> mustOccurConstraints = definition.getMustOccurConstraints();
		if (null != mustOccurConstraints && mustOccurConstraints.size() > 0) {
			for (MustOccurConstraint occursConstraint : mustOccurConstraints) {
				ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
				
				if (!processMustOccurConstraint(constraintValidationResult, occursConstraint, definition, attributeValueReader)) {
					// If the processing of this constraint was not successful then it's an error
					constraintValidationResult.setError(RiceKeyConstants.ERROR_OCCURS);
				} 
				
				// Store the label key (if one exists) for this constraint on the constraint validation result so it can be shown later
				constraintValidationResult.setConstraintLabelKey(occursConstraint.getLabelKey());
				// Add it to the DictionaryValidationResult object
				result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
				// And also add it to the list of 
				constraintValidationResults.add(constraintValidationResult);
			}
		}
		
		// If there were results, then return them
		if (constraintValidationResults.size() > 0)
			return new ProcessorResult(constraintValidationResults);
		
		// Otherwise, it's a case of not really having a constraint
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
	public Class<MustOccurConstrained> getType() {
		return MustOccurConstrained.class;
	}
	
    protected boolean processMustOccurConstraint(ConstraintValidationResult topLevelResult, MustOccurConstraint constraint, Validatable definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        boolean isSuccessful = false;
        int trueCount = 0;
        
        List<PrerequisiteConstraint> prerequisiteConstraints = constraint.getPrerequisiteConstraints();
        if (prerequisiteConstraints != null) {
	        for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
	        	ConstraintValidationResult constraintValidationResult = processPrerequisiteConstraint(prerequisiteConstraint, attributeValueReader);
	        	// Add the result of each prerequisite constraint validation to the top level result object as a child
	        	topLevelResult.addChild(constraintValidationResult);
	            trueCount += (constraintValidationResult.getStatus().getLevel() <= ErrorLevel.WARN.getLevel()) ? 1 : 0;
	        }
        }

        List<MustOccurConstraint> mustOccurConstraints = constraint.getMustOccurConstraints();
        if (mustOccurConstraints != null) {
	        for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
	        	// Create a new constraint validation result for this must occur constraint and make it child of the top-level constraint, 
	        	// then pass it in to the recursive call so that prerequisite constraints can be placed under it
	        	ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
	        	topLevelResult.addChild(constraintValidationResult);
	            trueCount += (processMustOccurConstraint(constraintValidationResult, mustOccurConstraint, definition, attributeValueReader)) ? 1 : 0;
	        }
        }

        int minimum = constraint.getMin() != null ? constraint.getMin().intValue() : 0;
        int maximum = constraint.getMax() != null ? constraint.getMax().intValue() : 0;
        
        isSuccessful = (trueCount >= minimum && trueCount <= maximum) ? true : false;

//        if (!isSuccessful) {
//        	result.setError(RiceKeyConstants.ERROR_OCCURS);
//        }

        return isSuccessful;
    }

}
