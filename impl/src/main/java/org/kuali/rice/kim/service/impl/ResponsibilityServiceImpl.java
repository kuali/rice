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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.RiceDebugUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.dao.KimResponsibilityDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.ResponsibilityUpdateService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityServiceImpl implements ResponsibilityService, ResponsibilityUpdateService {
	private static final String DEFAULT_RESPONSIBILITY_TYPE_SERVICE = "defaultResponsibilityTypeService";
	private static final Logger LOG = Logger.getLogger( ResponsibilityServiceImpl.class );
	private static final Integer DEFAULT_PRIORITY_NUMBER = new Integer(1);
	private BusinessObjectService businessObjectService;
	private RoleService roleService;
	private KimResponsibilityDao responsibilityDao;   
	private KimResponsibilityTypeService responsibilityTypeService;

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
    	pk.put( KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, responsibilityId );
    	return (KimResponsibilityImpl)getBusinessObjectService().findByPrimaryKey( KimResponsibilityImpl.class, pk );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimResponsibilityImpl> getResponsibilityImplsByName( String namespaceCode, String responsibilityName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
    	pk.put( KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, responsibilityName );
		pk.put( KNSPropertyConstants.ACTIVE, "Y");
    	return (List<KimResponsibilityImpl>)getBusinessObjectService().findMatching( KimResponsibilityImpl.class, pk );
    }

    @SuppressWarnings("unchecked")
	protected List<KimResponsibilityImpl> getResponsibilityImplsByTemplateName( String namespaceCode, String responsibilityTemplateName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 4 );
    	pk.put( "template.namespaceCode", namespaceCode );
    	pk.put( "template.name", responsibilityTemplateName );
		pk.put( "template.active", "Y");
		pk.put( KNSPropertyConstants.ACTIVE, "Y");
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
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
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
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
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
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	for ( KimResponsibilityInfo r : applicableResponsibilities ) {
    		List<String> roleIds = getRoleIdsForResponsibility( r, qualification );
    		results.addAll( getActionsForResponsibilityRoles( r, roleIds, qualification) );
    	}
    	return results;
    }

    protected void logResponsibilityCheck(String namespaceCode, String responsibilityName, AttributeSet responsibilityDetails, AttributeSet qualification ) {
		StringBuffer sb = new StringBuffer();
		sb.append(  '\n' );
		sb.append( "Get Resp Actions: " ).append( namespaceCode ).append( "/" ).append( responsibilityName ).append( '\n' );
		sb.append( "             Details:\n" );
		if ( responsibilityDetails != null ) {
			sb.append( responsibilityDetails.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		sb.append( "             Qualifiers:\n" );
		if ( qualification != null ) {
			sb.append( qualification.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		LOG.debug( sb.append( RiceDebugUtils.getTruncatedStackTrace(true) ).toString() );
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilityActions(String, java.lang.String, AttributeSet, AttributeSet)
     */
    public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName( String namespaceCode, String responsibilityTemplateName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logResponsibilityCheck( namespaceCode, responsibilityTemplateName, responsibilityDetails, qualification );
    	}
    	// get all the responsibility objects whose name match that requested
    	List<KimResponsibilityImpl> responsibilities = getResponsibilityImplsByTemplateName( namespaceCode, responsibilityTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	for ( KimResponsibilityInfo r : applicableResponsibilities ) {
    		List<String> roleIds = getRoleIdsForResponsibility( r, qualification );
    		results.addAll( getActionsForResponsibilityRoles( r, roleIds, qualification) );
    	}
    	return results;
    }
    
    protected List<ResponsibilityActionInfo> getActionsForResponsibilityRoles( KimResponsibilityInfo responsibility, List<String> roleIds, AttributeSet qualification ) {
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {    		
    		ResponsibilityActionInfo rai;
    		if ( rm.getMemberTypeCode().equals( KimRole.PRINCIPAL_MEMBER_TYPE ) ) {
    			rai = new ResponsibilityActionInfo( rm.getMemberId(), null, rm.getEmbeddedRoleId(), responsibility, rm.getRoleId(), rm.getQualifier(), rm.getDelegates() );
    		} else {
    			rai = new ResponsibilityActionInfo( null, rm.getMemberId(), rm.getEmbeddedRoleId(), responsibility, rm.getRoleId(), rm.getQualifier(), rm.getDelegates() );
    		}
    		// get associated resp resolution objects
    		RoleResponsibilityActionImpl action = responsibilityDao.getResponsibilityAction( rm.getRoleId(), responsibility.getResponsibilityId(), rm.getRoleMemberId() );
    		if ( action == null ) {
    			LOG.error( "Unable to get responsibility action record for role/responsibility/roleMember: " 
    					+ rm.getRoleId() + "/" + responsibility.getResponsibilityId() + "/" + rm.getRoleMemberId() );
    			LOG.error( "Skipping this role member in getActionsForResponsibilityRoles()");
    			continue;
    		}
    		// add the data to the ResponsibilityActionInfo objects
    		rai.setActionTypeCode( action.getActionTypeCode() );
    		rai.setActionPolicyCode( action.getActionPolicyCode() );
    		rai.setPriorityNumber(action.getPriorityNumber() == null ? DEFAULT_PRIORITY_NUMBER : action.getPriorityNumber());
    		rai.setIgnorePrevious( action.isIgnorePrevious() );
    		rai.setActionGroupingCode( action.getRoleMemberId() );
    		results.add( rai );
    	}
    	return results;
    }
    
    
    protected Map<String,KimResponsibilityTypeService> getResponsibilityTypeServicesByTemplateId(Collection<KimResponsibilityImpl> responsibilities) {
    	Map<String,KimResponsibilityTypeService> responsibilityTypeServices = new HashMap<String, KimResponsibilityTypeService>(responsibilities.size());
    	for ( KimResponsibilityImpl responsibility : responsibilities ) {
    		String serviceName = responsibility.getTemplate().getKimType().getKimTypeServiceName();
    		if ( serviceName != null ) {
    			KimResponsibilityTypeService responsibiltyTypeService = (KimResponsibilityTypeService)KIMServiceLocator.getService(serviceName);
    			if ( responsibiltyTypeService != null ) {
    	    		responsibilityTypeServices.put(responsibility.getTemplateId(), responsibiltyTypeService);    				
    			} else {
    				responsibilityTypeServices.put(responsibility.getTemplateId(), getDefaultResponsibilityTypeService());
    			}
    		}
    	}
    	return responsibilityTypeServices;
    }
    
    protected Map<String,List<KimResponsibilityInfo>> groupResponsibilitiesByTemplate(Collection<KimResponsibilityImpl> responsibilities) {
    	Map<String,List<KimResponsibilityInfo>> results = new HashMap<String,List<KimResponsibilityInfo>>();
    	for (KimResponsibilityImpl responsibility : responsibilities) {
    		List<KimResponsibilityInfo> responsibilityInfos = results.get( responsibility.getTemplateId() );
    		if ( responsibilityInfos == null ) {
    			responsibilityInfos = new ArrayList<KimResponsibilityInfo>();
    			results.put( responsibility.getTemplateId(), responsibilityInfos );
    		}
    		responsibilityInfos.add(responsibility.toSimpleInfo());
    	}
    	return results;
    }
    
    /**
     * Compare each of the passed in responsibilities with the given responsibilityDetails.  Those that
     * match are added to the result list.
     */
    protected List<KimResponsibilityInfo> getMatchingResponsibilities( List<KimResponsibilityImpl> responsibilities, AttributeSet responsibilityDetails ) {
    	List<KimResponsibilityInfo> applicableResponsibilities = new ArrayList<KimResponsibilityInfo>();    	
    	if ( responsibilityDetails == null || responsibilityDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		for ( KimResponsibilityImpl responsibility : responsibilities ) {
    			applicableResponsibilities.add(responsibility.toSimpleInfo());
    		}
    	} else {
    		// otherwise, attempt to match the permission details
    		// build a map of the template IDs to the type services
    		Map<String,KimResponsibilityTypeService> responsibilityTypeServices = getResponsibilityTypeServicesByTemplateId(responsibilities);
    		// build a map of permissions by template ID
    		Map<String,List<KimResponsibilityInfo>> responsibilityMap = groupResponsibilitiesByTemplate(responsibilities);
    		// loop over the different templates, matching all of the same template against the type
    		// service at once
    		for ( String templateId : responsibilityMap.keySet() ) {
    			KimResponsibilityTypeService responsibilityTypeService = responsibilityTypeServices.get( templateId );
    			List<KimResponsibilityInfo> responsibilityInfos = responsibilityMap.get( templateId );
    			if (responsibilityTypeService == null) {
    				responsibilityTypeService = getDefaultResponsibilityTypeService();
    			}
				applicableResponsibilities.addAll(responsibilityTypeService.getMatchingResponsibilities(responsibilityDetails, responsibilityInfos));    				
    		}
    	}
    	return applicableResponsibilities;
    }
	
    protected List<String> getRoleIdsForResponsibilities( List<KimResponsibilityInfo> responsibilities, AttributeSet qualification ) {
    	return responsibilityDao.getRoleIdsForResponsibilities( responsibilities );    	
    }

    protected List<String> getRoleIdsForResponsibility( KimResponsibilityInfo responsibility, AttributeSet qualification ) {
    	return responsibilityDao.getRoleIdsForResponsibility( responsibility );    	
    }
    
    protected boolean areActionsAtAssignmentLevel( KimResponsibilityImpl responsibility ) {
    	AttributeSet details = responsibility.getDetails();
    	if ( details == null ) {
    		return false;
    	}
    	String actionDetailsAtRoleMemberLevel = details.get( KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL );
    	return Boolean.valueOf(actionDetailsAtRoleMemberLevel);
    }

    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#areActionsAtAssignmentLevel(org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo)
     */
    public boolean areActionsAtAssignmentLevel( KimResponsibilityInfo responsibility ) {
    	AttributeSet details = responsibility.getDetails();
    	if ( details == null ) {
    		return false;
    	}
    	String actionDetailsAtRoleMemberLevel = details.get( KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL );
    	return Boolean.valueOf(actionDetailsAtRoleMemberLevel);
    }

    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#areActionsAtAssignmentLevelById(String)
     */
    public boolean areActionsAtAssignmentLevelById( String responsibilityId ) {
    	KimResponsibilityImpl responsibility = getResponsibilityImpl(responsibilityId);
    	if ( responsibility == null ) {
    		return false;
    	}
    	return areActionsAtAssignmentLevel(responsibility);
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

	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleManagementService();		
		}

		return roleService;
	}

	public KimResponsibilityDao getResponsibilityDao() {
		return this.responsibilityDao;
	}

	public void setResponsibilityDao(KimResponsibilityDao responsibilityDao) {
		this.responsibilityDao = responsibilityDao;
	}

	protected KimResponsibilityTypeService getDefaultResponsibilityTypeService() {
		if (responsibilityTypeService == null) {
			responsibilityTypeService = (KimResponsibilityTypeService)KIMServiceLocator.getBean(DEFAULT_RESPONSIBILITY_TYPE_SERVICE);
		}
		return responsibilityTypeService;
	}

}
