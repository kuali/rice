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
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleService;
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

	private RoleService roleService;
	private LookupService lookupService;

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
	
	private List<ResponsibilityImpl> searchResponsibilities(Map<String, String> responsibilitySearchCriteria){
		return getResponsibilitiesSearchResultsCopy((List<KimResponsibilityImpl>)
					KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(
							KimResponsibilityImpl.class, responsibilitySearchCriteria));	
	}
	
	private List<KimRoleImpl> searchRoles(Map<String, String> roleSearchCriteria){
		return (List<KimRoleImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(
					KimRoleImpl.class, roleSearchCriteria);
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
		AttributeSet criteria;
		for(RoleResponsibilityImpl roleResponsibility: responsibility.getRoleResponsibilities()){
			criteria = new AttributeSet();
			criteria.put("roleId", roleResponsibility.getRoleId());
			responsibility.getAssignedToRoles().add((KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria));
		}
	}
	
	private List<ResponsibilityImpl> getResponsibilitiesWithResponsibilitySearchCriteria(Map<String, String> responsibilitySearchCriteria){
		List<ResponsibilityImpl> responsibilities = searchResponsibilities(responsibilitySearchCriteria);
		for(ResponsibilityImpl responsibility: responsibilities){
			populateAssignedToRoles(responsibility);
		}
		return responsibilities;
	}
	
	private List<ResponsibilityImpl> getResponsibilitiesSearchResultsCopy(List<KimResponsibilityImpl> responsibilitySearchResults){
		List<ResponsibilityImpl> responsibilitySearchResultsCopy = new ArrayList<ResponsibilityImpl>();
		ResponsibilityImpl responsibilityCopy;
		for(KimResponsibilityImpl responsibilityImpl: responsibilitySearchResults){
			responsibilityCopy = new ResponsibilityImpl();
			try{
				PropertyUtils.copyProperties(responsibilityCopy, responsibilityImpl);
			} catch(Exception ex){
				//TODO: remove this
				ex.printStackTrace();
			}
			responsibilitySearchResultsCopy.add(responsibilityCopy);
		}
		return responsibilitySearchResultsCopy;
	}
	

	/**
	 * @return the lookupService
	 */
	public LookupService getLookupService() {
		return this.lookupService;
	}

	/**
	 * @param lookupService the lookupService to set
	 */
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	/**
	 * @return the roleService
	 */
	public RoleService getRoleService() {
		return this.roleService;
	}

	/**
	 * @param roleService the roleService to set
	 */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

 
}