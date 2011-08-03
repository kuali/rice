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
package org.kuali.rice.kim.api.identity;

import org.kuali.rice.kim.util.KimConstants;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Service to purge local Kim caches when backend KIM data has changed. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "IdentityManagementNotificationServiceSoap", targetNamespace = KimConstants.Namespaces.KIM_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IdentityManagementNotificationService {
    public void principalUpdated();
    public void groupUpdated();
    public void roleUpdated();
    public void roleMemberUpdated();
    public void permissionUpdated();
    public void responsibilityUpdated();
    public void delegationUpdated();
    public void delegationMemberUpdated();
}
