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
package edu.iu.uis.eden.edl;


/**
 * Represents a User Action in eDoc Lite.  Also contains methods
 * for classifying the user action.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserAction {

    public static final String ACTION_CREATE = "initiate";
    public static final String ACTION_LOAD = "load";
    public static final String ACTION_UNDEFINED = "undefined";
    public static final String ACTION_ROUTE = "route";
    public static final String ACTION_APPROVE = "approve";
    public static final String ACTION_DISAPPROVE = "disapprove";
    public static final String ACTION_CANCEL = "cancel";
    public static final String ACTION_BLANKETAPPROVE = "blanketApprove";
    public static final String ACTION_FYI = "fyi";
    public static final String ACTION_ACKNOWLEDGE = "acknowledge";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_COMPLETE = "complete";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_RETURN_TO_PREVIOUS = "returnToPrevious";
    
    public static final String[] LOAD_ACTIONS = new String[] {
	ACTION_LOAD,
	ACTION_CREATE
    };
    
    public static final String[] REPLACE_VERSION_ACTIONS = new String[] {
	ACTION_UNDEFINED
    };
    
    public static final String[] VALIDATABLE_ACTIONS = new String[] {
	ACTION_SAVE,
	ACTION_ROUTE,
	ACTION_APPROVE,
	ACTION_ACKNOWLEDGE,
	ACTION_COMPLETE,
	ACTION_FYI,
	ACTION_DISAPPROVE,
	ACTION_RETURN_TO_PREVIOUS
    };
    
    public static final String[] ANNOTATABLE_ACTIONS = new String[] {
    	ACTION_APPROVE,
    	ACTION_ACKNOWLEDGE,
    	ACTION_COMPLETE,
    	ACTION_FYI,
    	ACTION_DISAPPROVE,
    	ACTION_CANCEL,
    	ACTION_RETURN_TO_PREVIOUS
    };
    
    public static final String[] EDITABLE_ACTIONS = new String[] { 
	ACTION_CREATE,
	ACTION_ROUTE,
	ACTION_APPROVE,
	ACTION_DISAPPROVE,
	ACTION_COMPLETE
    };
    
    private String action;
    
    public UserAction(String action) {
	this.action = action;
    }
    
    public String getAction() {
	return action;
    }
    
    public boolean isLoadAction() {
	return containsAction(LOAD_ACTIONS, action);
    }
    
    public boolean isIncrementVersionAction() {
	return !isLoadAction() && !isReplaceVersionAction();
    }
    
    public boolean isReplaceVersionAction() {
	return containsAction(REPLACE_VERSION_ACTIONS, action);
    }

    public boolean isValidatableAction() {
	return containsAction(VALIDATABLE_ACTIONS, action);
    }
    
    public boolean isAnnotatableAction() {
	return containsAction(ANNOTATABLE_ACTIONS, action);
    }
    
    public boolean isEditableAction() {
	return containsAction(EDITABLE_ACTIONS, action);
    }
    
    private boolean containsAction(String[] actions, String action) {
	for (int index = 0; index < actions.length; index++) {
	    if (actions[index].equals(action)) {
		return true;
	    }
	}
	return false;
    }
    
}
