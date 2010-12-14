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
package org.kuali.rice.kew.notes;

import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kns.UserSession;


/**
 * Default implementation of the {@link CustomNoteAttribute}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomNoteAttributeImpl implements CustomNoteAttribute {

    private RouteHeaderDTO routeHeaderVO;
    private UserSession userSession;

    @Override
	public boolean isAuthorizedToAddNotes() throws Exception {
        return true;
    }

    /**
     * By default the individual who authored the note is the only one allowed to edit it.
     */
    @Override
	public boolean isAuthorizedToEditNote(Note note) throws Exception {
    	return note.getNoteAuthorWorkflowId().equalsIgnoreCase(userSession.getPrincipalId());
    }

    @Override
	public RouteHeaderDTO getRouteHeaderVO() {
        return routeHeaderVO;
    }

    @Override
	public void setRouteHeaderVO(RouteHeaderDTO routeHeaderVO) {
        this.routeHeaderVO = routeHeaderVO;
    }

	@Override
	public UserSession getUserSession() {
		return userSession;
	}

	@Override
	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}


}
