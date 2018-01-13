/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.IdentityManagementTypeAttributeTransactionalDocument;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.util.AutoPopulatingList;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_GRP_DOCUMENT_T")
public class IdentityManagementGroupDocument extends IdentityManagementTypeAttributeTransactionalDocument {
    private static final Logger LOG = Logger.getLogger(IdentityManagementGroupDocument.class);
    private static final long serialVersionUID = 1L;

    // principal data                       
    @Column(name = "GRP_ID")
    protected String groupId;

    @Column(name = "KIM_TYP_ID")
    protected String groupTypeId;

    @Transient
    protected String groupTypeName;

    @Column(name = "GRP_NMSPC")
    protected String groupNamespace;

    @Column(name = "GRP_NM")
    protected String groupName;

    @Column(name = "GRP_DESC")
    protected String groupDescription;

    //@Type(type="yes_no")                       
    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean active = true;

    @Transient
    protected boolean editing;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = GroupDocumentMember.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    private List<GroupDocumentMember> members = new AutoPopulatingList<GroupDocumentMember>(GroupDocumentMember.class);

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = GroupDocumentQualifier.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false)
    private List<GroupDocumentQualifier> qualifiers = new AutoPopulatingList<GroupDocumentQualifier>(GroupDocumentQualifier.class);

    public IdentityManagementGroupDocument() {
    }

    /**
	 * @return the active
	 */
    public boolean isActive() {
        return this.active;
    }

    /**
	 * @param active the active to set
	 */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
	 * @param groupId the groupId to set
	 */
    public void setRoleId(String groupId) {
        this.groupId = groupId;
    }

    /**
	 * @param member the members to set
	 */
    public void addMember(GroupDocumentMember member) {
        getMembers().add(member);
    }

    /**
	 * @return the kimType
	 */
    public KimType getKimType() {
        if (getGroupTypeId() != null) {
            return KimApiServiceLocator.getKimTypeInfoService().getKimType(getGroupTypeId());
        }
        return null;
    }

    public GroupDocumentMember getBlankMember() {
        return new GroupDocumentMember();
    }

    /**
	 * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
	 */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            KIMServiceLocatorInternal.getUiDocumentService().saveGroup(this);
        }
    }

    @Override
    public void prepareForSave() {
        String groupId;
        if (StringUtils.isBlank(getGroupId())) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_GROUP_ID_S);
            groupId = incrementer.nextStringValue();
            setGroupId(groupId);
        } else {
            groupId = getGroupId();
        }
        if (getMembers() != null) {
            for (GroupDocumentMember member : getMembers()) {
                member.setGroupId(getGroupId());
                if (StringUtils.isBlank(member.getGroupMemberId())) {
                    DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), "KRIM_GRP_MBR_ID_S");
                    member.setGroupMemberId(incrementer.nextStringValue());
                }
                if (StringUtils.isBlank(member.getDocumentNumber())) {
                    member.setDocumentNumber(getDocumentNumber());
                }
            }
        }
        int index = 0;
        // this needs to be checked - are all qualifiers present?                       
        if (getDefinitions() != null) {
            for (KimAttributeField key : getDefinitions()) {
                if (getQualifiers().size() > index) {
                    GroupDocumentQualifier qualifier = getQualifiers().get(index);
                    qualifier.setKimAttrDefnId(getKimAttributeDefnId(key));
                    qualifier.setKimTypId(getKimType().getId());
                    qualifier.setGroupId(groupId);
                    qualifier.setDocumentNumber(getDocumentNumber());
                }
                index++;
            }
        }
    }

    public void initializeDocumentForNewGroup() {
        if (StringUtils.isBlank(this.groupId)) {
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(KimImplServiceLocator.getDataSource(), KimConstants.SequenceNames.KRIM_GROUP_ID_S);
            this.groupId = incrementer.nextStringValue();
        }
        if (StringUtils.isBlank(this.groupTypeId)) {
            this.groupTypeId = "1";
        }
    }

    public String getGroupId() {
        //		if(StringUtils.isBlank(this.groupId)){                       
        //			initializeDocumentForNewGroup();                       
        //		}                       
        return groupId;
    }

    /**
	 * @param groupId the groupId to set
	 */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
	 * @return the groupName
	 */
    public String getGroupName() {
        return this.groupName;
    }

    /**
	 * @param groupName the groupName to set
	 */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return this.groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    /**
	 * @return the groupNamespace
	 */
    public String getGroupNamespace() {
        return this.groupNamespace;
    }

    /**
	 * @param groupNamespace the groupNamespace to set
	 */
    public void setGroupNamespace(String groupNamespace) {
        this.groupNamespace = groupNamespace;
    }

    /**
	 * @return the groupTypeId
	 */
    public String getGroupTypeId() {
        return this.groupTypeId;
    }

    /**
	 * @param groupTypeId the groupTypeId to set
	 */
    public void setGroupTypeId(String groupTypeId) {
        this.groupTypeId = groupTypeId;
    }

    /**
	 * @return the groupTypeName
	 */
    public String getGroupTypeName() {
        return this.groupTypeName;
    }

    /**
	 * @param groupTypeName the groupTypeName to set
	 */
    public void setGroupTypeName(String groupTypeName) {
        this.groupTypeName = groupTypeName;
    }

    /**
	 * @return the members
	 */
    public List<GroupDocumentMember> getMembers() {
        return this.members;
    }

    /**
	 * @param members the members to set
	 */
    public void setMembers(List<GroupDocumentMember> members) {
        this.members = members;
    }

    /**
	 * @return the qualifiers
	 */
    public List<GroupDocumentQualifier> getQualifiers() {
        return this.qualifiers;
    }

    /**
	 * @param qualifiers the qualifiers to set
	 */
    public void setQualifiers(List<GroupDocumentQualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public GroupDocumentQualifier getQualifier(String kimAttributeDefnId) {
        for (GroupDocumentQualifier qualifier : qualifiers) {
            if (qualifier.getKimAttrDefnId().equals(kimAttributeDefnId))
                return qualifier;
        }
        return null;
    }

    public Map<String, String> getQualifiersAsAttributes() {
        Map<String, String> attributes = new HashMap<String, String>();
        for (GroupDocumentQualifier qualifier : qualifiers) {
            if (qualifier.getKimAttribute() != null) {
                attributes.put(qualifier.getKimAttribute().getAttributeName(), qualifier.getAttrVal());
            } else {
                LOG.warn("Unknown attribute ID on group: " + qualifier.getKimAttrDefnId() + " / value=" + qualifier.getAttrVal());
                attributes.put("Unknown Attribute ID: " + qualifier.getKimAttrDefnId(), qualifier.getAttrVal());
            }
        }
        return attributes;
    }

    public void setDefinitions(List<KimAttributeField> definitions) {
        super.setDefinitions(definitions);
        if (getQualifiers() == null || getQualifiers().size() < 1) {
            GroupDocumentQualifier qualifier;
            setQualifiers(new ArrayList<GroupDocumentQualifier>());
            if (getDefinitions() != null) {
                for (KimAttributeField key : getDefinitions()) {
                    qualifier = new GroupDocumentQualifier();
                    qualifier.setKimAttrDefnId(getKimAttributeDefnId(key));
                    getQualifiers().add(qualifier);
                }
            }
        }
    }

    public boolean isEditing() {
        return this.editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public void setKimType(KimType kimType) {
        super.setKimType(kimType);
        if (kimType != null) {
            setGroupTypeId(kimType.getId());
            setGroupTypeName(kimType.getName());
        }
    }
}
