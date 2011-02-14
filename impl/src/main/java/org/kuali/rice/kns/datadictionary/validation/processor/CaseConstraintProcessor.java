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

import java.util.List;

import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.CaseConstraint;
import org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor;
import org.kuali.rice.kns.datadictionary.validation.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validation.DataType;
import org.kuali.rice.kns.datadictionary.validation.DateParser;
import org.kuali.rice.kns.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.kns.datadictionary.validation.DictionaryValidationResult;
import org.kuali.rice.kns.datadictionary.validation.ServerDateParser;
import org.kuali.rice.kns.datadictionary.validation.ValidatorUtils;
import org.kuali.rice.kns.datadictionary.validation.WhenConstraint;
import org.kuali.rice.kns.datadictionary.validation.capability.CaseConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.HierarchicallyConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class CaseConstraintProcessor extends MandatoryElementConstraintProcessor<CaseConstrained> {

	private static final String CONSTRAINT_NAME = "case constraint";
	private DataDictionaryService dataDictionaryService;
	
	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validation.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(DictionaryValidationResult result, Object value, CaseConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		CaseConstraint caseConstraint = definition.getCaseConstraint();
		
		// Assume that we're going to return the current definition and attributeValueReader until we decide otherwise (below). 
		ConstraintValidationResult constraintValidationResult = null; // new ConstraintValidationResult(definition, attributeValueReader);
		
		// Don't process this constraint if it's null
        if (null == caseConstraint) {
        	constraintValidationResult = result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);
        	constraintValidationResult.setTransients(definition, attributeValueReader);
            return constraintValidationResult;
        }

        String operator = (ValidatorUtils.hasText(caseConstraint.getOperator())) ? caseConstraint.getOperator() : "EQUALS";
        AttributeValueReader nestedReader = (ValidatorUtils.hasText(caseConstraint.getFieldPath())) ? getChildAttributeValueReader(caseConstraint.getFieldPath(), attributeValueReader) : null;

        // TODO: What happens when the field is not in the dataProvider?
        Validatable caseField = (null != nestedReader) ? nestedReader.getDefinition(nestedReader.getAttributeName()) : null;
        Object fieldValue = (null != nestedReader) ? nestedReader.getValue(nestedReader.getAttributeName()) : value;
        DataType fieldDataType = (null != caseField ? caseField.getDataType():null);

        // If fieldValue is null then skip Case check
        if (null == fieldValue) {
        	constraintValidationResult = result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
        	constraintValidationResult.setTransients(caseField, nestedReader);
            return constraintValidationResult;
        }

        DateParser dateParser = new ServerDateParser();
        // Extract value for field Key
        for (WhenConstraint wc : caseConstraint.getWhenConstraint()) {

        	List<Object> whenValueList = wc.getValues();

        	for (Object whenValue : whenValueList) {
        		if (ValidatorUtils.compareValues(fieldValue, whenValue, fieldDataType, operator, caseConstraint.isCaseSensitive(), dateParser) && null != wc.getConstraint()) {
        			constraintValidationResult = result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
                	constraintValidationResult.setTransients(wc.getConstraint(), nestedReader);
                    return constraintValidationResult;
        		}
        	}
        }

        // Assuming that not finding any case constraints is equivalent to 'skipping' the constraint
        constraintValidationResult = result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
    	constraintValidationResult.setTransients(caseField, nestedReader);
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
	public Class<CaseConstrained> getType() {
		return CaseConstrained.class;
	}
	
	private AttributeValueReader getChildAttributeValueReader(String key, AttributeValueReader attributeValueReader) throws AttributeValidationException {
		String[] lookupPathTokens = ValidatorUtils.getPathTokens(key);
		
		AttributeValueReader localAttributeValueReader = attributeValueReader;
		for(int i = 0; i < lookupPathTokens.length; i++) {
			for (Validatable definition : localAttributeValueReader.getDefinitions()) {
				String attributeName = definition.getName();
				if (attributeName.equals(lookupPathTokens[i])) {
					if(i==lookupPathTokens.length-1){
						localAttributeValueReader.setAttributeName(attributeName);
						return localAttributeValueReader;
					}
					if (definition instanceof HierarchicallyConstrained) {
						String childEntryName = ((HierarchicallyConstrained)definition).getChildEntryName();
						DataDictionaryEntry entry = KNSServiceLocator.getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(childEntryName);
						Object value = attributeValueReader.getValue(attributeName);
						attributeValueReader.setAttributeName(attributeName);
						String attributePath = attributeValueReader.getPath();
						localAttributeValueReader = new DictionaryObjectAttributeValueReader(value, childEntryName, entry, attributePath);
					} 
					break;
				}
			}
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
