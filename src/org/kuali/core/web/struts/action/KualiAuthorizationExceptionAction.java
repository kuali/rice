/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.Constants;
import org.kuali.core.exceptions.AuthorizationException;


/**
 * Retrieves and populates error messages during exception-handling
 */
public class KualiAuthorizationExceptionAction extends Action {
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = mapping.findForward(Constants.MAPPING_BASIC);

        String messageKey = null;
        AuthorizationException e = (AuthorizationException) request.getAttribute(Globals.EXCEPTION_KEY);

        ActionMessage errorMessage = new ActionMessage(e.getErrorMessageKey(), e.getUserId(), e.getAction(), e.getTargetType());
        ActionMessages errors = new ActionMessages();
        errors.add(ActionMessages.GLOBAL_MESSAGE, errorMessage);
        saveErrors(request, errors);


        return returnForward;
    }
}