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
package org.kuali.rice.kim.impl.identity.email;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_EMAIL_T")
public class EntityEmailBo extends EntityEmailBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_EMAIL_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_EMAIL_ID_S")
    @Id
    @Column(name = "ENTITY_EMAIL_ID")
    private String id;

    @ManyToOne(targetEntity = EntityEmailTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "EMAIL_TYP_CD", referencedColumnName = "EMAIL_TYP_CD", insertable = false, updatable = false)
    private EntityEmailTypeBo emailType;

    public static EntityEmail to(EntityEmailBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityEmail.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEmailBo business object from an immutable representation of a EntityEmail.
     *
     * @param immutable an immutable EntityEmail
     * @return a EntityEmailBo
     */
    public static EntityEmailBo from(EntityEmail immutable) {
        if (immutable == null) {
            return null;
        }
        EntityEmailBo bo = new EntityEmailBo();
        bo.setId(immutable.getId());
        bo.setActive(immutable.isActive());
        bo.setEntityId(immutable.getEntityId());
        bo.setEntityTypeCode(immutable.getEntityTypeCode());
        if (immutable.getEmailType() != null) {
            bo.setEmailTypeCode(immutable.getEmailType().getCode());
        }
        bo.setEmailAddress(immutable.getEmailAddressUnmasked());
        bo.setEmailType(EntityEmailTypeBo.from(immutable.getEmailType()));
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public EntityEmailTypeBo getEmailType() {
        return this.emailType;
    }

    public void setEmailType(EntityEmailTypeBo emailType) {
        this.emailType = emailType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
