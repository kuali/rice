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
import org.kuali.rice.kim.bo.RoleQualificationForPrincipal;

/**
 * This is a description of what this class does - vrk4 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PrincipalInquirable extends KualiInquirableImpl{
	
	
	 /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.core.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		// TODO vrk4 - THIS METHOD NEEDS JAVADOCS
		BusinessObject bo = super.getBusinessObject(fieldValues);
		if(bo==null) return bo;
		
		if(bo instanceof Principal){
			 Principal principal = (Principal)bo;
			 populateRoleQualifications(principal);
		 }
		
		return bo;
	}

	 
	 
	 
	    /**
	     * This method is responsible for taking the persisted principal qualified role attributes and pushing them into 
	     * the appropriate value added helper bos for displaying in the maintenance document user interface.
	     * 
	     * @param principal
	     */
	    private void populateRoleQualifications(Principal principal) {
	            principal.getRoleQualificationsForPrincipal().clear();

	            ArrayList<Role> roles = principal.getRoles();
	            for (Role r : roles) {
	                RoleQualificationForPrincipal rqfp = new RoleQualificationForPrincipal();
	                rqfp.setId(r.getId());
	                rqfp.setName(r.getName());
	                rqfp.setPrincipalId(principal.getId());

	                ArrayList<PrincipalQualifiedRoleAttribute> pqrAttribs = principal.getPrincipalQualifiedRoleAttributes();
	                for(PrincipalQualifiedRoleAttribute pqrAttrib : pqrAttribs) {
	                    if(pqrAttrib.getRoleId().equals(rqfp.getId())) {
	                    	
	                        rqfp.getQualifiedRoleAttributes().add(pqrAttrib);
	                    }
	                }
	                
	                principal.getRoleQualificationsForPrincipal().add(rqfp);
	            }
	    }

	 
}
