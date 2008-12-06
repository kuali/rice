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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentAuthorizer extends DocumentAuthorizerBase {

	@Override
    public void canInitiate(String documentTypeName, Person user) {
        super.canInitiate(documentTypeName, user);
        if (!canCreatePersonDocument(user)) {
            throw new RuntimeException("Can Not initiate Person Document");
        }
    }
	
    private boolean canCreatePersonDocument(Person user) {
		IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
		AttributeSet permDetail = new AttributeSet();
		permDetail.put("entityTypeCode", "Person");
		List<? extends KimPermission> permissions = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-KIM", "Modify Entity", permDetail, null);
		// TODO : WIP finish it.
		return (!permissions.isEmpty());
    }

	@Override
	public Map getEditMode(Document doc, Person user) {
        Map editModeMap = super.getEditMode(doc, user);
		IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument) doc;
		IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
		AttributeSet permDetail = new AttributeSet();
		permDetail.put("propertyName", "line3");
		List<? extends KimPermission> permissions = identityManagementService.getAuthorizedPermissionsByTemplateName(user.getPrincipalId(), "KR-KIM", "Modify Entity Field(s)", permDetail, null);
        if (!permissions.isEmpty()) {
        	editModeMap.put("line3", true);
        }
		return editModeMap;
	}

}
