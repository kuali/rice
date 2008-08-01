package org.kuali.rice.kim.v2.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Group;
import org.kuali.rice.kim.v2.bo.GroupType;
import org.kuali.rice.kim.v2.service.GroupService;

// TODO implement this class
public abstract class GroupServiceImpl implements GroupService {	
	public boolean isMemberOfGroup(String principalId, String groupId) {
		return true;
	}

	public List<String> getMemberPrincipalIds(String groupId) {
		return null;
	}

	public List<String> getDirectMemberPrincipalIds(String groupId) {
		return null;
	}
	
    public List<Group> getGroupsForPrincipal(String principalId) {
		return null;
	}

    public List<Group> getMemberGroups(String groupId) {
		return null;
	}

    public List<Group> getDirectMemberGroups(String groupId) {
		return null;
	}

    public Group getGroup(String groupId) {
		return null;
	}

    public List<Group> lookupGroups(Map<String,String> searchCriteria) {
		return null;
	}

    public List<Group> lookupGroups(Map<String,String> searchCriteria, Map<String, String> groupAttributes) {
		return null;
	}

    public void saveGroupType(GroupType groupType) {
    }

    public void saveGroup(Group group) {
    }
    
    public List<Group> getParentGroups(String groupId) {
		return null;
	}

    public List<Group> getDirectParentGroups(String groupId) {
		return null;
	}
}
