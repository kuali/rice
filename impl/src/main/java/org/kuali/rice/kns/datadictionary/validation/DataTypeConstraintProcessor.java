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
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public class DataTypeConstraintProcessor extends MandatoryConstraintProcessor<Validatable> {

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validator.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(Validatable definition,
			AttributeValueReader attributeValueReader)
			throws AttributeValidationException {
		
		ConstraintValidationResult result = new ConstraintValidationResult(definition, attributeValueReader);

		DataType dataType = definition.getDataType();
		
		if (dataType == null)
			return result;
		
		Object value = attributeValueReader.getValue();
		
		if (ValidatorUtils.isNullOrEmpty(value))
			return result;
		
		try {
			ValidatorUtils.convertToDataType(value, dataType);
		} catch (Exception e) {		
			switch (dataType) {
			case BOOLEAN:
				result.setError(RiceKeyConstants.ERROR_BOOLEAN);
				break;
			case INTEGER:
				result.setError(RiceKeyConstants.ERROR_INTEGER);
				break;
			case LONG:
				result.setError(RiceKeyConstants.ERROR_LONG);
				break;
			case DOUBLE:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case FLOAT:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case TRUNCATED_DATE:
			case DATE:
				result.setError(RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case STRING:
			case COMPLEX:
				result.setError(RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
				break;
			}
		}
		
		return result;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<Validatable> getType() {
		return Validatable.class;
	}
	
}
