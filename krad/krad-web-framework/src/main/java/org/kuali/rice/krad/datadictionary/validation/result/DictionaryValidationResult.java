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
package org.kuali.rice.krad.datadictionary.validation.result;

import org.kuali.rice.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.ErrorLevel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public class DictionaryValidationResult {
	
	private Map<String, EntryValidationResult> entryValidationResultMap;
	private ErrorLevel errorLevel;
	
	private int numberOfErrors;
	private int numberOfWarnings;
	
	private Iterator<ConstraintValidationResult> iterator;
	
	public DictionaryValidationResult() {
		this.entryValidationResultMap = new LinkedHashMap<String, EntryValidationResult>();
		this.errorLevel = ErrorLevel.ERROR;
		this.numberOfErrors = 0;
		this.numberOfWarnings = 0;
	}
	
	public void addConstraintValidationResult(AttributeValueReader attributeValueReader, ConstraintValidationResult constraintValidationResult) {
		
		// Don't bother to store this if the error level of the constraint validation result is lower than the level this dictionary validation result is tracking
		if (constraintValidationResult.getStatus().getLevel() < errorLevel.getLevel())
			return;
		
		switch (constraintValidationResult.getStatus()) {
		case ERROR:
			numberOfErrors++;
			break;
		case WARN:
			numberOfWarnings++;
			break;
		default:
			// Do nothing
		}
		
		// Give the constraint a chance to override the entry and attribute name - important if the attribute name is not the same as the one in the attribute value reader!
		String entryName = constraintValidationResult.getEntryName();
		String attributeName = constraintValidationResult.getAttributeName();
		
		if (entryName == null)
			entryName = attributeValueReader.getEntryName();
		
		if (attributeName == null)
			attributeName = attributeValueReader.getAttributeName();
		
		constraintValidationResult.setEntryName(entryName);
		constraintValidationResult.setAttributeName(attributeName);
		
		getEntryValidationResult(entryName).getAttributeValidationResult(attributeName).addConstraintValidationResult(constraintValidationResult);
	}
	
	public ConstraintValidationResult addError(AttributeValueReader attributeValueReader, String constraintName, String errorKey, String... errorParameters) {
		ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
		constraintValidationResult.setError(errorKey, errorParameters);
		numberOfErrors++;
		return constraintValidationResult;
	}
	
	public ConstraintValidationResult addWarning(AttributeValueReader attributeValueReader, String constraintName, String errorKey, String... errorParameters) {
		if (errorLevel.getLevel() > ErrorLevel.WARN.getLevel())
			return new ConstraintValidationResult(constraintName, ErrorLevel.WARN);
		
		ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
		constraintValidationResult.setWarning(errorKey, errorParameters);
		numberOfWarnings++;
		return constraintValidationResult;
	}

	public ConstraintValidationResult addSuccess(AttributeValueReader attributeValueReader, String constraintName) {
		if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
			return new ConstraintValidationResult(constraintName, ErrorLevel.OK);
		
		return getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
	}
	
	public ConstraintValidationResult addSkipped(AttributeValueReader attributeValueReader, String constraintName) {
		if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
			return new ConstraintValidationResult(constraintName, ErrorLevel.INAPPLICABLE);
		
		ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
		constraintValidationResult.setStatus(ErrorLevel.INAPPLICABLE);
		return constraintValidationResult;
	}
	
	public ConstraintValidationResult addNoConstraint(AttributeValueReader attributeValueReader, String constraintName) {
		if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
			return new ConstraintValidationResult(constraintName, ErrorLevel.NOCONSTRAINT);
		
		ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
		constraintValidationResult.setStatus(ErrorLevel.NOCONSTRAINT);
		return constraintValidationResult;
	}
	
	public Iterator<ConstraintValidationResult> iterator() {
		
//		if (iterator == null || iterator.hasNext() == false) {
			iterator = new Iterator<ConstraintValidationResult>() {
	
				private Iterator<EntryValidationResult> entryIterator;
				private Iterator<AttributeValidationResult> attributeIterator;
				private Iterator<ConstraintValidationResult> constraintIterator;
				
				@Override
				public boolean hasNext() {	
					Iterator<ConstraintValidationResult> currentConstraintIterator = getCurrentConstraintIterator();
					return currentConstraintIterator != null && currentConstraintIterator.hasNext();
				}
	
				@Override
				public ConstraintValidationResult next() {
					Iterator<ConstraintValidationResult> currentConstraintIterator = getCurrentConstraintIterator();
					return currentConstraintIterator != null ? currentConstraintIterator.next() : null;
				}
	
				@Override
				public void remove() {
					throw new RuntimeException("Can't remove from this iterator!");
				}
				
				private Iterator<ConstraintValidationResult> getCurrentConstraintIterator() {
					if (constraintIterator == null || constraintIterator.hasNext() == false) {
						Iterator<AttributeValidationResult> currentAttributeIterator = getCurrentAttributeIterator();
						if (currentAttributeIterator != null && currentAttributeIterator.hasNext()) {
							AttributeValidationResult currentAttributeValidationResult = currentAttributeIterator.next();
							constraintIterator = currentAttributeValidationResult.iterator();
						}
					}
					return constraintIterator;
				}
				
				private Iterator<AttributeValidationResult> getCurrentAttributeIterator() {
					if (attributeIterator == null || attributeIterator.hasNext() == false) {
						Iterator<EntryValidationResult> currentEntryIterator = getCurrentEntryIterator();
						if (currentEntryIterator != null && currentEntryIterator.hasNext()) {
							EntryValidationResult currentEntryValidationResult = currentEntryIterator.next();
							attributeIterator = currentEntryValidationResult.iterator();
						}
					}
					return attributeIterator;
				}
	
				private Iterator<EntryValidationResult> getCurrentEntryIterator() {
					if (entryIterator == null) // || entryIterator.hasNext() == false)
						entryIterator = entryValidationResultMap.values().iterator();
					return entryIterator;
				}
				
			};
//		}
		
		return iterator;
	}
	
	protected EntryValidationResult getEntryValidationResult(String entryName) {
		EntryValidationResult entryValidationResult = entryValidationResultMap.get(entryName);
		if (entryValidationResult == null) {
			entryValidationResult = new EntryValidationResult(entryName);
			entryValidationResultMap.put(entryName, entryValidationResult);
		}
		return entryValidationResult;
	}
	
	private ConstraintValidationResult getConstraintValidationResult(String entryName, String attributeName, String attributePath, String constraintName) {
		ConstraintValidationResult constraintValidationResult = getEntryValidationResult(entryName).getAttributeValidationResult(attributeName).getConstraintValidationResult(constraintName);
		constraintValidationResult.setEntryName(entryName);
		constraintValidationResult.setAttributeName(attributeName);
		constraintValidationResult.setAttributePath(attributePath);
		return constraintValidationResult;
	}

	/**
	 * @return the errorLevel
	 */
	public ErrorLevel getErrorLevel() {
		return this.errorLevel;
	}

	/**
	 * @param errorLevel the errorLevel to set
	 */
	public void setErrorLevel(ErrorLevel errorLevel) {
		this.errorLevel = errorLevel;
	}

	/**
	 * @return the numberOfErrors
	 */
	public int getNumberOfErrors() {
		return this.numberOfErrors;
	}

	/**
	 * @return the numberOfWarnings
	 */
	public int getNumberOfWarnings() {
		return this.numberOfWarnings;
	}
	
}
