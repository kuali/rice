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
import java.util.List;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor;
import org.kuali.rice.kns.datadictionary.validation.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.ErrorLevel;
import org.kuali.rice.kns.datadictionary.validation.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.MustOccurConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class MustOccurConstraintProcessor extends BasePrerequisiteConstraintProcessor<MustOccurConstrained> {

	private static final String CONSTRAINT_NAME = "must occur constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DictionaryValidationResult result,
			Object value, MustOccurConstrained definition, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		if (ValidatorUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
		
		List<MustOccurConstraint> mustOccurConstraints = definition.getMustOccurConstraints();
		if (null != mustOccurConstraints && mustOccurConstraints.size() > 0) {
			for (MustOccurConstraint occursConstraint : mustOccurConstraints) {
				
				// Only keep these validation results if the overall must occurs constraint fails... otherwise they're wrong
				if (processMustOccurConstraint(result, occursConstraint, definition, attributeValueReader)) 
					result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
				else
					result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_OCCURS);
			}
		}
		
		// If no actual must occur constraints exist then this is a case of not having a constraint
		return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
	}
	
	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<MustOccurConstrained> getType() {
		return MustOccurConstrained.class;
	}
	
    protected boolean processMustOccurConstraint(DictionaryValidationResult result, MustOccurConstraint constraint, Validatable definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        boolean isSuccessful = false;
        int trueCount = 0;
        
        List<PrerequisiteConstraint> prerequisiteConstraints = constraint.getPrerequisiteConstraints();
        if (prerequisiteConstraints != null) {
	        for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
	        	ConstraintValidationResult constraintValidationResult = processPrerequisiteConstraint(prerequisiteConstraint, attributeValueReader);
	            trueCount += (constraintValidationResult.getStatus().getLevel() <= ErrorLevel.WARN.getLevel()) ? 1 : 0;
	        }
        }

        List<MustOccurConstraint> mustOccurConstraints = constraint.getMustOccurConstraints();
        if (mustOccurConstraints != null) {
	        for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
	            trueCount += (processMustOccurConstraint(result, mustOccurConstraint, definition, attributeValueReader)) ? 1 : 0;
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
