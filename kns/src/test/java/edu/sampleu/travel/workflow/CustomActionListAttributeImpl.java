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
package edu.sampleu.travel.workflow;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.DisplayParameters;
import edu.iu.uis.eden.actions.ActionSet;
import edu.iu.uis.eden.plugin.attributes.CustomActionListAttribute;
import edu.iu.uis.eden.web.session.UserSession;

public class CustomActionListAttributeImpl implements CustomActionListAttribute {

	private static final long serialVersionUID = 6129615406164385616L;

	public DisplayParameters getDocHandlerDisplayParameters(UserSession userSession, ActionItem actionItem) throws Exception {
		return new DisplayParameters(new Integer(400));
	}

	public ActionSet getLegalActions(UserSession userSession, ActionItem actionItem) throws Exception {
		ActionSet actionSet = new ActionSet();
		actionSet.addAcknowledge();
		actionSet.addApprove();
		actionSet.addFyi();
		actionSet.addComplete();
		return actionSet;
	}

}
