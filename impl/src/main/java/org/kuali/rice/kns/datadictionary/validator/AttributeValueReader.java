/**
 * Copyright 2010 The Kuali Foundation Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.kuali.rice.kns.datadictionary.validator;

import java.util.List;

import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;

public interface AttributeValueReader {

	public String getAttributeName();
	
	public Validatable getDefinition(String attributeName);
	
	public List<Validatable> getDefinitions();
	
	public DataDictionaryEntry getEntry();
	
	public DataDictionaryEntry getEntry(String entryName);
	
	public String getEntryName();
	
	public String getLabel(String attributeName);
	
	public String getPath();
	
	public Class<?> getType(String attributeName);
	
	public <X> X getValue() throws AttributeValidationException;
	
	public <X> X getValue(String attributeName) throws AttributeValidationException;
	
	public List<String> getCleanSearchableValues(String attributeName) throws AttributeValidationException;
	
	public void setAttributeName(String attributeName);
	
	public void setEntryName(String entryName);
	
}

