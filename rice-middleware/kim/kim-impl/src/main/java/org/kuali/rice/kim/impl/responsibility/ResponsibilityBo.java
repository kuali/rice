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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "KRIM_RSP_T")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class ResponsibilityBo extends DataObjectBase implements ResponsibilityContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_RSP_ID_S")
    @GeneratedValue(generator = "KRIM_RSP_ID_S")
    @Id
    @Column(name = "RSP_ID")
    String id;

    @Column(name = "NMSPC_CD")
    String namespaceCode;

    @Column(name = "NM")
    String name;

    @Column(name = "DESC_TXT")
    String description;

    @Column(name = "RSP_TMPL_ID")
    String templateId;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    boolean active;

    @ManyToOne(targetEntity = ResponsibilityTemplateBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "RSP_TMPL_ID", referencedColumnName = "RSP_TMPL_ID", insertable = false, updatable = false)
    ResponsibilityTemplateBo template = new ResponsibilityTemplateBo();

    @OneToMany(targetEntity = ResponsibilityAttributeBo.class, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "RSP_ID", referencedColumnName = "RSP_ID")
    List<ResponsibilityAttributeBo> attributeDetails = new AutoPopulatingList<ResponsibilityAttributeBo>(ResponsibilityAttributeBo.class);

    @OneToMany(mappedBy = "kimResponsibility")
    @JoinColumn(name = "RSP_ID", referencedColumnName = "RSP_ID", insertable = false, updatable = false)
    List<RoleResponsibilityBo> roleResponsibilities = new AutoPopulatingList<RoleResponsibilityBo>(RoleResponsibilityBo.class);

    @Transient
    Map<String, String> attributes;

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
    public static Responsibility to(ResponsibilityContract bo) {
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

    @Override
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

    /*
    This is being done because there is a  major issue with lazy relationships, in ensuring that the relationship is
    still available after the object has been detached, or serialized. For most JPA providers, after serialization
    any lazy relationship that was not instantiated will be broken, and either throw an error when accessed,
    or return null.
     */

    private void writeObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        attributeDetails.size();
        roleResponsibilities.size();
        stream.defaultWriteObject();
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
