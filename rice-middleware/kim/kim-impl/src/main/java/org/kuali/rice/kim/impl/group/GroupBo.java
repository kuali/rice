/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.impl.group;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Cacheable(false)
@Entity
@Table(name = "KRIM_GRP_T")
public class GroupBo extends GroupBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_GRP_ID_S")
    @GeneratedValue(generator = "KRIM_GRP_ID_S")
    @Id
    @Column(name = "GRP_ID")
    private String id;

    @OneToMany(targetEntity = GroupMemberBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = false, updatable = false)
    private List<GroupMemberBo> members;

    @OneToMany(targetEntity = GroupAttributeBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "GRP_ID", referencedColumnName = "GRP_ID", insertable = false, updatable = false)
    private List<GroupAttributeBo> attributeDetails;

    @Transient
    private List<Person> memberPersons;

    @Transient
    private List<Group> memberGroups;

    @Override
    public Map<String, String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<GroupMemberBo> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMemberBo> members) {
        this.members = members;
    }

    public List<GroupAttributeBo> getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(List<GroupAttributeBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Group to(GroupBo bo) {
        if (bo == null) {
            return null;
        }
        return Group.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static GroupBo from(Group im) {
        if (im == null) {
            return null;
        }
        GroupBo bo = new GroupBo();
        bo.setId(im.getId());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setName(im.getName());
        bo.setDescription(im.getDescription());
        bo.setActive(im.isActive());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setAttributes(im.getAttributes());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    //helper function to get Attribute Value with specific id                      
    public String getGroupAttributeValueById(String attributeId) {
        for (GroupAttributeBo gad : getAttributeDetails()) {
            if (gad.getKimAttributeId().equals(attributeId.trim())) {
                return gad.getAttributeValue();
            }
        }
        return null;
    }

    private void splitMembersToTypes() {
        memberPersons = new ArrayList<Person>();
        memberGroups = new ArrayList<Group>();
        if (getMembers() != null) {
            for (GroupMemberBo groupMember : getMembers()) {
                if (groupMember.isActive(new DateTime())) {
                    if (KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(groupMember.getType())) {
                        Person tempPerson = KimApiServiceLocator.getPersonService().getPerson(groupMember.getMemberId());
                        if (tempPerson != null && tempPerson.isActive()) {
                            memberPersons.add(tempPerson);
                        }
                    } else if (KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(groupMember.getType())) {
                        Group tempGroup = KimApiServiceLocator.getGroupService().getGroup(groupMember.getMemberId());
                        if (tempGroup != null && tempGroup.isActive()) {
                            memberGroups.add(tempGroup);
                        }
                    }
                }
            }
        }
    }

    public List<Person> getMemberPersons() {
        if (this.memberPersons == null) {
            splitMembersToTypes();
        }
        return this.memberPersons;
    }

    public void setMemberPersons(List<Person> memberPersons) {
        this.memberPersons = memberPersons;
    }

    public List<String> getMemberPrincipalIds() {
        List<String> principalIds = new ArrayList<String>();
        if (getMembers() != null) {
            for (GroupMemberBo groupMember : getMembers()) {
                if (groupMember.isActive(new DateTime())) {
                    if (KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(groupMember.getType())) {
                        principalIds.add(groupMember.getMemberId());
                    }
                }
            }
        }
        return principalIds;
    }

    public List<String> getMemberGroupIds() {
        List<String> principalIds = new ArrayList<String>();
        if (getMembers() != null) {
            for (GroupMemberBo groupMember : getMembers()) {
                if (groupMember.isActive(new DateTime())) {
                    if (KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(groupMember.getType())) {
                        principalIds.add(groupMember.getMemberId());
                    }
                }
            }
        }
        return principalIds;
    }

    public List<Group> getMemberGroups() {
        if (this.memberGroups == null) {
            splitMembersToTypes();
        }
        return this.memberGroups;
    }

    public void setMemberGroups(List<Group> memberGroups) {
        this.memberGroups = memberGroups;
    }
}
