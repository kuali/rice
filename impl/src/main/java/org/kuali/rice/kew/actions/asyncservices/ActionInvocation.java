/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actions.asyncservices;

import java.io.Serializable;


/**
 * Used for mass actions from the action list.
 * Represents the definition of an Action invocation against an action item.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ActionInvocation implements Serializable {

	private static final long serialVersionUID = -3828610998370758705L;
	private final Long actionItemId;
	private final String actionCode;
	
	public ActionInvocation(Long actionItemId, String actionCode) {
		if (actionItemId == null) {
			throw new IllegalArgumentException("Action Item Id cannot be null.");
		}
		if (org.apache.commons.lang.StringUtils.isEmpty(actionCode)) {
			throw new IllegalArgumentException("Action Code cannot be empty.");
		}
		this.actionItemId = actionItemId;
		this.actionCode = actionCode;
	}
		
	public String getActionCode() {
		return actionCode;
	}
	
	public Long getActionItemId() {
		return actionItemId;
	}
		
}
