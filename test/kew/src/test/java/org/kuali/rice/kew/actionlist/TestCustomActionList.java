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
package org.kuali.rice.kew.actionlist;

import java.io.Serializable;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.ActionSet;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.mail.CustomEmailAttribute;
import org.kuali.rice.kew.notes.CustomNoteAttribute;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kns.UserSession;


public class TestCustomActionList implements CustomActionListAttribute, Serializable, CustomEmailAttribute, CustomNoteAttribute {

	private static final long serialVersionUID = -7212208304658959134L;

	private RouteHeaderDTO routeHeaderVO;
    private ActionRequestDTO actionRequestVO;
    private UserSession userSession;
    
    public TestCustomActionList() {}
    
    public boolean isAuthorizedToAddNotes() throws Exception {
        return true;
    }

    public boolean isAuthorizedToEditNote(Note note) throws Exception {
        return true;
    }
    
    public String getCustomEmailBody() throws Exception {
        return "This is a test.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body." +
        "  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body." +
        "  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.  This is a Customized Email Body.";
    }

    public String getCustomEmailSubject() throws Exception {
        return "Customized Email Subject";
    }

	public ActionSet getLegalActions(String principalId, ActionItem actionItem) throws Exception {
		ActionSet actionSet = new ActionSet();
		actionSet.addAcknowledge();
		actionSet.addApprove();
		actionSet.addCancel();
		actionSet.addDisapprove();
		return actionSet;
	}
    
    public DisplayParameters getDocHandlerDisplayParameters(String principalId, ActionItem actionItem) throws Exception {
		return new DisplayParameters(new Integer(300));
	}
    
    public RouteHeaderDTO getRouteHeaderVO() {
        return routeHeaderVO;
    }

    public void setRouteHeaderVO(RouteHeaderDTO routeHeaderVO) {
        this.routeHeaderVO = routeHeaderVO;
    }

	public ActionRequestDTO getActionRequestVO() {
		return actionRequestVO;
	}

	public void setActionRequestVO(ActionRequestDTO actionRequestVO) {
		this.actionRequestVO = actionRequestVO;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
    
    
}
