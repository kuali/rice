package org.kuali.rice.kim.impl.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.services.IdentityService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.framework.type.KimDelegationTypeService;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.services.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleServiceBase {
    protected static final String ROLE_IMPL_CACHE_PREFIX = "RoleImpl-ID-";
    protected static final String ROLE_IMPL_BY_NAME_CACHE_PREFIX = "RoleImpl-Name-";
    protected static final String ROLE_IMPL_CACHE_GROUP = "RoleImpl";

    protected static final String ROLE_MEMBER_IMPL_CACHE_PREFIX = "RoleMemberBo-ID-";
    protected static final String ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX = "RoleMemberBo-List-";
    protected static final String ROLE_MEMBER_IMPL_CACHE_GROUP = "RoleMemberBo";

    protected static final String DELEGATION_IMPL_CACHE_PREFIX = "DelegateBo-ID-";
    protected static final String DELEGATION_IMPL_LIST_CACHE_PREFIX = "DelegateBo-List-";
    protected static final String DELEGATION_IMPL_CACHE_GROUP = "DelegateBo";

    protected static final String DELEGATION_MEMBER_IMPL_CACHE_PREFIX = "DelegateMemberBo-ID-";
    protected static final String DELEGATION_MEMBER_IMPL_BY_DLGN_AND_ID_CACHE_PREFIX = "DelegateMemberBo-DelegationAndId-";
    protected static final String DELEGATION_MEMBER_IMPL_LIST_CACHE_PREFIX = "DelegateMemberBo-List-";
    protected static final String DELEGATION_MEMBER_IMPL_LIST_BY_MBR_DLGN_CACHE_PREFIX = "DelegateMemberBo-List-MemberAndDelegationId-";
    protected static final String DELEGATION_MEMBER_IMPL_CACHE_GROUP = "DelegateMemberBo";

    private BusinessObjectService businessObjectService;
    private LookupService lookupService;
    private RiceCacheAdministrator cacheAdministrator;
    private SequenceAccessorService sequenceAccessorService;
    private IdentityService identityService;
    private GroupService groupService;
    private ResponsibilityInternalService responsibilityInternalService;

    private Map<String, KimRoleTypeService> roleTypeServiceCache = Collections.synchronizedMap(new HashMap<String, KimRoleTypeService>());
    private Map<String, KimDelegationTypeService> delegationTypeServiceCache = Collections.synchronizedMap(new HashMap<String, KimDelegationTypeService>());

    private Map<String, Boolean> applicationRoleTypeCache = Collections.synchronizedMap(new HashMap<String, Boolean>());
    private RoleDao roleDao;

    /**
     * A helper enumeration for indicating which KimRoleDao method to use when attempting to get role/delegation-related lists that are not in the cache.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    public static enum RoleDaoAction {
        ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS("principalMembers-"),
        ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS("groupMembers-"),
        ROLE_MEMBERS_FOR_ROLE_IDS("membersOfRole-"),
        ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS("rolesAsMembers-"),
        ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS("roleIdsWithFilters-"),
        DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS("delegationPrincipals-"),
        DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS("delegationGroups-"),
        DELEGATION_MEMBERS_FOR_DELEGATION_IDS("delegationMembers-");

        public final String DAO_ACTION_CACHE_PREFIX;

        private RoleDaoAction(String daoActionCachePrefix) {
            this.DAO_ACTION_CACHE_PREFIX = daoActionCachePrefix;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Role Membership Caching Methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Generates a String key to use for storing or retrieving a RoleMemberBo to/from the cache.
     *
     * @param roleMemberId The ID of the RoleMemberBo to generate a key for.
     * @return A cache key for the RoleMemberBo with the given ID.
     */
    protected String getRoleMemberCacheKey(String roleMemberId) {
        return ROLE_MEMBER_IMPL_CACHE_PREFIX + roleMemberId;
    }

    /**
     * Generates a String key to use for storing or retrieving a list of RoleMemberBos to/from the cache. The key is generated by specifying
     * certain properties that are common to all the RoleMemberBo instances in the list to be cached. Note that at least one common
     * property will always be null; for instance, a role member cannot have a member ID that refers to both a principal and a group, so at least
     * the principalId or the groupId parameter will have a null value passed in by the calling code in this RoleService implementation. Also,
     * the value of the RoleDaoAction parameter passed in will affect which subsequent parameters will be used in generating the cache key.
     *
     * @param roleDaoAction  The RoleDaoAction signifying which KimRoleDao call found this list; will determine how and which parameters are used.
     * @param roleId         The role ID (possibly as a member ID) shared among the members of the list; will be interpreted as an empty String if this is blank.
     * @param principalId    The (principal) member ID shared among the members of the list; will be interpreted as an empty String if this is blank.
     * @param groupId        The (group) member ID shared among the members of the list; will be interpreted as an empty String if this is blank.
     * @param memberTypeCode The member type code shared among the members of the list; will be interpreted as an empty String if this is blank.
     * @return A cache key for the RoleMemberBo list whose members share the given roleId, principalId, groupId, and memberTypeCode.
     * @throws IllegalArgumentException if the RoleDaoAction parameter does not refer to a role-member-related enumeration value.
     */
    protected String getRoleMemberListCacheKey(RoleDaoAction roleDaoAction, String roleId, String principalId, String groupId, String memberTypeCode) {
        switch (roleDaoAction) {
            case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS: // Search for principal role members only.
                return new StringBuilder(ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX).append(roleDaoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(roleId) ? "" : roleId).append('-').append(StringUtils.isBlank(principalId) ? "" : principalId).toString();
            case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS: // Search for group role members only.
                return new StringBuilder(ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX).append(roleDaoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(roleId) ? "" : roleId).append('-').append(StringUtils.isBlank(groupId) ? "" : groupId).toString();
            case ROLE_MEMBERS_FOR_ROLE_IDS: // Search for role members with the given member type code.
                return new StringBuilder(ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX).append(roleDaoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(roleId) ? "" : roleId).append('-').append(StringUtils.isBlank(memberTypeCode) ? "" : memberTypeCode).toString();
            case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS: // Search for role members who are also roles.
                return new StringBuilder(ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX).append(roleDaoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(roleId) ? "" : roleId).toString();
            case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS: // Search for role members that might be roles, principals, or groups.
                return new StringBuilder(ROLE_MEMBER_IMPL_LIST_CACHE_PREFIX).append(roleDaoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(roleId) ? "" : roleId).append('-').append(StringUtils.isBlank(principalId) ? "" : principalId).append(
                        '-').append(StringUtils.isBlank(groupId) ? "" : groupId).append('-').append(
                        StringUtils.isBlank(memberTypeCode) ? "" : memberTypeCode).toString();
            default: // The daoActionToTake parameter is invalid; throw an exception.
                throw new IllegalArgumentException("The 'roleDaoAction' parameter cannot refer to a non-role-member-related value!");
        }
    }

    /**
     * Converts the Qualifier Name/Value Role qualification set into Qualifier AttributeID/Value set
     *
     * @param qualification The original role qualification attribute set
     * @return Converted Map<String, String> containing ID/value pairs
     */
    private Map<String, String> convertQualifierKeys(Map<String, String> qualification) {
        Map<String, String> convertedQualification = new HashMap<String, String>();
        if (qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
            for (String attributeName : qualification.keySet()) {
                if (StringUtils.isNotEmpty(getKimAttributeId(attributeName))) {
                    convertedQualification.put(getKimAttributeId(attributeName), qualification.get(attributeName));
                }
            }
        }
        return convertedQualification;
    }

    public Set<String> getRoleTypeRoleMemberIds(String roleId) {
        Set<String> results = new HashSet();
        getNestedRoleTypeMemberIds(roleId, results);
        return results;
    }

    protected void getNestedRoleTypeMemberIds(String roleId, Set members) throws RuntimeException {
        ArrayList<String> roleList = new ArrayList<String>(1);
        roleList.add(roleId);
        List<RoleMemberBo> firstLevelMembers = getStoredRoleMembersForRoleIds(roleList, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, null);
        for (RoleMemberBo member : firstLevelMembers) {
            if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(member.getMemberTypeCode())) {
                if (!members.contains(member.getMemberId())) {
                    members.add(member.getMemberId());
                    getNestedRoleTypeMemberIds(member.getMemberId(), members);
                }
            }
        }
    }

    public List<String> getMemberParentRoleIds(String memberType, String memberId) {
        List<RoleMemberBo> parentRoleMembers = roleDao.getRoleMembershipsForMemberId(memberType, memberId, null);

        List<String> parentRoleIds = new ArrayList<String>(parentRoleMembers.size());
        for (RoleMemberBo parentRoleMember : parentRoleMembers) {
            parentRoleIds.add(parentRoleMember.getRoleId());
        }

        return parentRoleIds;
    }

    /**
     * Retrieves a list of RoleMemberBo instances from the cache and/or the KimRoleDao as appropriate.
     *
     * @param daoActionToTake An indicator for which KimRoleDao method should be used to get the results if the desired RoleMemberBos are not cached.
     * @param roleIds         The role IDs to filter by; may get used as the IDs for members that are also roles, depending on the daoActionToTake value.
     * @param principalId     The principal ID to filter by; may get ignored depending on the daoActionToTake value.
     * @param groupIds        The group IDs to filter by; may get ignored depending on the daoActionToTake value.
     * @param memberTypeCode  The member type code to filter by; may get overridden depending on the daoActionToTake value.
     * @param qualification   The original role qualification attribute set
     * @return A list of RoleMemberBo instances based on the provided parameters.
     * @throws IllegalArgumentException if daoActionToTake refers to an enumeration constant that is not role-member-related.
     */
    protected List<RoleMemberBo> getRoleMemberBoList(RoleDaoAction daoActionToTake, Collection<String> roleIds, String principalId,
                                                     Collection<String> groupIds, String memberTypeCode, Map<String, String> qualification) {
        List<RoleMemberBo> finalResults = new ArrayList<RoleMemberBo>();
        List<RoleMemberCacheKeyHelper> searchKeys = new ArrayList<RoleMemberCacheKeyHelper>();
        List<RoleMemberCacheKeyHelper> uncachedKeys = new ArrayList<RoleMemberCacheKeyHelper>();
        Set<String> usedKeys = new HashSet<String>();
        Map<String, String> convertedQualification = convertQualifierKeys(qualification);

        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = Collections.singletonList(null);
        }
        if (groupIds == null || groupIds.isEmpty()) {
            groupIds = Collections.singletonList(null);
        }

        // Attempt to find any pre-cached role members based on what KimRoleDao method call is desired.
        switch (daoActionToTake) {
            case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS: // Search for principal role members only.
                for (String roleId : roleIds) {
                    searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, principalId, null, Role.PRINCIPAL_MEMBER_TYPE));
                }
                break;
            case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS: // Search for group role members only.
                for (String roleId : roleIds) {
                    for (String groupId : groupIds) {
                        searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, null, groupId, Role.GROUP_MEMBER_TYPE));
                    }
                }
                break;
            case ROLE_MEMBERS_FOR_ROLE_IDS: // Search for role members with the given member type code.
                for (String roleId : roleIds) {
                    searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, null, null, memberTypeCode));
                }
                break;
            case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS: // Search for role members who are also roles.
                for (String roleId : roleIds) {
                    searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, null, null, Role.ROLE_MEMBER_TYPE));
                }
                break;
            case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS: // Search for role members that might be roles, principals, or groups.
                for (String roleId : roleIds) {
                    searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, null, null, Role.ROLE_MEMBER_TYPE));
                    searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, principalId, null, Role.PRINCIPAL_MEMBER_TYPE));
                    for (String groupId : groupIds) {
                        searchKeys.add(new RoleMemberCacheKeyHelper(daoActionToTake, roleId, null, groupId, Role.GROUP_MEMBER_TYPE));
                    }
                }
                break;
            default: // The daoActionToTake parameter is invalid; throw an exception.
                throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-role-member-related value!");
        }

        // Attempt to find any pre-cached role members.
        for (RoleMemberCacheKeyHelper searchKey : searchKeys) {
            if (!usedKeys.contains(searchKey.getCacheKey())) {
                List<RoleMemberBo> tempMembers = (List<RoleMemberBo>) getCacheAdministrator().getFromCache(searchKey.getCacheKey());
                if (tempMembers != null) {
                    finalResults.addAll(tempMembers);
                } else {
                    uncachedKeys.add(searchKey);
                }
                usedKeys.add(searchKey.getCacheKey());
            }
        }

        // If any portion of the result set was not in the cache, then retrieve and cache the missing sections.
        if (!uncachedKeys.isEmpty()) {
            Set<String> uncachedRoleSet = new HashSet<String>();
            Set<String> uncachedGroupSet = new HashSet<String>();
            for (RoleMemberCacheKeyHelper uncachedKey : uncachedKeys) {
                if (uncachedKey.ROLE_ID != null) {
                    uncachedRoleSet.add(uncachedKey.ROLE_ID);
                }
                if (uncachedKey.GROUP_ID != null) {
                    uncachedGroupSet.add(uncachedKey.GROUP_ID);
                }
            }

            List<String> uncachedRoles = (uncachedRoleSet.isEmpty()) ? null : new ArrayList<String>(uncachedRoleSet);
            List<String> uncachedGroups = (uncachedGroupSet.isEmpty()) ? null : new ArrayList<String>(uncachedGroupSet);
            List<RoleMemberBo> uncachedResults;

            // Retrieve the uncached RoleMemberBos via the appropriate RoleDao method.
            switch (daoActionToTake) {
                case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS: // Search for principal role members only.
                    uncachedResults = roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(uncachedRoles, principalId, convertedQualification);
                    break;
                case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS: // Search for group role members only.
                    uncachedResults = roleDao.getRoleGroupsForGroupIdsAndRoleIds(uncachedRoles, uncachedGroups, convertedQualification);
                    break;
                case ROLE_MEMBERS_FOR_ROLE_IDS: // Search for role members with the given member type code.
                    uncachedResults = roleDao.getRoleMembersForRoleIds(uncachedRoles, memberTypeCode, convertedQualification);
                    break;
                case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS: // Search for role members who are also roles.
                    uncachedResults = roleDao.getRoleMembershipsForRoleIdsAsMembers(uncachedRoles, convertedQualification);
                    break;
                case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS: // Search for role members that might be roles, principals, or groups.
                    uncachedResults = roleDao.getRoleMembersForRoleIdsWithFilters(uncachedRoles, principalId, uncachedGroups, convertedQualification);
                    break;
                default: // This should never happen, since the previous switch block should handle this case appropriately.
                    throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-role-member-related value!");
            }

            cacheRoleMemberLists(uncachedKeys, uncachedResults);
            for (RoleMemberBo uncachedMember : uncachedResults) {
                addRoleMemberBoToCache(uncachedMember);
            }
            finalResults.addAll(uncachedResults);
        }
        return finalResults;
    }

    /**
     * Caches several Lists of role members that are constructed based on the given search keys. Note that a given List will not be cached if
     * it contains any role members that belong to a role that disallows caching of its members.
     *
     * @param keysToCache    The keys of the role member Lists that will be used to store the uncached results.
     * @param membersToCache The uncached role members.
     */
    private void cacheRoleMemberLists(List<RoleMemberCacheKeyHelper> keysToCache, List<RoleMemberBo> membersToCache) {
        if (membersToCache == null) {
            membersToCache = new ArrayList<RoleMemberBo>();
        }
        for (RoleMemberCacheKeyHelper keyToCache : keysToCache) {
            List<RoleMemberBo> roleMembers = new ArrayList<RoleMemberBo>();

            // Cache the Lists that do not contain role members belonging to roles that forbid caching of their members.
            if (RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS.equals(keyToCache.ROLE_DAO_ACTION)) {
                boolean safeToCacheList = true;
                for (RoleMemberBo memberToCache : membersToCache) {
                    if ((keyToCache.ROLE_ID == null || keyToCache.ROLE_ID.equals(memberToCache.getMemberId())) &&
                            (keyToCache.MEMBER_TYPE_CODE == null || keyToCache.MEMBER_TYPE_CODE.equals(memberToCache.getMemberTypeCode()))) {
                        if (shouldCacheMembersOfRole(memberToCache.getRoleId())) {
                            roleMembers.add(memberToCache);
                        } else {
                            safeToCacheList = false;
                            break;
                        }
                    }
                }
                if (safeToCacheList) {
                    getCacheAdministrator().putInCache(keyToCache.getCacheKey(), roleMembers, ROLE_MEMBER_IMPL_CACHE_GROUP);
                }
            } else if (keyToCache.ROLE_ID == null || shouldCacheMembersOfRole(keyToCache.ROLE_ID)) {
                for (RoleMemberBo memberToCache : membersToCache) {
                    if ((keyToCache.ROLE_ID == null || keyToCache.ROLE_ID.equals(memberToCache.getRoleId())) &&
                            (keyToCache.PRINCIPAL_ID == null || keyToCache.PRINCIPAL_ID.equals(memberToCache.getMemberId())) &&
                            (keyToCache.GROUP_ID == null || keyToCache.GROUP_ID.equals(memberToCache.getMemberId())) &&
                            (keyToCache.MEMBER_TYPE_CODE == null || keyToCache.MEMBER_TYPE_CODE.equals(memberToCache.getMemberTypeCode()))) {
                        roleMembers.add(memberToCache);
                    }
                }
                getCacheAdministrator().putInCache(keyToCache.getCacheKey(), roleMembers, ROLE_MEMBER_IMPL_CACHE_GROUP);
            }
        }
    }

    /**
     * Calls the KimRoleDao's "getRolePrincipalsForPrincipalIdAndRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRolePrincipalsForPrincipalIdAndRoleIds(Collection<String> roleIds, String principalId, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS, roleIds, principalId, null, null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleGroupsForGroupIdsAndRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleGroupsForGroupIdsAndRoleIds(Collection<String> roleIds, Collection<String> groupIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS, roleIds, null, groupIds, null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembersForRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS, roleIds, null, null, memberTypeCode, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembershipsForRoleIdsAsMembers" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS, roleIds, null, null, null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembersForRoleIdsWithFilters" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembersForRoleIdsWithFilters(Collection<String> roleIds, String principalId, List<String> groupIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS, roleIds, principalId, groupIds, null, qualification);
    }

    /**
     * Determines whether or not the given role should allow its members to be cached. The default implementation always returns true, but
     * subclasses can override this method if other non-default Role implementations forbid their members from being cached.
     *
     * @param roleId The ID of the role to check for determining whether or not to allow caching of its members.
     * @return True if the given role allows its members to be cached; false otherwise.
     */
    protected boolean shouldCacheMembersOfRole(String roleId) {
        return true;
    }

    protected void addRoleMemberBoToCache(RoleMemberBo roleMember) {
        if (roleMember != null && shouldCacheMembersOfRole(roleMember.getRoleId())) {
            getCacheAdministrator().putInCache(getRoleMemberCacheKey(roleMember.getRoleMemberId()), roleMember, ROLE_MEMBER_IMPL_CACHE_GROUP);
        }
    }

    protected RoleMemberBo getRoleMemberFromCache(String roleMemberId) {
        return (RoleMemberBo) getCacheAdministrator().getFromCache(getRoleMemberCacheKey(roleMemberId));
    }

    public void flushInternalRoleMemberCache() {
        getCacheAdministrator().flushGroup(ROLE_MEMBER_IMPL_CACHE_GROUP);
    }

    /**
     * Retrieves a RoleMemberBo object by its ID. If the role member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it (if it belongs to a role that allows
     * its members to be cached) before returning it.
     */
    protected RoleMemberBo getRoleMemberBo(String roleMemberId) {
        if (StringUtils.isBlank(roleMemberId)) {
            return null;
        }

        // If the RoleMemberBo exists in the cache, return the cached one.
        RoleMemberBo tempRoleMemberBo = getRoleMemberFromCache(roleMemberId);
        if (tempRoleMemberBo != null) {
            return tempRoleMemberBo;
        }
        // Otherwise, retrieve it normally.
        tempRoleMemberBo = getBusinessObjectService().findByPrimaryKey(RoleMemberBo.class,
                Collections.singletonMap(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId));
        addRoleMemberBoToCache(tempRoleMemberBo);
        return tempRoleMemberBo;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Delegation Caching Methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Generates a String key to use for storing or retrieving a DelegateBo to/from the cache.
     *
     * @param delegationId The ID of the DelegateBo to generate a key for.
     * @return A cache key for the DelegateBo with the given ID.
     */
    protected String getDelegationCacheKey(String delegationId) {
        return DELEGATION_IMPL_CACHE_PREFIX + delegationId;
    }

    /**
     * Generates a String key to use for storing or retrieving a List of DelegationBos to/from the cache based on a role's ID.
     *
     * @param roleId The ID of the role that the KIM delegations belong to.
     * @return A cache key for the DelegationBos with the given role ID.
     */
    protected String getDelegationListCacheKey(String roleId) {
        return DELEGATION_IMPL_LIST_CACHE_PREFIX + (StringUtils.isBlank(roleId) ? "" : roleId);
    }

    /**
     * Calls the KimRoleDao's "getDelegationImplMapFromRoleIds" method and/or retrieves any corresponding delegations from the cache.
     */
    protected Map<String, DelegateBo> getStoredDelegationImplMapFromRoleIds(Collection<String> roleIds) {
        Map<String, DelegateBo> finalResults = Collections.emptyMap();

        if (roleIds != null && !roleIds.isEmpty()) {
            Map<String, List<DelegateBo>> uncachedLists = new HashMap<String, List<DelegateBo>>();
            // Retrieve any existing results from the cache.
            finalResults = getDelegationBoMap(uncachedLists, roleIds);

            // Retrieve any uncached results from the database and then cache them.
            if (!uncachedLists.isEmpty()) {
                Map<String, DelegateBo> uncachedResults = roleDao.getDelegationImplMapFromRoleIds(uncachedLists.keySet());

                for (Map.Entry<String, DelegateBo> uncachedResult : uncachedResults.entrySet()) {
                    finalResults.put(uncachedResult.getKey(), uncachedResult.getValue());
                    addDelegationBoToCache(uncachedResult.getValue());
                    uncachedLists.get(uncachedResult.getValue().getRoleId()).add(uncachedResult.getValue());
                }
                for (Map.Entry<String, List<DelegateBo>> uncachedList : uncachedLists.entrySet()) {
                    getCacheAdministrator().putInCache(getDelegationListCacheKey(uncachedList.getKey()),
                            uncachedList.getValue(), DELEGATION_IMPL_CACHE_GROUP);
                }
            }
        }

        return finalResults;
    }

    /**
     * Calls the KimRoleDao's "getDelegationBosForRoleIds" method and/or retrieves any corresponding delegations from the cache.
     */
    protected List<DelegateBo> getStoredDelegationImplsForRoleIds(Collection<String> roleIds) {
        List<DelegateBo> finalResults = new ArrayList<DelegateBo>();

        if (roleIds != null && !roleIds.isEmpty()) {
            Map<String, List<DelegateBo>> uncachedLists = new HashMap<String, List<DelegateBo>>();
            // Retrieve any existing results from the cache.
            Map<String, DelegateBo> tempDelegations = getDelegationBoMap(uncachedLists, roleIds);
            finalResults.addAll(tempDelegations.values());

            // Retrieve any uncached results from the database and then cache them.
            if (!uncachedLists.isEmpty()) {
                List<DelegateBo> uncachedResults = roleDao.getDelegationBosForRoleIds(uncachedLists.keySet());

                for (DelegateBo uncachedResult : uncachedResults) {
                    addDelegationBoToCache(uncachedResult);
                    uncachedLists.get(uncachedResult.getRoleId()).add(uncachedResult);
                }
                for (Map.Entry<String, List<DelegateBo>> uncachedList : uncachedLists.entrySet()) {
                    getCacheAdministrator().putInCache(getDelegationListCacheKey(uncachedList.getKey()),
                            uncachedList.getValue(), DELEGATION_IMPL_CACHE_GROUP);
                }
                finalResults.addAll(uncachedResults);
            }
        }

        return finalResults;
    }

    /**
     * Retrieves any existing delegation lists from the cache for the given role IDs. If the delegations for a given role have not been cached,
     * then a new entry containing the uncached role ID and an empty List will be added to the given Map.
     *
     * @param uncachedLists The Map in which to place any uncached lists; cannot be null.
     * @param roleIds       The IDs of the roles containing the delegations.
     * @return A mutable Map containing any existing results from the cache and which maps the delegations' IDs to the delegation objects.
     * @throws IllegalArgumentException if the provided Map is null.
     */
    protected Map<String, DelegateBo> getDelegationBoMap(Map<String, List<DelegateBo>> uncachedLists, Collection<String> roleIds) {
        if (uncachedLists == null) {
            throw new IllegalArgumentException("'uncachedLists' parameter cannot be null!");
        }
        Map<String, DelegateBo> delegationMap = new HashMap<String, DelegateBo>();

        // Retrieve any existing results from the cache.
        if (roleIds != null && !roleIds.isEmpty()) {
            for (String roleId : roleIds) {
                List<DelegateBo> tempDelegates = (List<DelegateBo>) getCacheAdministrator().getFromCache(getDelegationListCacheKey(roleId));
                if (tempDelegates != null) {
                    for (DelegateBo tempDelegate : tempDelegates) {
                        delegationMap.put(tempDelegate.getDelegationId(), tempDelegate);
                    }
                } else {
                    uncachedLists.put(roleId, new ArrayList<DelegateBo>());
                }
            }
        }

        return delegationMap;
    }

    protected void addDelegationBoToCache(DelegateBo delegate) {
        if (delegate != null) {
            getCacheAdministrator().putInCache(getDelegationCacheKey(delegate.getDelegationId()),
                    delegate, DELEGATION_IMPL_CACHE_GROUP);
        }
    }

    protected DelegateBo getDelegationFromCache(String delegationId) {
        return (DelegateBo) getCacheAdministrator().getFromCache(getDelegationCacheKey(delegationId));
    }

    public void flushInternalDelegationCache() {
        getCacheAdministrator().flushGroup(DELEGATION_IMPL_CACHE_GROUP);
    }

    /**
     * Retrieves a DelegateBo object by its ID. If the delegation already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateBo getDelegationBo(String delegationId) {
        if (StringUtils.isBlank(delegationId)) {
            return null;
        }

        // If the DelegateBo exists in the cache, return the cached one.
        DelegateBo tempDelegate = getDelegationFromCache(delegationId);
        if (tempDelegate != null) {
            return tempDelegate;
        }
        // Otherwise, retrieve it normally.
        tempDelegate = (DelegateBo) getBusinessObjectService().findByPrimaryKey(DelegateBo.class,
                Collections.singletonMap(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId));
        addDelegationBoToCache(tempDelegate);
        return tempDelegate;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Delegation Membership Caching Methods
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Generates a String key to use for storing or retrieving a DelegateMemberBo to/from the cache.
     *
     * @param delegationMemberId The ID of the DelegateMemberBo to generate a key for.
     * @return A cache key for the DelegateMemberBo with the given ID.
     */
    protected String getDelegationMemberCacheKey(String delegationMemberId) {
        return DELEGATION_MEMBER_IMPL_CACHE_PREFIX + delegationMemberId;
    }

    /**
     * Generates a String key to use for storing or retrieving a DelegateMemberBo to/from the cache by both delegation ID and delegation member ID.
     *
     * @param delegationId       The ID of the delegation that the DelegateMemberBo belongs to.
     * @param delegationMemberId The ID of the DelegateMemberBo to generate a key for.
     * @return A cache key for the DelegateMemberBo with the given delegation ID and delegation member ID.
     */
    protected String getDelegationMemberByDelegationAndIdCacheKey(String delegationId, String delegationMemberId) {
        return new StringBuilder(DELEGATION_MEMBER_IMPL_BY_DLGN_AND_ID_CACHE_PREFIX).append(delegationId).append('-').append(delegationMemberId).toString();
    }

    /**
     * Generates a String key to use for storing or retrieving a DelegateMemberBo List to/from the cache by both delegation ID and member ID.
     *
     * @param memberId     The principal/group/role ID of the DelegateMemberBo(s) in the List.
     * @param delegationId The ID of the delegation that the DelegateMemberBo(s) in the List belong to.
     * @return A cache key for the DelegateMemberBo List with the given delegation ID and member ID.
     */
    protected String getDelegationMemberListByMemberAndDelegationIdCacheKey(String memberId, String delegationId) {
        return new StringBuilder(DELEGATION_MEMBER_IMPL_LIST_BY_MBR_DLGN_CACHE_PREFIX).append(StringUtils.isBlank(memberId) ? "" : memberId).append(
                '-').append(StringUtils.isBlank(delegationId) ? "" : delegationId).toString();
    }

    /**
     * Generates a String key to use for storing or retrieving a List of DelegateMemberBo lists to/from the cache. Some parameters may get
     * ignored depending on which KimRoleDao call is desired.
     *
     * @param daoAction    The RoleDaoAction signifying which KimRoleDao call found this list; will determine how and which parameters are used.
     * @param delegationId The ID of the DelegateBo that the indicated delegation member belongs to; will be interpreted as an empty String if blank.
     * @param principalId  The (principal) member ID of the delegation members; will be interpreted as an empty String if blank.
     * @param groupId      The (group) member ID of the delegation members; will be interpreted as an empty String if blank.
     * @return A cache key for the DelegateMemberBo List with the given criteria.
     * @throws IllegalArgumentException if daoAction does not represent a delegation-member-related enumeration value.
     */
    protected String getDelegationMemberListCacheKey(RoleDaoAction daoAction, String delegationId, String principalId, String groupId) {
        switch (daoAction) {
            case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS: // Search for principal delegation members.
                return new StringBuilder(DELEGATION_MEMBER_IMPL_LIST_CACHE_PREFIX).append(daoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(delegationId) ? "" : delegationId).append('-').append(
                        StringUtils.isBlank(principalId) ? "" : principalId).toString();
            case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS: // Search for group delegation members.
                return new StringBuilder(DELEGATION_MEMBER_IMPL_LIST_CACHE_PREFIX).append(daoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(delegationId) ? "" : delegationId).append('-').append(StringUtils.isBlank(groupId) ? "" : groupId).toString();
            case DELEGATION_MEMBERS_FOR_DELEGATION_IDS: // Search for delegation members regardless of their member type.
                return new StringBuilder(DELEGATION_MEMBER_IMPL_LIST_CACHE_PREFIX).append(daoAction.DAO_ACTION_CACHE_PREFIX).append(
                        StringUtils.isBlank(delegationId) ? "" : delegationId).toString();
            default: // daoActionToTake is invalid; throw an exception.
                throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-delegation-member-related value!");
        }
    }

    /**
     * Retrieves a List of delegation members from the cache and/or the KimRoleDao as appropriate.
     *
     * @param daoActionToTake An indicator for which KimRoleDao method to use for retrieving uncached results.
     * @param delegationIds   The IDs of the delegations that the members belong to.
     * @param principalId     The principal ID of the principal delegation members; may get ignored depending on the RoleDaoAction value.
     * @param groupIds        The group IDs of the group delegation members; may get ignored depending on the RoleDaoAction value.
     * @return A List of DelegateMemberBo objects based on the provided parameters.
     * @throws IllegalArgumentException if daoActionToTake does not represent a delegation-member-list-related enumeration value.
     */
    protected List<DelegateMemberBo> getDelegationMemberBoList(RoleDaoAction daoActionToTake, Collection<String> delegationIds,
                                                               String principalId, List<String> groupIds) {
        List<DelegateMemberBo> finalResults = new ArrayList<DelegateMemberBo>();
        List<String[]> uncachedKeys = new ArrayList<String[]>();
        Set<String> usedKeys = new HashSet<String>();
        if (delegationIds == null || delegationIds.isEmpty()) {
            delegationIds = Collections.singletonList(null);
        }
        if (groupIds == null || groupIds.isEmpty()) {
            groupIds = Collections.singletonList(null);
        }

        // Search for cached values based on the intended search action.
        switch (daoActionToTake) {
            case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS: // Search for principal delegation members.
                for (String delegationId : delegationIds) {
                    String tempKey = getDelegationMemberListCacheKey(daoActionToTake, delegationId, principalId, null);
                    if (!usedKeys.contains(tempKey)) {
                        List<DelegateMemberBo> tempMembers = (List<DelegateMemberBo>) getCacheAdministrator().getFromCache(tempKey);
                        if (tempMembers != null) {
                            finalResults.addAll(tempMembers);
                        } else {
                            uncachedKeys.add(new String[]{delegationId, principalId, null});
                        }
                        usedKeys.add(tempKey);
                    }
                }
                break;
            case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS: // Search for group delegation members.
                for (String delegationId : delegationIds) {
                    for (String groupId : groupIds) {
                        String tempKey = getDelegationMemberListCacheKey(daoActionToTake, delegationId, null, groupId);
                        if (!usedKeys.contains(tempKey)) {
                            List<DelegateMemberBo> tempMembers = (List<DelegateMemberBo>) getCacheAdministrator().getFromCache(tempKey);
                            if (tempMembers != null) {
                                finalResults.addAll(tempMembers);
                            } else {
                                uncachedKeys.add(new String[]{delegationId, null, groupId});
                            }
                        }
                    }
                }
                break;
            default: // daoActionToTake is invalid; throw an exception.
                throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-delegation-member-list-related value!");
        }

        // Retrieve any uncached values based on the intended search action.
        if (!uncachedKeys.isEmpty()) {
            List<DelegateMemberBo> uncachedResults = new ArrayList<DelegateMemberBo>();
            Set<String> uncachedDelegationSet = new HashSet<String>();
            Set<String> uncachedGroupSet = new HashSet<String>();
            for (String[] uncachedKey : uncachedKeys) {
                if (uncachedKey[0] != null) {
                    uncachedDelegationSet.add(uncachedKey[0]);
                }
                if (uncachedKey[2] != null) {
                    uncachedGroupSet.add(uncachedKey[2]);
                }
            }
            List<String> uncachedDelegations = (uncachedDelegationSet.isEmpty()) ? null : new ArrayList<String>(uncachedDelegationSet);
            List<String> uncachedGroups = (uncachedGroupSet.isEmpty()) ? null : new ArrayList<String>(uncachedGroupSet);
            String memberTypeCode = null;

            // Search for uncached values based on the intended search action.
            switch (daoActionToTake) {
                case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS: // Search for principal delegation members.
                    uncachedResults = roleDao.getDelegationPrincipalsForPrincipalIdAndDelegationIds(uncachedDelegations, principalId);
                    memberTypeCode = Role.PRINCIPAL_MEMBER_TYPE;
                    break;
                case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS: // Search for group delegation members.
                    uncachedResults = roleDao.getDelegationGroupsForGroupIdsAndDelegationIds(uncachedDelegations, uncachedGroups);
                    memberTypeCode = Role.GROUP_MEMBER_TYPE;
                    break;
                default: // This should never happen since the previous switch block should handle this case appropriately.
                    throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-delegation-member-list-related value!");
            }

            // Cache the delegation members and add them to the final results.
            cacheDelegationMemberLists(daoActionToTake, uncachedKeys, memberTypeCode, uncachedResults);
            for (DelegateMemberBo uncachedResult : uncachedResults) {
                addDelegateMemberBoToCache(uncachedResult);
            }
            finalResults.addAll(uncachedResults);
        }

        return finalResults;
    }

    /**
     * Caches several Lists of delegation members that are constructed based on the given search parameters.
     *
     * @param daoActionToTake The enumeration constant representing the KimRoleDao call that returned the results.
     * @param uncachedKeys    The keys of the delegation member Lists that will be used to store the uncached results.
     * @param memberTypeCode  The member type code of all the delegation members in the uncached results.
     * @param uncachedMembers The uncached delegation members.
     */
    private void cacheDelegationMemberLists(RoleDaoAction daoActionToTake, List<String[]> uncachedKeys,
                                            String memberTypeCode, List<DelegateMemberBo> uncachedMembers) {
        // Place the uncached delegation members into the list cache appropriately.
        for (String[] uncachedKey : uncachedKeys) {
            List<DelegateMemberBo> tempMembers = new ArrayList<DelegateMemberBo>();
            for (DelegateMemberBo uncachedMember : uncachedMembers) {
                if ((memberTypeCode == null || memberTypeCode.equals(uncachedMember.getTypeCode())) &&
                        (uncachedKey[0] == null || uncachedKey[0].equals(uncachedMember.getDelegationId())) &&
                        (uncachedKey[1] == null || uncachedKey[1].equals(uncachedMember.getMemberId())) &&
                        (uncachedKey[2] == null || uncachedKey[2].equals(uncachedMember.getMemberId()))) {
                    tempMembers.add(uncachedMember);
                }
            }
            getCacheAdministrator().putInCache(getDelegationMemberListCacheKey(daoActionToTake, uncachedKey[0], uncachedKey[1], uncachedKey[2]),
                    tempMembers, DELEGATION_MEMBER_IMPL_CACHE_GROUP);
        }
    }

    /**
     * Calls the KimRoleDao's "getDelegationPrincipalsForPrincipalIdAndDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<DelegateMemberBo> getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(Collection<String> delegationIds, String principalId) {
        return getDelegationMemberBoList(RoleDaoAction.DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS, delegationIds, principalId, null);
    }

    /**
     * Calls the KimRoleDao's "getDelegationGroupsForGroupIdAndDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<DelegateMemberBo> getStoredDelegationGroupsForGroupIdsAndDelegationIds(Collection<String> delegationIds, List<String> groupIds) {
        return getDelegationMemberBoList(RoleDaoAction.DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS, delegationIds, null, groupIds);
    }

    /**
     * Calls the KimRoleDao's "getDelegationMembersForDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected Map<String, List<DelegateMemberBo>> getStoredDelegationMembersForDelegationIds(List<String> delegationIds) {
        Map<String, List<DelegateMemberBo>> finalResults = new HashMap<String, List<DelegateMemberBo>>();
        Set<String> uncachedDelegationIds = new HashSet<String>();
        boolean idListWasNullOrEmpty = (delegationIds == null || delegationIds.isEmpty());
        if (idListWasNullOrEmpty) {
            delegationIds = Collections.singletonList(null);
        }

        // Retrieve any existing Lists from the cache.
        for (String delegationId : delegationIds) {
            List<DelegateMemberBo> tempMembers = (List<DelegateMemberBo>) getCacheAdministrator().getFromCache(
                    getDelegationMemberListCacheKey(RoleDaoAction.DELEGATION_MEMBERS_FOR_DELEGATION_IDS, delegationId, null, null));
            if (tempMembers != null) {
                finalResults.put(delegationId, tempMembers);
            } else {
                uncachedDelegationIds.add(delegationId);
            }
        }

        // Retrieve and cache any uncached results. If the initial delegation ID List was null or empty, then also cache a List holding all the results.
        if (!uncachedDelegationIds.isEmpty()) {
            List<String> uncachedIdsList = (idListWasNullOrEmpty) ? new ArrayList<String>() : new ArrayList<String>(uncachedDelegationIds);

            Map<String, List<DelegateMemberBo>> tempMemberMap = roleDao.getDelegationMembersForDelegationIds(uncachedIdsList);
            List<DelegateMemberBo> allMembers = new ArrayList<DelegateMemberBo>();

            for (Map.Entry<String, List<DelegateMemberBo>> tempMemberEntry : tempMemberMap.entrySet()) {
                getCacheAdministrator().putInCache(getDelegationMemberListCacheKey(RoleDaoAction.DELEGATION_MEMBERS_FOR_DELEGATION_IDS,
                        tempMemberEntry.getKey(), null, null), tempMemberEntry.getValue(), DELEGATION_MEMBER_IMPL_CACHE_GROUP);
                for (DelegateMemberBo tempMember : tempMemberEntry.getValue()) {
                    addDelegateMemberBoToCache(tempMember);
                }
                if (idListWasNullOrEmpty) {
                    allMembers.addAll(tempMemberEntry.getValue());
                }
                finalResults.put(tempMemberEntry.getKey(), tempMemberEntry.getValue());
            }

            if (idListWasNullOrEmpty) {
                getCacheAdministrator().putInCache(getDelegationMemberListCacheKey(RoleDaoAction.DELEGATION_MEMBERS_FOR_DELEGATION_IDS,
                        null, null, null), allMembers, DELEGATION_MEMBER_IMPL_CACHE_GROUP);
            }
        }

        return finalResults;
    }

    protected void addDelegateMemberBoToCache(DelegateMemberBo delegateMember) {
        if (delegateMember != null) {
            getCacheAdministrator().putInCache(getDelegationMemberCacheKey(delegateMember.getDelegationMemberId()),
                    delegateMember, DELEGATION_MEMBER_IMPL_CACHE_GROUP);
            getCacheAdministrator().putInCache(getDelegationMemberByDelegationAndIdCacheKey(delegateMember.getDelegationId(),
                    delegateMember.getDelegationMemberId()), delegateMember, DELEGATION_MEMBER_IMPL_CACHE_GROUP);
        }
    }

    protected void addDelegationMemberBoListByMemberAndDelegationIdToCache(
            List<DelegateMemberBo> memberList, String memberId, String delegationId) {
        if (memberList != null) {
            getCacheAdministrator().putInCache(getDelegationMemberListByMemberAndDelegationIdCacheKey(memberId, delegationId),
                    memberList, DELEGATION_MEMBER_IMPL_CACHE_GROUP);
        }
    }

    protected DelegateMemberBo getDelegationMemberFromCache(String delegationMemberId) {
        return (DelegateMemberBo) getCacheAdministrator().getFromCache(getDelegationMemberCacheKey(delegationMemberId));
    }

    protected DelegateMemberBo getDelegationMemberByDelegationAndIdFromCache(String delegationId, String delegationMemberId) {
        return (DelegateMemberBo) getCacheAdministrator().getFromCache(getDelegationMemberByDelegationAndIdCacheKey(delegationId, delegationMemberId));
    }

    protected List<DelegateMemberBo> getDelegationMemberListByMemberAndDelegationIdFromCache(String memberId, String delegationId) {
        return (List<DelegateMemberBo>)
                getCacheAdministrator().getFromCache(getDelegationMemberListByMemberAndDelegationIdCacheKey(memberId, delegationId));
    }

    public void flushInternalDelegationMemberCache() {
        getCacheAdministrator().flushGroup(DELEGATION_MEMBER_IMPL_CACHE_GROUP);
    }

    /**
     * Retrieves a DelegateMemberBo object by its ID. If the delegation member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateMemberBo getDelegateMemberBo(String delegationMemberId) {
        if (StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        // If the DelegateMemberBo exists in the cache, return the cached one.
        DelegateMemberBo tempDelegateMember = getDelegationMemberFromCache(delegationMemberId);
        if (tempDelegateMember != null) {
            return tempDelegateMember;
        }
        // Otherwise, retrieve it normally.
        tempDelegateMember = (DelegateMemberBo) getBusinessObjectService().findByPrimaryKey(DelegateMemberBo.class,
                Collections.singletonMap(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId));
        addDelegateMemberBoToCache(tempDelegateMember);
        return tempDelegateMember;
    }

    /**
     * Retrieves a DelegateMemberBo object by its ID and the ID of the delegation it belongs to. If the delegation member exists in the cache,
     * this method will return the cached one; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateMemberBo getDelegationMemberBoByDelegationAndId(String delegationId, String delegationMemberId) {
        if (StringUtils.isBlank(delegationId) || StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        // If the DelegateMemberBo exists in the cache, return the cached one.
        DelegateMemberBo tempDelegateMember = getDelegationMemberByDelegationAndIdFromCache(delegationId, delegationMemberId);
        if (tempDelegateMember != null) {
            return tempDelegateMember;
        }
        // Otherwise, retrieve it normally.
        Map<String, String> searchCriteria = new HashMap<String, String>();
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId);
        List<DelegateMemberBo> memberList =
                (List<DelegateMemberBo>) getBusinessObjectService().findMatching(DelegateMemberBo.class, searchCriteria);
        if (memberList != null && !memberList.isEmpty()) {
            tempDelegateMember = memberList.get(0);
            addDelegateMemberBoToCache(tempDelegateMember);
        }
        return tempDelegateMember;
    }

    /**
     * Retrieves a DelegateMemberBo List by (principal/group/role) member ID and delegation ID. If the List already exists in the cache,
     * this method will return the cached one; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected List<DelegateMemberBo> getDelegationMemberBoListByMemberAndDelegationId(String memberId, String delegationId) {
        // If the DelegateMemberBo List exists in the cache, return the cached one.
        List<DelegateMemberBo> memberList = getDelegationMemberListByMemberAndDelegationIdFromCache(memberId, delegationId);
        if (memberList != null) {
            return memberList;
        }

        // Otherwise, retrieve it normally.
        Map<String, String> searchCriteria = new HashMap<String, String>();
        searchCriteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        List<DelegateMemberBo> tempList =
                (List<DelegateMemberBo>) getBusinessObjectService().findMatching(DelegateMemberBo.class, searchCriteria);
        if (tempList != null && !tempList.isEmpty()) {
            memberList = new ArrayList<DelegateMemberBo>();
            memberList.addAll(tempList);
            addDelegationMemberBoListByMemberAndDelegationIdToCache(memberList, memberId, delegationId);
        }
        return memberList;
    }

    public void flushInternalRoleCache() {
        getCacheAdministrator().flushGroup(ROLE_IMPL_CACHE_GROUP);
    }

    public RoleMember findRoleMember(String roleMemberId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
        List<RoleMember> roleMembers = findRoleMembers(fieldValues);
        if (roleMembers != null && roleMembers.size() > 0) {
            return roleMembers.get(0);
        }
        return null;
    }

    public List<RoleMember> findRoleMembers(Map<String, String> fieldValues) {
        List<RoleMember> roleMembers = new ArrayList<RoleMember>();
        List<RoleMemberBo> roleMemberBos = (List<RoleMemberBo>) getLookupService().findCollectionBySearchHelper(
                RoleMemberBo.class, fieldValues, true);

        for (RoleMemberBo bo : roleMemberBos) {
            RoleMember roleMember = RoleMemberBo.to(bo);
            roleMembers.add(roleMember);
        }
        return roleMembers;
    }

    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(String roleMemberId) {
        Map<String, String> criteria = new HashMap<String, String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);

        List<RoleResponsibilityActionBo> responsibilityActionBoList = (List<RoleResponsibilityActionBo>)
                getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);

        List<RoleResponsibilityAction> roleResponsibilityActionsList = new ArrayList<RoleResponsibilityAction>();
        for (RoleResponsibilityActionBo roleResponsibilityActionBo : responsibilityActionBoList) {
            RoleResponsibilityAction roleResponsibility = RoleResponsibilityActionBo.to(roleResponsibilityActionBo);
            roleResponsibilityActionsList.add(roleResponsibility);
        }
        return roleResponsibilityActionsList;
    }

    public List<DelegateMember> findDelegateMembers(final Map<String, String> fieldValues) {
        List<DelegateMember> delegateMembers = new ArrayList<DelegateMember>();
        List<DelegateBo> delegateBoList = (List<DelegateBo>) getLookupService().findCollectionBySearchHelper(
                DelegateBo.class, fieldValues, true);

        if (delegateBoList != null && !delegateBoList.isEmpty()) {
            Map<String, String> delegationMemberFieldValues = new HashMap<String, String>();
            for (String key : fieldValues.keySet()) {
                if (key.startsWith(KimConstants.KimUIConstants.MEMBER_ID_PREFIX)) {
                    delegationMemberFieldValues.put(
                            key.substring(key.indexOf(
                                    KimConstants.KimUIConstants.MEMBER_ID_PREFIX) + KimConstants.KimUIConstants.MEMBER_ID_PREFIX.length()),
                            fieldValues.get(key));
                }
            }

            StringBuffer memberQueryString = new StringBuffer();
            for (DelegateBo delegate : delegateBoList) {
                memberQueryString.append(delegate.getDelegationId() + KimConstants.KimUIConstants.OR_OPERATOR);
            }
            delegationMemberFieldValues.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID,
                    StringUtils.stripEnd(memberQueryString.toString(), KimConstants.KimUIConstants.OR_OPERATOR));
            List<DelegateMemberBo> delegateMemberBoList = (List<DelegateMemberBo>) getLookupService().findCollectionBySearchHelper(
                    DelegateMemberBo.class, delegationMemberFieldValues, true);


            for (DelegateMemberBo delegateMemberBo : delegateMemberBoList) {
                DelegateMember delegateMember = DelegateMemberBo.to(delegateMemberBo);
                delegateMembers.add(delegateMember);
            }
        }
        return delegateMembers;
    }

    protected DelegateBo getDelegationImpl(List<DelegateBo> delegates, String delegationId) {
        if (StringUtils.isEmpty(delegationId) || delegates == null) {
            return null;
        }
        for (DelegateBo delegate : delegates) {
            if (StringUtils.equals(delegate.getDelegationId(), delegationId)) {
                return delegate;
            }
        }
        return null;
    }

    protected Object getMember(String memberTypeCode, String memberId) {
        if (StringUtils.isBlank(memberId)) {
            return null;
        }
        if (KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)) {
            return getIdentityService().getPrincipal(memberId);
        } else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
            return getGroupService().getGroup(memberId);
        } else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
            return getRoleBo(memberId);
        }
        return null;
    }

    protected String getMemberName(Object member) {
        if (member == null) {
            return "";
        }
        if (member instanceof Principal) {
            return ((Principal) member).getPrincipalName();
        }
        if (member instanceof Group) {
            return ((Group) member).getName();
        }
        if (member instanceof Role) {
            return ((Role) member).getName();
        }
        return member.toString();
    }

    protected String getMemberNamespaceCode(Object member) {
        if (member == null) {
            return "";
        }
        if (member instanceof Principal) {
            return "";
        }
        if (member instanceof Group) {
            return ((Group) member).getNamespaceCode();
        }
        if (member instanceof Role) {
            return ((Role) member).getNamespaceCode();
        }
        return "";
    }

    protected RoleBo getRoleBo(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return null;
        }
        // check for a non-null result in the cache, return it if found
        RoleBo cachedResult = getRoleFromCache(roleId);
        if (cachedResult != null) {
            return cachedResult;
        }
        // otherwise, run the query
        RoleBo result = (RoleBo) getBusinessObjectService().findBySinglePrimaryKey(RoleBo.class, roleId);
        addRoleBoToCache(result);
        return result;
    }

    protected DelegateBo getDelegationOfType(String roleId, String delegationTypeCode) {
        List<DelegateBo> roleDelegates = getRoleDelegations(roleId);
        if (isDelegationPrimary(delegationTypeCode)) {
            return getPrimaryDelegation(roleId, roleDelegates);
        } else {
            return getSecondaryDelegation(roleId, roleDelegates);
        }
    }

    private DelegateBo getSecondaryDelegation(String roleId, List<DelegateBo> roleDelegates) {
        DelegateBo secondaryDelegate = null;
        RoleBo roleBo = getRoleBo(roleId);
        for (DelegateBo delegate : roleDelegates) {
            if (isDelegationSecondary(delegate.getDelegationTypeCode())) {
                secondaryDelegate = delegate;
            }
        }
        if (secondaryDelegate == null) {
            secondaryDelegate = new DelegateBo();
            secondaryDelegate.setRoleId(roleId);
            secondaryDelegate.setDelegationId(getNewDelegationId());
            secondaryDelegate.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
            secondaryDelegate.setKimTypeId(roleBo.getKimTypeId());
        }
        return secondaryDelegate;
    }

    protected DelegateBo getPrimaryDelegation(String roleId, List<DelegateBo> roleDelegates) {
        DelegateBo primaryDelegate = null;
        RoleBo roleBo = getRoleBo(roleId);
        for (DelegateBo delegate : roleDelegates) {
            if (isDelegationPrimary(delegate.getDelegationTypeCode())) {
                primaryDelegate = delegate;
            }
        }
        if (primaryDelegate == null) {
            primaryDelegate = new DelegateBo();
            primaryDelegate.setRoleId(roleId);
            primaryDelegate.setDelegationId(getNewDelegationId());
            primaryDelegate.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
            primaryDelegate.setKimTypeId(roleBo.getKimTypeId());
        }
        return primaryDelegate;
    }

    protected RoleMemberBo matchingMemberRecord(List<RoleMemberBo> roleMembers, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        for (RoleMemberBo rm : roleMembers) {
            if (doesMemberMatch(rm, memberId, memberTypeCode, qualifier)) {
                return rm;
            }
        }
        return null;
    }

    protected boolean isDelegationPrimary(String delegationTypeCode) {
        return DelegationType.PRIMARY.getCode().equals(delegationTypeCode);
    }

    protected boolean isDelegationSecondary(String delegationTypeCode) {
        return DelegationType.PRIMARY.getCode().equals(delegationTypeCode);
    }


    private List<DelegateBo> getRoleDelegations(String roleId) {
        if (roleId == null) {
            return new ArrayList<DelegateBo>();
        }
        return getStoredDelegationImplsForRoleIds(Collections.singletonList(roleId));

    }

    protected RoleBo getRoleBoByName(String namespaceCode, String roleName) {
        if (StringUtils.isBlank(namespaceCode)
                || StringUtils.isBlank(roleName)) {
            return null;
        }
        // check for a non-null result in the cache, return it if found
        RoleBo cachedResult = getRoleFromCache(namespaceCode, roleName);
        if (cachedResult != null) {
            return cachedResult;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KimConstants.UniqueKeyConstants.NAME, roleName);
        criteria.put(KRADPropertyConstants.ACTIVE, "Y");
        // while this is not actually the primary key - there will be at most one row with these criteria
        RoleBo result = getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria);
        addRoleBoToCache(result);
        return result;
    }

    protected boolean doAnyMemberRecordsMatch(List<RoleMemberBo> roleMembers, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        for (RoleMemberBo rm : roleMembers) {
            if (doesMemberMatch(rm, memberId, memberTypeCode, qualifier)) {
                return true;
            }
        }
        return false;
    }

    protected boolean doesMemberMatch(RoleMemberBo roleMember, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        if (roleMember.getMemberId().equals(memberId) && roleMember.getMemberTypeCode().equals(memberTypeCode)) {
            // member ID/type match
            Map<String, String> roleQualifier = roleMember.getQualifier();
            if ((qualifier == null || qualifier.isEmpty())
                    && (roleQualifier == null || roleQualifier.isEmpty())) {
                return true; // blank qualifier match
            } else {
                if (qualifier != null && roleQualifier != null && qualifier.equals(roleQualifier)) {
                    return true; // qualifier match
                }
            }
        }
        return false;
    }

    /**
     * This method tests to see if assigning a roleBo to another roleBo will create a circular reference.
     * The Role is checked to see if it is a member (direct or nested) of the roleBo to be assigned as a member.
     *
     * @param newMemberId
     * @param roleBo
     * @return true  - assignment is allowed, no circular reference will be created.
     *         false - illegal assignment, it will create a circular membership
     */
    protected boolean checkForCircularRoleMembership(String newMemberId, RoleBo roleBo) {
        // get all nested roleBo members that are of type roleBo
        Set<String> newRoleMemberIds = getRoleTypeRoleMemberIds(newMemberId);
        if (newRoleMemberIds.contains(roleBo.getId())) {
            return false;
        }
        return true;
    }

    // TODO: pulling attribute IDs repeatedly is inefficient - consider caching the entire list as a map
    @SuppressWarnings("unchecked")
    protected String getKimAttributeId(String attributeName) {
        String result = null;
        Map<String, Object> critieria = new HashMap<String, Object>(1);
        critieria.put("attributeName", attributeName);
        Collection<KimAttributeBo> defs = getBusinessObjectService().findMatching(KimAttributeBo.class, critieria);
        if (CollectionUtils.isNotEmpty(defs)) {
            result = defs.iterator().next().getId();
        }
        return result;
    }

    /**
     * @return the applicationRoleTypeCache
     */
    protected Map<String, Boolean> getApplicationRoleTypeCache() {
        return this.applicationRoleTypeCache;
    }

    /**
     * @return the roleTypeServiceCache
     */
    protected Map<String, KimRoleTypeService> getRoleTypeServiceCache() {
        return this.roleTypeServiceCache;
    }

    /**
     * @return the delegationTypeServiceCache
     */
    protected Map<String, KimDelegationTypeService> getDelegationTypeServiceCache() {
        return this.delegationTypeServiceCache;
    }

    protected String getRoleCacheKey(String roleId) {
        return ROLE_IMPL_CACHE_PREFIX + roleId;
    }

    protected String getRoleByNameCacheKey(String namespaceCode, String roleName) {
        return ROLE_IMPL_BY_NAME_CACHE_PREFIX + namespaceCode + "-" + roleName;
    }

    protected void addRoleBoToCache(RoleBo roleBo) {
        if (roleBo != null) {
            getCacheAdministrator().putInCache(getRoleCacheKey(roleBo.getId()), roleBo, ROLE_IMPL_CACHE_GROUP);
            getCacheAdministrator().putInCache(getRoleByNameCacheKey(roleBo.getNamespaceCode(), roleBo.getName()), roleBo, ROLE_IMPL_CACHE_GROUP);
        }
    }

    protected RoleBo getRoleFromCache(String roleId) {
        return (RoleBo) getCacheAdministrator().getFromCache(getRoleCacheKey(roleId));
    }

    protected RoleBo getRoleFromCache(String namespaceCode, String roleName) {
        return (RoleBo) getCacheAdministrator().getFromCache(getRoleByNameCacheKey(namespaceCode, roleName));
    }

    protected String getNewDelegationId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_ID_S,
                DelegateBo.class);
        return nextSeq.toString();
    }

    protected String getNewAttributeDataId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S,
                RoleMemberAttributeDataBo.class);
        return nextSeq.toString();
    }

    protected String getNewDelegationMemberId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_MBR_ID_S,
                DelegateBo.class);
        return nextSeq.toString();
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    /**
     * @return the lookupService
     */
    protected LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return lookupService;
    }

    protected RiceCacheAdministrator getCacheAdministrator() {
        if (cacheAdministrator == null) {
            cacheAdministrator = KsbApiServiceLocator.getCacheAdministrator();
        }
        return cacheAdministrator;
    }

    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }

        return identityService;
    }
    
    protected GroupService getGroupService() {
        if (groupService == null) {
            groupService = KimApiServiceLocator.getGroupService();
        }

        return groupService;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        if (sequenceAccessorService == null) {
            sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
    }

    protected ResponsibilityInternalService getResponsibilityInternalService() {
        if (responsibilityInternalService == null) {
            responsibilityInternalService = KIMServiceLocatorInternal.getResponsibilityInternalService();
        }
        return responsibilityInternalService;
    }

    protected IdentityManagementNotificationService getIdentityManagementNotificationService() {
        return (IdentityManagementNotificationService) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
    }

    /**
     * An internal helper class for encapsulating the information related to generating a key for a RoleMemberBo list.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private class RoleMemberCacheKeyHelper {
        private final RoleDaoAction ROLE_DAO_ACTION;
        private final String ROLE_ID;
        private final String PRINCIPAL_ID;
        private final String GROUP_ID;
        private final String MEMBER_TYPE_CODE;
        private String cacheKey;

        private RoleMemberCacheKeyHelper(RoleDaoAction roleDaoAction, String roleId, String principalId, String groupId, String memberTypeCode) {
            this.ROLE_DAO_ACTION = roleDaoAction;
            this.ROLE_ID = roleId;
            this.PRINCIPAL_ID = principalId;
            this.GROUP_ID = groupId;
            this.MEMBER_TYPE_CODE = memberTypeCode;
        }

        private String getCacheKey() {
            if (this.cacheKey == null) {
                this.cacheKey = getRoleMemberListCacheKey(ROLE_DAO_ACTION, ROLE_ID, PRINCIPAL_ID, GROUP_ID, MEMBER_TYPE_CODE);
            }
            return this.cacheKey;
        }
    }

    /**
     * @return the roleDao
     */
    public RoleDao getRoleDao() {
        return this.roleDao;
    }

    /**
     * @param roleDao the roleDao to set
     */
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
