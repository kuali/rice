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
package org.kuali.rice.kim.impl.permission;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "KRIM_PERM_T")
public class PermissionBo extends PersistableBusinessObjectBase implements PermissionContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_PERM_ID_S")
    @GeneratedValue(generator = "KRIM_PERM_ID_S")
    @Id
    @Column(name = "PERM_ID")
    protected String id;

    @Column(name = "NMSPC_CD")
    protected String namespaceCode;

    @Column(name = "NM")
    protected String name;

    @Column(name = "DESC_TXT")
    protected String description;

    @Column(name = "PERM_TMPL_ID")
    protected String templateId;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean active;

    @ManyToOne(targetEntity = PermissionTemplateBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PERM_TMPL_ID", referencedColumnName = "PERM_TMPL_ID", insertable = false, updatable = false)
    protected PermissionTemplateBo template = new PermissionTemplateBo();

    @OneToMany(targetEntity = PermissionAttributeBo.class, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PERM_ID", referencedColumnName = "PERM_ID")
    protected List<PermissionAttributeBo> attributeDetails;

    @Transient
    protected Map<String, String> attributes;

    @OneToMany(mappedBy = "permission")
    @JoinColumn(name = "PERM_ID", referencedColumnName = "PERM_ID", insertable = false, updatable = false)
    protected List<RolePermissionBo> rolePermissions = new AutoPopulatingList<RolePermissionBo>(RolePermissionBo.class);

    @Override
    public Map<String, String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
    }

    //TODO: rename/fix later - only including this method and attributeDetails field for Role conversion
    public Map<String, String> getDetails() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
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

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<PermissionAttributeBo> getAttributeDetails() {
        return attributeDetails;
    }

    public void setAttributeDetails(List<PermissionAttributeBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    public List<RolePermissionBo> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermissionBo> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Permission to(PermissionBo bo) {
        if (bo == null) {
            return null;
        }
        return Permission.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static PermissionBo from(Permission im) {
        if (im == null) {
            return null;
        }
        PermissionBo bo = new PermissionBo();
        bo.setId(im.getId());
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setName(im.getName());
        bo.setDescription(im.getDescription());
        bo.setActive(im.isActive());
        bo.setTemplateId(im.getTemplate() != null ? im.getTemplate().getId() : null);
        bo.setTemplate(PermissionTemplateBo.from(im.getTemplate()));
        bo.setAttributes(im.getAttributes());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public PermissionTemplateBo getTemplate() {
        return template;
    }

    public void setTemplate(PermissionTemplateBo template) {
        this.template = template;
    }

    public String getDetailObjectsValues() {
        StringBuffer detailObjectsToDisplayBuffer = new StringBuffer();
        Iterator<PermissionAttributeBo> permIter = attributeDetails.iterator();
        while (permIter.hasNext()) {
            PermissionAttributeBo permissionAttributeData = permIter.next();
            detailObjectsToDisplayBuffer.append(permissionAttributeData.getAttributeValue());
            if (permIter.hasNext()) {
                detailObjectsToDisplayBuffer.append(KimConstants.KimUIConstants.COMMA_SEPARATOR);
            }
        }
        return detailObjectsToDisplayBuffer.toString();
    }

    public String getDetailObjectsToDisplay() {
        final KimType kimType = getTypeInfoService().getKimType(getTemplate().getKimTypeId());
        StringBuffer detailObjects = new StringBuffer();
        Iterator<PermissionAttributeBo> permIter = attributeDetails.iterator();
        while (permIter.hasNext()) {
            PermissionAttributeBo bo = permIter.next();
            detailObjects.append(getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(bo.getKimAttributeId()))).append(":").append(bo.getAttributeValue());
            if (permIter.hasNext()) {
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
}
