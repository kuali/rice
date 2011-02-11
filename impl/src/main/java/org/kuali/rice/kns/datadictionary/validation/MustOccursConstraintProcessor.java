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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.MustOccurConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.ValidationResultInfo;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public class MustOccursConstraintProcessor extends BaseDependencyConstraintProcessor<MustOccurConstrained> {

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validator.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(MustOccurConstrained definition,
			AttributeValueReader attributeValueReader)
			throws AttributeValidationException {
		ConstraintValidationResult result = new ConstraintValidationResult(definition, attributeValueReader);
		Object value = attributeValueReader.getValue();
		
		if (ValidatorUtils.isNullOrEmpty(value))
			return result;
		
		List<MustOccurConstraint> mustOccurConstraints = definition.getOccursConstraint();
		if (null != mustOccurConstraints && mustOccurConstraints.size() > 0) {
			for (MustOccurConstraint occursConstraint : mustOccurConstraints) {
				processMustOccurConstraint(result, occursConstraint, definition, attributeValueReader);
			}
		}
		
		return result;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<MustOccurConstrained> getType() {
		return MustOccurConstrained.class;
	}
	
    protected boolean processMustOccurConstraint(ConstraintValidationResult result, MustOccurConstraint constraint, Validatable definition, AttributeValueReader dataProvider) throws AttributeValidationException {

        boolean isSuccessful = false;
        int trueCount = 0;
        
        for (DependencyConstraint rc : constraint.getRequiredFields()) {
            trueCount += (processRequiredConstraint(result, rc, definition, dataProvider)) ? 1 : 0;
        }

        for (MustOccurConstraint oc : constraint.getOccurs()) {
            trueCount += (processMustOccurConstraint(result, oc, definition, dataProvider)) ? 1 : 0;
        }

        int minimum = constraint.getMin() != null ? constraint.getMin().intValue() : 0;
        int maximum = constraint.getMax() != null ? constraint.getMax().intValue() : 0;
        
        isSuccessful = (trueCount >= minimum && trueCount <= maximum) ? true : false;

        if (!isSuccessful) {
        	result.setError(RiceKeyConstants.ERROR_OCCURS);
        }

        return isSuccessful;
    }

}
