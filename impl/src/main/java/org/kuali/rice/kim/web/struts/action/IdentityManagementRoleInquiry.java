/*
 * Copyright 2007-2009 The Kuali Foundation
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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityManagementRoleInquiry extends IdentityManagementBaseInquiryAction {
	private static final Logger LOG = Logger.getLogger(IdentityManagementRoleInquiry.class);	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementBaseInquiryAction#loadKimObject(javax.servlet.http.HttpServletRequest, org.kuali.rice.kim.web.struts.form.IdentityManagementDocumentFormBase)
	 */
	@Override
	protected void loadKimObject(HttpServletRequest request,
			IdentityManagementDocumentFormBase form) {
        IdentityManagementRoleDocumentForm roleDocumentForm = (IdentityManagementRoleDocumentForm) form;
        String roleId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_ID);
    	roleDocumentForm.setRoleId(roleId);
        KimRoleInfo role = KIMServiceLocator.getRoleService().getRole(roleId);
        if (role != null) {
        	getUiDocumentService().loadRoleDoc(roleDocumentForm.getRoleDocument(), role);
        } else {
        	LOG.error("No records found for Role Inquiry.");
            GlobalVariables.getMessageMap().putError(KNSConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INQUIRY);
        }
	}
	
}
