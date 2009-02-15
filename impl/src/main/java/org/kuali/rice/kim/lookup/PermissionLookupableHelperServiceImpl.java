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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
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
public class PermissionLookupableHelperServiceImpl extends RoleMemberLookupableHelperServiceImpl {

	private RoleService roleService;
	private LookupService lookupService;
	
	/**
	 * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria) {
		Map<String, String> permissionSearchCriteria = buildSearchCriteria(searchCriteria);
		Map<String, String> roleSearchCriteria = buildRoleSearchCriteria(searchCriteria);
		boolean permissionCriteriaEmpty = permissionSearchCriteria==null || permissionSearchCriteria.isEmpty();
		boolean roleCriteriaEmpty = roleSearchCriteria==null || roleSearchCriteria.isEmpty();
		
		List<PermissionImpl> permissionSearchResultsCopy = new ArrayList<PermissionImpl>();
		if(!permissionCriteriaEmpty && !roleCriteriaEmpty){
			permissionSearchResultsCopy = getCombinedSearchResults(permissionSearchCriteria, roleSearchCriteria);
		} else if(permissionCriteriaEmpty && !roleCriteriaEmpty){
			permissionSearchResultsCopy = getPermissionsWithRoleSearchCriteria(roleSearchCriteria);
		} else if(!permissionCriteriaEmpty && roleCriteriaEmpty){
			permissionSearchResultsCopy = getPermissionsWithPermissionSearchCriteria(permissionSearchCriteria);
		} else if(permissionCriteriaEmpty && roleCriteriaEmpty){
			return getAllPermissions();
		}
		return permissionSearchResultsCopy;
	}
	
	private List<PermissionImpl> getAllPermissions(){
		List<PermissionImpl> permissions = searchPermissions(new HashMap<String, String>());
		for(PermissionImpl permission: permissions)
			populateAssignedToRoles(permission);
		return permissions;
	}
	
	private List<PermissionImpl> getCombinedSearchResults(
			Map<String, String> permissionSearchCriteria, Map<String, String> roleSearchCriteria){
		List<PermissionImpl> permissionSearchResults = searchPermissions(permissionSearchCriteria);
		List<KimRoleImpl> roleSearchResults = searchRoles(roleSearchCriteria);
		List<PermissionImpl> permissionsForRoleSearchResults = getPermissionsForRoleSearchResults(roleSearchResults);
		List<PermissionImpl> matchedPermissions = new ArrayList<PermissionImpl>();
		if((permissionSearchResults!=null && !permissionSearchResults.isEmpty()) && 
				(permissionsForRoleSearchResults!=null && !permissionsForRoleSearchResults.isEmpty())){
			for(PermissionImpl permission: permissionSearchResults){
				for(PermissionImpl permissionFromRoleSearch: permissionsForRoleSearchResults){
					if(permissionFromRoleSearch.getPermissionId().equals(permission.getPermissionId()))
						matchedPermissions.add(permissionFromRoleSearch);
				}
			}
		}
		/*for(PermissionImpl permission: permissionSearchResults){
			for(RolePermissionImpl rolePermission: permission.getRolePermissions()){
				for(KimRoleImpl roleImpl: roleSearchResults){
					if(roleImpl.getRoleId().equals(rolePermission.getRoleId())){
						permission.getAssignedToRoles().add(roleImpl);
						matchedPermissions.add(permission);
					}
				}
			}
		}*/
		return matchedPermissions;
	}
	
	private List<PermissionImpl> searchPermissions(Map<String, String> permissionSearchCriteria){
		return getPermissionsSearchResultsCopy((List<KimPermissionImpl>)
					KNSServiceLocator.getLookupService().findCollectionBySearchHelper(
							KimPermissionImpl.class, permissionSearchCriteria, false));	
	}
	
	private List<KimRoleImpl> searchRoles(Map<String, String> roleSearchCriteria){
		return (List<KimRoleImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchHelper(
					KimRoleImpl.class, roleSearchCriteria, false);
	}
	
	private List<PermissionImpl> getPermissionsWithRoleSearchCriteria(Map<String, String> roleSearchCriteria){
		List<KimRoleImpl> roleSearchResults = searchRoles(roleSearchCriteria);
		return getPermissionsForRoleSearchResults(roleSearchResults);
	}

	private List<PermissionImpl> getPermissionsForRoleSearchResults(List<KimRoleImpl> roleSearchResults){
		List<PermissionImpl> permissions = new ArrayList<PermissionImpl>();
		List<PermissionImpl> tempPermissions;
		List<String> collectedPermissionIds = new ArrayList<String>();
		Map<String, String> permissionCriteria;
		for(KimRoleImpl roleImpl: roleSearchResults){
			permissionCriteria = new HashMap<String, String>();
			permissionCriteria.put("rolePermissions.roleId", roleImpl.getRoleId());
			tempPermissions = searchPermissions(permissionCriteria);
			for(PermissionImpl permission: tempPermissions){
				if(!collectedPermissionIds.contains(permission.getPermissionId())){
					populateAssignedToRoles(permission);
					collectedPermissionIds.add(permission.getPermissionId());
					permissions.add(permission);
				}
			}
		}
		return permissions;
	}

	private void populateAssignedToRoles(PermissionImpl permission){
		AttributeSet criteria;
		for(RolePermissionImpl rolePermission: permission.getRolePermissions()){
			criteria = new AttributeSet();
			criteria.put("roleId", rolePermission.getRoleId());
			permission.getAssignedToRoles().add((KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria));
		}
	}
	
	private List<PermissionImpl> getPermissionsWithPermissionSearchCriteria(Map<String, String> permissionSearchCriteria){
		List<PermissionImpl> permissions = searchPermissions(permissionSearchCriteria);
		for(PermissionImpl permission: permissions){
			populateAssignedToRoles(permission);
		}
		return permissions;
	}
	
	private List<PermissionImpl> getPermissionsSearchResultsCopy(List<KimPermissionImpl> permissionSearchResults){
		List<PermissionImpl> permissionSearchResultsCopy = new ArrayList<PermissionImpl>();
		PermissionImpl permissionCopy;
		for(KimPermissionImpl permissionImpl: permissionSearchResults){
			permissionCopy = new PermissionImpl();
			try{
				PropertyUtils.copyProperties(permissionCopy, permissionImpl);
			} catch(Exception ex){
				//TODO: remove this
				ex.printStackTrace();
			}
			permissionSearchResultsCopy.add(permissionCopy);
		}
		return permissionSearchResultsCopy;
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