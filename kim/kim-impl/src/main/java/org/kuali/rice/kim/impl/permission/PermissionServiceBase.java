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

import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.impl.role.RoleMemberAttributeDataBo;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
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
	
	private BusinessObjectService businessObjectService;
	private SequenceAccessorService sequenceAccessorService;
	private RiceCacheAdministrator cacheAdministrator;
	
	private Map<List<KimPermissionInfo>,MaxAgeSoftReference<List<String>>> permissionToRoleCache = Collections.synchronizedMap( new HashMap<List<KimPermissionInfo>,MaxAgeSoftReference<List<String>>>() );

    // Not ThreadLocal or time limited- should not change during the life of the system
	private Map<String,KimPermissionTypeService> permissionTypeServiceByNameCache = Collections.synchronizedMap( new HashMap<String, KimPermissionTypeService>() );
	

	private static final long CACHE_MAX_AGE_SECONDS = 60L;
	
	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KRADServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
   
	public void flushPermissionImplCache() {
    	getCacheAdministrator().flushGroup(PERMISSION_IMPL_CACHE_GROUP);
    }
	
	/**
	 * @return the permissionTypeServiceByNameCache
	 */
	protected Map<String, KimPermissionTypeService> getPermissionTypeServiceByNameCache() {
		return this.permissionTypeServiceByNameCache;
	}
	
	protected void addRolesForPermissionsToCache( List<KimPermissionInfo> key, List<String> roleIds ) {
    	permissionToRoleCache.put( key, new MaxAgeSoftReference<List<String>>( CACHE_MAX_AGE_SECONDS, roleIds ) );
    }
	
	protected List<String> getRolesForPermissionsFromCache( List<KimPermissionInfo> key ) {
    	List<String> roleIds = null; 
    	MaxAgeSoftReference<List<String>> cacheRef = permissionToRoleCache.get( key );
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
		SequenceAccessorService sas = getSequenceAccessorService();		
		Long nextSeq = sas.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataBo.class );
		return nextSeq.toString();
    }
	
	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}
	
	protected RiceCacheAdministrator getCacheAdministrator() {
		if ( cacheAdministrator == null ) {
			cacheAdministrator = KsbApiServiceLocator.getCacheAdministrator();
		}
		return cacheAdministrator;
	}
}
