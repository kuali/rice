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
package org.kuali.rice.kew.service.impl;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AdhocRecipientPermissionTypeServiceImpl extends
		DocumentTypeAndActionRequestTypePermissionTypeServiceImpl {
/*
 * 
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) 
    VALUES('64', UUID(), 1, 'Ad Hoc Recipient Permission Type', 'adhocRecipientPermissionTypeService', 'Y', 'KR-WKFLW')
/
UPDATE krim_perm_tmpl_t
    SET KIM_TYP_ID = '64'
    WHERE PERM_TMPL_ID = '9'
/
COMMIT
/

 */
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#filterRoleQualifier(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public AttributeSet filterRoleQualifier(String namespaceCode,
			String permissionTemplateName, String permissionName,
			AttributeSet roleQualifier) {
		AttributeSet filteredAttributeSet = new AttributeSet();
		// for the roles assigned to this, we don't need to pass any qualifiers
		return filteredAttributeSet;
	}
}
