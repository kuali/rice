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
 * See the License for the specific language governing responsibilities and
 * limitations under the License.
 */
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityLookupableHelperServiceImpl extends RoleMemberLookupableHelperServiceImpl {

	private static final Logger LOG = Logger.getLogger( ResponsibilityLookupableHelperServiceImpl.class );
	
	private static final long serialVersionUID = -2882500971924192124L;
	
	private static LookupService lookupService;

	/**
	 * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria) {
		Map<String, String> responsibilitySearchCriteria = buildSearchCriteria(searchCriteria);
		Map<String, String> roleSearchCriteria = buildRoleSearchCriteria(searchCriteria);
		boolean responsibilityCriteriaEmpty = responsibilitySearchCriteria==null || responsibilitySearchCriteria.isEmpty();
		boolean roleCriteriaEmpty = roleSearchCriteria==null || roleSearchCriteria.isEmpty();
		
		List<ResponsibilityImpl> responsibilitySearchResultsCopy = new ArrayList<ResponsibilityImpl>();
		if(!responsibilityCriteriaEmpty && !roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getCombinedSearchResults(responsibilitySearchCriteria, roleSearchCriteria);
		} else if(responsibilityCriteriaEmpty && !roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getResponsibilitiesWithRoleSearchCriteria(roleSearchCriteria);
		} else if(!responsibilityCriteriaEmpty && roleCriteriaEmpty){
			responsibilitySearchResultsCopy = getResponsibilitiesWithResponsibilitySearchCriteria(responsibilitySearchCriteria);
		} else if(responsibilityCriteriaEmpty && roleCriteriaEmpty){
			return getAllResponsibilities();
		}
		return responsibilitySearchResultsCopy;
	}
	
	private List<ResponsibilityImpl> getAllResponsibilities(){
		List<ResponsibilityImpl> responsibilities = searchResponsibilities(new HashMap<String, String>());
		for(ResponsibilityImpl responsibility: responsibilities)
			populateAssignedToRoles(responsibility);
		return responsibilities;
	}
	
	private List<ResponsibilityImpl> getCombinedSearchResults(
			Map<String, String> responsibilitySearchCriteria, Map<String, String> roleSearchCriteria){
		List<ResponsibilityImpl> responsibilitySearchResults = searchResponsibilities(responsibilitySearchCriteria);
		List<KimRoleImpl> roleSearchResults = searchRoles(roleSearchCriteria);
		List<ResponsibilityImpl> responsibilitiesForRoleSearchResults = getResponsibilitiesForRoleSearchResults(roleSearchResults);
		List<ResponsibilityImpl> matchedResponsibilities = new ArrayList<ResponsibilityImpl>();
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
				for(KimRoleImpl roleImpl: roleSearchResults){
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
	private List<ResponsibilityImpl> searchResponsibilities(Map<String, String> responsibilitySearchCriteria){
		return getResponsibilitiesSearchResultsCopy((List<KimResponsibilityImpl>)
					getLookupService().findCollectionBySearchHelper(
							KimResponsibilityImpl.class, responsibilitySearchCriteria, true));	
	}
	
	@SuppressWarnings("unchecked")
	private List<KimRoleImpl> searchRoles(Map<String, String> roleSearchCriteria){
		return (List<KimRoleImpl>)getLookupService().findCollectionBySearchHelper(
					KimRoleImpl.class, roleSearchCriteria, true);
	}
	
	private List<ResponsibilityImpl> getResponsibilitiesWithRoleSearchCriteria(Map<String, String> roleSearchCriteria){
		List<KimRoleImpl> roleSearchResults = searchRoles(roleSearchCriteria);
		return getResponsibilitiesForRoleSearchResults(roleSearchResults);
	}

	private List<ResponsibilityImpl> getResponsibilitiesForRoleSearchResults(List<KimRoleImpl> roleSearchResults){
		List<ResponsibilityImpl> responsibilities = new ArrayList<ResponsibilityImpl>();
		List<ResponsibilityImpl> tempResponsibilities;
		List<String> collectedResponsibilityIds = new ArrayList<String>();
		Map<String, String> responsibilityCriteria;
		for(KimRoleImpl roleImpl: roleSearchResults){
			responsibilityCriteria = new HashMap<String, String>();
			responsibilityCriteria.put("roleResponsibilities.roleId", roleImpl.getRoleId());
			tempResponsibilities = searchResponsibilities(responsibilityCriteria);
			for(ResponsibilityImpl responsibility: tempResponsibilities){
				if(!collectedResponsibilityIds.contains(responsibility.getResponsibilityId())){
					populateAssignedToRoles(responsibility);
					collectedResponsibilityIds.add(responsibility.getResponsibilityId());
					responsibilities.add(responsibility);
				}
			}
		}
		return responsibilities;
	}

	private void populateAssignedToRoles(ResponsibilityImpl responsibility){
		AttributeSet criteria = new AttributeSet();
		if ( responsibility.getAssignedToRoles().isEmpty() ) {
			for(RoleResponsibilityImpl roleResponsibility: responsibility.getRoleResponsibilities()){
				criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleResponsibility.getRoleId());
				responsibility.getAssignedToRoles().add((KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria));
			}
		}
	}
	
	/* Since most queries will only be on the template namespace and name, cache the results for 30 seconds
	 * so that queries against the details, which are done in memory, do not require repeated database trips.
	 */
    private static final Map<Map<String,String>,MaxAgeSoftReference<List<ResponsibilityImpl>>> respResultCache = new HashMap<Map<String,String>, MaxAgeSoftReference<List<ResponsibilityImpl>>>(); 
	private static final long RESP_CACHE_EXPIRE_SECONDS = 30L;
	
	private List<ResponsibilityImpl> getResponsibilitiesWithResponsibilitySearchCriteria(Map<String, String> responsibilitySearchCriteria){
		String detailCriteriaStr = responsibilitySearchCriteria.get( DETAIL_CRITERIA );
		AttributeSet detailCriteria = parseDetailCriteria(detailCriteriaStr);
//		if ( LOG.isDebugEnabled() ) {
//			LOG.debug("Detail Criteria: " + detailCriteriaStr);
//			LOG.debug("Parsed Detail Criteria: " + detailCriteria);
//		}
		MaxAgeSoftReference<List<ResponsibilityImpl>> cachedResult = respResultCache.get(responsibilitySearchCriteria);
		List<ResponsibilityImpl> responsibilities = null;
		if ( cachedResult == null || cachedResult.get() == null ) {
			responsibilities = searchResponsibilities(responsibilitySearchCriteria);
			respResultCache.put(responsibilitySearchCriteria, new MaxAgeSoftReference<List<ResponsibilityImpl>>( RESP_CACHE_EXPIRE_SECONDS, responsibilities ) ); 
		} else {
			responsibilities = cachedResult.get();
		}
		List<ResponsibilityImpl> filteredResponsibilities = new ArrayList<ResponsibilityImpl>(); 
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
		List<ResponsibilityImpl> responsibilitySearchResultsCopy = new ArrayList<ResponsibilityImpl>();
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
			lookupService = KNSServiceLocator.getLookupService();
		}
		return lookupService;
	}
 
}