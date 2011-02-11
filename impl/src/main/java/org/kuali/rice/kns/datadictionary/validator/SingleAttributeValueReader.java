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
package org.kuali.rice.kns.datadictionary.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;


/**
 * This class allows a single attribute value to be exposed to the validation service, along 
 * with some guidance about how that value should be interpreted, provided by the AttributeDefinition
 * that corresponds. It's a special AttributeValueReader since it explicitly doesn't expose any
 * other attribute values, so it should only be used when the underlying business object is not available
 * and we want to limit access to (for example) validation that requires only a single attribute value. 
 * This eliminates more complicated validation like 'this field is required when another field is filled in.'
 * 
 * @author James Renfro, University of Washington 
 */
public class SingleAttributeValueReader extends BaseAttributeValueReader {

	private Object value;
	private AttributeDefinition definition;
	
	public SingleAttributeValueReader(Object value, String entryName, String attributeName, AttributeDefinition definition) {
		this.value = value;
		this.entryName = entryName;
		this.attributeName = attributeName;
		this.definition = definition;
	}
	
	@Override
	public Validatable getDefinition(String attributeName) {
		// Only return the definition if you have it, and if it's the definition for the passed attribute name
		return definition != null && definition.getName() != null && definition.getName().equals(attributeName) ? definition : null;
	}
	
	@Override
	public List<Validatable> getDefinitions() {
		return null;
	}
	

	@Override
	public DataDictionaryEntry getEntry() {
		return null;
	}

	@Override
	public String getLabel(String attributeName) {
		if (definition != null && definition.getName() != null && definition.getName().equals(attributeName))
			return definition.getLabel();
		
		return attributeName;
	}
	
	@Override
	public String getPath() {
		DataDictionaryEntry dictionaryEntry = getEntry();
		return dictionaryEntry != null ? dictionaryEntry.getFullClassName() : "";
	}

	@Override
	public Class<?> getType(String attributeName) {
		Validatable attributeDefinition = getDefinition(attributeName);
		
		if (attributeDefinition != null && attributeDefinition.getDataType() != null)
			return attributeDefinition.getDataType().getType();
		
		// Assuming we can reliably guess
		return value != null ? value.getClass() : null;
	}

	@Override
	public <X> X getValue() throws AttributeValidationException {
		return (X) value;
	}
	
	@Override
	public <X> X getValue(String attributeName) throws AttributeValidationException {
		Validatable attributeDefinition = getDefinition(attributeName);
		
		if (attributeDefinition != null)
			return (X) value;
		
		return null;
	}

}
