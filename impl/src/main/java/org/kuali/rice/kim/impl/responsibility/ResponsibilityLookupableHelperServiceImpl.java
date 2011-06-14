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
 * See the License for the specific language governing responsibilities and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.responsibility;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityBo;
import org.kuali.rice.kim.impl.responsibility.ReviewResponsibilityBo;
import org.kuali.rice.kim.impl.responsibility.UberResponsibilityBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.kim.lookup.RoleMemberLookupableHelperServiceImpl;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.HtmlData;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityLookupableHelperServiceImpl extends RoleMemberLookupableHelperServiceImpl {

	private static final Logger LOG = Logger.getLogger( ResponsibilityLookupableHelperServiceImpl.class );
	
	private static final long serialVersionUID = -2882500971924192124L;
	
	private static LookupService lookupService;

	private static boolean reviewResponsibilityDocumentTypeNameLoaded = false;
	private static String reviewResponsibilityDocumentTypeName = null;
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
    	List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
    	// convert the UberResponsibilityBo class into a ReviewResponsibility object
        if ( ((UberResponsibilityBo)businessObject).getTemplate().getName().equals( KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME ) ) {
        	ReviewResponsibilityBo reviewResp = new ReviewResponsibilityBo( (UberResponsibilityBo)businessObject );
        	businessObject = reviewResp;
	        if (allowsMaintenanceEditAction(businessObject)) {
	        	htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
	        }
	        if (allowsMaintenanceNewOrCopyAction()) {
	        	htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
	        }
        }
        return htmlDataList;
	}

    @SuppressWarnings("unchecked")
	protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        // TODO: why is this not using the businessObject parmeter's class?
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KRADConstants.OVERRIDE_KEYS, KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID);
        parameters.put(KRADConstants.COPY_KEYS, KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID);
        if (StringUtils.isNotBlank(getReturnLocation())) {
        	parameters.put(KRADConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());
		}
        parameters.putAll(getParametersFromPrimaryKey(businessObject, pkNames));
        return UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);
    }
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.lookup.AbstractLookupableHelperServiceImpl#getMaintenanceDocumentTypeName()
	 */
	@Override
	protected String getMaintenanceDocumentTypeName() {
		if ( !reviewResponsibilityDocumentTypeNameLoaded ) {
			reviewResponsibilityDocumentTypeName = getMaintenanceDocumentDictionaryService().getDocumentTypeName(ReviewResponsibilityBo.class);
			reviewResponsibilityDocumentTypeNameLoaded = true;
		}
		return reviewResponsibilityDocumentTypeName;
	}
	
	/**
	 * @see org.kuali.rice.krad.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria, boolean unbounded) {
		Map<String, String> responsibilitySearchCriteria = buildSearchCriteria(searchCriteria);
		Map<String, String> roleSearchCriteria = buildRoleSearchCriteria(searchCriteria);
		boolean responsibilityCriteriaEmpty = responsibilitySearchCriteria==null || responsibilitySearchCriteria.isEmpty();
		boolean roleCriteriaEmpty = roleSearchCriteria==null || roleSearchCriteria.isEmpty();
		
		List<UberResponsibilityBo> responsibilitySearchResultsCopy = new CollectionIncomplete<UberResponsibilityBo>(new ArrayList<UberResponsibilityBo>(), new Long(0));
		if(!responsibilityCriteriaEmpty && !roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getCombinedSearchResults(responsibilitySearchCriteria, roleSearchCriteria, unbounded);
		} else if(responsibilityCriteriaEmpty && !roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getResponsibilitiesWithRoleSearchCriteria(roleSearchCriteria, unbounded);
		} else if(!responsibilityCriteriaEmpty && roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getResponsibilitiesWithResponsibilitySearchCriteria(responsibilitySearchCriteria, unbounded);
		} else if(responsibilityCriteriaEmpty && roleCriteriaEmpty){
			return getAllResponsibilities(unbounded);
		}
		return responsibilitySearchResultsCopy;
	}
	
	private List<UberResponsibilityBo> getAllResponsibilities(boolean unbounded){
		List<UberResponsibilityBo> responsibilities = searchResponsibilities(new HashMap<String, String>(), unbounded);
		for(UberResponsibilityBo responsibility: responsibilities)
			populateAssignedToRoles(responsibility);
		return responsibilities;
	}
	
	private List<UberResponsibilityBo> getCombinedSearchResults(
			Map<String, String> responsibilitySearchCriteria, Map<String, String> roleSearchCriteria, boolean unbounded){
		List<UberResponsibilityBo> responsibilitySearchResults = searchResponsibilities(responsibilitySearchCriteria, unbounded);
		List<RoleImpl> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		List<UberResponsibilityBo> responsibilitiesForRoleSearchResults = getResponsibilitiesForRoleSearchResults(roleSearchResults, unbounded);
		List<UberResponsibilityBo> matchedResponsibilities = new CollectionIncomplete<UberResponsibilityBo>(
				new ArrayList<UberResponsibilityBo>(), getActualSizeIfTruncated(responsibilitiesForRoleSearchResults));
		if((responsibilitySearchResults!=null && !responsibilitySearchResults.isEmpty()) && 
				(responsibilitiesForRoleSearchResults!=null && !responsibilitiesForRoleSearchResults.isEmpty())){
			for(UberResponsibilityBo responsibility: responsibilitySearchResults){
				for(UberResponsibilityBo responsibilityFromRoleSearch: responsibilitiesForRoleSearchResults){
					if(responsibilityFromRoleSearch.getId().equals(responsibility.getId()))
						matchedResponsibilities.add(responsibilityFromRoleSearch);
				}
			}
		}

		return matchedResponsibilities;
	}
	
	@SuppressWarnings("unchecked")
	private List<UberResponsibilityBo> searchResponsibilities(Map<String, String> responsibilitySearchCriteria, boolean unbounded){
		return getResponsibilitiesSearchResultsCopy((List<ResponsibilityBo>)
					getLookupService().findCollectionBySearchHelper(
							ResponsibilityBo.class, responsibilitySearchCriteria, unbounded));
	}
	
	private List<UberResponsibilityBo> getResponsibilitiesWithRoleSearchCriteria(Map<String, String> roleSearchCriteria, boolean unbounded){
		List<RoleImpl> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		return getResponsibilitiesForRoleSearchResults(roleSearchResults, unbounded);
	}

	private List<UberResponsibilityBo> getResponsibilitiesForRoleSearchResults(List<RoleImpl> roleSearchResults, boolean unbounded){
		Long actualSizeIfTruncated = getActualSizeIfTruncated(roleSearchResults);
		List<UberResponsibilityBo> responsibilities = new ArrayList<UberResponsibilityBo>();
		List<UberResponsibilityBo> tempResponsibilities;
		List<String> collectedResponsibilityIds = new ArrayList<String>();
		Map<String, String> responsibilityCriteria;
		
		for(RoleImpl roleImpl: roleSearchResults){
			responsibilityCriteria = new HashMap<String, String>();
			responsibilityCriteria.put("roleResponsibilities.roleId", roleImpl.getRoleId());
			tempResponsibilities = searchResponsibilities(responsibilityCriteria, unbounded);
			actualSizeIfTruncated += getActualSizeIfTruncated(tempResponsibilities);
			for(UberResponsibilityBo responsibility: tempResponsibilities){
				if(!collectedResponsibilityIds.contains(responsibility.getId())){
					populateAssignedToRoles(responsibility);
					collectedResponsibilityIds.add(responsibility.getId());
					responsibilities.add(responsibility);
				}
				//need to find roles that current role is a member of and build search string
				List<String> parentRoleIds = KimApiServiceLocator.getRoleService().getMemberParentRoleIds(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, roleImpl.getRoleId());
				for (String parentRoleId : parentRoleIds) {
					Map<String, String> roleSearchCriteria = new HashMap<String, String>();
					roleSearchCriteria.put("roleId", parentRoleId);
					//get all parent role permissions and merge them with current permissions
					responsibilities = mergeResponsibilityLists(responsibilities, getResponsibilitiesWithRoleSearchCriteria(roleSearchCriteria, unbounded));
				}
			}
		}
		return new CollectionIncomplete<UberResponsibilityBo>(responsibilities, actualSizeIfTruncated);
	}

	private void populateAssignedToRoles(UberResponsibilityBo responsibility){
		AttributeSet criteria = new AttributeSet();
		if ( responsibility.getAssignedToRoles().isEmpty() ) {
			for(RoleResponsibilityBo roleResponsibility: responsibility.getRoleResponsibilities()){
				criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleResponsibility.getRoleId());
				responsibility.getAssignedToRoles().add((RoleBo)getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria));
			}
		}
	}
	
	/* Since most queries will only be on the template namespace and name, cache the results for 30 seconds
	 * so that queries against the details, which are done in memory, do not require repeated database trips.
	 */
    private static final Map<Map<String,String>,MaxAgeSoftReference<List<UberResponsibilityBo>>> respResultCache = new HashMap<Map<String,String>, MaxAgeSoftReference<List<UberResponsibilityBo>>>(); 
	private static final long RESP_CACHE_EXPIRE_SECONDS = 30L;
	
	private List<UberResponsibilityBo> getResponsibilitiesWithResponsibilitySearchCriteria(Map<String, String> responsibilitySearchCriteria, boolean unbounded){
		String detailCriteriaStr = responsibilitySearchCriteria.remove( DETAIL_CRITERIA );
		AttributeSet detailCriteria = parseDetailCriteria(detailCriteriaStr);
		MaxAgeSoftReference<List<UberResponsibilityBo>> cachedResult = respResultCache.get(responsibilitySearchCriteria);
		List<UberResponsibilityBo> responsibilities = null;
		if ( cachedResult == null || cachedResult.get() == null ) {
			responsibilities = searchResponsibilities(responsibilitySearchCriteria, unbounded);
			synchronized( respResultCache ) {
				respResultCache.put(responsibilitySearchCriteria, new MaxAgeSoftReference<List<UberResponsibilityBo>>( RESP_CACHE_EXPIRE_SECONDS, responsibilities ) ); 
			}
		} else {
			responsibilities = cachedResult.get();
		}
		List<UberResponsibilityBo> filteredResponsibilities = new CollectionIncomplete<UberResponsibilityBo>(
				new ArrayList<UberResponsibilityBo>(), getActualSizeIfTruncated(responsibilities)); 
		for(UberResponsibilityBo responsibility: responsibilities){
			if ( detailCriteria.isEmpty() ) {
				filteredResponsibilities.add(responsibility);
				populateAssignedToRoles(responsibility);
			} else {
				if ( isMapSubset( new AttributeSet(responsibility.getAttributes().toMap()), detailCriteria ) ) {
					filteredResponsibilities.add(responsibility);
					populateAssignedToRoles(responsibility);
				}
			}
		}
		return filteredResponsibilities;
	}
	
	private List<UberResponsibilityBo> getResponsibilitiesSearchResultsCopy(List<ResponsibilityBo> responsibilitySearchResults){
		List<UberResponsibilityBo> responsibilitySearchResultsCopy = new CollectionIncomplete<UberResponsibilityBo>(
				new ArrayList<UberResponsibilityBo>(), getActualSizeIfTruncated(responsibilitySearchResults));
		for(ResponsibilityBo responsibilityImpl: responsibilitySearchResults){
			UberResponsibilityBo responsibilityCopy = new UberResponsibilityBo();
			try{
				PropertyUtils.copyProperties(responsibilityCopy, responsibilityImpl);
			} catch(Exception ex){
				LOG.error( "Unable to copy properties from KimUberResponsibilityBo to UberResponsibilityBo, skipping.", ex );
				continue;
			}
			responsibilitySearchResultsCopy.add(responsibilityCopy);
		}
		return responsibilitySearchResultsCopy;
	}
	

	/**
	 * @return the lookupService
	 */
	public LookupService getLookupService() {
		if ( lookupService == null ) {
			lookupService = KRADServiceLocatorWeb.getLookupService();
		}
		return lookupService;
	}
 
	private List<UberResponsibilityBo> mergeResponsibilityLists(List<UberResponsibilityBo> perm1, List<UberResponsibilityBo> perm2) {
		List<UberResponsibilityBo> returnList = new ArrayList<UberResponsibilityBo>(perm1);
		List<String> responsibilityIds = new ArrayList<String>(perm1.size());
		for (UberResponsibilityBo perm : returnList) {
			responsibilityIds.add(perm.getId());
		}
		for (int i=0; i<perm2.size(); i++) {
		    if (!responsibilityIds.contains(perm2.get(i).getId())) {
		    	returnList.add(perm2.get(i));
		    }
		}
		return returnList;
	}
}
