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

import org.kuali.rice.kim.dto.GroupTypeDTO;

/**
 * Service API for accessing KIM Group Type services.  This contract should be used by all 
 * Kuali software which needs to leverage identity management features of Group Type.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface GroupTypeService {
    /**
     * KIM service API method that returns the complete collection of GroupTypeDTO objects
     * 
     * @return         List of GroupTypeDTO objects
     * 
     */
    public List<GroupTypeDTO> getAllGroupTypes();

    /**
     * KIM service API method that returns associated List of names for all GroupType objects
     * 
     * @return         List of GroupType names
     * 
     */
    public List<String> getAllGroupTypeNames();
    
    /**
     * This method returns a GroupTypeDTO object when provided the Group Type's Id.
     * 
     * @param groupTypeId
     * @return GroupTypeDTO
     */
    public GroupTypeDTO getGroupType(Long groupTypeId);
    
    /**
     * This method returns a Group Type's name when provided the Group Type's Id.
     * 
     * @param groupTypeId
     * @return String
     */
    public String getGroupTypeName(Long groupTypeId);
}
