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

import java.util.Collection;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public abstract class BasePrerequisiteConstraintProcessor<D extends Validatable> extends MandatoryElementConstraintProcessor<D> {
	
	protected ConstraintValidationResult processPrerequisiteConstraint(PrerequisiteConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

    	// TODO: Does this code need to be able to look at more than just the other immediate members of the object? 
        String attributeName = constraint.getAttributePath();
        
        if (ValidatorUtils.isNullOrEmpty(attributeName)) {
        	throw new AttributeValidationException("Prerequisite constraints must include the name of the attribute that is required");
        }
        
        Object value = attributeValueReader.getValue(attributeName);

        boolean isSuccessful = true;

        if (value instanceof java.lang.String) {
        	isSuccessful = ValidatorUtils.hasText((String) value);
        } else if (value instanceof Collection) {
        	isSuccessful = (((Collection<?>) value).size() > 0);
        } else {
        	isSuccessful = (null != value) ? true : false;
        }

        ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(getName());
        
        if (!isSuccessful) {        	
        	String label = attributeValueReader.getLabel(attributeName); 
        	if (label != null)
        		attributeName = label;
        	
        	constraintValidationResult.setError(RiceKeyConstants.ERROR_REQUIRES_FIELD, attributeName);
        } 
        
        return constraintValidationResult;
    }

}
