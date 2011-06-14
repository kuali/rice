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
package org.kuali.rice.krad.datadictionary.validation.processor;

import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.DataType;
import org.kuali.rice.krad.datadictionary.validation.ValidationUtils;
import org.kuali.rice.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.DataTypeConstraint;
import org.kuali.rice.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.datadictionary.validation.result.ProcessorResult;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class DataTypeConstraintProcessor extends MandatoryElementConstraintProcessor<DataTypeConstraint> {

	private static final String CONSTRAINT_NAME = "data type constraint";
	
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.processor.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Constrainable, org.kuali.rice.krad.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, DataTypeConstraint constraint, AttributeValueReader attributeValueReader)
			throws AttributeValidationException {

		DataType dataType = constraint.getDataType();
		
		return new ProcessorResult(processDataTypeConstraint(result, dataType, value, attributeValueReader));
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
		return DataTypeConstraint.class;
	}
	
	protected ConstraintValidationResult processDataTypeConstraint(DictionaryValidationResult result, DataType dataType, Object value, AttributeValueReader attributeValueReader) {
		if (dataType == null)
			return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
		
		if (ValidationUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
		
		try {
			ValidationUtils.convertToDataType(value, dataType, dateTimeService);
		} catch (Exception e) {		
			switch (dataType) {
			case BOOLEAN:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BOOLEAN);
			case INTEGER:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_INTEGER);
			case LONG:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_LONG);
			case DOUBLE:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case FLOAT:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case TRUNCATED_DATE:
			case DATE:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_BIG_DECIMAL);
			case STRING:
			case COMPLEX:
				return result.addError(attributeValueReader, CONSTRAINT_NAME, RiceKeyConstants.ERROR_CUSTOM, e.getMessage());
			}
		}
		
		// If we get here then it was a success!
		return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
	}
	
}
