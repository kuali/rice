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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.impl.ReviewResponsibility;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;

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
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
    	List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
    	// convert the ResponsibilityImpl class into a ReviewResponsibility object
        if ( ((ResponsibilityImpl)businessObject).getTemplate().getName().equals( KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME ) ) {
        	ReviewResponsibility reviewResp = new ReviewResponsibility( (ResponsibilityImpl)businessObject );
        	businessObject = reviewResp;
	        if (allowsMaintenanceEditAction(businessObject)) {
	        	htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
	        }
	        if (allowsMaintenanceNewOrCopyAction()) {
	        	htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
	        }
        }
        return htmlDataList;
	}

    @SuppressWarnings("unchecked")
	protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        // TODO: why is this not using the businessObject parmeter's class?
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KNSConstants.OVERRIDE_KEYS, KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID);
        parameters.put(KNSConstants.COPY_KEYS, KimConstants.PrimaryKeyConstants.RESPONSIBILITY_ID);
        if (StringUtils.isNotBlank(getReturnLocation())) {
        	parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());	 
		}
        parameters.putAll(getParametersFromPrimaryKey(businessObject, pkNames));
        return UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);
    }
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getMaintenanceDocumentTypeName()
	 */
	@Override
	protected String getMaintenanceDocumentTypeName() {
		if ( !reviewResponsibilityDocumentTypeNameLoaded ) {
			reviewResponsibilityDocumentTypeName = getMaintenanceDocumentDictionaryService().getDocumentTypeName(ReviewResponsibility.class);
			reviewResponsibilityDocumentTypeNameLoaded = true;
		}
		return reviewResponsibilityDocumentTypeName;
	}
	
	/**
	 * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria, boolean unbounded) {
		Map<String, String> responsibilitySearchCriteria = buildSearchCriteria(searchCriteria);
		Map<String, String> roleSearchCriteria = buildRoleSearchCriteria(searchCriteria);
		boolean responsibilityCriteriaEmpty = responsibilitySearchCriteria==null || responsibilitySearchCriteria.isEmpty();
		boolean roleCriteriaEmpty = roleSearchCriteria==null || roleSearchCriteria.isEmpty();
		
		List<ResponsibilityImpl> responsibilitySearchResultsCopy = new CollectionIncomplete<ResponsibilityImpl>(new ArrayList<ResponsibilityImpl>(), new Long(0));
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
	
	private List<ResponsibilityImpl> getAllResponsibilities(boolean unbounded){
		List<ResponsibilityImpl> responsibilities = searchResponsibilities(new HashMap<String, String>(), unbounded);
		for(ResponsibilityImpl responsibility: responsibilities)
			populateAssignedToRoles(responsibility);
		return responsibilities;
	}
	
	private List<ResponsibilityImpl> getCombinedSearchResults(
			Map<String, String> responsibilitySearchCriteria, Map<String, String> roleSearchCriteria, boolean unbounded){
		List<ResponsibilityImpl> responsibilitySearchResults = searchResponsibilities(responsibilitySearchCriteria, unbounded);
		List<RoleImpl> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		List<ResponsibilityImpl> responsibilitiesForRoleSearchResults = getResponsibilitiesForRoleSearchResults(roleSearchResults, unbounded);
		List<ResponsibilityImpl> matchedResponsibilities = new CollectionIncomplete<ResponsibilityImpl>(
				new ArrayList<ResponsibilityImpl>(), getActualSizeIfTruncated(responsibilitiesForRoleSearchResults));
		if((responsibilitySearchResults!=null && !responsibilitySearchResults.isEmpty()) && 
				(responsibilitiesForRoleSearchResults!=null && !responsibilitiesForRoleSearchResults.isEmpty())){
			for(ResponsibilityImpl responsibility: responsibilitySearchResults){
				for(ResponsibilityImpl responsibilityFromRoleSearch: responsibilitiesForRoleSearchResults){
					if(responsibilityFromRoleSearch.getResponsibilityId().equals(responsibility.getResponsibilityId()))
						matchedResponsibilities.add(responsibilityFromRoleSearch);
				}
			}
		}
		/*for(ResponsibilityImpl responsibility: responsibilitySearchResults){
			for(RoleResponsibilityImpl roleResponsibility: responsibility.getRoleResponsibilities()){
				for(RoleImpl roleImpl: roleSearchResults){
					if(roleImpl.getRoleId().equals(roleResponsibility.getRoleId())){
						responsibility.getAssignedToRoles().add(roleImpl);
						matchedResponsibilities.add(responsibility);
					}
				}
			}
		}*/
		return matchedResponsibilities;
	}
	
	@SuppressWarnings("unchecked")
	private List<ResponsibilityImpl> searchResponsibilities(Map<String, String> responsibilitySearchCriteria, boolean unbounded){
		return getResponsibilitiesSearchResultsCopy((List<KimResponsibilityImpl>)
					getLookupService().findCollectionBySearchHelper(
							KimResponsibilityImpl.class, responsibilitySearchCriteria, unbounded));	
	}
	
	private List<ResponsibilityImpl> getResponsibilitiesWithRoleSearchCriteria(Map<String, String> roleSearchCriteria, boolean unbounded){
		List<RoleImpl> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		return getResponsibilitiesForRoleSearchResults(roleSearchResults, unbounded);
	}

	private List<ResponsibilityImpl> getResponsibilitiesForRoleSearchResults(List<RoleImpl> roleSearchResults, boolean unbounded){
		Long actualSizeIfTruncated = getActualSizeIfTruncated(roleSearchResults);
		List<ResponsibilityImpl> responsibilities = new ArrayList<ResponsibilityImpl>();
		List<ResponsibilityImpl> tempResponsibilities;
		List<String> collectedResponsibilityIds = new ArrayList<String>();
		Map<String, String> responsibilityCriteria;
		
		for(RoleImpl roleImpl: roleSearchResults){
			responsibilityCriteria = new HashMap<String, String>();
			responsibilityCriteria.put("roleResponsibilities.roleId", roleImpl.getRoleId());
			tempResponsibilities = searchResponsibilities(responsibilityCriteria, unbounded);
			actualSizeIfTruncated += getActualSizeIfTruncated(tempResponsibilities);
			for(ResponsibilityImpl responsibility: tempResponsibilities){
				if(!collectedResponsibilityIds.contains(responsibility.getResponsibilityId())){
					populateAssignedToRoles(responsibility);
					collectedResponsibilityIds.add(responsibility.getResponsibilityId());
					responsibilities.add(responsibility);
				}
				//need to find roles that current role is a member of and build search string
				List<String> parentRoleIds = KIMServiceLocator.getRoleService().getMemberParentRoleIds(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, roleImpl.getRoleId());
				for (String parentRoleId : parentRoleIds) {
					Map<String, String> roleSearchCriteria = new HashMap<String, String>();
					roleSearchCriteria.put("roleId", parentRoleId);
					//get all parent role permissions and merge them with current permissions
					responsibilities = mergeResponsibilityLists(responsibilities, getResponsibilitiesWithRoleSearchCriteria(roleSearchCriteria, unbounded));
				}
			}
		}
		return new CollectionIncomplete<ResponsibilityImpl>(responsibilities, actualSizeIfTruncated);
	}

	private void populateAssignedToRoles(ResponsibilityImpl responsibility){
		AttributeSet criteria = new AttributeSet();
		if ( responsibility.getAssignedToRoles().isEmpty() ) {
			for(RoleResponsibilityImpl roleResponsibility: responsibility.getRoleResponsibilities()){
				criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleResponsibility.getRoleId());
				responsibility.getAssignedToRoles().add((RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria));
			}
		}
	}
	
	/* Since most queries will only be on the template namespace and name, cache the results for 30 seconds
	 * so that queries against the details, which are done in memory, do not require repeated database trips.
	 */
    private static final Map<Map<String,String>,MaxAgeSoftReference<List<ResponsibilityImpl>>> respResultCache = new HashMap<Map<String,String>, MaxAgeSoftReference<List<ResponsibilityImpl>>>(); 
	private static final long RESP_CACHE_EXPIRE_SECONDS = 30L;
	
	private List<ResponsibilityImpl> getResponsibilitiesWithResponsibilitySearchCriteria(Map<String, String> responsibilitySearchCriteria, boolean unbounded){
		String detailCriteriaStr = responsibilitySearchCriteria.remove( DETAIL_CRITERIA );
		AttributeSet detailCriteria = parseDetailCriteria(detailCriteriaStr);
		MaxAgeSoftReference<List<ResponsibilityImpl>> cachedResult = respResultCache.get(responsibilitySearchCriteria);
		List<ResponsibilityImpl> responsibilities = null;
		if ( cachedResult == null || cachedResult.get() == null ) {
			responsibilities = searchResponsibilities(responsibilitySearchCriteria, unbounded);
			synchronized( respResultCache ) {
				respResultCache.put(responsibilitySearchCriteria, new MaxAgeSoftReference<List<ResponsibilityImpl>>( RESP_CACHE_EXPIRE_SECONDS, responsibilities ) ); 
			}
		} else {
			responsibilities = cachedResult.get();
		}
		List<ResponsibilityImpl> filteredResponsibilities = new CollectionIncomplete<ResponsibilityImpl>(
				new ArrayList<ResponsibilityImpl>(), getActualSizeIfTruncated(responsibilities)); 
		for(ResponsibilityImpl responsibility: responsibilities){
			if ( detailCriteria.isEmpty() ) {
				filteredResponsibilities.add(responsibility);
				populateAssignedToRoles(responsibility);
			} else {
				if ( isMapSubset( responsibility.getDetails(), detailCriteria ) ) {
					filteredResponsibilities.add(responsibility);
					populateAssignedToRoles(responsibility);
				}
			}
		}
		return filteredResponsibilities;
	}
	
	private List<ResponsibilityImpl> getResponsibilitiesSearchResultsCopy(List<KimResponsibilityImpl> responsibilitySearchResults){
		List<ResponsibilityImpl> responsibilitySearchResultsCopy = new CollectionIncomplete<ResponsibilityImpl>(
				new ArrayList<ResponsibilityImpl>(), getActualSizeIfTruncated(responsibilitySearchResults));
		for(KimResponsibilityImpl responsibilityImpl: responsibilitySearchResults){
			ResponsibilityImpl responsibilityCopy = new ResponsibilityImpl();
			try{
				PropertyUtils.copyProperties(responsibilityCopy, responsibilityImpl);
			} catch(Exception ex){
				LOG.error( "Unable to copy properties from KimResponsibilityImpl to ResponsibilityImpl, skipping.", ex );
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
			lookupService = KNSServiceLocatorWeb.getLookupService();
		}
		return lookupService;
	}
 
	private List<ResponsibilityImpl> mergeResponsibilityLists(List<ResponsibilityImpl> perm1, List<ResponsibilityImpl> perm2) {
		List<ResponsibilityImpl> returnList = new ArrayList<ResponsibilityImpl>(perm1);
		List<String> responsibilityIds = new ArrayList<String>(perm1.size());
		for (ResponsibilityImpl perm : returnList) {
			responsibilityIds.add(perm.getResponsibilityId());
		}
		for (int i=0; i<perm2.size(); i++) {
		    if (!responsibilityIds.contains(perm2.get(i).getResponsibilityId())) {
		    	returnList.add(perm2.get(i));
		    }
		}
		return returnList;
	}
}
