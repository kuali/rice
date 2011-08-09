/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.framework.common.delegate;


import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.framework.type.KimTypeService;

import java.util.Map;

/**
 * A {@link KimTypeService} with specific methods for Delegations.
 */
public interface DelegationTypeService extends KimTypeService {

    /**
     * Gets whether a role assignment with the given qualifier is applicable for the given qualification.
     * 
     * For example, the qualifier for a role could be as follows:
     *   chartOfAccountsCode = BL
     *   organizationCode = ARSC
     *   descendsHierarchy = true
     *   
     * The qualification could be:
     *   chartOfAccountsCode = BL
     *   organizationCode = PSY    (reports to BL-ARSC)
     *   
     * This method would return true for this set of arguments.  This would require a query of 
     * the client app's organization hierarchy, so an implementation of this sort must be done by
     * a service which lives within the client app and will be called remotely by KIM.
     * 
     * The contents of the passed in attribute sets should not be modified as they may be used in future calls by
     * the role service.
     *
     * @param qualification the qualification.  cannot be null.
     * @param delegationQualifier the delegation qualifier. cannot be null.
     * @return true if the qualifications match
     * @throws IllegalArgumentException if the qualification or delegationQualifier is null
     */
    boolean doesDelegationQualifierMatchQualification( Map<String, String> qualification, Map<String, String> delegationQualifier ) throws RiceIllegalArgumentException;

}
