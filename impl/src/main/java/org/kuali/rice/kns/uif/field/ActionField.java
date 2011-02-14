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
package org.kuali.rice.kns.uif.field;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.View;

/**
 * Field that presents an action that can be taken on the UI such as submitting
 * the form or invoking a script
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionField extends FieldBase {
	private static final long serialVersionUID = 1025672792657238829L;

	private String methodToCall;
	private String navigateToPageId;

	private boolean clientSideCall;

	private String actionLabel;

	private Map<String, String> actionParameters;

	public ActionField() {
		clientSideCall = false;

		actionParameters = new HashMap<String, String>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the actionLabel if blank to the Field label</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (StringUtils.isBlank(actionLabel)) {
			actionLabel = this.getLabel();
		}
	}

	/**
	 * Add methodToCall action parameter if set and setup event code for client
	 * side method calls
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performFinalize(View view, Object model) {
		super.performFinalize(view, model);

		if (!actionParameters.containsKey(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME)
				&& StringUtils.isNotBlank(methodToCall) && !clientSideCall) {
			actionParameters.put(UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME, methodToCall);
		}
	}

	public String getMethodToCall() {
		return this.methodToCall;
	}

	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

	public boolean isClientSideCall() {
		return this.clientSideCall;
	}

	public void setClientSideCall(boolean clientSideCall) {
		this.clientSideCall = clientSideCall;
	}

	/**
	 * Builds up the client side JavaScript code for calling the action's method
	 * 
	 * @return String JavaScript handing code
	 */
	public String getClientSideEventCode() {
		String eventCode = "";

		// TODO: need client side method parameters once el support is in
		if (clientSideCall) {
			eventCode = methodToCall + "();";
		}

		return eventCode;
	}

	public String getActionLabel() {
		return this.actionLabel;
	}

	public void setActionLabel(String actionLabel) {
		this.actionLabel = actionLabel;
	}

	public String getNavigateToPageId() {
		return this.navigateToPageId;
	}

	public void setNavigateToPageId(String navigateToPageId) {
		this.navigateToPageId = navigateToPageId;
	}

	/**
	 * Parameters that should be sent when the action is invoked
	 * 
	 * <p>
	 * Action renderer will decide how the parameters are sent for the action
	 * (via script generated hiddens, or script parameters, ...)
	 * </p>
	 * 
	 * <p>
	 * Can be set by other components such as the <code>CollectionGroup</code>
	 * to provide the context the action is in (such as the collection name and
	 * line the action applies to)
	 * </p>
	 * 
	 * @return Map<String, String> action parameters
	 */
	public Map<String, String> getActionParameters() {
		return this.actionParameters;
	}

	/**
	 * Setter for the action parameters
	 * 
	 * @param actionParameters
	 */
	public void setActionParameters(Map<String, String> actionParameters) {
		this.actionParameters = actionParameters;
	}

	/**
	 * Convenience method to add a parameter to the action parameters Map
	 * 
	 * @param parameterName
	 *            - name of parameter to add
	 * @param parameterValue
	 *            - value of parameter to add
	 */
	public void addActionParameter(String parameterName, String parameterValue) {
		if (actionParameters == null) {
			this.actionParameters = new HashMap<String, String>();
		}

		this.actionParameters.put(parameterName, parameterValue);
	}

}
