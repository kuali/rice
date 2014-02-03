/**
 * Copyright 2005-2014 The Kuali Foundation
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
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class ResponsibilityInternalServiceImpl implements ResponsibilityInternalService {

    protected DataObjectService dataObjectService;
    protected ResponsibilityService responsibilityService;
    protected DateTimeService dateTimeService;

    @Override
	public RoleMemberBo saveRoleMember(RoleMemberBo roleMember){

		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibility> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// add row to member table
    	RoleMemberBo member = dataObjectService.save( roleMember, PersistenceOption.FLUSH );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibility> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));

        return member;
	}

    @Override
    public DelegateMemberBo saveDelegateMember(DelegateMemberBo delegateMember) {

        // add row to member table
        DelegateMemberBo member = dataObjectService.save(delegateMember, PersistenceOption.FLUSH);

        return member;
    }

    @Override
	public void removeRoleMember(RoleMemberBo roleMember){
		//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	List<RoleResponsibility> oldRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	// need to set end date to inactivate, not delete
        roleMember.setActiveToDateValue(dateTimeService.getCurrentTimestamp());
    	roleMember = dataObjectService.save( roleMember, PersistenceOption.FLUSH );

    	//need to find what responsibilities changed so we can notify interested clients.  Like workflow.
    	// the new member has been added
    	List<RoleResponsibility> newRoleResp = getRoleResponsibilities(roleMember.getRoleId());

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(oldRoleResp, newRoleResp));
	}

    @Override
	@SuppressWarnings("unchecked")
	public void updateActionRequestsForRoleChange(String roleId) {
    	List<RoleResponsibility> newRoleResp = getRoleResponsibilities(roleId);

    	updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(Collections.EMPTY_LIST, newRoleResp));
	}


    @Override
	public void updateActionRequestsForResponsibilityChange(Set<String> responsibilityIds) {
        KewApiServiceLocator.getResponsibilityChangeQueue().responsibilitiesChanged(responsibilityIds);
	}

	@Override
    public List<RoleResponsibility> getRoleResponsibilities(String roleId){
		List<RoleResponsibilityBo> rrBoList = dataObjectService.findMatching( RoleResponsibilityBo.class, QueryByCriteria.Builder.fromPredicates(
		            PredicateFactory.equal(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, roleId),
		            PredicateFactory.equal(KRADPropertyConstants.ACTIVE,Boolean.TRUE) ) ).getResults();
		List<RoleResponsibility> result = new ArrayList<RoleResponsibility>( rrBoList.size() );
		for ( RoleResponsibilityBo bo : rrBoList ) {
			result.add( RoleResponsibilityBo.to(bo) );
		}
		return result;
    }

    /**
     *
     * This method compares the two lists of responsibility IDs and does a union. returns a unique
     * list of responsibility ids.
     *
     * @param oldRespList
     * @param newRespList
     * @return
     */
    protected Set<String> getChangedRoleResponsibilityIds(
			List<RoleResponsibility> oldRespList,
			List<RoleResponsibility> newRespList) {
		Set<String> lRet = new HashSet<String>();

		for (RoleResponsibility resp : oldRespList) {
			lRet.add(resp.getResponsibilityId());
		}
		for (RoleResponsibility resp : newRespList) {
			lRet.add(resp.getResponsibilityId());
		}

		return lRet;
	}

    @Override
    public boolean areActionsAtAssignmentLevel(Responsibility responsibility ) {
    	Map<String, String> details = responsibility.getAttributes();
    	if ( details == null ) {
    		return false;
    	}
    	String actionDetailsAtRoleMemberLevel = StringUtils.trimToEmpty( details.get( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL ) );
    	return Truth.strToBooleanIgnoreCase(actionDetailsAtRoleMemberLevel, Boolean.FALSE);
    }

    @Override
    public boolean areActionsAtAssignmentLevelById( String responsibilityId ) {
    	Responsibility responsibility = responsibilityService.getResponsibility(responsibilityId);
    	if ( responsibility == null ) {
    		return false;
    	}
    	return areActionsAtAssignmentLevel(responsibility);
    }

    public void setResponsibilityService(ResponsibilityService responsibilityService) {
        this.responsibilityService = responsibilityService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
