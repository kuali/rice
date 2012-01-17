/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.processor;

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;
import org.kuali.rice.krad.datadictionary.validation.ValidationUtils;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.ProcessorResult;

import java.util.List;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MustOccurConstraintProcessor extends BasePrerequisiteConstraintProcessor<MustOccurConstraint> {

	private static final String CONSTRAINT_NAME = "must occur constraint";
	
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.processor.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Constrainable, org.kuali.rice.krad.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result,
			Object value, MustOccurConstraint constraint, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		if (ValidationUtils.isNullOrEmpty(value))
			return new ProcessorResult(result.addSkipped(attributeValueReader, CONSTRAINT_NAME));
		

		ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
				
		if (!processMustOccurConstraint(constraintValidationResult, constraint, attributeValueReader)) {
			// If the processing of this constraint was not successful then it's an error
		    if (attributeValueReader.getAttributeName() == null){
		        constraintValidationResult.setAttributeName(attributeValueReader.getPath());
		    } else{
		        constraintValidationResult.setAttributeName(attributeValueReader.getAttributeName());
		        constraintValidationResult.setAttributePath(attributeValueReader.getPath());
		    }
			constraintValidationResult.setError(RiceKeyConstants.ERROR_OCCURS);
		} 

		// Store the label key (if one exists) for this constraint on the constraint validation result so it can be shown later
		constraintValidationResult.setConstraintLabelKey(constraint.getLabelKey());
		// Add it to the DictionaryValidationResult object
		result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);

		return new ProcessorResult(constraintValidationResult);

	}
	
	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}

	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.processor.ConstraintProcessor#getConstraintType()
	 */
	@Override
	public Class<? extends Constraint> getConstraintType() {
		return MustOccurConstraint.class;
	}
	
    protected boolean processMustOccurConstraint(ConstraintValidationResult topLevelResult, MustOccurConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

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
	            trueCount += (processMustOccurConstraint(constraintValidationResult, mustOccurConstraint, attributeValueReader)) ? 1 : 0;
	        }
        }

        int minimum = constraint.getMin() != null ? constraint.getMin().intValue() : 0;
        int maximum = constraint.getMax() != null ? constraint.getMax().intValue() : 0;
        
        isSuccessful = (trueCount >= minimum && trueCount <= maximum) ? true : false;

        return isSuccessful;
    }

}
