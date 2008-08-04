/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kim.bo.GroupType;
import org.kuali.rice.kim.bo.Namespace;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Principal;
import org.kuali.rice.kim.dto.GroupTypeDTO;
import org.kuali.rice.kim.dto.NamespaceDTO;
import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.service.GroupTypeService;

/**
 * This is the default KIM Group Type service implementation that is provided by Rice.  It is OJB specific.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupTypeServiceImpl implements GroupTypeService {
    /**
	 * @see org.kuali.rice.kim.service.GroupTypeService#getAllGroupTypeNames()
	 */
	public List<String> getAllGroupTypeNames() {
        final Collection<GroupType> groupTypes = (Collection<GroupType>) KNSServiceLocator.getBusinessObjectService().findAll(GroupType.class);
        final ArrayList<String> names = new ArrayList<String>(groupTypes.size());
        for (GroupType groupType : groupTypes) {
            names.add(groupType.getName());
        }
        return names;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupTypeService#getAllGroupTypes()
	 */
	public List<GroupTypeDTO> getAllGroupTypes() {
		final Collection<GroupType> groupTypes = (Collection<GroupType>) KNSServiceLocator.getBusinessObjectService().findAll(GroupType.class);
        final ArrayList<GroupTypeDTO> groupTypeDtos = new ArrayList<GroupTypeDTO>(groupTypes.size());
        for (GroupType groupType : groupTypes) {
            groupTypeDtos.add(GroupType.toDTO(groupType));
        }
        return groupTypeDtos;
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupTypeService#getGroupType(java.lang.Long)
	 */
	public GroupTypeDTO getGroupType(Long groupTypeId) {
		final HashMap<String, Long> criteria = new HashMap<String, Long>(1);
		criteria.put("ID", groupTypeId);
		final Collection<GroupType> groupType = (Collection<GroupType>) KNSServiceLocator
				.getBusinessObjectService().findMatching(GroupType.class, criteria);
		if(groupType.isEmpty()) {
			return null;
		} else {
			return GroupType.toDTO(groupType.iterator().next());
		}
	}

	/**
	 * @see org.kuali.rice.kim.service.GroupTypeService#getGroupTypeName(java.lang.Long)
	 */
	public String getGroupTypeName(Long groupTypeId) {
		final HashMap<String, Long> criteria = new HashMap<String, Long>(1);
		criteria.put("ID", groupTypeId);
		final Collection<GroupType> groupType = (Collection<GroupType>) KNSServiceLocator
				.getBusinessObjectService().findMatching(GroupType.class, criteria);
		if(groupType.isEmpty()) {
			return null;
		} else {
			return ((GroupType) groupType.iterator().next()).getName();
		}
	}
}
