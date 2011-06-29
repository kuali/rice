/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kim.impl.permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionTypeService;
import org.kuali.rice.kim.impl.role.RoleMemberAttributeDataBo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionServiceBase {
	protected static final String PERMISSION_IMPL_CACHE_PREFIX = "PermissionImpl-Template-";
	protected static final String PERMISSION_IMPL_NAME_CACHE_PREFIX = "PermissionImpl-Name-";
	protected static final String PERMISSION_IMPL_ID_CACHE_PREFIX = "PermissionImpl-Id-";
	protected static final String PERMISSION_IMPL_CACHE_GROUP = "PermissionImpl";

	protected static final String DEFAULT_PERMISSION_TYPE_SERVICE = "defaultPermissionTypeService";
	
	protected BusinessObjectService businessObjectService;
	protected CriteriaLookupService criteriaLookupService;
	protected SequenceAccessorService sequenceAccessorService;
	protected RiceCacheAdministrator cacheAdministrator;
	
	private Map<List<Permission>,MaxAgeSoftReference<List<String>>> permissionToRoleCache = Collections.synchronizedMap( new HashMap<List<Permission>,MaxAgeSoftReference<List<String>>>() );

    // Not ThreadLocal or time limited- should not change during the life of the system
	private Map<String,PermissionTypeService> permissionTypeServiceByNameCache = Collections.synchronizedMap( new HashMap<String, PermissionTypeService>() );
	

	private static final long CACHE_MAX_AGE_SECONDS = 60L;
   
	public void flushPermissionImplCache() {
    	cacheAdministrator.flushGroup(PERMISSION_IMPL_CACHE_GROUP);
    }
	
	/**
	 * @return the permissionTypeServiceByNameCache
	 */
	protected Map<String, PermissionTypeService> getPermissionTypeServiceByNameCache() {
		return this.permissionTypeServiceByNameCache;
	}
	
	protected void addRolesForPermissionsToCache( List<Permission> applicablePermissions, List<String> roleIds ) {
    	permissionToRoleCache.put( applicablePermissions, new MaxAgeSoftReference<List<String>>( CACHE_MAX_AGE_SECONDS, roleIds ) );
    }
	
	protected List<String> getRolesForPermissionsFromCache( List<Permission> applicablePermissions ) {
    	List<String> roleIds = null; 
    	MaxAgeSoftReference<List<String>> cacheRef = permissionToRoleCache.get( applicablePermissions );
    	if ( cacheRef != null ) {
    		roleIds = cacheRef.get();
    	}
    	return roleIds;
    }
	
	protected String getPermissionImplByTemplateNameCacheKey( String namespaceCode, String permissionTemplateName ) {
    	return PERMISSION_IMPL_CACHE_PREFIX + namespaceCode + "-" + permissionTemplateName;
    }
    protected String getPermissionImplByNameCacheKey( String namespaceCode, String permissionName ) {
    	return PERMISSION_IMPL_NAME_CACHE_PREFIX + namespaceCode + "-" + permissionName;
    }
    protected String getPermissionImplByIdCacheKey( String permissionId ) {
    	return PERMISSION_IMPL_ID_CACHE_PREFIX + permissionId;
    }
    
	protected String getNewAttributeDataId(){	
		Long nextSeq = sequenceAccessorService.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataBo.class );
		return nextSeq.toString();
    }
	
    /**
     * Sets the sequenceAccessorService attribute value.
     *
     * @param sequenceAccessorService The sequenceAccessorService to set.
     */
    public void setSequenceAccessorService(final SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }
    
    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the criteriaLookupService attribute value.
     *
     * @param criteriaLookupService The criteriaLookupService to set.
     */
    public void setCriteriaLookupService(final CriteriaLookupService criteriaLookupService) {
        this.criteriaLookupService = criteriaLookupService;
    }
	

    /**
     * Sets the cacheAdministrator attribute value.
     *
     * @param cacheAdministrator The cacheAdministrator to set.
     */
    protected void setCacheAdministrator(RiceCacheAdministrator cacheAdministrator) {
		this.cacheAdministrator = cacheAdministrator;
	}
}
