package org.kuali.rice.kim.impl.responsibility;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.springframework.util.AutoPopulatingList;

import java.util.List;

public class UberResponsibilityBo extends ResponsibilityBo {
    private static final long serialVersionUID = 1L;
    private List<RoleBo> assignedToRoles = new AutoPopulatingList<RoleBo>(RoleBo.class);
    private String assignedToRoleNamespaceForLookup;
    private String assignedToRoleNameForLookup;
    private RoleBo assignedToRole;
    private String assignedToPrincipalNameForLookup;
    private Person assignedToPrincipal;
    private String assignedToGroupNamespaceForLookup;
    private String assignedToGroupNameForLookup;
    private GroupBo assignedToGroup;
    private String attributeName;
    private String attributeValue;
    private String detailCriteria;

    public String getAssignedToRolesToDisplay() {
        StringBuffer assignedToRolesToDisplay = new StringBuffer();
        for (RoleBo roleImpl : assignedToRoles) {
            assignedToRolesToDisplay.append(getRoleDetailsToDisplay(roleImpl));
        }

        return StringUtils.chomp(assignedToRolesToDisplay.toString(), KimConstants.KimUIConstants.COMMA_SEPARATOR);
    }

    public String getRoleDetailsToDisplay(RoleBo roleImpl) {
        return roleImpl.getNamespaceCode().trim() + " " + roleImpl.getName().trim() + KimConstants.KimUIConstants.COMMA_SEPARATOR;
    }

    public List<RoleBo> getAssignedToRoles() {
        return assignedToRoles;
    }

    public void setAssignedToRoles(List<RoleBo> assignedToRoles) {
        this.assignedToRoles = assignedToRoles;
    }

    public String getAssignedToRoleNamespaceForLookup() {
        return assignedToRoleNamespaceForLookup;
    }

    public void setAssignedToRoleNamespaceForLookup(String assignedToRoleNamespaceForLookup) {
        this.assignedToRoleNamespaceForLookup = assignedToRoleNamespaceForLookup;
    }

    public String getAssignedToRoleNameForLookup() {
        return assignedToRoleNameForLookup;
    }

    public void setAssignedToRoleNameForLookup(String assignedToRoleNameForLookup) {
        this.assignedToRoleNameForLookup = assignedToRoleNameForLookup;
    }

    public RoleBo getAssignedToRole() {
        return assignedToRole;
    }

    public void setAssignedToRole(RoleBo assignedToRole) {
        this.assignedToRole = assignedToRole;
    }

    public String getAssignedToPrincipalNameForLookup() {
        return assignedToPrincipalNameForLookup;
    }

    public void setAssignedToPrincipalNameForLookup(String assignedToPrincipalNameForLookup) {
        this.assignedToPrincipalNameForLookup = assignedToPrincipalNameForLookup;
    }

    public Person getAssignedToPrincipal() {
        return assignedToPrincipal;
    }

    public void setAssignedToPrincipal(Person assignedToPrincipal) {
        this.assignedToPrincipal = assignedToPrincipal;
    }

    public String getAssignedToGroupNamespaceForLookup() {
        return assignedToGroupNamespaceForLookup;
    }

    public void setAssignedToGroupNamespaceForLookup(String assignedToGroupNamespaceForLookup) {
        this.assignedToGroupNamespaceForLookup = assignedToGroupNamespaceForLookup;
    }

    public String getAssignedToGroupNameForLookup() {
        return assignedToGroupNameForLookup;
    }

    public void setAssignedToGroupNameForLookup(String assignedToGroupNameForLookup) {
        this.assignedToGroupNameForLookup = assignedToGroupNameForLookup;
    }

    public GroupBo getAssignedToGroup() {
        return assignedToGroup;
    }

    public void setAssignedToGroup(GroupBo assignedToGroup) {
        this.assignedToGroup = assignedToGroup;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getDetailCriteria() {
        return detailCriteria;
    }

    public void setDetailCriteria(String detailCriteria) {
        this.detailCriteria = detailCriteria;
    }


}
