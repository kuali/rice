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
package edu.iu.uis.eden.actionlist;

import java.io.Serializable;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actions.ActionSet;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.notes.CustomNoteAttribute;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.plugin.attributes.CustomActionListAttribute;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.web.session.UserSession;

public class TestCustomActionList implements CustomActionListAttribute, Serializable, CustomEmailAttribute, CustomNoteAttribute {

	private static final long serialVersionUID = -7212208304658959134L;

	private RouteHeaderVO routeHeaderVO;
    private ActionRequestVO actionRequestVO;
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

	public ActionSet getLegalActions(UserSession userSession, ActionItem actionItem) throws Exception {
		ActionSet actionSet = new ActionSet();
		actionSet.addAcknowledge();
		actionSet.addApprove();
		actionSet.addCancel();
		actionSet.addDisapprove();
		return actionSet;
	}
    
    public DisplayParameters getDocHandlerDisplayParameters(UserSession userSession, ActionItem actionItem) throws Exception {
		return new DisplayParameters(new Integer(300));
	}
    
    public RouteHeaderVO getRouteHeaderVO() {
        return routeHeaderVO;
    }

    public void setRouteHeaderVO(RouteHeaderVO routeHeaderVO) {
        this.routeHeaderVO = routeHeaderVO;
    }

	public ActionRequestVO getActionRequestVO() {
		return actionRequestVO;
	}

	public void setActionRequestVO(ActionRequestVO actionRequestVO) {
		this.actionRequestVO = actionRequestVO;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}
    
    
}
