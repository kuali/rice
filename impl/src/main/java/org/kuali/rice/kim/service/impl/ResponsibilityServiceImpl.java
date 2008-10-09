/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityServiceImpl implements ResponsibilityService {

	private BusinessObjectService businessObjectService;
	private GroupService groupService;
	private RoleService roleService;

    // --------------------------
    // Responsibility Methods
    // --------------------------
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibility(java.lang.String)
     */
    public KimResponsibilityInfo getResponsibility(String responsibilityId) {
    	KimResponsibilityImpl impl = getResponsibilityImpl( responsibilityId );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityByName(java.lang.String)
     */
    public KimResponsibilityInfo getResponsibilityByName( String responsibilityName) {
    	KimResponsibilityImpl impl = getResponsibilityImplByName( responsibilityName );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    protected KimResponsibilityImpl getResponsibilityImpl(String responsibilityId) {
    	if ( StringUtils.isBlank( responsibilityId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( "responsibilityId", responsibilityId );
    	return (KimResponsibilityImpl)getBusinessObjectService().findByPrimaryKey( KimResponsibilityImpl.class, pk );
    }
    
    protected KimResponsibilityImpl getResponsibilityImplByName( String responsibilityName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( "responsibilityName", responsibilityName );
		pk.put("active", "Y");
    	return (KimResponsibilityImpl)getBusinessObjectService().findByPrimaryKey( KimResponsibilityImpl.class, pk );
    }
    
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getPrincipalIdsWithResponsibility(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<String> getPrincipalIdsWithResponsibility(String responsibilityId,
    		Map<String,String> qualification, Map<String,String> responsibilityDetails) {
    	
    	// find matching role/resp records based on resp details (use resp service)
    	// determine roles which directly infer those resp
    	// obtain list of principals who have those roles based on the qualifications
    	
    	throw new UnsupportedOperationException();
    	// return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getPrincipalIdsWithResponsibilityByName(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<String> getPrincipalIdsWithResponsibilityByName( String responsibilityName, Map<String,String> qualification,
    		Map<String,String> responsibilityDetails) {
    	KimResponsibilityInfo resp = getResponsibilityByName( responsibilityName );
    	return getPrincipalIdsWithResponsibility( resp.getResponsibilityId(), qualification, responsibilityDetails );
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#hasQualifiedResponsibilityWithDetails(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
     */
    public boolean hasQualifiedResponsibilityWithDetails(String principalId,
    		String responsibilityId, Map<String,String> qualification,
    		Map<String,String> responsibilityDetails) {

    	// TODO: simple implementation, probably needs to be replaced
    	
    	List<String> ids = getPrincipalIdsWithResponsibility( responsibilityId, qualification, responsibilityDetails );
    	return ids.contains( principalId );
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityInfo(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<ResponsibilityActionInfo> getResponsibilityInfo(String responsibilityId,
    		Map<String,String> qualification, Map<String,String> responsibilityDetails) {

    	// find matching role/resp records based on resp details (use resp service)
    	// group the results by role
    	// for each role, determine the associated principals from the role service
    	// build ResponsibilityResolutionInfo objects which match the principals with the appropriate responsibility and details
    	
    	throw new UnsupportedOperationException();
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityInfoByName(java.lang.String, java.util.Map, java.util.Map)
     */
    public List<ResponsibilityActionInfo> getResponsibilityInfoByName( String responsibilityName, Map<String,String> qualification,
    		Map<String,String> responsibilityDetails) {
    	KimResponsibilityInfo resp = getResponsibilityByName( responsibilityName );
    	return getResponsibilityInfo( resp.getResponsibilityId(), qualification, responsibilityDetails );
    }


    // --------------------
    // Support Methods
    // --------------------
	
	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

    
	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();		
		}

		return groupService;
	}

	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleService();		
		}

		return roleService;
	}

}
