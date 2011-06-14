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
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.GenericPermission;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.HtmlData;
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
		
		List<PermissionImpl> permissionSearchResultsCopy = new CollectionIncomplete<PermissionImpl>(new ArrayList<PermissionImpl>(), new Long(0));
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
	
	private List<PermissionImpl> getAllPermissions(boolean unbounded){
		List<PermissionImpl> permissions = searchPermissions(new HashMap<String, String>(), unbounded);
		for(PermissionImpl permission: permissions)
			populateAssignedToRoles(permission);
		return permissions;
	}
	
	private List<PermissionImpl> getCombinedSearchResults(
			Map<String, String> permissionSearchCriteria, Map<String, String> roleSearchCriteria, boolean unbounded){
		List<PermissionImpl> permissionSearchResults = searchPermissions(permissionSearchCriteria, unbounded);
		List<RoleImpl> roleSearchResults = searchRoles(roleSearchCriteria, unbounded);
		List<PermissionImpl> permissionsForRoleSearchResults = getPermissionsForRoleSearchResults(roleSearchResults, unbounded);
		List<PermissionImpl> matchedPermissions = new CollectionIncomplete<PermissionImpl>(
			new ArrayList<PermissionImpl>(), getActualSizeIfTruncated(permissionsForRoleSearchResults));
		if((permissionSearchResults!=null && !permissionSearchResults.isEmpty()) && 
				(permissionsForRoleSearchResults!=null && !permissionsForRoleSearchResults.isEmpty())){
			for(PermissionImpl permission: permissionSearchResults){
				for(PermissionImpl permissionFromRoleSearch: permissionsForRoleSearchResults){
					if(permissionFromRoleSearch.getPermissionId().equals(permission.getPermissionId()))
						matchedPermissions.add(permissionFromRoleSearch);
				}
			}
		}
		return matchedPermissions;
	}
	
	@SuppressWarnings("unchecked")
	private List<PermissionImpl> searchPermissions(Map<String, String> permissionSearchCriteria, boolean unbounded){
		return getPermissionsSearchResultsCopy((List<KimPermissionImpl>)getLookupService().findCollectionBySearchHelper(
				KimPermissionImpl.class, permissionSearchCriteria, unbounded));	
	}
	
	private List<PermissionImpl> getPermissionsWithRoleSearchCriteria(Map<String, String> roleSearchCriteria, boolean unbounded){
		return getPermissionsForRoleSearchResults(searchRoles(roleSearchCriteria, unbounded), unbounded);
	}

	private List<PermissionImpl> getPermissionsForRoleSearchResults(List<RoleImpl> roleSearchResults, boolean unbounded){
		Long actualSizeIfTruncated = getActualSizeIfTruncated(roleSearchResults);
		List<PermissionImpl> permissions = new ArrayList<PermissionImpl>();
		List<PermissionImpl> tempPermissions;
		List<String> collectedPermissionIds = new ArrayList<String>();
		Map<String, String> permissionCriteria;
		
		for(RoleImpl roleImpl: roleSearchResults){
			permissionCriteria = new HashMap<String, String>();
			permissionCriteria.put("rolePermissions.roleId", roleImpl.getRoleId());
			tempPermissions = searchPermissions(permissionCriteria, unbounded);
			actualSizeIfTruncated += getActualSizeIfTruncated(tempPermissions);
			for(PermissionImpl permission: tempPermissions){
				if(!collectedPermissionIds.contains(permission.getPermissionId())){
					populateAssignedToRoles(permission);
					collectedPermissionIds.add(permission.getPermissionId());
					permissions.add(permission);
				}
			}
			//need to find roles that current role is a member of and build search string
			List<String> parentRoleIds = KimApiServiceLocator.getRoleService().getMemberParentRoleIds(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, roleImpl.getRoleId());
			for (String parentRoleId : parentRoleIds) {
				Map<String, String> roleSearchCriteria = new HashMap<String, String>();
				roleSearchCriteria.put("roleId", parentRoleId);
				//get all parent role permissions and merge them with current permissions
				permissions = mergePermissionLists(permissions, getPermissionsWithRoleSearchCriteria(roleSearchCriteria, unbounded));
			}
		}
		
		return new CollectionIncomplete<PermissionImpl>(permissions, actualSizeIfTruncated);
	}
	

	private void populateAssignedToRoles(PermissionImpl permission){
		AttributeSet criteria;
		for(RolePermissionImpl rolePermission: permission.getRolePermissions()){
			if ( rolePermission.isActive() ) {
				criteria = new AttributeSet();
				criteria.put("id", rolePermission.getRoleId());
				permission.getAssignedToRoles().add((RoleBo)getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria));
			}
		}
	}
	
	/* Since most queries will only be on the template namespace and name, cache the results for 30 seconds
	 * so that queries against the details, which are done in memory, do not require repeated database trips.
	 */
    private static final HashMap<Map<String,String>,MaxAgeSoftReference<List<PermissionImpl>>> permResultCache = new HashMap<Map<String,String>, MaxAgeSoftReference<List<PermissionImpl>>>(); 
	private static final long PERM_CACHE_EXPIRE_SECONDS = 30L;
	
	private List<PermissionImpl> getPermissionsWithPermissionSearchCriteria(
			Map<String, String> permissionSearchCriteria, boolean unbounded){
		String detailCriteriaStr = permissionSearchCriteria.remove( DETAIL_CRITERIA );
		AttributeSet detailCriteria = parseDetailCriteria(detailCriteriaStr);

		MaxAgeSoftReference<List<PermissionImpl>> cachedResult = permResultCache.get(permissionSearchCriteria);
		List<PermissionImpl> permissions = null;
		if ( cachedResult == null || cachedResult.get() == null ) {
			permissions = searchPermissions(permissionSearchCriteria, unbounded);
			synchronized (permResultCache) {
				permResultCache.put(permissionSearchCriteria, new MaxAgeSoftReference<List<PermissionImpl>>( PERM_CACHE_EXPIRE_SECONDS, permissions ) ); 
			} 
		} else {
			permissions = cachedResult.get();
		}
		List<PermissionImpl> filteredPermissions = new CollectionIncomplete<PermissionImpl>(
				new ArrayList<PermissionImpl>(), getActualSizeIfTruncated(permissions)); 
		for(PermissionImpl perm: permissions){
			if ( detailCriteria.isEmpty() ) {
				filteredPermissions.add(perm);
				populateAssignedToRoles(perm);
			} else {
				if ( isMapSubset( perm.getDetails(), detailCriteria ) ) {
					filteredPermissions.add(perm);
					populateAssignedToRoles(perm);
				}
			}
		}
		return filteredPermissions;
	}
	
	private List<PermissionImpl> getPermissionsSearchResultsCopy(List<KimPermissionImpl> permissionSearchResults){
		List<PermissionImpl> permissionSearchResultsCopy = new CollectionIncomplete<PermissionImpl>(
			new ArrayList<PermissionImpl>(), getActualSizeIfTruncated(permissionSearchResults));
		for(KimPermissionImpl permissionImpl: permissionSearchResults){
			PermissionImpl permissionCopy = new PermissionImpl();
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

	private List<PermissionImpl> mergePermissionLists(List<PermissionImpl> perm1, List<PermissionImpl> perm2) {
		List<PermissionImpl> returnList = new ArrayList<PermissionImpl>(perm1);
		List<String> permissionIds = new ArrayList<String>(perm1.size());
		for (PermissionImpl perm : returnList) {
			permissionIds.add(perm.getPermissionId());
		}
		for (int i=0; i<perm2.size(); i++) {
		    if (!permissionIds.contains(perm2.get(i).getPermissionId())) {
		    	returnList.add(perm2.get(i));
		    }
		}
		return returnList;
	}
}
