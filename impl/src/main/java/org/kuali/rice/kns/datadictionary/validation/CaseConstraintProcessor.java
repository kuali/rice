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

import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.CaseConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.HierarchicallyConstrained;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validator.AttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ConstraintValidationResult;
import org.kuali.rice.kns.datadictionary.validator.DateParser;
import org.kuali.rice.kns.datadictionary.validator.DictionaryObjectAttributeValueReader;
import org.kuali.rice.kns.datadictionary.validator.ServerDateParser;
import org.kuali.rice.kns.datadictionary.validator.ValidatorUtils;

/**
 * 
 * @author James Renfro, University of Washington 
 */
public class CaseConstraintProcessor extends MandatoryConstraintProcessor<CaseConstrained> {

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.ConstraintProcessor#process(org.kuali.rice.kns.datadictionary.validation.capability.Validatable, org.kuali.rice.kns.datadictionary.validator.AttributeValueReader)
	 */
	@Override
	public ConstraintValidationResult process(CaseConstrained definition, AttributeValueReader attributeValueReader) throws AttributeValidationException {

		CaseConstraint caseConstraint = definition.getCaseConstraint();
		
		// Assume that we're going to return the current definition and attributeValueReader until we decide otherwise (below). 
		ConstraintValidationResult result = new ConstraintValidationResult(definition, attributeValueReader);
		
		// Don't process this constraint if it's null
        if (null == caseConstraint) {
            return result;
        }

        Object value = attributeValueReader.getValue();


        String operator = (ValidatorUtils.hasText(caseConstraint.getOperator())) ? caseConstraint.getOperator() : "EQUALS";
        AttributeValueReader nestedReader = (ValidatorUtils.hasText(caseConstraint.getFieldPath())) ? getChildAttributeValueReader(caseConstraint.getFieldPath(), attributeValueReader) : null;

        result.setAttributeValueReader(nestedReader);

        // TODO: What happens when the field is not in the dataProvider?
        Validatable caseField = (null != nestedReader) ? nestedReader.getDefinition(nestedReader.getAttributeName()) : null;
        Object fieldValue = (null != nestedReader) ? nestedReader.getValue(nestedReader.getAttributeName()) : value;
        DataType fieldDataType = (null != caseField ? caseField.getDataType():null);

        // If fieldValue is null then skip Case check
        if(null == fieldValue) {
        	return result;
        }

        DateParser dateParser = new ServerDateParser();
        // Extract value for field Key
        for (WhenConstraint wc : caseConstraint.getWhenConstraint()) {

        	List<Object> whenValueList = wc.getValues();

        	for (Object whenValue : whenValueList) {
        		if (ValidatorUtils.compareValues(fieldValue, whenValue, fieldDataType, operator, caseConstraint.isCaseSensitive(), dateParser) && null != wc.getConstraint()) {
        			result.setDefinition(wc.getConstraint());
        			return result;
        		}
        	}
        }

        
        return result;
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
						DataDictionaryEntry entry = attributeValueReader.getEntry(childEntryName);
						Object value = attributeValueReader.getValue(attributeName);
						localAttributeValueReader = new DictionaryObjectAttributeValueReader(value, childEntryName, entry);
					} 
					break;
				}
			}
		 }
		return null;
	}

}
