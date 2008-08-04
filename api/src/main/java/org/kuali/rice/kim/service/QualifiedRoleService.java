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
import java.util.Map;

import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;

/**
 * Service API for accessing KIM QualifiedRole services. This contract should be used by all Kuali software which needs to
 * leverage identity management features that require fine-grained QualifiedRole attributes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface QualifiedRoleService {
    /**
     * KIM QualifiedRole service API method that returns the complete List of Principal objects that possess a given Role and
     * matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Principal
     * @return List of PrincipalDTO objects that satisfy both Role and qualifying role attributes
     */
    public List<PrincipalDTO> getPrincipals(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns associated List of principal names for all Principal objects that possess a
     * given Role and matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Principal
     * @return associated List principal names for Principal objects that satisfy both Role and qualifying role attributes
     */
    public List<String> getPrincipalNames(String roleName, Map<String, String> qualifiedRoleAttributes);
    
    /**
     * KIM QualifiedRole service API method that returns the complete List of Person objects that possess a given Role and
     * matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Person
     * @return List of PersonDTO objects that satisfy both Role and qualifying role attributes
     */
    public List<PersonDTO> getPersons(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns associated List of person ids for all Person objects that possess a
     * given Role and matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Person
     * @return associated List person Ids for Person objects that satisfy both Role and qualifying role attributes
     */
    public List<Long> getPersonIds(String roleName, Map<String, String> qualifiedRoleAttributes);
    
    /**
     * KIM QualifiedRole service API method that returns the complete List of Entity objects that possess a given Role and
     * matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Entity
     * @return List of EntityDTO objects that satisfy both Role and qualifying role attributes
     */
    public List<EntityDTO> getEntitys(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns associated List of entity ids for all Entity objects that possess a
     * given Role and matching role attributes
     * 
     * @param roleName name identifying Role
     * @param qualifiedRoleAttributes Map<String, String> of role attribute name/value pairs to qualify a Entity
     * @return associated List entity Ids for Entity objects that satisfy both Role and qualifying role attributes
     */
    public List<Long> getEntityIds(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns the complete List of Group objects matching a given role and
     * qualified role attributes.
     * 
     * @param roleName
     *            name identifying Role
     * @param qualifiedRoleAttributes
     *            Map<String, String> of role attribute name/value pairs to qualify a group
     * @return List of all GroupDTO objects matching the role and role attributes
     */
    public List<GroupDTO> getGroups(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns associated List of group names for all Group objects matching a
     * given role and qualified role attributes.
     * 
     * @param roleName
     *            name identifying Role
     * @param qualifiedRoleAttributes
     *            Map<String, String> of role attribute name/value pairs to qualify a group
     * @return associated List of names for all Groups matching the role and role attributes
     */
    public List<String> getGroupNames(String roleName, Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns unique List of all Role objects matching qualified role attributes.
     * 
     * @param qualifiedRoleAttributes
     *            Map<String, String> of role attribute name/value pairs to qualify a group
     * @return unique set of all RoleDTO objects matching the role attributes
     */
    public List<RoleDTO> getRoles(Map<String, String> qualifiedRoleAttributes);

    /**
     * KIM QualifiedRole service API method that returns unique List of all Role names matching qualified role attributes.
     * 
     * @param qualifiedRoleAttributes
     *            Map<String, String> of role attribute name/value pairs to qualify a group
     * @return unique set of all Role names matching the role attributes
     */
    public List<String> getRoleNames(Map<String, String> qualifiedRoleAttributes);
}
