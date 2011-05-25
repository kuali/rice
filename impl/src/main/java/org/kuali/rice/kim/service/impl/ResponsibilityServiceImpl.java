/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.dao.KimResponsibilityDao;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityTemplateBo;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimResponsibilityTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.util.KNSPropertyConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityServiceImpl extends ResponsibilityServiceBase implements ResponsibilityService {
	private static final Logger LOG = Logger.getLogger( ResponsibilityServiceImpl.class );
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
    	ResponsibilityBo impl = getResponsibilityImpl( responsibilityId );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#getResponsibilitiesByName(String,java.lang.String)
     */
    public List<KimResponsibilityInfo> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
    	List<ResponsibilityBo> impls = getResponsibilityImplsByName( namespaceCode, responsibilityName );
    	List<KimResponsibilityInfo> results = new ArrayList<KimResponsibilityInfo>( impls.size() );
    	for ( ResponsibilityBo impl : impls ) {
    		results.add( impl.toSimpleInfo() );
    	}
    	return results;
    }
    
    public ResponsibilityBo getResponsibilityImpl(String responsibilityId) {
    	if ( StringUtils.isBlank( responsibilityId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID, responsibilityId );
    	return getBusinessObjectService().findByPrimaryKey( ResponsibilityBo.class, pk );
    }

    public KimResponsibilityTemplateInfo getResponsibilityTemplate(
    		String responsibilityTemplateId) {
        ResponsibilityTemplateBo impl = getResponsibilityTemplateImpl(responsibilityTemplateId);
        return toInfo(impl);
    }

    private static KimResponsibilityTemplateInfo toInfo(ResponsibilityTemplateBo bo) {
        if ( bo != null ) {
    		KimResponsibilityTemplateInfo info = new KimResponsibilityTemplateInfo();
            info.setResponsibilityTemplateId(bo.getId());
		    info.setNamespaceCode(bo.getNamespaceCode());
		    info.setName(bo.getName());
		    info.setDescription(bo.getDescription());
	    	info.setKimTypeId(bo.getKimTypeId());
		    info.setActive(bo.isActive());
            return info;
    	}
        return null;
    }
    
    public KimResponsibilityTemplateInfo getResponsibilityTemplateByName(
    		String namespaceCode, String responsibilityTemplateName) {
    	ResponsibilityTemplateBo impl = getResponsibilityTemplateImplsByName(namespaceCode, responsibilityTemplateName);
        return toInfo(impl);
    }
    
    public ResponsibilityTemplateBo getResponsibilityTemplateImpl(
    		String responsibilityTemplateId) {
    	return getBusinessObjectService().findBySinglePrimaryKey(ResponsibilityTemplateBo.class, responsibilityTemplateId);
    }
    
	public ResponsibilityTemplateBo getResponsibilityTemplateImplsByName(
    		String namespaceCode, String responsibilityTemplateName) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
    	pk.put( KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME, responsibilityTemplateName );
		pk.put( KNSPropertyConstants.ACTIVE, "Y");
    	return getBusinessObjectService().findByPrimaryKey( ResponsibilityTemplateBo.class, pk );
    }
    
    public RoleResponsibilityImpl getRoleResponsibilityImpl(String roleResponsibilityId) {
    	if ( StringUtils.isBlank( roleResponsibilityId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId );
    	return getBusinessObjectService().findByPrimaryKey( RoleResponsibilityImpl.class, pk );
    }
    
    
    @SuppressWarnings("unchecked")
	protected List<ResponsibilityBo> getResponsibilityImplsByName( String namespaceCode, String responsibilityName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
    	pk.put( KimConstants.UniqueKeyConstants.RESPONSIBILITY_NAME, responsibilityName );
		pk.put( KNSPropertyConstants.ACTIVE, "Y");
    	return (List<ResponsibilityBo>)getBusinessObjectService().findMatching( ResponsibilityBo.class, pk );
    }

    @SuppressWarnings("unchecked")
	protected List<ResponsibilityBo> getResponsibilityImplsByTemplateName( String namespaceCode, String responsibilityTemplateName ) {
    	String cacheKey = getResponsibilityImplByTemplateNameCacheKey(namespaceCode, responsibilityTemplateName);
    	List<ResponsibilityBo> result = (List<ResponsibilityBo>)getCacheAdministrator().getFromCache(cacheKey);
    	if ( result == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 4 );
	    	pk.put( "template."+KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
	    	pk.put( "template."+KimConstants.UniqueKeyConstants.RESPONSIBILITY_TEMPLATE_NAME, responsibilityTemplateName );
			pk.put( "template."+KNSPropertyConstants.ACTIVE, "Y");
			pk.put( KNSPropertyConstants.ACTIVE, "Y");
			result = (List<ResponsibilityBo>)getBusinessObjectService().findMatching( ResponsibilityBo.class, pk );
	    	getCacheAdministrator().putInCache(cacheKey, result, RESPONSIBILITY_IMPL_CACHE_GROUP);
    	}
    	return result;
    }
    
    
    
    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean hasResponsibility(String principalId, String namespaceCode,
    		String responsibilityName, AttributeSet qualification,
    		AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<ResponsibilityBo> responsibilities = getResponsibilityImplsByName( namespaceCode, responsibilityName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );    	
    	List<String> roleIds = getRoleIdsForResponsibilities( applicableResponsibilities, qualification );
    	return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.ResponsibilityService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet, org.kuali.rice.core.util.AttributeSet)
     */
    public boolean hasResponsibilityByTemplateName(String principalId,
    		String namespaceCode, String responsibilityTemplateName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
    	// get all the responsibility objects whose name match that requested
    	List<ResponsibilityBo> responsibilities = getResponsibilityImplsByTemplateName( namespaceCode, responsibilityTemplateName );
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
    	List<ResponsibilityBo> responsibilities = getResponsibilityImplsByName( namespaceCode, responsibilityName );
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
		StringBuilder sb = new StringBuilder();
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
		if (LOG.isTraceEnabled()) { 
			LOG.trace( sb.append(ExceptionUtils.getStackTrace(new Throwable())));
		} else {
			LOG.debug(sb.toString());
		}
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
    	List<ResponsibilityBo> responsibilities = getResponsibilityImplsByTemplateName( namespaceCode, responsibilityTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimResponsibilityInfo> applicableResponsibilities = getMatchingResponsibilities( responsibilities, responsibilityDetails );
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	for ( KimResponsibilityInfo r : applicableResponsibilities ) {
    		List<String> roleIds = getRoleIdsForResponsibility( r, qualification );
    		results.addAll( getActionsForResponsibilityRoles( r, roleIds, qualification) );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Found " + results.size() + " matching ResponsibilityActionInfo objects");
    		if ( LOG.isTraceEnabled() ) {
    			LOG.trace( results );
    		}
    	}
    	return results;
    }
    
    protected List<ResponsibilityActionInfo> getActionsForResponsibilityRoles( KimResponsibilityInfo responsibility, List<String> roleIds, AttributeSet qualification ) {
    	List<ResponsibilityActionInfo> results = new ArrayList<ResponsibilityActionInfo>();
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    	    // only add them to the list if the member ID has been populated
    	    if ( StringUtils.isNotBlank( rm.getMemberId() ) ) {
        		ResponsibilityActionInfo rai;
        		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
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
        		rai.setForceAction( action.isForceAction() );
        		rai.setParallelRoutingGroupingCode( (rm.getRoleSortingCode()==null)?"":rm.getRoleSortingCode() );
        		rai.setRoleResponsibilityActionId( action.getRoleResponsibilityActionId() );
        		results.add( rai );
    	    }
    	}
    	return results;
    }
    
    
    protected Map<String,KimResponsibilityTypeService> getResponsibilityTypeServicesByTemplateId(Collection<ResponsibilityBo> responsibilities) {
    	Map<String,KimResponsibilityTypeService> responsibilityTypeServices = new HashMap<String, KimResponsibilityTypeService>(responsibilities.size());
    	for ( ResponsibilityBo responsibility : responsibilities ) {

            String serviceName = KimApiServiceLocator.getKimTypeInfoService().getKimType(responsibility.getTemplate().getKimTypeId()).getServiceName();
    		if ( serviceName != null ) {
    			KimResponsibilityTypeService responsibiltyTypeService = (KimResponsibilityTypeService) KIMServiceLocatorInternal.getService(serviceName);
    			if ( responsibiltyTypeService != null ) {
    	    		responsibilityTypeServices.put(responsibility.getTemplateId(), responsibiltyTypeService);    				
    			} else {
    				responsibilityTypeServices.put(responsibility.getTemplateId(), getDefaultResponsibilityTypeService());
    			}
    		}
    	}
    	return responsibilityTypeServices;
    }
    
    protected Map<String,List<KimResponsibilityInfo>> groupResponsibilitiesByTemplate(Collection<ResponsibilityBo> responsibilities) {
    	Map<String,List<KimResponsibilityInfo>> results = new HashMap<String,List<KimResponsibilityInfo>>();
    	for (ResponsibilityBo responsibility : responsibilities) {
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
    protected List<KimResponsibilityInfo> getMatchingResponsibilities( List<ResponsibilityBo> responsibilities, AttributeSet responsibilityDetails ) {
    	List<KimResponsibilityInfo> applicableResponsibilities = new ArrayList<KimResponsibilityInfo>();    	
    	if ( responsibilityDetails == null || responsibilityDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		for ( ResponsibilityBo responsibility : responsibilities ) {
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
    		for ( Entry<String,List<KimResponsibilityInfo>> respEntry : responsibilityMap.entrySet() ) {
    			KimResponsibilityTypeService responsibilityTypeService = responsibilityTypeServices.get( respEntry.getKey() );
    			List<KimResponsibilityInfo> responsibilityInfos = respEntry.getValue();
    			if (responsibilityTypeService == null) {
    				responsibilityTypeService = getDefaultResponsibilityTypeService();
    			}
				applicableResponsibilities.addAll(responsibilityTypeService.getMatchingResponsibilities(responsibilityDetails, responsibilityInfos));    				
    		}
    	}
    	return applicableResponsibilities;
    }
	
    protected List<String> getRoleIdsForResponsibilities( List<KimResponsibilityInfo> responsibilities, AttributeSet qualification ) {
    	Collection<String> ids = new ArrayList<String>();
        for (KimResponsibilityInfo r : responsibilities) {
            ids.add(r.getResponsibilityId());
        }

    	// CHECKME: is this right? - the role qualifiers are not being checked
    	return responsibilityDao.getRoleIdsForResponsibilities( ids );
    }

    public List<String> getRoleIdsForResponsibility( KimResponsibilityInfo responsibility, AttributeSet qualification ) {
    	// CHECKME: is this right? - the role qualifiers are not being checked
    	return responsibilityDao.getRoleIdsForResponsibility( responsibility.getResponsibilityId() );
    }

    protected boolean areActionsAtAssignmentLevel( ResponsibilityBo responsibility ) {
    	AttributeSet details = new AttributeSet(responsibility.getAttributes().toMap());
    	if ( details == null ) {
    		return false;
    	}
    	String actionDetailsAtRoleMemberLevel = details.get( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL );
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
    	String actionDetailsAtRoleMemberLevel = details.get( KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL );
    	return Boolean.valueOf(actionDetailsAtRoleMemberLevel);
    }

    /**
     * @see org.kuali.rice.kim.service.ResponsibilityService#areActionsAtAssignmentLevelById(String)
     */
    public boolean areActionsAtAssignmentLevelById( String responsibilityId ) {
    	ResponsibilityBo responsibility = getResponsibilityImpl(responsibilityId);
    	if ( responsibility == null ) {
    		return false;
    	}
    	return areActionsAtAssignmentLevel(responsibility);
    }
    
    @SuppressWarnings("unchecked")
	public List<? extends KimResponsibilityInfo> lookupResponsibilityInfo( Map<String,String> searchCriteria, boolean unbounded ) {

		Lookupable responsibilityLookupable = KNSServiceLocatorWeb.getLookupable(
                KNSServiceLocatorWeb.getBusinessObjectDictionaryService().getLookupableID(ResponsibilityImpl.class)
        );
		responsibilityLookupable.setBusinessObjectClass(ResponsibilityImpl.class);
		final Collection baseResults;
        if ( unbounded ) {
			baseResults = responsibilityLookupable.getSearchResultsUnbounded( searchCriteria );
		} else {
			baseResults = responsibilityLookupable.getSearchResults(searchCriteria);
		}
		List<KimResponsibilityInfo> results = new ArrayList<KimResponsibilityInfo>( baseResults.size() );
		for ( ResponsibilityImpl resp : (Collection<ResponsibilityImpl>)baseResults ) {
			results.add( resp.toSimpleInfo() );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimResponsibilityInfo>( results, ((CollectionIncomplete<KimResponsibilityInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
    	
    }

 
    
    

    
    
    // --------------------
    // Support Methods
    // --------------------
	
	

	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KimApiServiceLocator.getRoleManagementService();
		}

		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public KimResponsibilityDao getResponsibilityDao() {
		return this.responsibilityDao;
	}

	public void setResponsibilityDao(KimResponsibilityDao responsibilityDao) {
		this.responsibilityDao = responsibilityDao;
	}

	protected KimResponsibilityTypeService getDefaultResponsibilityTypeService() {
		if (responsibilityTypeService == null) {
			responsibilityTypeService = (KimResponsibilityTypeService) KIMServiceLocatorInternal.getBean(DEFAULT_RESPONSIBILITY_TYPE_SERVICE);
		}
		return responsibilityTypeService;
	}


	
	
}
