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

import java.util.Collection;

import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public abstract class BaseDependencyConstraintProcessor<D extends Validatable> extends MandatoryConstraintProcessor<D> {

	private DataDictionaryService dataDictionaryService;
	
	protected boolean processRequiredConstraint(ConstraintValidationResult result, DependencyConstraint constraint, Validatable definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

    	String entryName = attributeValueReader.getEntryName();
    	
    	// TODO: Does this code need to be able to look at more than just the other immediate members of the object? 
        String fieldName = constraint.getFieldPath();
        Object fieldValue = attributeValueReader.getValue(fieldName);

        boolean isSuccessful = true;

        if (fieldValue instanceof java.lang.String) {
        	isSuccessful = ValidatorUtils.hasText((String) fieldValue);
        } else if (fieldValue instanceof Collection) {
        	isSuccessful = (((Collection<?>) fieldValue).size() > 0);
        } else {
        	isSuccessful = (null != fieldValue) ? true : false;
        }

        if (!isSuccessful) {        	
        	Validatable attributeDefinition = getDataDictionaryService().getAttributeDefinition(entryName, fieldName);
        	if (attributeDefinition != null)
        		fieldName = attributeDefinition.getLabel();
        	
        	result.setError(RiceKeyConstants.ERROR_REQUIRES_FIELD, fieldName);
        }
        
        return isSuccessful;
    }
	
	/**
	 * @return the dataDictionaryService
	 */
	public DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null)
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		return this.dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

}
