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

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor;
import org.kuali.rice.kns.datadictionary.validation.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.DataType;
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils.Result;
import org.kuali.rice.kns.datadictionary.validation.capability.LengthConstrained;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class LengthConstraintProcessor extends MandatoryElementConstraintProcessor<LengthConstrained> {

	private static final String CONSTRAINT_NAME = "length constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DictionaryValidationResult result, Object value, LengthConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		// Can't process any range constraints on null values
		if (ValidatorUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);

		
		DataType dataType = definition.getDataType();
		Object typedValue = value;

		if (dataType != null) {
			typedValue = ValidatorUtils.convertToDataType(value, dataType);
		}	

		// The only thing that can have a length constraint currently is a string. 
		if (typedValue instanceof String) {
			return validateLength(result, (String)typedValue, definition, attributeValueReader);
		} 
		
		return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
	}

	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<LengthConstrained> getType() {
		return LengthConstrained.class;
	}
	
	
	protected ConstraintValidationResult validateLength(DictionaryValidationResult result, String value, LengthConstrained attribute, AttributeValueReader attributeValueReader) throws IllegalArgumentException {
		Integer valueLength = Integer.valueOf(value.length());
		
		Integer maxLength = attribute.getMaxLength();
		Integer minLength = attribute.getMinLength();
		
		Result lessThanMax = ValidatorUtils.isLessThan(valueLength, maxLength);
		Result greaterThanMin = ValidatorUtils.isGreaterThan(valueLength, minLength);
		
        // It's okay for one end of the range to be undefined - that's not an error. It's only an error if one of them is invalid 
        if (lessThanMax != Result.INVALID && greaterThanMin != Result.INVALID) { 
        	// Of course, if they're both undefined then we didn't actually have a real constraint
        	if (lessThanMax == Result.UNDEFINED && greaterThanMin == Result.UNDEFINED)
        		return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
        	
        	// In this case, we've succeeded
        	return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }
        
		String maxErrorParameter = maxLength != null ? maxLength.toString() : null;
		String minErrorParameter = minLength != null ? minLength.toString() : null;
        
        // If both comparisons happened then if either comparison failed we can show the end user the expected range on both sides.
        if (lessThanMax != Result.UNDEFINED && greaterThanMin != Result.UNDEFINED) 
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_OUT_OF_RANGE, minErrorParameter, maxErrorParameter);
        // If it's the max comparison that fails, then just tell the end user what the max can be
        else if (lessThanMax == Result.INVALID)
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INCLUSIVE_MAX, maxErrorParameter);
        // Otherwise, just tell them what the min can be
        else 
        	return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_EXCLUSIVE_MIN, minErrorParameter);
        
	}

}
