/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.data.DataIntegrityService;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DataIntegrityAction extends KualiAction {

	/**
	 * To avoid having to go through the pain of setting up a KIM permission for "Use Screen" for this utility screen,
	 * we'll hardcode this screen to the "KR-SYS Technical Administrator" role. Without doing this, the screen is open
	 * to all users until that permission is setup which could be considered a security issue.
	 */
	protected void checkAuthorization( ActionForm form, String methodToCall) throws AuthorizationException
	{
		boolean authorized = false;
		String principalId = GlobalVariables.getUserSession().getPrincipalId();
		RoleService roleService = KimApiServiceLocator.getRoleService();
		String roleId = roleService.getRoleIdByNamespaceCodeAndName("KR-SYS", "Technical Administrator");
		if (roleId != null) {
			authorized = roleService.principalHasRole(principalId, Collections.singletonList(roleId),
					new HashMap<String, String>(), true);
		}

		if (!authorized) {
			throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
					methodToCall,
					this.getClass().getSimpleName());
		}
	}

	public ActionForward check(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<String> messages = getDataIntegrityService().checkIntegrity();
		if (messages.isEmpty()) {
			messages = Collections.singletonList("No data integrity issues found.");
		}
		request.setAttribute("checkMessages", messages);
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	public ActionForward repair(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<String> messages = getDataIntegrityService().repair();
		if (messages.isEmpty()) {
			messages = Collections.singletonList("No data repair was necessary.");
		}
		request.setAttribute("repairMessages", messages);
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}

	public DataIntegrityService getDataIntegrityService() {
		return KimImplServiceLocator.getDataIntegrityService();
	}

}
