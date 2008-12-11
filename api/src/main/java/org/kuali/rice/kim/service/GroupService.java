package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.group.dto.GroupInfo;

public interface GroupService {

    /** Get all the groups for a given principal.
     * 
     * This will include all groups directly assigned as well as those inferred
     * by the fact that they are members of higher level groups.
     */
    public List<GroupInfo> getGroupsForPrincipal(String principalId);

    /**
     * Get all the groups within a namespace for a given principal.
     * 
     * This is the same as the {@link #getGroupsForPrincipal(String)} method except that
     * the results will be filtered by namespace after retrieval.
     */
    public List<GroupInfo> getGroupsForPrincipalByNamespace(String principalId, String namespaceCode);
	
    List<String> lookupGroupIds(Map<String, String> searchCriteria);
    
    GroupInfo getGroupInfo(String groupId);
    
    GroupInfo getGroupInfoByName(String namespaceCode, String groupName);

    Map<String, GroupInfo> getGroupInfos(List<String> groupIds);
    
	/** 
	 * Check whether the give principal is a member of the group.
	 * 
	 * This will also return true if the principal is a member of a groups assigned to this group.
	 */
	boolean isMemberOfGroup(String principalId, String groupId);

	/** 
	 * Check whether the give principal is a member of the group.
	 * 
	 * This will not recurse into contained groups.
	 */
	boolean isDirectMemberOfGroup(String principalId, String groupId);
	
	/**
     * Get all the groups for the given principal.  Recurses into parent groups
     * to provide a comprehensive list.
     */
	List<String> getGroupIdsForPrincipal(String principalId);

	/**
     * Get all the groups for the given principal in the given namespace.  Recurses into
     *  parent groups to provide a comprehensive list.
     */
    List<String> getGroupIdsForPrincipalByNamespace(String principalId, String namespaceCode);
    
    /**
     * Get the groupIds in which the principal has direct membership only.
     */
    List<String> getDirectGroupIdsForPrincipal(String principalId);
	
    
    /**
     * Check whether the group identified by groupMemberId is a member of the group
     * identified by groupId.  This will recurse through all groups.
     */
    boolean isGroupMemberOfGroup(String groupMemberId, String groupId);

    boolean isGroupActive( String groupId );
    
    /**
     * Get all the principals of the given group.  Recurses into contained groups
     * to provide a comprehensive list.
     */
	List<String> getMemberPrincipalIds(String groupId);

    /**
     * Get all the principals directly assigned to the given group.
     */
	List<String> getDirectMemberPrincipalIds(String groupId);
	
	/**
	 * Get all the groups contained by the given group.  Recurses into contained groups
     * to provide a comprehensive list.
	 */
	List<String> getMemberGroupIds( String groupId );
	
    /**
     * Get all the groups which are direct members of the given group.
     */
	List<String> getDirectMemberGroupIds( String groupId );
	
	/**
     * Get the groups which are parents of the given group.
     * 
     * This will recurse into groups above the given group and build a complete
     * list of all groups included above this group.
     */
    List<String> getParentGroupIds(String groupId);
    
    /**
     * Get the groupIds which that are directly above this group.
     */
    List<String> getDirectParentGroupIds(String groupId);
    

	/**
	 * Get all the attributes of the given group.
	 */
	Map<String,String> getGroupAttributes( String groupId );
	
    boolean addGroupToGroup(String childId, String parentId);
    
    boolean removeGroupFromGroup(String childId, String parentId);
    
    boolean addPrincipalToGroup(String principalId, String groupId);
    
    boolean removePrincipalFromGroup(String principalId, String groupId);
}
