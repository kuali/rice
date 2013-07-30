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
package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "KRIM_RSP_T")
public class ResponsibilityBo extends PersistableBusinessObjectBase implements ResponsibilityContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "RSP_ID")
    private String id;
    @Column(name = "NMSPC_CD")
    private String namespaceCode;
    @Column(name = "NM")
    private String name;
    @Column(name = "DESC_TXT", length = 400)
    private String description;
    @Column(name = "PERM_TMPL_ID")
    private String templateId;
    @Column(name = "ACTV_IND")
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    private boolean active;
    @OneToOne(targetEntity = ResponsibilityTemplateBo.class, cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "PERM_TMPL_ID", insertable = false, updatable = false)
    private ResponsibilityTemplateBo template = new ResponsibilityTemplateBo();
    @OneToMany(targetEntity = ResponsibilityAttributeBo.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER,
            mappedBy = "id")
    private List<ResponsibilityAttributeBo> attributeDetails = new AutoPopulatingList(ResponsibilityAttributeBo.class);
    @OneToMany(targetEntity = RoleResponsibilityBo.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER,
            mappedBy = "id")
    private List<RoleResponsibilityBo> roleResponsibilities = new AutoPopulatingList(RoleResponsibilityBo.class);
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
        final KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );

        StringBuffer detailObjects = new StringBuffer();
        Iterator<ResponsibilityAttributeBo> respIter = attributeDetails.iterator();
        while (respIter.hasNext()) {
            ResponsibilityAttributeBo bo = respIter.next();
            detailObjects.append(getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(bo.getKimAttributeId())))
                    .append(":")
                    .append(bo.getAttributeValue());
            if (respIter.hasNext()) {
                detailObjects.append(KimConstants.KimUIConstants.COMMA_SEPARATOR);
            }
        }

        return detailObjects.toString();
    }

    private String getKimAttributeLabelFromDD(KimTypeAttribute attribute) {
        return getDataDictionaryService().getAttributeLabel(attribute.getKimAttribute().getComponentName(),
                attribute.getKimAttribute().getAttributeName());
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


}
