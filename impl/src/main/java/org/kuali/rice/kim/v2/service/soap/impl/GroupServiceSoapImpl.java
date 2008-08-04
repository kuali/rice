package org.kuali.rice.kim.v2.service.soap.impl;

import java.util.List;

import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.v2.service.soap.GroupServiceSoap;

// TODO implement this class by piggy backing on the standard implementation ("has a" not "is a") and translating from interface to DTOs
public class GroupServiceSoapImpl implements GroupServiceSoap {
	public List<GroupDTO> getWebServiceSafeDirectMemberGroups(String groupId) {
		return null;
	}

	public List<GroupDTO> getWebServiceSafeGroupsForPrincipal(String principalId) {
		return null;
	}

	public List<GroupDTO> getWebServiceSafeMemberGroups(String groupId) {
		return null;
	}

	public List<String> getDirectMemberPrincipalIds(String groupId) {
		return null;
	}

	public List<String> getMemberPrincipalIds(String groupId) {
		return null;
	}

	public boolean isMemberOfGroup(String principalId, String groupId) {
		return false;
	}
}
