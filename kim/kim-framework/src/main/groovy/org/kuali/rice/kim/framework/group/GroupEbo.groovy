/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.framework.group

import groovy.transform.ToString
import org.kuali.rice.kim.api.group.Group
import org.kuali.rice.kim.api.group.GroupAttribute
import org.kuali.rice.kim.api.group.GroupContract
import org.kuali.rice.kim.api.group.GroupMember
import org.kuali.rice.kns.bo.ExternalizableBusinessObject

@ToString
class GroupEbo implements GroupContract, ExternalizableBusinessObject {
        private static final long serialVersionUID = 1L;

	String id
	String name
	String description
	boolean active
	String kimTypeId
	String namespaceCode
	List<GroupMember> members
	List<GroupAttribute> attributes
    Long versionNumber
	String objectId

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Group to(GroupEbo bo) {
        if (bo == null) {
            return null
        }

        return Group.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static GroupEbo from(Group im) {
        if (im == null) {
            return null
        }

        GroupEbo bo = new GroupEbo()
        bo.id = im.id
        bo.namespaceCode = im.namespaceCode
        bo.name = im.name
        bo.description = im.description
        bo.active = im.active
        bo.kimTypeId = im.kimTypeId

        //todo: members???

        bo.attributes = im.attributes
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId;

        return bo
    }

    void refresh() { }
}
