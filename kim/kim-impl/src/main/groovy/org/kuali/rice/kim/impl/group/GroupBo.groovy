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

package org.kuali.rice.kim.impl.group

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.group.Group
import org.kuali.rice.kim.api.group.GroupContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_GRP_T")
public class GroupBo extends PersistableBusinessObjectBase implements GroupContract {
    private static final long serialVersionUID = 1L;

    @Id
	@Column(name="GRP_ID")
	String id

    @Column(name="GRP_NM")
	String name

    @Column(name="GRP_DESC",length=4000)
	String description

	@Column(name="ACTV_IND")
	@Type(type="yes_no")
	boolean active

	@Column(name="KIM_TYP_ID")
	String kimTypeId

    @Column(name="NMSPC_CD")
	String namespaceCode

	@OneToMany(targetEntity=GroupMemberBo.class,cascade=[CascadeType.ALL],fetch=FetchType.EAGER,mappedBy="id")
	@Fetch(value = FetchMode.SELECT)
	List<GroupMemberBo> members

	@OneToMany(targetEntity=GroupAttributeBo.class,cascade=[CascadeType.ALL],fetch=FetchType.EAGER,mappedBy="id")
	@Fetch(value = FetchMode.SELECT)
	List<GroupAttributeBo> attributes

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Group to(GroupBo bo) {
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
    static GroupBo from(Group im) {
        if (im == null) {
            return null
        }

        GroupBo bo = new GroupBo()
        bo.id = im.id
        bo.namespaceCode = im.namespaceCode
        bo.name = im.name
        bo.description = im.description
        bo.active = im.active
        bo.kimTypeId = im.kimTypeId
        bo.members = new ArrayList<GroupMemberBo>()
        for (member in im.members) {
            bo.members.add (GroupMemberBo.from(member))
        }

        bo.attributes = new ArrayList<GroupAttributeBo>()
        for (attr in im.attributes) {
            bo.attributes.add (GroupAttributeBo.from(attr))
        }
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId;

        return bo
    }

}
