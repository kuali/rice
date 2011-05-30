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
package org.kuali.rice.kim.service.impl;

import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class IdentityManagementNotificationServiceImpl implements
        IdentityManagementNotificationService {

    /**
     * This method clears the IdentityManagementService's group cache
     * 
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#groupUpdated()
     */
    public void groupUpdated() {
        KimApiServiceLocator.getIdentityManagementService().flushGroupCaches();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#permissionUpdated()
     */
    public void permissionUpdated() {
        KimApiServiceLocator.getIdentityManagementService().flushPermissionCaches();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#principalUpdated()
     */
    public void principalUpdated() {
    	KimApiServiceLocator.getIdentityManagementService().flushEntityPrincipalCaches();
    	KimApiServiceLocator.getIdentityManagementService().flushGroupCaches();
    	KimApiServiceLocator.getIdentityManagementService().flushResponsibilityCaches();
    	KimApiServiceLocator.getPersonService().flushPersonCaches();
    	roleUpdated();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#responsibilityUpdated()
     */
    public void responsibilityUpdated() {
        KimApiServiceLocator.getIdentityManagementService().flushResponsibilityCaches();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#roleUpdated()
     */
    public void roleUpdated() {
        KimApiServiceLocator.getRoleManagementService().flushRoleCaches();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#roleMemberUpdated()
     */
    public void roleMemberUpdated() {
    	KimApiServiceLocator.getRoleManagementService().flushRoleMemberCaches();
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#delegationUpdated()
     */
    public void delegationUpdated() {
    	KimApiServiceLocator.getRoleManagementService().flushDelegationCaches();
    }

    /**
     * @see org.kuali.rice.kim.service.IdentityManagementNotificationService#delegationMemberUpdated()
     */
    public void delegationMemberUpdated() {
    	KimApiServiceLocator.getRoleManagementService().flushDelegationMemberCaches();
    }
}
