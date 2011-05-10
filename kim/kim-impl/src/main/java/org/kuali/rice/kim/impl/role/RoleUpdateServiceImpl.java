package org.kuali.rice.kim.impl.role;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.SqlDateAdapter;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleUpdateService;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;


public class RoleUpdateServiceImpl implements RoleUpdateService {
    @Override
    public void assignPrincipalToRole(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void assignGroupToRole(@WebParam(name = "groupId") String groupId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void assignRoleToRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RoleMember saveRoleMemberForRole(@WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "memberId") String memberId, @WebParam(name = "memberTypeCode") String memberTypeCode, @WebParam(name = "roleId") String roleId, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeFromDate") Date activeFromDate, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeToDate") Date activeToDate) throws UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveRoleRspActions(@WebParam(name = "roleResponsibilityActionId") String roleResponsibilityActionId, @WebParam(name = "roleId") String roleId, @WebParam(name = "roleResponsibilityId") String roleResponsibilityId, @WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "actionTypeCode") String actionTypeCode, @WebParam(name = "actionPolicyCode") String actionPolicyCode, @WebParam(name = "priorityNumber") Integer priorityNumber, @WebParam(name = "forceAction") Boolean forceAction) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveDelegationMemberForRole(@WebParam(name = "delegationMemberId") String delegationMemberId, @WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "memberId") String memberId, @WebParam(name = "memberTypeCode") String memberTypeCode, @WebParam(name = "delegationTypeCode") String delegationTypeCode, @WebParam(name = "roleId") String roleId, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeFromDate") Date activeFromDate, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeToDate") Date activeToDate) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removePrincipalFromRole(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeGroupFromRole(@WebParam(name = "groupId") String groupId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeRoleFromRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "roleName") String roleName, @WebParam(name = "roleDescription") String roleDescription, @WebParam(name = "active") boolean active, @WebParam(name = "kimTypeId") String kimTypeId, @WebParam(name = "namespaceCode") String namespaceCode) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNextAvailableRoleId() throws UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
