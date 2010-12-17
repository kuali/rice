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
package org.kuali.rice.kim.service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

/**
 * 
 * This service provides operations for creating and updating groups.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.GroupUpdateService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupUpdateService {

	/**
	 * Creates a new group using the given GroupInfo.
	 */
	GroupInfo createGroup(@WebParam(name="groupInfo") GroupInfo groupInfo) throws UnsupportedOperationException;

	/**
	 * Updates the group with the given groupId using the supplied GroupInfo.
	 */
    GroupInfo updateGroup(@WebParam(name="groupId") String groupId, @WebParam(name="groupInfo") GroupInfo groupInfo) throws UnsupportedOperationException;

    /**
     * Adds the group with the id supplied in childId as a member of the group with the id supplied in parentId.
     */
    boolean addGroupToGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws UnsupportedOperationException;
    
    /**
     * Removes the group with the id supplied in childId from the group with the id supplied in parentId.
     */
    boolean removeGroupFromGroup(@WebParam(name="childId") String childId, @WebParam(name="parentId") String parentId) throws UnsupportedOperationException;
    
    /**
     * Add the principal with the given principalId as a member of the group with the given groupId.
     */
    boolean addPrincipalToGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws UnsupportedOperationException;
    
    /**
     * Removes the member principal with the given principalId from the group with the given groupId.
     */
    boolean removePrincipalFromGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId) throws UnsupportedOperationException;
    
    /**
     * Removes all members from the group with the given groupId.
     */
    void removeAllGroupMembers( @WebParam(name="groupId") String groupId ) throws UnsupportedOperationException;
}
