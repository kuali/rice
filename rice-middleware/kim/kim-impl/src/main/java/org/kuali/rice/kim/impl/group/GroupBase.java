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
package org.kuali.rice.kim.impl.group;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@MappedSuperclass
public abstract class GroupBase extends DataObjectBase implements GroupEbo {
    private static final long serialVersionUID = 1L;

    @Column(name="GRP_NM")
    private String name;

    @Column(name="GRP_DESC",length=4000)
    private String description;

    @Column(name="ACTV_IND")
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    private boolean active;

    @Column(name="KIM_TYP_ID")
    private String kimTypeId;

    @Column(name="NMSPC_CD")
    private String namespaceCode;

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

    public KimTypeBo getKimTypeInfo() {
        return KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(this.kimTypeId));
    }

    @Override
    public void refresh() {
    }
}
