/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.framework.postprocessor;

import org.kuali.rice.kew.api.action.ActionTaken;

/**
 * Event sent to the postprocessor when an action has been taken
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionTakenEvent implements IDocumentEvent {

	private static final long serialVersionUID = 2945081851810845704L;
	private String documentId;
	private String appDocId;
	private ActionTaken actionTaken;

	public ActionTakenEvent(String documentId, String appDocId, ActionTaken actionTaken) {
		this.documentId = documentId;
		this.appDocId = appDocId;
		this.actionTaken = actionTaken;
	}

	public String getDocumentEventCode() {
		return ACTION_TAKEN;
	}

	public String getDocumentId() {
		return documentId;
	}

	public ActionTaken getActionTaken() {
		return actionTaken;
	}

	public String getAppDocId() {
		return appDocId;
	}

}
