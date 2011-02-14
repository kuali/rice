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

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.LogicalOperators;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor;
import org.kuali.rice.kns.datadictionary.validation.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.ValidCharactersConstraint;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.capability.Formatable;
import org.kuali.rice.kns.datadictionary.validation.capability.ValidCharactersConstrained;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.format.DateFormatter;

/**
 * This class defines a constraint processor to ensure that attribute values are constrained to valid characters, as defined by some regular expression. Of the 
 * constraint processors written for this version, this one is potentially the most difficult to understand because it holds on to a lot of legacy processing.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class ValidCharactersConstraintProcessor extends MandatoryElementConstraintProcessor<ValidCharactersConstrained> {

	public static final String VALIDATE_METHOD = "validate";
	
	private static final Logger LOG = Logger.getLogger(ValidCharactersConstraintProcessor.class);
	private static final String[] DATE_RANGE_ERROR_PREFIXES = { KNSConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX, KNSConstants.LOOKUP_RANGE_UPPER_BOUND_PROPERTY_PREFIX };
    
	private static final String CONSTRAINT_NAME = "valid characters constraint";
	
	private DataDictionaryService dataDictionaryService;
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DictionaryValidationResult result, Object value, ValidCharactersConstrained definition, AttributeValueReader attributeValueReader)	throws AttributeValidationException {
		
		ValidCharactersConstraint validCharsConstraint = definition.getValidCharactersConstraint();

		if (validCharsConstraint == null) 
			return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
		
		if (ValidatorUtils.isNullOrEmpty(value))
			return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
		
		// This mix-in interface is here to allow some definitions to avoid the extra processing that goes on in KNS
		// to decipher and validate things like date range strings -- something that looks like "02/02/2002..03/03/2003"
    	if (definition instanceof Formatable) {
    		return doProcessFormattableValidCharConstraint(result, validCharsConstraint, (Formatable)definition, value, attributeValueReader);
    	} 
    	
    	ConstraintValidationResult constraintValidationResult = doProcessValidCharConstraint(validCharsConstraint, value);
    	if (constraintValidationResult == null)
    		return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
    	
    	result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
    	return constraintValidationResult;
	}

	@Override 
	public String getName() {
		return CONSTRAINT_NAME;
	}
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#getType()
	 */
	@Override
	public Class<ValidCharactersConstrained> getType() {
		return ValidCharactersConstrained.class;
	}
	
	
    protected ConstraintValidationResult doProcessFormattableValidCharConstraint(DictionaryValidationResult result, ValidCharactersConstraint validCharsConstraint, Formatable definition, Object value, AttributeValueReader attributeValueReader) throws AttributeValidationException {
    	String entryName = attributeValueReader.getEntryName();
    	String attributeName = attributeValueReader.getAttributeName();
    	
    	// This is a strange KNS thing for validating searchable fields -- they sometimes come in a date range format, for example 2/12/2010..2/14/2010, and need to be split up
		List<String> parsedAttributeValues = attributeValueReader.getCleanSearchableValues(attributeName);
		
		if (parsedAttributeValues != null) {
			
			Class<?> formatterClass = null;
			Boolean doValidateDateRangeOrder = null;
			
			// It can't be a date range if it's more than two fields, for example "a .. b | c" is not a date range -- this saves us a tiny bit of processing later
			if (parsedAttributeValues.size() != 2)
				doValidateDateRangeOrder = Boolean.FALSE;
			
			// Use integer to iterate since we need to track which field we're looking at
			for (int i=0;i<parsedAttributeValues.size();i++) {
				String parsedAttributeValue = parsedAttributeValues.get(i);
				
				ConstraintValidationResult constraintValidationResult = doProcessValidCharConstraint(validCharsConstraint, parsedAttributeValue);
		
				// If this is an error then some non-null validation result will be returned
				if (constraintValidationResult != null) {
					// Another strange KNS thing -- if the validation fails (not sure why only in that case) then some further error checking is done using the formatter, if one exists
					if (formatterClass == null) {
    					String formatterClassName = definition.getFormatterClass();
    					if (formatterClassName != null)
    						formatterClass = ClassLoaderUtils.getClass(formatterClassName);
					}
					
					if (formatterClass != null) {
						// Use the Boolean value being null to ensure we only do this once
						if (doValidateDateRangeOrder == null) {
							// We only want to validate a date range if we're dealing with something that has a date formatter on it and that looks like an actual range (is made up of 2 values with a between operator between them)
    						doValidateDateRangeOrder = Boolean.valueOf(DateFormatter.class.isAssignableFrom(formatterClass) && StringUtils.contains(ValidatorUtils.getString(value), LogicalOperators.BETWEEN_OPERATOR)); 
						}
						
						constraintValidationResult = processFormatterValidation(result, formatterClass, entryName, attributeName, parsedAttributeValue, DATE_RANGE_ERROR_PREFIXES[i]);
						
						if (constraintValidationResult != null) {
							result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
							return constraintValidationResult;
						}
					} else {
						// Otherwise, just report the validation result (apparently the formatter can't provide any fall-through validation because it doesn't exist)
						result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
						return constraintValidationResult;
					}
				}
			}
			
	    	if (doValidateDateRangeOrder != null && doValidateDateRangeOrder.booleanValue()) {
	    		ConstraintValidationResult dateOrderValidationResult = validateDateOrder(parsedAttributeValues.get(0), parsedAttributeValues.get(1), entryName, attributeName);
	    		
	    		if (dateOrderValidationResult != null) {
	    			result.addConstraintValidationResult(attributeValueReader, dateOrderValidationResult);
					return dateOrderValidationResult;
	    		}
	    	}
	    	
	    	return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
		}
		return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
    }
	
    protected ConstraintValidationResult doProcessValidCharConstraint(ValidCharactersConstraint validCharsConstraint, Object value) {

        StringBuilder fieldValue = new StringBuilder();
        String validChars = validCharsConstraint.getValue();

        fieldValue.append(ValidatorUtils.getString(value));

        int typIdx = validChars.indexOf(":");
        String processorType = "regex";
        if (-1 == typIdx) {
            validChars = "[" + validChars + "]*";
        } else {
            processorType = validChars.substring(0, typIdx);
            validChars = validChars.substring(typIdx + 1);
        }

        if ("regex".equalsIgnoreCase(processorType) && !validChars.equals(".*")) {
            if (!fieldValue.toString().matches(validChars)) {
            	ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
            	if (validCharsConstraint.getLabelKey() != null) {
            		// FIXME: This shouldn't surface label key itself to the user - it should look up the label key, but this needs to be implemented in Rice
            		constraintValidationResult.setError(RiceKeyConstants.ERROR_CUSTOM, validCharsConstraint.getLabelKey());
            		return constraintValidationResult;
            	} 
            	
            	constraintValidationResult.setError(RiceKeyConstants.ERROR_INVALID_FORMAT, fieldValue.toString());
            	return constraintValidationResult;
            }
        }
        
        return null;
    }

    protected ConstraintValidationResult processFormatterValidation(DictionaryValidationResult result, Class<?> formatterClass, String entryName, String attributeName, String parsedAttributeValue, String errorKeyPrefix) {
    	
    	boolean isError = false;
    	
    	try {
    		Method validatorMethod = formatterClass.getDeclaredMethod(VALIDATE_METHOD, new Class<?>[] {String.class});
    		Object o = validatorMethod.invoke(formatterClass.newInstance(), parsedAttributeValue);
    		if (o instanceof Boolean) {
    			isError = !((Boolean)o).booleanValue();
    		}
    	} catch (Exception e) {
    		if ( LOG.isDebugEnabled() ) 
    			LOG.debug(e.getMessage(), e);

    		isError = true;
    	}

    	if (isError) {
    		String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
    		String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);
    		
    		ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
			constraintValidationResult.setEntryName(entryName);
			constraintValidationResult.setAttributeName(errorKeyPrefix + attributeName);
			constraintValidationResult.setError(errorMessageKey, errorMessageParameters);
    		
			return constraintValidationResult;
    	}
			
		return null;
    }
    
	protected ConstraintValidationResult validateDateOrder(String firstDateTime, String secondDateTime, String entryName, String attributeName) {
		// this means that we only have 2 values and it's a date range.
		java.sql.Timestamp lVal = null;
		java.sql.Timestamp uVal = null;
		try {
			lVal = KNSServiceLocator.getDateTimeService().convertToSqlTimestamp(firstDateTime);
			uVal = KNSServiceLocator.getDateTimeService().convertToSqlTimestamp(secondDateTime);
		} catch (Exception ex){
			// this shouldn't happen because the tests passed above.
			String errorMessageKey = KNSServiceLocator.getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
			String[] errorMessageParameters = KNSServiceLocator.getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);
			ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
			constraintValidationResult.setEntryName(entryName);
			constraintValidationResult.setAttributeName(KNSConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + attributeName);
			constraintValidationResult.setError(errorMessageKey, errorMessageParameters);
			return constraintValidationResult;
		}

		if (lVal != null && lVal.compareTo(uVal) > 0){ // check the bounds
			String errorMessageKey = KNSServiceLocator.getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
			String[] errorMessageParameters = KNSServiceLocator.getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);
			ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
			constraintValidationResult.setEntryName(entryName);
			constraintValidationResult.setAttributeName(KNSConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + attributeName);
			constraintValidationResult.setError(errorMessageKey + ".range", errorMessageParameters);
			return constraintValidationResult;
		}
		
		return null;
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
