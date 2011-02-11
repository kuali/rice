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
package org.kuali.rice.kns.datadictionary.validation;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public class ExistenceConstraintProcessor extends OptionalConstraintProcessor<ExistenceConstrained> {

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(org.kuali.rice.kns.datadictionary.validation.Validatable, org.kuali.rice.kns.datadictionary.validator.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(ExistenceConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		Object value = attributeValueReader.getValue();
		ConstraintValidationResult result = new ConstraintValidationResult(definition, attributeValueReader);
		
		if (definition.isRequired() != null && definition.isRequired().booleanValue()) {
			if (ValidatorUtils.isNullOrEmpty(value)) {
				result.setError(RiceKeyConstants.ERROR_REQUIRED);
			}
		}

		return result;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<ExistenceConstrained> getType() {
		return ExistenceConstrained.class;
	}

}
