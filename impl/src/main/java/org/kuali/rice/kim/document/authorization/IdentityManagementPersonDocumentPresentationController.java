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
package org.kuali.rice.kim.document.authorization;

import java.util.List;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentPresentationController extends TransactionalDocumentPresentationControllerBase {

	// this controller class is not quite clear yet.
	@Override
    public Set<String> getEditMode(Document document){
    	Set<String> editModes = super.getEditMode(document);
		IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument) document;
		IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
		Person user = GlobalVariables.getUserSession().getPerson();
		
		// this should be done at the super class's 
		// this is a copy fro documentauthorizerbase
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        else if (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        editModes.add(editMode);

		
        // idmpersondoc's edit mode check
		if (!editModes.contains("modifyEntity")) {
			AttributeSet permDetail = new AttributeSet();
			permDetail.put("entityTypeCode", "Person");
			// TODO : get qualification what should be here?
			AttributeSet qualification = new AttributeSet();
			qualification.put("principalId", user.getPrincipalId());
			List<? extends KimPermission> modifyEntityPerm = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Modify Entity", permDetail, qualification);
	        if (!modifyEntityPerm.isEmpty()) {
	        	editModes.add("modifyEntity");
	        } else {
	            throw new RuntimeException("Can Not Edit Person Document");
	        }
        }
		
		AttributeSet permDetail = new AttributeSet();
		permDetail.put("propertyName", "line3");
		// TODO : get qualification ?
		AttributeSet qualification = new AttributeSet();
		qualification.put("principalId", user.getPrincipalId());

		// modify entity fields are not finalized yet.  if permattribute is null, then it will return all 'modify entity fields' perms assigned to user
		List<? extends KimPermission> permissions = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-KIM", "Modify Entity Field(s)", permDetail, qualification);
        if (!permissions.isEmpty()) {
        	editModes.add("line3");
        }
        // TODO : get assign role
		List<? extends KimPermission> assignRolePerms = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-KIM", "Assign Role", null, qualification);
        if (!assignRolePerms.isEmpty()) {
        	editModes.add("assignRole");
        }
        // TODO : get populate group
		List<? extends KimPermission> populateGroupPerms = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-KIM", "Populate Group", null, qualification);
        if (!populateGroupPerms.isEmpty()) {
        	editModes.add("populateGroup");
        }

		return editModes;

    }

    private boolean hasInitiateAuthorization(Document document, Person user) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(user.getPrincipalName());
    }

}
