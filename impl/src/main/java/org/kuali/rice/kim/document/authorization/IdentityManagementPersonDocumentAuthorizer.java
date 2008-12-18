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
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {

	@Override
    public void canInitiate(String documentTypeName, Person user) {
        super.canInitiate(documentTypeName, user);
        if (!canCreatePersonDocument(user)) {
        	throw new DocumentTypeAuthorizationException(user.getPrincipalName(),"Initiate", documentTypeName);
        }
    }
	
    private boolean canCreatePersonDocument(Person user) {
		IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
		AttributeSet permDetail = new AttributeSet();
		permDetail.put("entityTypeCode", "Person");
		// TODO : get qualification what should be here?
		AttributeSet qualification = new AttributeSet();
		qualification.put("principalId", user.getPrincipalId());
		List<? extends KimPermission> permissions = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Modify Entity", permDetail, qualification);
		// TODO : WIP finish it.
		return (!permissions.isEmpty());
    }

	@Override
	public Map getEditMode(Document doc, Person user) {
        Map editModeMap = super.getEditMode(doc, user);
		IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument) doc;
		IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();

        if (editModeMap.get("modifyEntity") == null) {
			AttributeSet permDetail = new AttributeSet();
			permDetail.put("entityTypeCode", "Person");
			// TODO : get qualification what should be here?
			AttributeSet qualification = new AttributeSet();
			qualification.put("principalId", user.getPrincipalId());
			List<? extends KimPermission> modifyEntityPerm = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Modify Entity", permDetail, qualification);
	        if (!modifyEntityPerm.isEmpty()) {
	        	editModeMap.put("modifyEntity", true);
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
		List<? extends KimPermission> permissions = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Modify Entity Field(s)", permDetail, qualification);
        if (!permissions.isEmpty()) {
        	editModeMap.put("line3", true);
        }
        // TODO : get assign role
		List<? extends KimPermission> assignRolePerms = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Assign Role", null, qualification);
        if (!assignRolePerms.isEmpty()) {
        	editModeMap.put("assignRole", true);
        }
        // TODO : get populate group
		List<? extends KimPermission> populateGroupPerms = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-IDM", "Populate Group", null, qualification);
        if (!populateGroupPerms.isEmpty()) {
        	editModeMap.put("populateGroup", true);
        }

		return editModeMap;
	}

	/**
	 *
	 * TODO : too much change right now.  temporarily set these up for testing for now.
	 * 
	 * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
	 */
    @Override
    public Set getDocumentActions(Document document, Person user, Set<String> documentActions) {
        Set docActions = super.getDocumentActions(document, user, documentActions);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        boolean hasInitiateAuthorization = hasInitiateAuthorization(document, user);
        if(hasInitiateAuthorization(document, user)){
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_CANCEL);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_SAVE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_ROUTE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_FYI);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_APPROVE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE);
        	docActions.add(KNSConstants.KUALI_ACTION_CAN_ANNOTATE);
        }
        
        return docActions;
    }


}
