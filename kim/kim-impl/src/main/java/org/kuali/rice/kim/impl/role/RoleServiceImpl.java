package org.kuali.rice.kim.impl.role;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.common.delegate.Delegate;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RoleServiceImpl implements RoleService {
    @Override
    public Role getRole(@WebParam(name = "roleId") String roleId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Role> getRoles(@WebParam(name = "roleIds") List<String> roleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Role getRoleByName(@WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRoleIdByName(@WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRoleActive(@WebParam(name = "roleId") String roleId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<AttributeSet> getRoleQualifiersForPrincipal(@WebParam(name = "principalId") String principalId, @WebParam(name = "roleIds") List<String> roleIds, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<AttributeSet> getRoleQualifiersForPrincipal(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<AttributeSet> getNestedRoleQualifiersForPrincipal(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<AttributeSet> getNestedRoleQualifiersForPrincipal(@WebParam(name = "principalId") String principalId, @WebParam(name = "roleIds") List<String> roleIds, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleMembership> getRoleMembers(@WebParam(name = "roleIds") List<String> roleIds, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection<String> getRoleMemberPrincipalIds(@WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean principalHasRole(@WebParam(name = "principalId") String principalId, @WebParam(name = "roleIds") List<String> roleIds, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getPrincipalIdSubListWithRole(@WebParam(name = "principalIds") List<String> principalIds, @WebParam(name = "roleNamespaceCode") String roleNamespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<? extends Role> getRolesSearchResults(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "fieldValues") Map<String, String> fieldValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void principalInactivated(@WebParam(name = "principalId") String principalId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void roleInactivated(@WebParam(name = "roleId") String roleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void groupInactivated(@WebParam(name = "groupId") String groupId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleMembership> getFirstLevelRoleMembers(@WebParam(name = "roleIds") List<String> roleIds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleMembership> findRoleMembers(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "fieldValues") Map<String, String> fieldValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getMemberParentRoleIds(String memberType, String memberId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleMember> findRoleMembersCompleteInfo(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "fieldValues") Map<String, String> fieldValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Delegate> findDelegateMembersCompleteInfo(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "fieldValues") Map<String, String> fieldValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Delegate> getDelegationMembersByDelegationId(@WebParam(name = "delegationId") String delegationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Delegate getDelegationMemberByDelegationAndMemberId(@WebParam(name = "delegationId") String delegationId, @WebParam(name = "memberId") String memberId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Delegate getDelegationMemberById(@WebParam(name = "delegationMemberId") String delegationMemberId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleResponsibility> getRoleResponsibilities(@WebParam(name = "roleId") String roleId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActionInfo(@WebParam(name = "roleMemberId") String roleMemberId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DelegateType getDelegateTypeInfo(@WebParam(name = "roleId") String roleId, @WebParam(name = "delegationTypeCode") String delegationTypeCode) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DelegateType getDelegateTypeInfoById(@WebParam(name = "delegationId") String delegationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void applicationRoleMembershipChanged(@WebParam(name = "roleId") String roleId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Role> lookupRoles(@WebParam(name = "searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> searchCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flushInternalRoleCache() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flushInternalRoleMemberCache() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flushInternalDelegationCache() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flushInternalDelegationMemberCache() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
