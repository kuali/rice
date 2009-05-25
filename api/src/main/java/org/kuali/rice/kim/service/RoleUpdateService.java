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
package org.kuali.rice.kim.service;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@WebService(name = "RoleUpdateService", targetNamespace = "http://org.kuali.rice/kim/role")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RoleUpdateService {
   
    void assignPrincipalToRole(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualifications") AttributeSet qualifications) throws UnsupportedOperationException;
    void assignGroupToRole(@WebParam(name="groupId") String groupId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualifications") AttributeSet qualifications) throws UnsupportedOperationException;
    void removePrincipalFromRole(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualifications") AttributeSet qualifications) throws UnsupportedOperationException;
    void removeGroupFromRole(@WebParam(name="groupId") String groupId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualifications") AttributeSet qualifications) throws UnsupportedOperationException;
    List<RoleMembershipInfo> getFirstLevelRoleMembers(@WebParam(name="roleIds") List<String> roleIds);

}
