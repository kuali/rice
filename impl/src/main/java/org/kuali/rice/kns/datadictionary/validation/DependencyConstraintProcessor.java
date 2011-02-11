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

import java.util.List;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.DependencyConstrained;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public class DependencyConstraintProcessor extends BaseDependencyConstraintProcessor<DependencyConstrained> {
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validator.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DependencyConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {
		
		ConstraintValidationResult result = new ConstraintValidationResult(definition, attributeValueReader);
		Object value = attributeValueReader.getValue();
		
		if (ValidatorUtils.isNullOrEmpty(value))
			return result;
		
		List<DependencyConstraint> requiredConstraints = definition.getRequireConstraint();
		if (null != requiredConstraints && requiredConstraints.size() > 0) {
			for (DependencyConstraint requiredConstraint : requiredConstraints) {
				processRequiredConstraint(result, requiredConstraint, definition, attributeValueReader);
			}
		}
		
		return result;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<DependencyConstrained> getType() {
		return DependencyConstrained.class;
	}

}
