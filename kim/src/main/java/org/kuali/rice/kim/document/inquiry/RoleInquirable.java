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
package org.kuali.rice.kim.document.inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.PrincipalQualifiedRole;
import org.kuali.rice.kim.bo.PrincipalQualifiedRoleAttribute;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.RoleAttribute;

/**
 * This is a description of what this class does - vrk4 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleInquirable extends KualiInquirableImpl{
	
	
	 /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		// TODO vrk4 - THIS METHOD NEEDS JAVADOCS
		BusinessObject bo = super.getBusinessObject(fieldValues);
		if(bo!=null){
			if(bo instanceof Role){
				 Role role = (Role)bo;
				 populatePrincipalQualifiedRoles(role);
			 }
		}
		return bo;
	}


	
	 
	    /**
	     * This method is responsible for taking the persisted principal qualified role attributes and pushing them into 
	     * the appropriate value added helper bos for displaying in the maintenance document user interface.
	     * 
	     * @param role
	     */
	 
	    private void populatePrincipalQualifiedRoles(Role role) {
            role.getPrincipalQualifiedRoles().clear();

            ArrayList<Principal> principals = role.getPrincipals();
            for (Principal p : principals) {
                PrincipalQualifiedRole pqr = new PrincipalQualifiedRole();
                pqr.setId(p.getId());
                pqr.setName(p.getName());
                pqr.setRoleId(role.getId());

                ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = role.getPrincipalQualifiedRoleAttributes();
                for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
                    if(pqrAttrib.getPrincipalId().equals(pqr.getId())) {
                        pqr.getQualifiedRoleAttributes().add(pqrAttrib);
                    }
                }
                
                role.getPrincipalQualifiedRoles().add(pqr);
            }
    }

	 
}
