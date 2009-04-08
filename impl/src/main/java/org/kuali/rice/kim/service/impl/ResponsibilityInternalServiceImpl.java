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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.ListUtils;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.responsibility.ResponsibilityChangeProcessor;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.service.ResponsibilityInternalService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityInternalServiceImpl implements ResponsibilityInternalService {

	protected BusinessObjectService businessObjectService;

	public void saveRoleMember(RoleMemberImpl roleMember){

		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibilityImpl> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// add row to member table
    	getBusinessObjectService().save( roleMember );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibilityImpl> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));
	}

	public void removeRoleMember(RoleMemberImpl roleMember){
		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibilityImpl> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// add row to member table
    	getBusinessObjectService().delete( roleMember );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibilityImpl> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));
	}


	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.service.ResponsibilityInternalService#updateActionRequestsForResponsibilityChange(java.util.Set)
	 */
	public void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds) {

		KSBXMLService responsibilityChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
        .getServiceAsynchronously(new QName(MessageServiceNames.RESPONSIBILITY_CHANGE_SERVICE));
        try {
        	responsibilityChangeProcessor.invoke(ResponsibilityChangeProcessor.getResponsibilityChangeContents(responsibilityIds));

        } catch (Exception e) {
            throw new WorkflowRuntimeException(e);
        }

	}

	protected List<RoleResponsibilityImpl> getRoleResponsibilities(String roleId){
    	Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("roleId", roleId);

		List<RoleResponsibilityImpl> roleResponsibilities = new ArrayList<RoleResponsibilityImpl>();
		roleResponsibilities = (List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);

		return roleResponsibilities;
    }

	 /**
    *
    * This method compares the two lists of responsibilitiy IDs and does a union.  returns a unique list of responsibility ids.
    *
    * @param oldRespList
    * @param newRespList
    * @return
    */
   protected Set<String> getChangedRoleResponsibilityIds(List<RoleResponsibilityImpl> oldRespList, List<RoleResponsibilityImpl> newRespList){
   	Set<String> lRet = new HashSet<String>();
   	List<String> newResp = new ArrayList<String>();
		List<String> oldResp = new ArrayList<String>();

   	for(RoleResponsibilityImpl resp: oldRespList){
   		oldResp.add(resp.getResponsibilityId());
   	}
   	for(RoleResponsibilityImpl resp: newRespList){
   		newResp.add(resp.getResponsibilityId());
   	}

   	lRet.addAll(ListUtils.union(newResp, oldResp));

   	return lRet;
   }

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

}
