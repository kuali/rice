package org.kuali.rice.kim.v2.service.soap;

import java.util.List;

import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.v2.service.GroupServiceBase;

public interface GroupServiceSoap extends GroupServiceBase {
    public List<GroupDTO> getWebServiceSafeGroupsForPrincipal(String principalId);

    public List<GroupDTO> getWebServiceSafeMemberGroups(String groupId);

    public List<GroupDTO> getWebServiceSafeDirectMemberGroups(String groupId);
}
