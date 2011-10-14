package org.kuali.rice.kim.framework.role

import org.kuali.rice.kim.api.role.Role
import org.kuali.rice.kim.api.role.RoleContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject

class RoleEbo implements RoleContract, ExternalizableBusinessObject {
    private static final long serialVersionUID = 1L;

    String id;
    String name;
    String description;
    boolean active;
    String kimTypeId;
    String namespaceCode;
    Long versionNumber
    String objectId

    static Role to(RoleEbo ebo) {
        if (ebo == null) { return null}
        return Role.Builder.create(ebo).build();
    }

    static RoleEbo from(Role im) {
        if (im == null) {return null}
        RoleEbo ebo = new RoleEbo(
                id: im.id,
                name: im.name,
                description: im.description,
                active : im.active,
                kimTypeId : im.kimTypeId,
                namespaceCode : im.namespaceCode,
                versionNumber : im.versionNumber,
                objectId : im.objectId
        )
        return ebo;
    }

    void refresh() {}
}
