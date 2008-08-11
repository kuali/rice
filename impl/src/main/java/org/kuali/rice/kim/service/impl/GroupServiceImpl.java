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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupAttribute;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.dto.EntityDTO;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.PersonDTO;
import org.kuali.rice.kim.dto.PrincipalDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is the default KIM GroupService implementation that is provided by Rice.  This will mature over time as the KIM
 * component is developed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupServiceImpl implements GroupService {

    /**
     * KIM service API method that returns a list of all Groups in the system
     *
     * @return         List of all Groups in the system
     *
     *
     * @see org.kuali.rice.kim.service.GroupService#getAllGroupNames()
     */
    public List<String> getAllGroupNames() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Group> groups = KNSServiceLocator.getBusinessObjectService().findAll(Group.class);
        final ArrayList<String> names = new ArrayList<String>(groups.size());
        for (Group g : groups) {
            names.add(g.getName());
        }
        return names;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getAllGroups()
     */
    public List<GroupDTO> getAllGroups() {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final Collection<Group> groups = KNSServiceLocator.getBusinessObjectService().findAll(Group.class);
        final ArrayList<GroupDTO> groupDtos = new ArrayList<GroupDTO>(groups.size());
        for (Group g : groups) {
        	groupDtos.add(Group.toDTO(g));
        }
        return groupDtos;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getGroupMemberNames(java.lang.String)
     */
    public List<String> getGroupMemberNames(String groupName) {
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 			= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<Group> memberGroups 	= new ArrayList<Group>();
        final ArrayList<String> memberGroupNames= new ArrayList<String>(memberGroups.size());
        
        for (Group g : groups) {
        	memberGroups.addAll(g.getMemberGroups());
        }

        for (Group mg : memberGroups) {
        	memberGroupNames.add(mg.getName());
        }        
    	return memberGroupNames;

    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getGroupMembers(java.lang.String)
     */
    public List<GroupDTO> getGroupMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<Group> memberGroups = new ArrayList<Group>();
        final ArrayList<GroupDTO> memberGroupDtos = new ArrayList<GroupDTO>(memberGroups.size());
        
        for (Group g : groups) {
        	memberGroups.addAll(g.getMemberGroups());
        }

        for (Group mg : memberGroups) {
        	memberGroupDtos.add(Group.toDTO(mg));
        }        
    	return memberGroupDtos;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupParentNames(java.lang.String)
     */
    public List<String> getGroupParentNames(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 			= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<Group> parentGroups 	= new ArrayList<Group>();
        final ArrayList<String> parentGroupNames= new ArrayList<String>(parentGroups.size());
        
        for (Group g : groups) {
        	parentGroups.addAll(g.getParentGroups());
        }

        for (Group pg : parentGroups) {
        	parentGroupNames.add(pg.getName());
        }        
    	return parentGroupNames;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getGroupParents(java.lang.String)
     */
    public List<GroupDTO> getGroupParents(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<Group> parentGroups = new ArrayList<Group>();
        final ArrayList<GroupDTO> parentGroupDtos = new ArrayList<GroupDTO>(parentGroups.size());
        
        for (Group g : groups) {
        	parentGroups.addAll(g.getParentGroups());
        }

        for (Group mg : parentGroups) {
        	parentGroupDtos.add(Group.toDTO(mg));
        }        
    	return parentGroupDtos;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getGroupNamesWithAttributes(java.util.Map)
     */
    public List<String> getGroupNamesWithAttributes(Map<String, String> groupAttributes) {
    	
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
    	
        final Collection<Group> groups 							= KNSServiceLocator.getBusinessObjectService().findAll(Group.class);
        final ArrayList<String> groupNamesWithAttributes		= new ArrayList<String>();
        
        for (Group g : groups) {
        	List<GroupAttribute> gaList = g.getGroupAttributes();
        	if(areAllAttributesExist(groupAttributes,gaList))
        		groupNamesWithAttributes.add(g.getName());
        }

    	return groupNamesWithAttributes;
    }
    
    

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getGroupsWithAttributes(java.util.Map)
     */
    public List<GroupDTO> getGroupsWithAttributes(Map<String, String> groupAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        
        final Collection<Group> groups 							= KNSServiceLocator.getBusinessObjectService().findAll(Group.class);
        final ArrayList<GroupDTO> groupsWithAttributes		= new ArrayList<GroupDTO>();
        
        for (Group g : groups) {
        	List<GroupAttribute> gaList = g.getGroupAttributes();
        	if(areAllAttributesExist(groupAttributes,gaList))
        		groupsWithAttributes.add(Group.toDTO(g));
        }

    	return groupsWithAttributes;
    }


    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getPrincipalMemberNames(java.lang.String)
     */
    public List<String> getPrincipalMemberNames(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
    	
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<String> principalGroupNames= new ArrayList<String>();
        
        for (Group g : groups) {
        	List<Principal> prinicapals = g.getMemberPrincipals();
        	for(Principal p:prinicapals){
        		principalGroupNames.add(p.getName());
        	}
        }
    	return principalGroupNames;

    	
        
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getPrincipalMembers(java.lang.String)
     */
    public List<PrincipalDTO> getPrincipalMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<PrincipalDTO> principalGroupDtos= new ArrayList<PrincipalDTO>();
        
        for (Group g : groups) {
        	List<Principal> prinicapals = g.getMemberPrincipals();
        	for(Principal p:prinicapals){
        		principalGroupDtos.add(Principal.toDTO(p));
        	}
        }
    	return principalGroupDtos;


    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getRoleNamesForGroup(java.lang.String)
     */
    public List<String> getRoleNamesForGroup(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<String> roleNames= new ArrayList<String>();
        
        for (Group g : groups) {
        	List<Role> roles = g.getRoles();
        	for(Role r:roles){
        		roleNames.add(r.getName());
        	}
        }
    	return roleNames;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getRolesForGroup(java.lang.String)
     */
    public List<RoleDTO> getRolesForGroup(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);
        final Collection<Group> groups 		= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        final ArrayList<RoleDTO> roleDtos= new ArrayList<RoleDTO>();
        
        for (Group g : groups) {
        	List<Role> roles= g.getRoles();
        	for(Role r:roles){
        		roleDtos.add(Role.toDTO(r));
        	}
        }
    	return roleDtos;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#hasAttributes(java.lang.String, java.util.Map)
     */
    public boolean hasAttributes(String groupName, Map<String, String> groupAttributes) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
        boolean returnValue = false;
        final HashMap<String, String> criteria= new HashMap<String, String>();
        criteria.put("NAME", groupName);

        final Collection<Group> groups 							= KNSServiceLocator.getBusinessObjectService().findMatching(Group.class,criteria);
        for (Group g : groups) {
        	List<GroupAttribute> gaList = g.getGroupAttributes();
        	returnValue = areAllAttributesExist(groupAttributes,gaList);
        }
    	return returnValue;
    }

    
    private boolean areAllAttributesExist(Map<String,String> groupAttributes,List<GroupAttribute> gaList){
    	boolean attributesExist= false;
    	
    	Iterator I = groupAttributes.keySet().iterator();
    	while(I.hasNext()){
    		String key 		= (String)I.next();
    		String value 	= groupAttributes.get(key);
        	for(GroupAttribute ga: gaList ){
        		if(ga.getAttributeName().equals(key) && ga.getValue().equals(value)){
        			attributesExist = true;
        		}
        	}
        	if(attributesExist==false) break;
        	
    	}
    	return attributesExist;
    }

    
    
    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getPersonMemberIds(java.lang.String)
     */
    public List<Long> getPersonMemberIds(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
    	List<PrincipalDTO> principals  = this.getPrincipalMembers(groupName);
    	ArrayList<Long> personIds=new ArrayList<Long>();
    	for(PrincipalDTO principalDTO:principals){
    		if(principalDTO.getEntityTypeId()==1){
    			// Eliminate Duplicates
    			if(personIds.contains(principalDTO.getId()))
    				continue;
    			else
    				personIds.add( principalDTO.getId());
    		}
    	}
    	return personIds;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.service.GroupService#getPersonMembers(java.lang.String)
     */
    
    // HOW to get PerosonDTO???
    public List<PersonDTO> getPersonMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
    	List<PrincipalDTO> principals  = this.getPrincipalMembers(groupName);
    	ArrayList<Long> personIds=new ArrayList<Long>();
    	ArrayList<PersonDTO> personDtos=new ArrayList<PersonDTO>();
    	for(PrincipalDTO principalDTO:principals){
    		if(principalDTO.getEntityTypeId()==1){
    			// Eliminate Duplicates
    			if(personIds.contains(principalDTO.getId()))
    				continue;
    			else{
    				personIds.add( principalDTO.getId());
    				PersonDTO personDto = new PersonDTO();
    				
    				personDto.setId(principalDTO.getId());
    				personDto.setEntityType(principalDTO.getEntityTypeDto());
    				personDto.setEntityTypeId(principalDTO.getEntityTypeId());
    				personDtos.add(personDto);
    			}
    		}
    	}
    	
    	return personDtos;

    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getEntityMemberIds(java.lang.String)
     */
    public List<Long> getEntityMemberIds(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS

    	List<PrincipalDTO> principals  = this.getPrincipalMembers(groupName);
    	ArrayList<Long> entityIds=new ArrayList<Long>();
    	for(PrincipalDTO principalDTO:principals){
   			// Eliminate Duplicates
    		if(entityIds.contains(principalDTO.getId()))
   				continue;
   			else
   				entityIds.add( principalDTO.getId());
       	}
       	return entityIds;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.GroupService#getEntityMembers(java.lang.String)
     */
    public List<EntityDTO> getEntityMembers(String groupName) {
        // TODO ag266 - THIS METHOD NEEDS JAVADOCS
    	List<Long> entityIds = getEntityMemberIds(groupName);
    	ArrayList<Entity> entities = new ArrayList<Entity>();
    	for(Long entityId:entityIds){
            HashMap<String, Long> criteria= new HashMap<String, Long>();
    		criteria.put("ID", entityId);
    		entities.addAll(KNSServiceLocator.getBusinessObjectService().findMatching(Entity.class,criteria));
    	}
    	ArrayList<EntityDTO> entityDtos = new ArrayList<EntityDTO>();
    	for(Entity entity:entities){
    		entityDtos.add(Entity.toDTO(entity));
    	}
    	return entityDtos;
    }
    
}
