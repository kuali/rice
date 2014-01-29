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
package org.kuali.rice.kim.impl.identity.name;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Cacheable(false)
@Table(name = "KRIM_ENTITY_NM_T")
public class EntityNameBo extends EntityNameBase {

    private static final long serialVersionUID = -1449221117942310530L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_NM_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_NM_ID_S")
    @Id
    @Column(name = "ENTITY_NM_ID")
    private String id;

    @ManyToOne(targetEntity = EntityNameTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "NM_TYP_CD", referencedColumnName = "ENT_NM_TYP_CD", insertable = false, updatable = false)
    private EntityNameTypeBo nameType;

    public static EntityName to(EntityNameBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityName.Builder.create(bo).build();
    }

    /**
     * Creates a EntityNameBo business object from an immutable representation of a EntityName.
     *
     * @param immutable an immutable EntityName
     * @return a EntityNameBo
     */
    public static EntityNameBo from(EntityName immutable) {
        if (immutable == null) {
            return null;
        }
        EntityNameBo bo = new EntityNameBo();
        bo.setId(immutable.getId());
        bo.setActive(immutable.isActive());
        bo.setEntityId(immutable.getEntityId());
        bo.setNameType(EntityNameTypeBo.from(immutable.getNameType()));
        if (immutable.getNameType() != null) {
            bo.setNameCode(immutable.getNameType().getCode());
        }
        bo.setFirstName(immutable.getFirstNameUnmasked());
        bo.setLastName(immutable.getLastNameUnmasked());
        bo.setMiddleName(immutable.getMiddleNameUnmasked());
        bo.setNamePrefix(immutable.getNamePrefixUnmasked());
        bo.setNameTitle(immutable.getNameTitleUnmasked());
        bo.setNameSuffix(immutable.getNameSuffixUnmasked());
        bo.setNoteMessage(immutable.getNoteMessage());
        bo.setNameChangedDate(immutable.getNameChangedDate());
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public EntityNameTypeBo getNameType() {
        return this.nameType;
    }

    public void setNameType(EntityNameTypeBo nameType) {
        this.nameType = nameType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
