/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Stores context for validation.  This is essentially a Map of parameters
 * to the validation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ValidationContext implements java.io.Serializable {

	private static final long serialVersionUID = 824339582785034514L;

	private Map<String, Object> parameters = new HashMap<String, Object>();

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}
