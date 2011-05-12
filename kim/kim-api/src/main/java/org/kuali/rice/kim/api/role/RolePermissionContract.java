/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.role;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * This is the contract for a KimPermission.  
 *  
 */
public interface RolePermissionContract extends Versioned, GloballyUnique{

    /**
     * The unique identifier for the Role Permission.
     *
     * @return id
     */
    String getId();
    
    /**
     * The Role ID referenced by the Role Permission.
     * 
     * @return roleId
     */
	String getRoleId();  
	
    /**
     * The Permission ID referenced by the Role Permission.
     * 
     * @return permissionId
     */
	String getPermissionId();  
   
    /**
     * The flag indicating if the Role Permission is active.
     *
     * @return active
     */
    boolean isActive();   
}
