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
package org.kuali.rice.kim.service.support;

import java.util.List;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface KimPermissionTypeService extends KimTypeService {

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
    boolean doesPermissionDetailMatch( AttributeSet requestedDetails, KimPermission permission );

    /** Same as {@link #doesPermissionDetailMatch(AttributeSet, KimPermission)} except that it takes a list of details to check.
     */
    <E extends KimPermission> boolean doPermissionDetailsMatch( AttributeSet requestedDetails, List<E> permissionsList );

    /**
     * This method would return all permission details that the given details imply. (down)
     */
    List<AttributeSet> getAllImpliedDetails( AttributeSet requestedDetails );

    /**
     * This method would return all detail entries that imply this set of detail attributes. (up)
     * 
     * TODO: 
     * Allowing?
     * Allowed?
     * Granting?
     */
    List<AttributeSet> getAllImplyingDetails( AttributeSet requestedDetails );
    // TODO: need list versions of the implyed/ing methods?


 // TODO: uncomment this and implement a storage mechanism
// 	/**
// 	 * Returns a list of attribute names that must be passed when checking for 
// 	 * this permission.  This will be used to ensure that the calls for permission
// 	 * checks (when qualified) are executed properly.  It will also be used by
// 	 * the user interface to ensure that the role to which this permission is assigned
// 	 * can understand these attributes.
// 	 */
// 	List<String> getRequiredQualificationAttributeNames();

}
