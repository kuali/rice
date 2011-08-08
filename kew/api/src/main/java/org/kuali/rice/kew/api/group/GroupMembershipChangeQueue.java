/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api.group;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kew.api.KewApiConstants;

/**
 * Defines the contract for a message queue that handles group membership changes.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = "groupMembershipChangeQueueSoap", targetNamespace = KewApiConstants.Namespaces.KEW_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupMembershipChangeQueue {

    @WebMethod(operationName = "membershipChange")
    void notifyMembershipChange(@WebParam(name="operation") String operation, @WebParam(name="groupId") String groupId, @WebParam(name="principalId") String principalId);
    
}
