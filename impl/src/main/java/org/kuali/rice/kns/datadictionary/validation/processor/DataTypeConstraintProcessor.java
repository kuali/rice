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
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.DataType;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class DataTypeConstraintProcessor extends MandatoryElementConstraintProcessor<Validatable> {

	private static final String CONSTRAINT_NAME = "data type constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, Validatable definition, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		DataType dataType = definition.getDataType();
		
		return new ProcessorResult(processDataTypeConstraint(result, dataType, value, attributeValueReader));
	}
	
	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#getType()
	 */
	@Override
	public Class<Validatable> getType() {
		return Validatable.class;
	}
	
	protected ConstraintValidationResult processDataTypeConstraint(DictionaryValidationResult result, DataType dataType, Object value, AttributeValueReader attributeValueReader) {
		if (dataType == null)
			return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
		
		if (ValidatorUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
		
		try {
			ValidatorUtils.convertToDataType(value, dataType);
		} catch (Exception e) {		
			switch (dataType) {
			case BOOLEAN:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BOOLEAN);
				break;
			case INTEGER:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INTEGER);
				break;
			case LONG:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_LONG);
				break;
			case DOUBLE:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case FLOAT:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case TRUNCATED_DATE:
			case DATE:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
				break;
			case STRING:
			case COMPLEX:
				result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
				break;
			}
		}
		
		return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
	}
	
}
