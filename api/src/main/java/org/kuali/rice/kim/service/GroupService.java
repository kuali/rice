package org.kuali.rice.kim.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.JaxbStringMapAdapter;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;

@WebService(name = "GroupService", targetNamespace = "http://org.kuali.rice/kim/group")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupService {

    /** Get all the groups for a given principal.
     * 
     * This will include all groups directly assigned as well as those inferred
     * by the fact that they are members of higher level groups.
     */
    List<GroupInfo> getGroupsForPrincipal(@WebParam(name="principalId") String principalId);

    /**
     * Get all the groups within a namespace for a given principal.
     * 
     * This is the same as the {@link #getGroupsForPrincipal(String)} method except that
     * the results will be filtered by namespace after retrieval.
     */
    List<GroupInfo> getGroupsForPrincipalByNamespace(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode);
	
    List<String> lookupGroupIds(@WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = JaxbStringMapAdapter.class) Map<String, String> searchCriteria);
    
    GroupInfo getGroupInfo(@WebParam(name="groupId") String groupId);
    
    GroupInfo getGroupInfoByName(@WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="groupName") String groupName);

    @XmlJavaTypeAdapter(value = JaxbStringMapAdapter.class) Map<String, GroupInfo> getGroupInfos(@WebParam(name="groupIds") Collection<String> groupIds);
    
	/** 
	 * Check whether the give principal is a member of the group.
	 * 
	 * This will also return true if the principal is a member of a groups assigned to this group.
	 */
	boolean isMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId);

	/** 
	 * Check whether the give principal is a member of the group.
	 * 
	 * This will not recurse into contained groups.
	 */
	boolean isDirectMemberOfGroup(@WebParam(name="principalId") String principalId, @WebParam(name="groupId") String groupId);
	
	/**
     * Get all the groups for the given principal.  Recurses into parent groups
     * to provide a comprehensive list.
     */
	List<String> getGroupIdsForPrincipal(@WebParam(name="principalId") String principalId);

	/**
     * Get all the groups for the given principal in the given namespace.  Recurses into
     *  parent groups to provide a comprehensive list.
     */
	List<String> getGroupIdsForPrincipalByNamespace(@WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode);
    
    /**
     * Get the groupIds in which the principal has direct membership only.
     */
    List<String> getDirectGroupIdsForPrincipal(@WebParam(name="principalId") String principalId);
	
    
    /**
     * Check whether the group identified by groupMemberId is a member of the group
     * identified by groupId.  This will recurse through all groups.
     */
    boolean isGroupMemberOfGroup(@WebParam(name="groupMemberId") String groupMemberId, @WebParam(name="groupId") String groupId);

    boolean isGroupActive( @WebParam(name="groupId") String groupId );
    
    /**
     * Get all the principals of the given group.  Recurses into contained groups
     * to provide a comprehensive list.
     */
	List<String> getMemberPrincipalIds(@WebParam(name="groupId") String groupId);

    /**
     * Get all the principals directly assigned to the given group.
     */
	List<String> getDirectMemberPrincipalIds(@WebParam(name="groupId") String groupId);
	
	/**
	 * Get all the groups contained by the given group.  Recurses into contained groups
     * to provide a comprehensive list.
	 */
	List<String> getMemberGroupIds( @WebParam(name="groupId") String groupId );
	
    /**
     * Get all the groups which are direct members of the given group.
     */
	List<String> getDirectMemberGroupIds( @WebParam(name="groupId") String groupId );
	
	/**
     * Get the groups which are parents of the given group.
     * 
     * This will recurse into groups above the given group and build a complete
     * list of all groups included above this group.
     */
    List<String> getParentGroupIds(@WebParam(name="groupId") String groupId);
    
    /**
     * Get the groupIds which that are directly above this group.
     */
    List<String> getDirectParentGroupIds(@WebParam(name="groupId") String groupId);
    

	/**
	 * Get all the attributes of the given group.
	 */
    @XmlJavaTypeAdapter(value = JaxbStringMapAdapter.class) Map<String,String> getGroupAttributes( @WebParam(name="groupId") String groupId );
	
	Collection<GroupMembershipInfo> getGroupMembers( @WebParam(name="groupIds") List<String> groupIds );
	
    Collection<GroupMembershipInfo> getGroupMembersOfGroup( @WebParam(name="groupId") String groupId );
}
