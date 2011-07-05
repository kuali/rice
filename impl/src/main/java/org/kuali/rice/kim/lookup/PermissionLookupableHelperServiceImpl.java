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
 * See the License for the specific language governing permissions and
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
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.GenericPermission;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionLookupableHelperServiceImpl extends RoleMemberLookupableHelperServiceImpl {

	private static final long serialVersionUID = -3578448525862270477L;

	private static final Logger LOG = Logger.getLogger( PermissionLookupableHelperServiceImpl.class );
	
	private static LookupService lookupService;
	private static RoleService roleService;

	private static boolean genericPermissionDocumentTypeNameLoaded = false;
	private static String genericPermissionDocumentTypeName = null;
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
    	List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
    	// convert the PermissionImpl class into a GenericPermission object
    	businessObject = new GenericPermission( (PermissionImpl)businessObject );
        if (allowsMaintenanceEditAction(businessObject)) {
        	htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
        	htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
	}

    @SuppressWarnings("unchecked")
	protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KRADConstants.OVERRIDE_KEYS, KimConstants.PrimaryKeyConstants.PERMISSION_ID);
        parameters.put(KRADConstants.COPY_KEYS, KimConstants.PrimaryKeyConstants.PERMISSION_ID);
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
		if ( !genericPermissionDocumentTypeNameLoaded ) {
			genericPermissionDocumentTypeName = getMaintenanceDocumentDictionaryService().getDocumentTypeName(GenericPermission.class);
			genericPermissionDocumentTypeNameLoaded = true;
		}
		return genericPermissionDocumentTypeName;
	}
		
	/**
	 * @see org.kuali.rice.krad.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
	 */
	@Override
	protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria, boolean unbounded) {
		Map<String, String> permissionSearchCriteria = buildSearchCriteria(searchCriteria);
		Map<String, String> roleSearchCriteria = buildRoleSearchCriteria(searchCriteria);
		boolean permissionCriteriaEmpty = permissionSearchCriteria==null || permissionSearchCriteria.isEmpty();
		boolean roleCriteriaEmpty = roleSearchCriteria==null || roleSearchCriteria.isEmpty();
		
		List<PermissionBo> permissionSearchResultsCopy = new CollectionIncomplete<PermissionBo>(new ArrayList<PermissionBo>(), new Long(0));
		if(!permissionCriteriaEmpty && !roleCriteriaEmpty){
			permissionSearchResultsCopy = getCombinedSearchResults(permissionSearchCriteria, roleSearchCriteria, unbounded);
		} else if(permissionCriteriaEmpty && !roleCriteriaEmpty){
			permissionSearchResultsCopy = getPermissionsWithRoleSearchCriteria(roleSearchCriteria, unbounded);
		} else if(!permissionCriteriaEmpty && roleCriteriaEmpty){
			permissionSearchResultsCopy = getPermissionsWithPermissionSearchCriteria(permissionSearchCriteria, unbounded);
		} else if(permissionCriteriaEmpty && roleCriteriaEmpty){
			return getAllPermissions(unbounded);
		}
		return permissionSearchResultsCopy;
	}
	
	private List<PermissionBo> getAllPermissions(boolean unbounded){
		List<PermissionBo> permissions = searchPermissions(new HashMap<String, String>(), unbounded);
		for(PermissionBo permission: permissions) {
			populateAssignedToRoles(permission);
        }
		return permissions;
	}
	
	private List<PermissionBo> getCombinedSearchResults(
			Map<String, String> permissionSearchCriteria, Map<String, String> roleSearchCriteria, boolean unbounded){
		List<PermissionBo> permissionSearchResults = searchPermissions(permissionSearchCriteria, unbounded);
		List<RoleBo> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		List<PermissionBo> permissionsForRoleSearchResults = getPermissionsForRoleSearchResults(roleSearchResults, unbounded);
		List<PermissionBo> matchedPermissions = new CollectionIncomplete<PermissionBo>(
			new ArrayList<PermissionBo>(), getActualSizeIfTruncated(permissionsForRoleSearchResults));
		if((permissionSearchResults!=null && !permissionSearchResults.isEmpty()) && 
				(permissionsForRoleSearchResults!=null && !permissionsForRoleSearchResults.isEmpty())){
			for(PermissionBo permission: permissionSearchResults){
				for(PermissionBo permissionFromRoleSearch: permissionsForRoleSearchResults){
					if(permissionFromRoleSearch.getId().equals(permission.getId())) {
						matchedPermissions.add(permissionFromRoleSearch);
                    }
				}
			}
		}
		return matchedPermissions;
	}
	
	@SuppressWarnings("unchecked")
	private List<PermissionBo> searchPermissions(Map<String, String> permissionSearchCriteria, boolean unbounded){
		return getPermissionsSearchResultsCopy(new ArrayList<PermissionBo>(getLookupService().findCollectionBySearchHelper(
				PermissionBo.class, permissionSearchCriteria, unbounded)));

	}
	
	private List<PermissionBo> getPermissionsWithRoleSearchCriteria(Map<String, String> roleSearchCriteria, boolean unbounded){
		return getPermissionsForRoleSearchResults(searchRoles(roleSearchCriteria, unbounded), unbounded);
	}

	private List<PermissionBo> getPermissionsForRoleSearchResults(List<RoleBo> roleSearchResults, boolean unbounded){
		Long actualSizeIfTruncated = getActualSizeIfTruncated(roleSearchResults);
		List<PermissionBo> permissions = new ArrayList<PermissionBo>();
		List<PermissionBo> tempPermissions;
		List<String> collectedPermissionIds = new ArrayList<String>();
		Map<String, String> permissionCriteria;
		
		for(RoleBo roleImpl: roleSearchResults){
			permissionCriteria = new HashMap<String, String>();
			permissionCriteria.put("rolePermissions.roleId", roleImpl.getId());
			tempPermissions = searchPermissions(permissionCriteria, unbounded);
			actualSizeIfTruncated += getActualSizeIfTruncated(tempPermissions);
			for(PermissionBo permission: tempPermissions){
				if(!collectedPermissionIds.contains(permission.getId())){
					populateAssignedToRoles(permission);
					collectedPermissionIds.add(permission.getId());
					permissions.add(permission);
				}
			}
			//need to find roles that current role is a member of and build search string
			List<String> parentRoleIds = KimApiServiceLocator.getRoleService().getMemberParentRoleIds(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, roleImpl.getId());
			for (String parentRoleId : parentRoleIds) {
				Map<String, String> roleSearchCriteria = new HashMap<String, String>();
				roleSearchCriteria.put("roleId", parentRoleId);
				//get all parent role permissions and merge them with current permissions
				permissions = mergePermissionLists(permissions, getPermissionsWithRoleSearchCriteria(roleSearchCriteria, unbounded));
			}
		}
		
		return new CollectionIncomplete<PermissionBo>(permissions, actualSizeIfTruncated);
	}
	

	private void populateAssignedToRoles(PermissionBo permission){
		AttributeSet criteria;
		for(RolePermissionBo rolePermission: permission.getRolePermissions()){
			if ( rolePermission.isActive() ) {
				criteria = new AttributeSet();
				criteria.put("id", rolePermission.getRoleId());
	//			permission.getAssignedToRoles().add((RoleBo)getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria));
                RoleBo roleBo = getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria);

			}
		}
	}
	
	/* Since most queries will only be on the template namespace and name, cache the results for 30 seconds
	 * so that queries against the details, which are done in memory, do not require repeated database trips.
	 */
    private static final HashMap<Map<String,String>,MaxAgeSoftReference<List<PermissionBo>>> permResultCache = new HashMap<Map<String,String>, MaxAgeSoftReference<List<PermissionBo>>>();
	private static final long PERM_CACHE_EXPIRE_SECONDS = 30L;
	
	private List<PermissionBo> getPermissionsWithPermissionSearchCriteria(
			Map<String, String> permissionSearchCriteria, boolean unbounded){
		String detailCriteriaStr = permissionSearchCriteria.remove( DETAIL_CRITERIA );
		AttributeSet detailCriteria = parseDetailCriteria(detailCriteriaStr);

		MaxAgeSoftReference<List<PermissionBo>> cachedResult = permResultCache.get(permissionSearchCriteria);
		List<PermissionBo> permissions;
		if ( cachedResult == null || cachedResult.get() == null ) {
			permissions = searchPermissions(permissionSearchCriteria, unbounded);
			synchronized (permResultCache) {
				permResultCache.put(permissionSearchCriteria, new MaxAgeSoftReference<List<PermissionBo>>( PERM_CACHE_EXPIRE_SECONDS, permissions ) );
			} 
		} else {
			permissions = cachedResult.get();
		}
		List<PermissionBo> filteredPermissions = new CollectionIncomplete<PermissionBo>(
				new ArrayList<PermissionBo>(), getActualSizeIfTruncated(permissions));
		for(PermissionBo perm: permissions){
			if ( detailCriteria.isEmpty() ) {
				filteredPermissions.add(perm);
				populateAssignedToRoles(perm);
			} else {
				if ( isMapSubset( new AttributeSet(perm.getDetails().toMap()), detailCriteria ) ) {
					filteredPermissions.add(perm);
					populateAssignedToRoles(perm);
				}
			}
		}
		return filteredPermissions;
	}
	
	private List<PermissionBo> getPermissionsSearchResultsCopy(List<PermissionBo> permissionSearchResults){
		List<PermissionBo> permissionSearchResultsCopy = new CollectionIncomplete<PermissionBo>(
			new ArrayList<PermissionBo>(), getActualSizeIfTruncated(permissionSearchResults));
		for(PermissionBo permissionImpl: permissionSearchResults){
			PermissionBo permissionCopy = new PermissionBo();
			try{
				PropertyUtils.copyProperties(permissionCopy, permissionImpl);
			} catch(Exception ex){
				LOG.error( "Unable to copy properties from KimPermissionImpl to PermissionImpl, skipping.", ex );
				continue;
			}
			permissionSearchResultsCopy.add(permissionCopy);
		}
		return permissionSearchResultsCopy;
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

	public RoleService getRoleService() {
		if (roleService == null) {
			roleService = KimApiServiceLocator.getRoleService();
		}
		return roleService;
	}

	private List<PermissionBo> mergePermissionLists(List<PermissionBo> perm1, List<PermissionBo> perm2) {
		List<PermissionBo> returnList = new ArrayList<PermissionBo>(perm1);
		List<String> permissionIds = new ArrayList<String>(perm1.size());
		for (PermissionBo perm : returnList) {
			permissionIds.add(perm.getId());
		}
		for (int i=0; i<perm2.size(); i++) {
		    if (!permissionIds.contains(perm2.get(i).getId())) {
		    	returnList.add(perm2.get(i));
		    }
		}
		return returnList;
	}
}
