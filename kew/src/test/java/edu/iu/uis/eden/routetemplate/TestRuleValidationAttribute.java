/*
 * Copyright 2007 The Kuali Foundation
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
 * A simple test implementation of a RuleValidationAttribute that can be used in the unit tests.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class TestRuleValidationAttribute implements RuleValidationAttribute {

    public ValidationResults validate(RuleValidationContext validationContext) throws Exception {
	return new ValidationResults();
    }

}
