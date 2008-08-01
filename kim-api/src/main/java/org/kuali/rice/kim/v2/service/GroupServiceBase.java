package org.kuali.rice.kim.v2.service;

import java.util.List;

public interface GroupServiceBase {
	// CLIENT API	

	public boolean isMemberOfGroup(String principalId, String groupId);

    // EXTENDED CLIENT API
    
	public List<String> getMemberPrincipalIds(String groupId);

	public List<String> getDirectMemberPrincipalIds(String groupId);

    // KIM INTERNAL METHODS
}
