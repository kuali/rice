/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.api.permission;

import java.util.List;

import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.type.KimTypeService;


/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface PermissionTypeService extends KimTypeService {

    /** Return whether a permission assignment with the given details is applicable for the given request details. 
     * 
     * For example, the details for a permission (say edit) could be as follows:
     *   component = Account
     *   field = incomeStreamAccountNumber
     *   
     * The Account component is known to belong to the KFS-COA namespace.  If this service is requested...
     * component = Account, field = All  
     *   
     *   
     * TODO: clarify this description
     */
    List<Permission> getMatchingPermissions( Attributes requestedDetails, List<Permission> permissionsList );
}
