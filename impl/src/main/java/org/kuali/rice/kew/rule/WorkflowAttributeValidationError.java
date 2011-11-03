/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule;

import java.io.Serializable;

import org.kuali.rice.krad.util.MessageMap;


/**
 * An error returned from the validation of a {@link WorkflowRuleAttribute}.
 * Returned by a call to {@link WorkflowAttributeXmlValidator#validateClientRoutingData()}
 * and {@link org.kuali.rice.kew.framework.document.attribute.SearchableAttribute#validateSearchFieldParameters(org.kuali.rice.kew.api.extension.ExtensionDefinition, java.util.Map, String)}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkflowAttributeValidationError implements Serializable {

	private static final long serialVersionUID = 6785629049454272657L;

	private MessageMap messageMap;

	private String key;
	private String message;

	public WorkflowAttributeValidationError(String key, String message) {
		this.key = key;
		this.message = message;
	}

	public WorkflowAttributeValidationError(String key, String message, MessageMap messageMap) {
		this.key = key;
		this.message = message;
		this.messageMap = messageMap;
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

	/**
	 * @return the messageMap
	 */
	public MessageMap getMessageMap() {
		return this.messageMap;
	}

	/**
	 * @param messageMap the messageMap to set
	 */
	public void setMessageMap(MessageMap messageMap) {
		this.messageMap = messageMap;
	}

	public static org.kuali.rice.kew.api.document.attribute.WorkflowAttributeValidationError to(WorkflowAttributeValidationError validationError) {
	    if (validationError == null) {
	        return null;
	    }
	    return org.kuali.rice.kew.api.document.attribute.WorkflowAttributeValidationError
                .create(validationError.getKey(), validationError.getMessage());
	}

}
