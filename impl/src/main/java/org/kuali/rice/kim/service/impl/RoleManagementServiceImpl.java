/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.core.util.MaxSizeMap;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.role.RoleUpdateService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.api.type.KimTypeService;
import org.kuali.rice.kim.framework.type.KimRoleTypeService;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.RoleManagementService;
import org.springframework.beans.factory.InitializingBean;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleManagementServiceImpl implements RoleManagementService, InitializingBean {
    private static final Logger LOG = Logger.getLogger(RoleManagementServiceImpl.class);

    private RoleService roleService;
    private KimTypeInfoService typeInfoService;
    private RoleUpdateService roleUpdateService;

    // Max age defined in seconds
    protected int roleCacheMaxSize = 200;
    protected int roleCacheMaxAgeSeconds = 30;

    protected Map<String, MaxAgeSoftReference<Role>> roleByIdCache;
    protected Map<String, MaxAgeSoftReference<Role>> roleByNameCache;
    protected Map<String, MaxAgeSoftReference<List<RoleMembership>>> roleMembersWithDelegationCache;
    protected Map<String, MaxAgeSoftReference<List<Attributes>>> roleQualifiersForPrincipalCache;
    protected Map<String, MaxAgeSoftReference<Boolean>> principalHasRoleCache;
    protected Map<String, MaxAgeSoftReference<Collection<String>>> memberPrincipalIdsCache;

    protected Map<String, Boolean> shouldCacheRoleCache;

    public void afterPropertiesSet() throws Exception {
        roleByIdCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<Role>>(roleCacheMaxSize));
        roleByNameCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<Role>>(roleCacheMaxSize));
        roleMembersWithDelegationCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<List<RoleMembership>>>(roleCacheMaxSize));
        roleQualifiersForPrincipalCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<List<Attributes>>>(roleCacheMaxSize));
        principalHasRoleCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<Boolean>>(roleCacheMaxSize));
        memberPrincipalIdsCache = Collections.synchronizedMap(new MaxSizeMap<String, MaxAgeSoftReference<Collection<String>>>(roleCacheMaxSize));
        shouldCacheRoleCache = Collections.synchronizedMap(new HashMap<String, Boolean>());
    }

    public void flushRoleCaches() {
        flushInternalRoleCache();
        flushInternalRoleMemberCache();
        flushInternalDelegationCache();
        flushInternalDelegationMemberCache();
        roleByIdCache.clear();
        roleByNameCache.clear();
        roleMembersWithDelegationCache.clear();
        roleQualifiersForPrincipalCache.clear();
        principalHasRoleCache.clear();
        memberPrincipalIdsCache.clear();
        shouldCacheRoleCache.clear();
    }

    public void flushRoleMemberCaches() {
        flushInternalRoleMemberCache();
        roleMembersWithDelegationCache.clear();
        memberPrincipalIdsCache.clear();
    }

    public void flushDelegationCaches() {
        flushInternalDelegationCache();
        flushInternalDelegationMemberCache();
        roleMembersWithDelegationCache.clear();
    }

    public void flushDelegationMemberCaches() {
        flushInternalDelegationMemberCache();
        roleMembersWithDelegationCache.clear();
    }

    // Caching helper methods

    protected Role getRoleByIdCache(String roleId) {
        MaxAgeSoftReference<Role> roleRef = roleByIdCache.get(roleId);
        if (roleRef != null) {
            return roleRef.get();
        }
        return null;
    }

    protected Role getRoleByNameCache(String key) {
        MaxAgeSoftReference<Role> roleRef = roleByNameCache.get(key);
        if (roleRef != null) {
            return roleRef.get();
        }
        return null;
    }

    protected List<RoleMembership> getRoleMembersWithDelegationCache(String key) {
        MaxAgeSoftReference<List<RoleMembership>> roleMembersRef = roleMembersWithDelegationCache.get(key);
        if (roleMembersRef != null) {
            return roleMembersRef.get();
        }
        return null;
    }

    protected List<Attributes> getRoleQualifiersForPrincipalCache(String key) {
        MaxAgeSoftReference<List<Attributes>> qualifiersRef = roleQualifiersForPrincipalCache.get(key);
        if (qualifiersRef != null) {
            return qualifiersRef.get();
        }
        return null;
    }

    protected Boolean getPrincipalHasRoleCacheCache(String key) {
        MaxAgeSoftReference<Boolean> hasRoleRef = principalHasRoleCache.get(key);
        if (hasRoleRef != null) {
            return hasRoleRef.get();
        }
        return null;
    }

    protected void addRoleToCaches(Role role) {
        if (role != null) {
            roleByNameCache.put(role.getNamespaceCode() + "-" + role.getName(), new MaxAgeSoftReference<Role>(roleCacheMaxAgeSeconds, role));
            roleByIdCache.put(role.getId(), new MaxAgeSoftReference<Role>(roleCacheMaxAgeSeconds, role));
        }
    }

    protected void addRoleMembersWithDelegationToCache(String key, List<RoleMembership> members) {
        if (members != null) {
            roleMembersWithDelegationCache.put(key, new MaxAgeSoftReference<List<RoleMembership>>(roleCacheMaxAgeSeconds, members));
        }
    }

    protected void addRoleQualifiersForPrincipalToCache(String key, List<Attributes> qualifiers) {
        if (qualifiers != null) {
            roleQualifiersForPrincipalCache.put(key, new MaxAgeSoftReference<List<Attributes>>(roleCacheMaxAgeSeconds, qualifiers));
        }
    }

    protected void addPrincipalHasRoleToCache(String key, boolean hasRole) {
        principalHasRoleCache.put(key, new MaxAgeSoftReference<Boolean>(roleCacheMaxAgeSeconds, hasRole));
    }

    // Cached methods

    protected Collection<String> getRoleMemberPrincipalIdsCache(String key) {
        MaxAgeSoftReference<Collection<String>> memberPrincipalIdsRef = memberPrincipalIdsCache.get(key);
        if (memberPrincipalIdsRef != null) {
            return memberPrincipalIdsRef.get();
        }
        return null;
    }

    protected void addRoleMemberPrincipalIdsToCache(String key, Collection<String> principalIds) {
        memberPrincipalIdsCache.put(key, new MaxAgeSoftReference<Collection<String>>(roleCacheMaxAgeSeconds, principalIds));
    }

    @Override
    public Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, Attributes qualification) {
        StringBuffer cacheKey = new StringBuffer();
        cacheKey.append(namespaceCode).append('/').append(roleName);
        addAttributesToKey(cacheKey, qualification);
        String key = cacheKey.toString();
        Collection<String> principalIds = getRoleMemberPrincipalIdsCache(key);
        if (principalIds != null) {
            return principalIds;
        }
        principalIds = getRoleService().getRoleMemberPrincipalIds(namespaceCode, roleName, qualification);
        addRoleMemberPrincipalIdsToCache(key, principalIds);
        return principalIds;
    }

    @Override
    public Role getRole(String roleId) {
        Role role = getRoleByIdCache(roleId);
        if (role != null) {
            return role;
        }
        role = getRoleService().getRole(roleId);
        addRoleToCaches(role);
        return role;
    }

    @Override
    public Role getRoleByName(String namespaceCode, String roleName) {
        Role role = getRoleByNameCache(namespaceCode + "-" + roleName);
        if (role != null) {
            return role;
        }
        role = getRoleService().getRoleByName(namespaceCode, roleName);
        addRoleToCaches(role);
        return role;
    }

    @Override
    public String getRoleIdByName(String namespaceCode, String roleName) {
        Role role = getRoleByName(namespaceCode, roleName);
        if (role == null) {
            return null;
        }
        return role.getId();
    }

    @Override
    public List<Role> getRoles(List<String> roleIds) {
        return getRoleService().getRoles(roleIds);
    }

    protected void addIdsToKey(StringBuffer key, List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            key.append("[null]");
        } else {
            for (String id : idList) {
                key.append('|').append(id).append('|');
            }
        }
    }

    protected void addAttributesToKey(StringBuffer key, Attributes attributes) {
        if (attributes == null || attributes.isEmpty()) {
            key.append("[null]");
        } else {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                key.append(entry.getKey()).append('=').append(entry.getValue()).append('|');
            }
        }
    }

    @Override
    public List<RoleMembership> getRoleMembers(List<String> roleIds, Attributes qualification) {
        List<String>[] filteredRoles = filterRoleIdsByCachingAbility(roleIds);
        List<String> cacheRoles = filteredRoles[0];
        List<String> noCacheRoles = filteredRoles[1];

        List<RoleMembership> members = null;
        String key = null;

        if (!cacheRoles.isEmpty()) {
            StringBuffer cacheKey = new StringBuffer();
            addIdsToKey(cacheKey, cacheRoles);
            cacheKey.append('/');
            addAttributesToKey(cacheKey, qualification);
            key = cacheKey.toString();
            members = getRoleMembersWithDelegationCache(key);
        }
        if (members != null) {
            if (!noCacheRoles.isEmpty()) {
                members.addAll(getRoleService().getRoleMembers(noCacheRoles, qualification));
            }
            return members;
        }

        if (!cacheRoles.isEmpty()) {
            members = getRoleService().getRoleMembers(cacheRoles, qualification);
            addRoleMembersWithDelegationToCache(key, members);
        } else {
            members = new ArrayList<RoleMembership>();
        }
        if (!noCacheRoles.isEmpty()) {
            members.addAll(getRoleService().getRoleMembers(noCacheRoles, qualification));
        }
        return members;
    }

    @Override
    public List<Attributes> getRoleQualifiersForPrincipal(String principalId, List<String> roleIds, Attributes qualification) {
        StringBuffer cacheKey = new StringBuffer(principalId);
        cacheKey.append('/');
        addIdsToKey(cacheKey, roleIds);
        cacheKey.append('/');
        addAttributesToKey(cacheKey, qualification);
        String key = cacheKey.toString();
        List<Attributes> qualifiers = getRoleQualifiersForPrincipalCache(key);
        if (qualifiers != null) {
            return qualifiers;
        }
        qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
        addRoleQualifiersForPrincipalToCache(key, qualifiers);
        return qualifiers;
    }

    @Override
    public List<Attributes> getRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, Attributes qualification) {
        StringBuffer cacheKey = new StringBuffer(principalId);
        cacheKey.append('/');
        cacheKey.append(namespaceCode).append('-').append(roleName);
        cacheKey.append('/');
        addAttributesToKey(cacheKey, qualification);
        String key = cacheKey.toString();
        List<Attributes> qualifiers = getRoleQualifiersForPrincipalCache(key);
        if (qualifiers != null) {
            return qualifiers;
        }
        qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
        addRoleQualifiersForPrincipalToCache(key, qualifiers);
        return qualifiers;
    }

    public boolean isRoleActive(String roleId) {
        Role role = getRole(roleId);
        return role != null && role.isActive();
    }

    public boolean principalHasRole(String principalId, List<String> roleIds, Attributes qualification) {
        if (LOG.isDebugEnabled()) {
            logPrincipalHasRoleCheck(principalId, roleIds, qualification);
        }
        List<String>[] filteredRoles = filterRoleIdsByCachingAbility(roleIds);
        List<String> cacheRoles = filteredRoles[0];
        List<String> noCacheRoles = filteredRoles[1];

        Boolean hasRole = null;
        String key = null;
        if (!cacheRoles.isEmpty()) {
            StringBuffer cacheKey = new StringBuffer();
            cacheKey.append(principalId);
            cacheKey.append('/');
            addIdsToKey(cacheKey, cacheRoles);
            cacheKey.append('/');
            addAttributesToKey(cacheKey, qualification);
            key = cacheKey.toString();
            hasRole = getPrincipalHasRoleCacheCache(key);
        }
        if (hasRole == null || !hasRole.booleanValue()) {
            if (!cacheRoles.isEmpty()) {
                hasRole = getRoleService().principalHasRole(principalId, cacheRoles, qualification);
                addPrincipalHasRoleToCache(key, hasRole);
            }
            if ((hasRole == null || !hasRole.booleanValue()) && !noCacheRoles.isEmpty()) {
                hasRole = getRoleService().principalHasRole(principalId, noCacheRoles, qualification);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Result: " + hasRole);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Result Found in cache using key: " + key + "\nResult: " + hasRole);
            }
        }
        return hasRole;
    }

    /**
     * Determines if the role with the given id will be cached
     *
     * @param roleId the role id of the role to determine caching on
     * @return true if the role should be cached, false otherwise
     */
    protected boolean shouldCacheRole(String roleId) {
        Boolean shouldCacheRoleAnswer = shouldCacheRoleCache.get(roleId);
        if (shouldCacheRoleAnswer == null) {

            final KimRoleTypeService roleType = getRoleTypeService(roleId);
            final Role role = this.getRole(roleId);
            if (roleType != null && role != null) {
                try {
                    shouldCacheRoleAnswer = new Boolean(roleType.shouldCacheRoleMembershipResults(role.getNamespaceCode(), role.getName()));
                    shouldCacheRoleCache.put(roleId, shouldCacheRoleAnswer);
                } catch (Exception e) {//was: RiceRemoteServiceConnectionException
                    LOG.warn("Unable to connect to remote service for roleType " + role.getNamespaceCode() + "-" + role.getName());
                    LOG.warn(e.getMessage());
                    return Boolean.FALSE;
                }
            } else {
                shouldCacheRoleAnswer = Boolean.TRUE; // no type?  that means we get to do the default - cache
                shouldCacheRoleCache.put(roleId, shouldCacheRoleAnswer);
            }
        }
        return shouldCacheRoleAnswer.booleanValue();
    }

    /**
     * Splits the given List of role ids into two Lists, one with roles to cache, and one with roles
     * not to cache
     *
     * @param roleIds the List of role ids to split
     * @return an array of two Lists of role ids - the first, roles which can be cached and the second, roles which should not be cached
     */
    @SuppressWarnings("unchecked")
    protected List<String>[] filterRoleIdsByCachingAbility(List<String> roleIds) {
        List<String> cacheRoles = new ArrayList<String>();
        List<String> noCacheRoles = new ArrayList<String>();
        Set<String> alreadyFilteredRoles = new HashSet<String>();

        for (String roleId : roleIds) {
            if (!alreadyFilteredRoles.contains(roleId)) {
                alreadyFilteredRoles.add(roleId);
                if (shouldCacheRole(roleId)) {
                    cacheRoles.add(roleId);
                } else {
                    noCacheRoles.add(roleId);
                }
            }
        }
        return (List<String>[]) new List[]{cacheRoles, noCacheRoles};
    }

    /**
     * Retrieves the role type service associated with the given role ID
     *
     * @param roleId the role ID to get the role type service for
     * @return the Role Type Service
     */
    protected KimRoleTypeService getRoleTypeService(String roleId) {
        KimRoleTypeService service = null;

        final Role role = getRoleService().getRole(roleId);
        if (role != null) {
            final KimType roleType = getTypeInfoService().getKimType(role.getKimTypeId());
            if (roleType != null) {
                service = getRoleTypeService(roleType);
            }
        }
        return service;
    }

    /**
     * Retrieves a role type from the given type info
     *
     * @param typeInfo
     * @return
     */
    protected KimRoleTypeService getRoleTypeService(KimType typeInfo) {
        String serviceName = typeInfo.getServiceName();
        if (serviceName != null) {
            try {
                KimTypeService service = (KimTypeService) KIMServiceLocatorInternal.getService(serviceName);
                if (service != null && service instanceof KimRoleTypeService) {
                    return (KimRoleTypeService) service;
                } else {
                    return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
                }
            } catch (Exception ex) {
                LOG.error("Unable to find role type service with name: " + serviceName, ex);
                return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
            }
        }
        return null;
    }

    @Override
    public List<String> getPrincipalIdSubListWithRole(
            List<String> principalIds, String roleNamespaceCode,
            String roleName, Attributes qualification) {
        return getRoleService().getPrincipalIdSubListWithRole(principalIds,
                roleNamespaceCode, roleName, qualification);
    }


    // Helper methods

    public void removeCacheEntries(String roleId, String principalId) {
        if (principalId != null) {
            String keyPrefix = principalId + "-";
            synchronized (principalHasRoleCache) {
                Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.startsWith(keyPrefix)) {
                        cacheIterator.remove();
                    }
                }
            }
            synchronized (roleQualifiersForPrincipalCache) {
                Iterator<String> cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.startsWith(keyPrefix)) {
                        cacheIterator.remove();
                    }
                }
            }
        }
        if (roleId != null) {
            roleByIdCache.remove(roleId);
            roleByNameCache.clear();
            String keySubstring = "|" + roleId + "|";
            synchronized (principalHasRoleCache) {
                Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.contains(keySubstring)) {
                        cacheIterator.remove();
                    }
                }
            }
            synchronized (roleQualifiersForPrincipalCache) {
                Iterator<String> cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.contains(keySubstring)) {
                        cacheIterator.remove();
                    }
                }
            }
            synchronized (roleMembersWithDelegationCache) {
                Iterator<String> cacheIterator = roleMembersWithDelegationCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.contains(keySubstring)) {
                        cacheIterator.remove();
                    }
                }
            }
            synchronized (memberPrincipalIdsCache) {
                Iterator<String> cacheIterator = memberPrincipalIdsCache.keySet().iterator();
                while (cacheIterator.hasNext()) {
                    String cacheKey = cacheIterator.next();
                    if (cacheKey.contains(keySubstring)) {
                        cacheIterator.remove();
                    }
                }
            }
            shouldCacheRoleCache.remove(roleId);
        }
    }

    @Override
    public List<Attributes> getNestedRoleQualifiersForPrincipal(String principalId, List<String> roleIds, Attributes qualification) {
        return getRoleService().getNestedRoleQualifiersForPrincipal(principalId, roleIds, qualification);
    }

    @Override
    public List<Attributes> getNestedRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, Attributes qualification) {
        return getRoleService().getNestedRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
    }

    @Override
    public void assignGroupToRole(String groupId, String namespaceCode, String roleName,
                                  AttributeSet qualifications) {
        getRoleUpdateService().assignGroupToRole(groupId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName,
                                      AttributeSet qualifications) {
        Role role = getRoleByName(namespaceCode, roleName);
        getRoleUpdateService().assignPrincipalToRole(principalId, namespaceCode, roleName, qualifications);
        removeCacheEntries(role.getId(), principalId);
    }

    @Override
    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName,
                                    AttributeSet qualifications) {
        getRoleUpdateService().removeGroupFromRole(groupId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName,
                                        AttributeSet qualifications) {
        Role role = getRoleByName(namespaceCode, roleName);
        getRoleUpdateService().removePrincipalFromRole(principalId, namespaceCode, roleName, qualifications);
        removeCacheEntries(role.getId(), principalId);
    }

    @Override
    public List<Role> getRolesSearchResults(
            Map<String, String> fieldValues) {
        return getRoleService().getRolesSearchResults(fieldValues);
    }

    protected void logPrincipalHasRoleCheck(String principalId, List<String> roleIds, Attributes roleQualifiers) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("Has Role     : ").append(roleIds).append('\n');
        if (roleIds != null) {
            for (String roleId : roleIds) {
                Role role = getRole(roleId);
                if (role != null) {
                    sb.append("        Name : ").append(role.getNamespaceCode()).append('/').append(role.getName());
                    sb.append(" (").append(roleId).append(')');
                    sb.append('\n');
                }
            }
        }
        sb.append("   Principal : ").append(principalId);
        if (principalId != null) {
            Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipal(principalId);
            if (principal != null) {
                sb.append(" (").append(principal.getPrincipalName()).append(')');
            }
        }
        sb.append('\n');
        sb.append("     Details :\n");
        if (roleQualifiers != null) {
            sb.append(new AttributeSet(roleQualifiers.toMap()).formattedDump(15));
        } else {
            sb.append("               [null]\n");
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(sb.append(ExceptionUtils.getStackTrace(new Throwable())));
        } else {
            LOG.debug(sb.toString());
        }
    }

    @Override
    public void principalInactivated(String principalId) {
        getRoleService().principalInactivated(principalId);
        removeCacheEntries(null, principalId);
    }

    @Override
    public void roleInactivated(String roleId) {
        getRoleService().roleInactivated(roleId);
        removeCacheEntries(roleId, null);
    }

    @Override
    public void groupInactivated(String groupId) {
        getRoleService().groupInactivated(groupId);
    }

    @Override
    public List<RoleMembership> getFirstLevelRoleMembers(List<String> roleIds) {
        return getRoleService().getFirstLevelRoleMembers(roleIds);
    }

    @Override
    public List<RoleMember> findRoleMembers(Map<String, String> fieldValues) {
        return getRoleService().findRoleMembers(fieldValues);
    }

    @Override
    public List<RoleMembership> findRoleMemberships(Map<String, String> fieldValues) {
        return getRoleService().findRoleMemberships(fieldValues);
    }

    @Override
    public void assignRoleToRole(String roleId, String namespaceCode, String roleName,
                                 AttributeSet qualifications) {
        getRoleUpdateService().assignRoleToRole(
                roleId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void saveDelegationMemberForRole(String delegationMemberId, String roleMemberId, String memberId, String memberTypeCode,
                                            String delegationTypeCode, String roleId, AttributeSet qualifications,
                                            Date activeFromDate, Date activeToDate) throws UnsupportedOperationException {
        getRoleUpdateService().saveDelegationMemberForRole(delegationMemberId, roleMemberId, memberId, memberTypeCode, delegationTypeCode, roleId, qualifications, activeFromDate, activeToDate);
        Role role = getRole(roleId);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public RoleMember saveRoleMemberForRole(String roleMemberId, String memberId, String memberTypeCode,
                                            String roleId, AttributeSet qualifications, Date activeFromDate, Date activeToDate) throws UnsupportedOperationException {
        Role role = getRole(roleId);
        RoleMember roleMember = getRoleUpdateService().saveRoleMemberForRole(roleMemberId, memberId, memberTypeCode, roleId, qualifications, activeFromDate, activeToDate);
        removeCacheEntries(role.getId(), memberId);
        return roleMember;
    }

    @Override
    public void removeRoleFromRole(String roleId, String namespaceCode, String roleName,
                                   AttributeSet qualifications) {
        getRoleUpdateService().removeRoleFromRole(roleId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public List<DelegateMember> findDelegateMembers(Map<String, String> fieldValues) {
        return getRoleService().findDelegateMembers(fieldValues);
    }

    @Override
    public List<DelegateMember> getDelegationMembersByDelegationId(String delegationId) {
        return getRoleService().getDelegationMembersByDelegationId(delegationId);
    }

    @Override
    public DelegateMember getDelegationMemberByDelegationAndMemberId(String delegationId, String memberId) {
        return getRoleService().getDelegationMemberByDelegationAndMemberId(delegationId, memberId);
    }

    @Override
    public DelegateMember getDelegationMemberById(String delegationMemberId) {
        return getRoleService().getDelegationMemberById(delegationMemberId);
    }

    @Override
    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(String roleMemberId) {
        return getRoleService().getRoleMemberResponsibilityActions(roleMemberId);
    }

    public DelegateType getDelegateTypeInfo(String roleId, String delegationTypeCode) {
        return getRoleService().getDelegateTypeInfo(roleId, delegationTypeCode);
    }

    public DelegateType getDelegateTypeInfoById(String delegationId) {
        return getRoleService().getDelegateTypeInfoById(delegationId);
    }

    public void saveRoleRspActions(String roleResponsibilityActionId, String roleId, String roleResponsibilityId, String roleMemberId,
                                   String actionTypeCode, String actionPolicyCode, Integer priorityNumber, Boolean forceAction) {
        getRoleUpdateService().saveRoleRspActions(roleResponsibilityActionId, roleId, roleResponsibilityId, roleMemberId, actionTypeCode, actionPolicyCode, priorityNumber, forceAction);
        removeCacheEntries(roleId, null);
    }

    public List<RoleResponsibility> getRoleResponsibilities(String roleId) {
        return getRoleService().getRoleResponsibilities(roleId);
    }

    public void applicationRoleMembershipChanged(String roleId) {
        removeCacheEntries(roleId, null);
        getRoleService().applicationRoleMembershipChanged(roleId);
    }

    // Spring and injection methods

    public RoleService getRoleService() {
        if (roleService == null) {
            roleService = KimApiServiceLocator.getRoleService();
        }
        return roleService;
    }

    public KimTypeInfoService getTypeInfoService() {
        if (typeInfoService == null) {
            typeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        }
        return typeInfoService;
    }

    public RoleUpdateService getRoleUpdateService() {
        try {
            if (roleUpdateService == null) {
                roleUpdateService = KimApiServiceLocator.getRoleUpdateService();
                if (roleUpdateService == null) {
                    throw new UnsupportedOperationException("null returned for RoleUpdateService, unable to update role data");
                }
            }
        } catch (Exception ex) {
            throw new UnsupportedOperationException("unable to obtain a RoleUpdateService, unable to update role data", ex);
        }
        return roleUpdateService;
    }

    public void setRoleCacheMaxSize(int roleCacheMaxSize) {
        this.roleCacheMaxSize = roleCacheMaxSize;
    }

    public void setRoleCacheMaxAgeSeconds(int roleCacheMaxAge) {
        this.roleCacheMaxAgeSeconds = roleCacheMaxAge;
    }

    /**
     * This overridden method looks up roles based on criteria.  If you want
     * to return all roles pass in an empty map.
     */
    @Override
    public List<Role> lookupRoles(Map<String, String> searchCriteria) {
        return getRoleService().lookupRoles(searchCriteria);
    }

    @Override
    public void flushInternalRoleCache() {
        getRoleService().flushInternalRoleCache();
    }

    @Override
    public void flushInternalRoleMemberCache() {
        getRoleService().flushInternalRoleMemberCache();
    }

    @Override
    public void flushInternalDelegationCache() {
        getRoleService().flushInternalDelegationCache();
    }

    @Override
    public void flushInternalDelegationMemberCache() {
        getRoleService().flushInternalDelegationMemberCache();
    }

    @Override
    public void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException {
        getRoleUpdateService().assignPermissionToRole(permissionId, roleId);
    }

    @Override
    public String getNextAvailableRoleId() throws UnsupportedOperationException {
        return getRoleUpdateService().getNextAvailableRoleId();
    }

    @Override
    public void saveRole(String roleId, String roleName, String roleDescription, boolean active, String kimTypeId, String namespaceCode) throws UnsupportedOperationException {
        getRoleUpdateService().saveRole(roleId, roleName, roleDescription, active, kimTypeId, namespaceCode);
    }

    @Override
    public List<String> getMemberParentRoleIds(String memberType,
                                               String memberId) {
        return getRoleService().getMemberParentRoleIds(memberType, memberId);
    }
}
