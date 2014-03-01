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
package org.kuali.rice.kim.impl.common.template;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kim.api.common.template.TemplateContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.provider.annotation.BusinessKey;

@MappedSuperclass
public abstract class TemplateBo extends PersistableBusinessObjectBase implements TemplateContract {

    private static final long serialVersionUID = 1L;

    @Column(name="NMSPC_CD",nullable=false)
    @BusinessKey
    protected String namespaceCode;

    @Column(name="NM",nullable=false)
    @BusinessKey
    protected String name;

    @Column(name="DESC_TXT", length=400)
    protected String description;

    @Column(name="KIM_TYP_ID")
    protected String kimTypeId;

    @Column(name="ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    protected boolean active;

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

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}