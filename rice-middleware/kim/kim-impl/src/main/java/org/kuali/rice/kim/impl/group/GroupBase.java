/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Customizer;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@MappedSuperclass
public abstract class GroupBase extends PersistableBusinessObjectBase implements GroupEbo {
    private static final long serialVersionUID = 1L;

    //@Column(name="GRP_ID")
    //private String id;

    @Column(name="GRP_NM")
    private String name;

    @Column(name="GRP_DESC",length=4000)
    private String description;

    @Column(name="ACTV_IND")
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    private boolean active;

    @Column(name="KIM_TYP_ID")
    private String kimTypeId;

    @Column(name="NMSPC_CD")
    private String namespaceCode;

    //@OneToMany(targetEntity=GroupMemberBo.class,cascade={CascadeType.ALL},fetch= FetchType.EAGER)
    //@JoinColumn(name = "id", referencedColumnName = "groupId")
    //private List<GroupMemberBo> members;

    //@OneToMany(targetEntity=GroupAttributeBo.class,cascade={CascadeType.ALL},fetch=FetchType.EAGER)
    //@JoinColumn(name = "id", referencedColumnName = "assignedToId")
    //private List<GroupAttributeBo> attributeDetails;

    @Transient
    private List<Person> memberPersons;

    @Transient
    private List<Group> memberGroups;

    @Transient
    protected Map<String,String> attributes;


    @Override
    public Map<String,String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

/*    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }*/

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    /*public List<GroupMemberBo> getMembers() {
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
    }*/


/*    *//**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     *//*
    public static Group to(GroupBase bo) {
        if (bo == null) {
            return null;
        }

        return Group.Builder.create(bo).build();
    }

    *//**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     *//*
    public static GroupBase from(Group im) {
        if (im == null) {
            return null;
        }

        GroupBase bo = new GroupBase();
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
            for ( GroupMemberBo groupMember : getMembers() ) {
                if (groupMember.isActive(new DateTime())) {
                    if ( KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(groupMember.getType())) {
                        Person tempPerson =  KimApiServiceLocator.getPersonService().getPerson(groupMember.getMemberId());
                        if (tempPerson != null && tempPerson.isActive()) {
                            memberPersons.add(tempPerson);
                        }
                    } else if (KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(groupMember.getType())) {
                        Group tempGroup =  KimApiServiceLocator.getGroupService().getGroup(groupMember.getMemberId());
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
            for ( GroupMemberBo groupMember : getMembers() ) {
                if (groupMember.isActive(new DateTime())) {
                    if ( KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(groupMember.getType())) {
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
            for ( GroupMemberBo groupMember : getMembers() ) {
                if (groupMember.isActive(new DateTime())) {
                    if ( KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(groupMember.getType())) {
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
    }*/

    public KimTypeBo getKimTypeInfo() {
        return KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(this.kimTypeId));
    }
}
