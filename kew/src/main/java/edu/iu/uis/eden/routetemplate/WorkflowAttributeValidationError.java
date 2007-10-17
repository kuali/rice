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

import java.io.Serializable;
import java.util.Map;

import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttributeXmlValidator;

/**
 * An error returned from the validation of a {@link WorkflowAttribute}.
 * Returned by a call to {@link WorkflowAttributeXmlValidator#validateClientRoutingData()}
 * and {@link SearchableAttribute#validateUserSearchInputs(Map)}
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowAttributeValidationError implements Serializable {

	private static final long serialVersionUID = 6785629049454272657L;

	private String key;
	private String message;
	
	public WorkflowAttributeValidationError(String key, String message) {
		this.key = key;
		this.message = message;
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	
}
