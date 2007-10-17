/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of results from validation of a field of data.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ValidationResults {

	public static final String GLOBAL = "edu.iu.uis.eden.validation.ValidationResults.GLOBAL";
	
	private List validationResults = new ArrayList();
	
	public void addValidationResult(String errorMessage) {
		addValidationResult(GLOBAL, errorMessage);
	}
	
	public void addValidationResult(String fieldName, String errorMessage) {
		validationResults.add(new ValidationResult(fieldName, errorMessage));
	}
	
	public List getValidationResults() {
		return validationResults;
	}
	
}
