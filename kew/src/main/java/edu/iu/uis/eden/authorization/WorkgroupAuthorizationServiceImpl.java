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
package edu.iu.uis.eden.authorization;

import javax.servlet.http.HttpServletRequest;

import org.kuali.bus.auth.AuthorizationService;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkgroupAuthorizationServiceImpl implements AuthorizationService {
    
    private String workgroupName;

    /**
     * This overridden method ...
     * 
     * @see org.kuali.bus.auth.AuthorizationService#isAdministrator(javax.servlet.http.HttpServletRequest)
     */
    public boolean isAdministrator(HttpServletRequest request) {
        UserSession userSession = UserSession.getAuthenticatedUser();
        if (userSession == null) {
            throw new RuntimeException("Could not determine authenticated user.  UserSession was null.");
        }
        if (userSession.getWorkflowUser() == null) {
            throw new RuntimeException("Could not determine authenticated user.  UserSession.getWorkflowUser was null.");
        }
        try {
            return KEWServiceLocator.getWorkgroupService().isUserMemberOfGroup(new GroupNameId(getWorkgroupName()), userSession.getWorkflowUser());
        } catch (EdenUserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getWorkgroupName() {
        return this.workgroupName;
    }

    public void setWorkgroupName(String workgroupName) {
        this.workgroupName = workgroupName;
    }

}
