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

package org.kuali.rice.kim.impl.permission

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.api.type.KimType
import org.kuali.rice.kim.api.type.KimTypeAttribute
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.kim.api.permission.Permission
import org.kuali.rice.kim.api.permission.PermissionContract
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo
import org.kuali.rice.kim.impl.role.RolePermissionBo
import org.kuali.rice.kim.util.KimConstants
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb
import org.springframework.util.AutoPopulatingList

@Entity
@Table(name = "KRIM_PERM_T")
public class PermissionBo extends PersistableBusinessObjectBase implements PermissionContract {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "PERM_ID")
    String id

    @Column(name = "NMSPC_CD")
    String namespaceCode

    @Column(name = "NM")
    String name

    @Column(name = "DESC_TXT", length = 400)
    String description;

    @Column(name = "PERM_TMPL_ID")
    String templateId

    @Column(name = "ACTV_IND")
    @Type(type = "yes_no")
    boolean active

    @OneToOne(targetEntity = PermissionTemplateBo.class, cascade = [], fetch = FetchType.EAGER)
    @JoinColumn(name = "PERM_TMPL_ID", insertable = false, updatable = false)
    PermissionTemplateBo template;

    @OneToMany(targetEntity = PermissionAttributeBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "id")
    @Fetch(value = FetchMode.SELECT)
    List<PermissionAttributeBo> attributeDetails

    @Transient
    Map<String,String> attributes;

    @OneToMany(targetEntity = RolePermissionBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER, mappedBy = "id")
    @Fetch(value = FetchMode.SELECT)
    List<RolePermissionBo> rolePermissions = new AutoPopulatingList(RolePermissionBo.class)

    Map<String,String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes
    }

    //TODO: rename/fix later - only including this method and attributeDetails field for Role conversion

    Map<String,String> getDetails() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Permission to(PermissionBo bo) {
        if (bo == null) {
            return null
        }

        return Permission.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static PermissionBo from(Permission im) {
        if (im == null) {
            return null
        }

        PermissionBo bo = new PermissionBo()
        bo.id = im.id
        bo.namespaceCode = im.namespaceCode
        bo.name = im.name
        bo.description = im.description
        bo.active = im.active
        bo.templateId = im.template != null ? im.template.getId() : null;
        bo.template = PermissionTemplateBo.from(im.template)
        bo.attributes = im.attributes
        bo.versionNumber = im.versionNumber
        bo.objectId = im.objectId;

        return bo
    }

    PermissionTemplateBo getTemplate() {
        return template;
    }
    
    public String getDetailObjectsValues() {
        StringBuffer detailObjectsToDisplay = new StringBuffer();
        Iterator<PermissionAttributeBo> permIter = attributeDetails.iterator();
        while (permIter.hasNext()) {
            PermissionAttributeBo permissionAttributeData = permIter.next();
            detailObjectsToDisplay.append(permissionAttributeData.getAttributeValue());
            if (permIter.hasNext()) {
                detailObjectsToDisplay.append(KimConstants.KimUIConstants.COMMA_SEPARATOR);
            }
        }
        return detailObjectsToDisplay.toString();
    }

    String getDetailObjectsToDisplay() {
        final KimType kimType = getTypeInfoService().getKimType( getTemplate().getKimTypeId() );

        return attributeDetails.collect {
            getKimAttributeLabelFromDD(kimType.getAttributeDefinitionById(it.kimAttributeId)) + ":" + it.attributeValue
        }.join(",")
    }
    
    private String getKimAttributeLabelFromDD( KimTypeAttribute attribute ){
        return getDataDictionaryService().getAttributeLabel(attribute.getKimAttribute().getComponentName(), attribute.getKimAttribute().getAttributeName() );
    }

    private DataDictionaryService dataDictionaryService;
    private DataDictionaryService getDataDictionaryService() {
        if(dataDictionaryService == null){
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }
    
    private KimTypeInfoService kimTypeInfoService;
    private KimTypeInfoService getTypeInfoService() {
        if(kimTypeInfoService == null){
            kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        }
        return kimTypeInfoService;
    }
}
