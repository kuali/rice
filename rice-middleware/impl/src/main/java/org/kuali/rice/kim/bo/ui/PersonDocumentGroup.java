/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_GRP_PRNCPL_MT")
public class PersonDocumentGroup extends KimDocumentBoActivatableToFromEditableBase {

    private static final long serialVersionUID = -1551337026170706411L;

    @PortableSequenceGenerator(name = "KRIM_GRP_MBR_ID_S")
    @GeneratedValue(generator = "KRIM_GRP_MBR_ID_S")
    @Id
    @Column(name = "GRP_MBR_ID")
    protected String groupMemberId;

    @Column(name = "GRP_TYPE")
    protected String groupType;

    @Column(name = "GRP_ID")
    protected String groupId;

    @Column(name = "GRP_NM")
    protected String groupName;

    @Column(name = "NMSPC_CD")
    protected String namespaceCode;

    @Column(name = "PRNCPL_ID")
    protected String principalId;

    @Transient
    protected transient KimTypeBo kimGroupType;

    @Transient
    protected String kimTypeId;

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public KimTypeBo getKimGroupType() {
        if (StringUtils.isNotBlank(getKimTypeId())) {
            if (kimGroupType == null || (!StringUtils.equals(kimGroupType.getId(), kimTypeId))) {
                kimGroupType = KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
            }
        }
        return kimGroupType;
    }

    public String getKimTypeId() {
        return this.kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public String getGroupMemberId() {
        return this.groupMemberId;
    }

    public void setGroupMemberId(String groupMemberId) {
        this.groupMemberId = groupMemberId;
    }

    public String getPrincipalId() {
        return this.principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getGroupType() {
        return this.groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getNamespaceCode() {
        return this.namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }
}
