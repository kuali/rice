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
package org.kuali.rice.kim.impl.responsibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.responsibility.ResponsibilityChangeProcessor;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityInternalServiceImpl implements ResponsibilityInternalService {

	private BusinessObjectService businessObjectService;
    private ResponsibilityService responsibilityService;

	public void saveRoleMember(RoleMemberImpl roleMember){

		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibilityInfo> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// add row to member table
    	getBusinessObjectService().save( roleMember );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibilityInfo> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));
	}

	public void removeRoleMember(RoleMemberImpl roleMember){
		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibilityInfo> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// add row to member table
    	getBusinessObjectService().delete( roleMember );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibilityInfo> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));
	}

	@SuppressWarnings("unchecked")
	public void updateActionRequestsForRoleChange(String roleId) {
    	List<RoleResponsibilityInfo> newRoleResp = getRoleResponsibilities(roleId);
		
    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(Collections.EMPTY_LIST, newRoleResp));
	}
	

	/**
	 * This overridden method ...
	 *
	 * @see ResponsibilityInternalService#updateActionRequestsForResponsibilityChange(java.util.Set)
	 */
	public void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds) {

		KSBXMLService responsibilityChangeProcessor = (KSBXMLService) KsbApiServiceLocator.getMessageHelper()
        .getServiceAsynchronously(new QName(MessageServiceNames.RESPONSIBILITY_CHANGE_SERVICE));
        try {
        	responsibilityChangeProcessor.invoke(ResponsibilityChangeProcessor.getResponsibilityChangeContents(responsibilityIds));

        } catch (Exception e) {
            throw new WorkflowRuntimeException(e);
        }

	}

	@SuppressWarnings("unchecked")
	public List<RoleResponsibilityInfo> getRoleResponsibilities(String roleId){		
		List<RoleResponsibilityImpl> roleResponsibilities = 
				(List<RoleResponsibilityImpl>)getBusinessObjectService()
				.findMatching(RoleResponsibilityImpl.class, Collections.singletonMap(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId));
		List<RoleResponsibilityInfo> result = new ArrayList<RoleResponsibilityInfo>( roleResponsibilities.size() );
		for ( RoleResponsibilityImpl roleResp : roleResponsibilities ) {
			result.add( roleResp.toSimpleInfo() );
		}

		return result;
    }

	 /**
    *
    * This method compares the two lists of responsibilitiy IDs and does a union.  returns a unique list of responsibility ids.
    *
    * @param oldRespList
    * @param newRespList
    * @return
    */
   protected Set<String> getChangedRoleResponsibilityIds(
			List<RoleResponsibilityInfo> oldRespList,
			List<RoleResponsibilityInfo> newRespList) {
		Set<String> lRet = new HashSet<String>();

		for (RoleResponsibilityInfo resp : oldRespList) {
			lRet.add(resp.getResponsibilityId());
		}
		for (RoleResponsibilityInfo resp : newRespList) {
			lRet.add(resp.getResponsibilityId());
		}

		return lRet;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

    public boolean areActionsAtAssignmentLevel(Responsibility responsibility ) {
    	Attributes details = responsibility.getAttributes();
    	if ( details == null ) {
    		return false;
    	}
    	String actionDetailsAtRoleMemberLevel = details.get( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL );
    	return Boolean.valueOf(actionDetailsAtRoleMemberLevel);
    }

    public boolean areActionsAtAssignmentLevelById( String responsibilityId ) {
    	Responsibility responsibility = responsibilityService.getResponsibility(responsibilityId);
    	if ( responsibility == null ) {
    		return false;
    	}
    	return areActionsAtAssignmentLevel(responsibility);
    }

}
