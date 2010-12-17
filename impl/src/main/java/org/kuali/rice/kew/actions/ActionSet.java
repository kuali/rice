/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.util.KEWConstants;


/**
 * Specifies a set of Action codes.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionSet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7857749268529671300L;
	private List<String> actionSet = new ArrayList<String>();
	
	public boolean hasAction(String actionCode) {
		return actionSet.contains(actionCode);
	}
	
	public boolean addAction(String actionCode) {
		if (!actionSet.contains(actionCode)) {
			actionSet.add(actionCode);
			return true;
		}
		return false;
	}
	
	public boolean removeAction(String actionCode) {
		return actionSet.remove(actionCode);
	}
	
	// some convienance methods for common actions
	
	public boolean hasApprove() {
		return hasAction(KEWConstants.ACTION_TAKEN_APPROVED_CD);
	}
	
	public boolean hasComplete() {
		return hasAction(KEWConstants.ACTION_TAKEN_COMPLETED_CD);
	}
	
	public boolean hasAcknowledge() {
		return hasAction(KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD);
	}
	
	public boolean hasFyi() {
		return hasAction(KEWConstants.ACTION_TAKEN_FYI_CD);
	}
	
	public boolean hasDisapprove() {
		return hasAction(KEWConstants.ACTION_TAKEN_DENIED_CD);
	}
	
	public boolean hasCancel() {
		return hasAction(KEWConstants.ACTION_TAKEN_CANCELED_CD);
	}

    public boolean hasRouted() {
        return hasAction(KEWConstants.ACTION_TAKEN_ROUTED_CD);
    }

	public boolean addApprove() {
		return addAction(KEWConstants.ACTION_TAKEN_APPROVED_CD);
	}
	
	public boolean addComplete() {
		return addAction(KEWConstants.ACTION_TAKEN_COMPLETED_CD);
	}
	
	public boolean addAcknowledge() {
		return addAction(KEWConstants.ACTION_TAKEN_ACKNOWLEDGED_CD);
	}
	
	public boolean addFyi() {
		return addAction(KEWConstants.ACTION_TAKEN_FYI_CD);
	}
	
	public boolean addDisapprove() {
		return addAction(KEWConstants.ACTION_TAKEN_DENIED_CD);
	}
	
	public boolean addCancel() {
		return addAction(KEWConstants.ACTION_TAKEN_CANCELED_CD);
	}

    public boolean addRouted() {
        return addAction(KEWConstants.ACTION_TAKEN_ROUTED_CD);
    }
}
