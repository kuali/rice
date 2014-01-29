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
package org.kuali.rice.kim.impl.responsibility;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name="ZZZ_FAKE_KRIM_RSP_T")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class UberResponsibilityBo extends PersistableBusinessObjectBase implements ResponsibilityContract {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String namespaceCode;

    private String name;

    private String description;

    private String templateId;

    private boolean active;

    private ResponsibilityTemplateBo template = new ResponsibilityTemplateBo();

    private List<ResponsibilityAttributeBo> attributeDetails = new AutoPopulatingList<ResponsibilityAttributeBo>(ResponsibilityAttributeBo.class);

    private List<RoleResponsibilityBo> roleResponsibilities = new AutoPopulatingList<RoleResponsibilityBo>(RoleResponsibilityBo.class);

    private Map<String, String> attributes;

    @Override
    public Map<String, String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Responsibility to(ResponsibilityBo bo) {
        if (bo == null) {
            return null;
        }
        return Responsibility.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ResponsibilityBo from(Responsibility im) {
        if (im == null) {
            return null;
        }
        ResponsibilityBo bo = new ResponsibilityBo();
        bo.id = im.getId();
        bo.namespaceCode = im.getNamespaceCode();
        bo.name = im.getName();
        bo.description = im.getDescription();
        bo.active = im.isActive();
        bo.templateId = im.getTemplate() != null ? im.getTemplate().getId() : null;
        bo.template = ResponsibilityTemplateBo.from(im.getTemplate());
        bo.attributes = im.getAttributes();
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    public ResponsibilityTemplateBo getTemplate() {
        return template;
    }

    public String getDetailObjectsValues() {
        StringBuffer detailObjectsToDisplayBuffer = new StringBuffer();
        Iterator<ResponsibilityAttributeBo> respIter = attributeDetails.iterator();
        while (respIter.hasNext()) {
            ResponsibilityAttributeBo respAttributeData = respIter.next();
            detailObjectsToDisplayBuffer.append(respAttributeData.getAttributeValue());
            if (respIter.hasNext()) {
                detailObjectsToDisplayBuffer.append(KimConstants.KimUIConstants.COMMA_SEPARATOR);
            }
        }
        return detailObjectsToDisplayBuffer.toString();
    }

    public String getDetailObjectsToDisplay() {
        final KimType kimType = getTypeInfoService().getKimType(getTemplate().getKimTypeId());
        StringBuffer detailObjects = new StringBuffer();
        Iterator<ResponsibilityAttributeBo> respIter = attributeDetails.iterator();
        while (respIter.hasNext()) {
            ResponsibilityAttributeBo bo = respIter.next();
            detailObjects.append(getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(bo.getKimAttributeId()))).append(":").append(bo.getAttributeValue());
            if (respIter.hasNext()) {
                detailObjects.append(KimConstants.KimUIConstants.COMMA_SEPARATOR);
            }
        }
        return detailObjects.toString();
    }

    private String getKimAttributeLabelFromDD(KimTypeAttribute attribute) {
        return getDataDictionaryService().getAttributeLabel(attribute.getKimAttribute().getComponentName(), attribute.getKimAttribute().getAttributeName());
    }

    private DataDictionaryService getDataDictionaryService() {
        return KRADServiceLocatorWeb.getDataDictionaryService();
    }

    private KimTypeInfoService getTypeInfoService() {
        return KimApiServiceLocator.getKimTypeInfoService();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

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

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTemplate(ResponsibilityTemplateBo template) {
        this.template = template;
    }

    public List<ResponsibilityAttributeBo> getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(List<ResponsibilityAttributeBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    public List<RoleResponsibilityBo> getRoleResponsibilities() {
        return roleResponsibilities;
    }

    public void setRoleResponsibilities(List<RoleResponsibilityBo> roleResponsibilities) {
        this.roleResponsibilities = roleResponsibilities;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    private List<RoleBo> assignedToRoles = new AutoPopulatingList<RoleBo>(RoleBo.class);
    private String assignedToRoleNamespaceForLookup;
    private String assignedToRoleNameForLookup;
    private RoleBo assignedToRole = new RoleBo();
    private String assignedToPrincipalNameForLookup;
    private Person assignedToPrincipal = new PersonImpl();
    private String assignedToGroupNamespaceForLookup;
    private String assignedToGroupNameForLookup;
    private GroupBo assignedToGroup = new GroupBo();
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
