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
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.ExistenceConstrained;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class ExistenceConstraintProcessor extends OptionalElementConstraintProcessor<ExistenceConstrained> {

	private static final String CONSTRAINT_NAME = "existence constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.Validatable, org.kuali.rice.kns.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DictionaryValidationResult result, Object value, ExistenceConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		// If it's not set, then there's no constraint
		if (definition.isRequired() == null)
			return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
		
		if (definition.isRequired().booleanValue()) {
			// If this attribute is required and the value is null then 
			if (ValidatorUtils.isNullOrEmpty(value)) 
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_REQUIRED);
			
			return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
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
	public Class<ExistenceConstrained> getType() {
		return ExistenceConstrained.class;
	}

}
