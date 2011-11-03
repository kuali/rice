/**
 * Copyright 2005-2011 The Kuali Foundation
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
