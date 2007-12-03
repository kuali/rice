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
package edu.iu.uis.eden.routetemplate;

import edu.iu.uis.eden.validation.RuleValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;

/**
 * A simple interface for handling validation of rules.  Validation results are returned in a
 * ValidationResults object which consists of a series of error messages regarding the
 * rule.  The user who is adding or editing the rule is passed to validate as well as the
 * rule to be validated.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RuleValidationAttribute {
	
	/**
	 * Validates the rule within the given RuleValidationContext.
	 * 
	 * @return a ValidationResults object representing the results of the validation, if this is
	 * empty or <code>null</code> this signifies that validation was successful.
	 */
	public ValidationResults validate(RuleValidationContext validationContext) throws Exception;

}
