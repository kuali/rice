/*
 * Copyright 2007-2008 The Kuali Foundation
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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.responsibility;


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Type
import org.kuali.rice.kim.bo.role.KimResponsibilityTemplate
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityTemplateInfo
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_RSP_TMPL_T")
public class ResponsibilityTemplateBo extends PersistableBusinessObjectBase implements KimResponsibilityTemplate {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "RSP_TMPL_ID")
    String responsibilityTemplateId;

    @Column(name = "NMSPC_CD")
    String namespaceCode;

    @Column(name = "NM")
    String name;

    @Column(name = "KIM_TYP_ID")
    String kimTypeId;

    @Column(name = "DESC_TXT", length = 400)
    String description;

    @Type(type = "yes_no")
    @Column(name = "ACTV_IND")
    boolean active;


    public KimResponsibilityTemplateInfo toInfo() {
        KimResponsibilityTemplateInfo info = new KimResponsibilityTemplateInfo();
        info.setResponsibilityTemplateId(responsibilityTemplateId);
        info.setNamespaceCode(namespaceCode);
        info.setName(name);
        info.setDescription(description);
        info.setKimTypeId(kimTypeId);
        info.setActive(active);
        return info;
    }
}
