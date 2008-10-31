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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.dao.KimResponsibilityDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

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
	private KimResponsibilityDao responsibilityDao;   

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
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilitiesByName(String,java.lang.String)
     */
    public List<KimResponsibilityInfo> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
    	List<KimResponsibilityImpl> impls = getResponsibilityImplsByName( namespaceCode, responsibilityName );
    	List<KimResponsibilityInfo> results = new ArrayList<KimResponsibilityInfo>( impls.size() );
    	for ( KimResponsibilityImpl impl : impls ) {
    		results.add( impl.toSimpleInfo() );
    	}
    	return results;
    }
    
    protected KimResponsibilityImpl getResponsibilityImpl(String responsibilityId) {
    	if ( StringUtils.isBlank( responsibilityId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( "responsibilityId", responsibilityId );
    	return (KimResponsibilityImpl)getBusinessObjectService().findByPrimaryKey( KimResponsibilityImpl.class, pk );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimResponsibilityImpl> getResponsibilityImplsByName( String namespaceCode, String responsibilityName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( "namespaceCode", namespaceCode );
    	pk.put( "responsibilityName", responsibilityName );
		pk.put("active", "Y");
    	return (List<KimResponsibilityImpl>)getBusinessObjectService().findMatching( KimResponsibilityImpl.class, pk );
    }

    @SuppressWarnings("unchecked")
	protected List<KimResponsibilityImpl> getResponsibilityImplsByTemplateName( String namespaceCode, String responsibilityTemplateName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 4 );
    	pk.put( "template.namespaceCode", namespaceCode );
    	pk.put( "template.responsibilityName", responsibilityTemplateName );
		pk.put( "template.active", "Y");
		pk.put( "active", "Y");
    	return (List<KimResponsibilityImpl>)getBusinessObjectService().findMatching( KimResponsibilityImpl.class, pk );
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean hasResponsibility(String principalId, String namespaceCode,
    		String responsibilityName, AttributeSet qualification,
    		AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<KimResponsibilityImpl> responsibilities = getResponsibilityImplsByName( namespaceCode, responsibilityName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityImpl> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
    	List<String> roleIds = getRoleIdsForResponsibilities( applicableResponsibilities, qualification );
    	return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.ResponsibilityService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean hasResponsibilityByTemplateName(String principalId,
    		String namespaceCode, String responsibilityTemplateName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<KimResponsibilityImpl> responsibilities = getResponsibilityImplsByTemplateName( namespaceCode, responsibilityTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityImpl> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
    	List<String> roleIds = getRoleIdsForResponsibilities( applicableResponsibilities, qualification );
    	return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityActions(String, java.lang.String, AttributeSet, AttributeSet)
     */
    public List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<KimResponsibilityImpl> responsibilities = getResponsibilityImplsByName( namespaceCode, responsibilityName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityImpl> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	for ( KimResponsibilityImpl r : applicableResponsibilities ) {
    		List<String> roleIds = getRoleIdsForResponsibility( r, qualification );
    		results.addAll( getActionsForResponsibilityRoles( r, roleIds, qualification) );
    	}
    	return results;
    }

    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityActions(String, java.lang.String, AttributeSet, AttributeSet)
     */
    public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName( String namespaceCode, String responsibilityTemplateName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<KimResponsibilityImpl> responsibilities = getResponsibilityImplsByTemplateName( namespaceCode, responsibilityTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityImpl> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	for ( KimResponsibilityImpl r : applicableResponsibilities ) {
    		List<String> roleIds = getRoleIdsForResponsibility( r, qualification );
    		results.addAll( getActionsForResponsibilityRoles( r, roleIds, qualification) );
    	}
    	return results;
    }
    
    protected List<ResponsibilityActionInfo> getActionsForResponsibilityRoles( KimResponsibilityImpl responsibility, List<String> roleIds, AttributeSet qualification ) {
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		ResponsibilityActionInfo rai = new ResponsibilityActionInfo( rm.getPrincipalId(), rm.getGroupId(), responsibility, rm.getRoleId(), rm.getQualifier(), rm.getDelegates() );
    		// get associated resp resolution objects
    		RoleResponsibilityActionImpl action = responsibilityDao.getResponsibilityAction( responsibility.getResponsibilityId(), rm.getPrincipalId(), rm.getGroupId() );
    		// add the data to the ResponsibilityActionInfo objects
    		rai.setActionTypeCode( action.getActionTypeCode() );
    		rai.setActionPolicyCode( action.getActionPolicyCode() );
    		rai.setPriorityNumber( action.getPriorityNumber() );

    		results.add( rai );
    	}
    	return results;
    }
    
//    /**
//     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityInfo(java.lang.String, java.util.Map, java.util.Map)
//     */
//    public List<ResponsibilityActionInfo> getResponsibilityInfo(String responsibilityId,
//    		Map<String,String> qualification, Map<String,String> responsibilityDetails) {
//
//    	// find matching role/resp records based on resp details (use resp service)
//    	// group the results by role
//    	// for each role, determine the associated principals from the role service
//    	// build ResponsibilityResolutionInfo objects which match the principals with the appropriate responsibility and details
//    	
//    	throw new UnsupportedOperationException();
//    }
    
    
    /**
     * Compare each of the passed in responsibilities with the given responsibilityDetails.  Those that
     * match are added to the result list.
     */
    protected List<KimResponsibilityImpl> getMatchingResponsibilities( List<KimResponsibilityImpl> responsibilities, AttributeSet responsibilityDetails ) {
    	List<KimResponsibilityImpl> applicableResponsibilities;    	
    	if ( responsibilityDetails == null || responsibilityDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		applicableResponsibilities = responsibilities;
    	} else {
    		// otherwise, attempt to match the 
    		applicableResponsibilities = new ArrayList<KimResponsibilityImpl>();
    		for ( KimResponsibilityImpl perm : responsibilities ) {
    			String serviceName = perm.getTemplate().getKimType().getKimTypeServiceName();
    			if ( serviceName == null ) { // no service - assume a match
    				applicableResponsibilities.add( perm );
    			} else {
    				KimResponsibilityTypeService responsibilityTypeService = (KimResponsibilityTypeService)KIMServiceLocator.getBean( serviceName );
    				if ( responsibilityTypeService == null ) { // can't find the service - assume a match
    					applicableResponsibilities.add( perm );
    				} else { // got a service - check with it
    					if ( responsibilityTypeService.doesResponsibilityDetailMatch( responsibilityDetails, perm.getDetails() ) ) {
    						applicableResponsibilities.add( perm );
    					}
    				}
    			}
    		}
    	}
    	return applicableResponsibilities;
    }
	
    protected List<String> getRoleIdsForResponsibilities( List<KimResponsibilityImpl> responsibilities, AttributeSet qualification ) {
    	return responsibilityDao.getRoleIdsForResponsibilities( responsibilities );    	
    }

    protected List<String> getRoleIdsForResponsibility( KimResponsibilityImpl responsibility, AttributeSet qualification ) {
    	return responsibilityDao.getRoleIdsForResponsibility( responsibility );    	
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

	public KimResponsibilityDao getResponsibilityDao() {
		return this.responsibilityDao;
	}

	public void setResponsibilityDao(KimResponsibilityDao responsibilityDao) {
		this.responsibilityDao = responsibilityDao;
	}

}
