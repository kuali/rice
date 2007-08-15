/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.messaging.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.kuali.bus.auth.AuthorizationService;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;

/**
 * A RequestProcessor implementation for Struts which handles determining whether or not access
 * should be allowed to the requested KSB page.
 *
 * @author Eric Westfall
 */
public class KSBStrutsRequestProcessor extends RequestProcessor {

    @Override
    protected ActionForward processActionPerform(HttpServletRequest request, HttpServletResponse response, Action action, ActionForm form, ActionMapping mapping) throws IOException, ServletException {
	if (!isAdministrator()) {
	    return mapping.findForward("NotAuthorized");
	}
	return super.processActionPerform(request, response, action, form, mapping);
    }

    private boolean isAdministrator() {
	AuthorizationService authService = (AuthorizationService)Core.getCurrentContextConfig().getObject(RiceConstants.KSB_AUTH_SERVICE);
	// if no auth service is defined then everyone's an admin
	return authService == null || authService.isAdministrator();
    }

}
