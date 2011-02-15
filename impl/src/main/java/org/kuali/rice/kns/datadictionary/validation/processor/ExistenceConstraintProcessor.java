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

import java.util.Collections;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.capability.ExistenceConstrained;
import org.kuali.rice.kns.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.result.ProcessorResult;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class ExistenceConstraintProcessor extends OptionalElementConstraintProcessor<ExistenceConstrained> {

	private static final String CONSTRAINT_NAME = "existence constraint";
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.Validatable, org.kuali.rice.kns.datadictionary.validation.capability.AttributeValueReader)
	 */
	@Override
	public ProcessorResult process(DictionaryValidationResult result, Object value, ExistenceConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		// To accommodate the needs of other processors, the ConstraintProcessor.process() method returns a list of ConstraintValidationResult objects
		// but since a definition that is existence constrained only provides a single isRequired field, there is effectively a single constraint
		// being imposed.
		return new ProcessorResult(processSingleExistenceConstraint(result, value, definition, attributeValueReader));
	}

	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#getType()
	 */
	@Override
	public Class<ExistenceConstrained> getType() {
		return ExistenceConstrained.class;
	}

	protected ConstraintValidationResult processSingleExistenceConstraint(DictionaryValidationResult result, Object value, ExistenceConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {
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
	
}
