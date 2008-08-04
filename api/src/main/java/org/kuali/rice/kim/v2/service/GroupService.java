package org.kuali.rice.kim.v2.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Group;
import org.kuali.rice.kim.v2.bo.GroupType;

public interface GroupService extends GroupServiceBase {
	// CLIENT API	

    // EXTENDED CLIENT API

    public List<Group> getGroupsForPrincipal(String principalId);

    public List<Group> getMemberGroups(String groupId);

    public List<Group> getDirectMemberGroups(String groupId);

    // KIM INTERNAL METHODS
    
    public Group getGroup(String groupId);

    public List<Group> lookupGroups(Map<String,String> searchCriteria);

    public List<Group> lookupGroups(Map<String,String> searchCriteria, Map<String, String> groupAttributes);

    public void saveGroupType(GroupType groupType);

    public void saveGroup(Group group);
    
    public List<Group> getParentGroups(String groupId);

    public List<Group> getDirectParentGroups(String groupId);
}
