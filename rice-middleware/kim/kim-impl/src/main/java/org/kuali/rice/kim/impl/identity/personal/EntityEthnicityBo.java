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
package org.kuali.rice.kim.impl.identity.personal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_ETHNIC_T")
public class EntityEthnicityBo extends DataObjectBase implements EntityEthnicityContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_ETHNIC_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_ETHNIC_ID_S")
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "ETHNCTY_CD")
    private String ethnicityCode;

    @Column(name = "SUB_ETHNCTY_CD")
    private String subEthnicityCode;

    @Transient
    private boolean suppressPersonal;

    public static EntityEthnicity to(EntityEthnicityBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityEthnicity.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEthnicityBo business object from an immutable representation of a EntityEthnicity.
     *
     * @param immutable an immutable EntityEthnicity
     * @return a EntityEthnicityBo
     */
    public static EntityEthnicityBo from(EntityEthnicity immutable) {
        if (immutable == null) {
            return null;
        }
        EntityEthnicityBo bo = new EntityEthnicityBo();
        bo.entityId = immutable.getEntityId();
        bo.id = immutable.getId();
        bo.ethnicityCode = immutable.getEthnicityCodeUnmasked();
        bo.subEthnicityCode = immutable.getSubEthnicityCodeUnmasked();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public boolean isSuppressPersonal() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());
            if (privacy != null) {
                this.suppressPersonal = privacy.isSuppressPersonal();
            } else {
                this.suppressPersonal = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return suppressPersonal;
    }

    @Override
    public String getEthnicityCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.ethnicityCode;
    }

    @Override
    public String getSubEthnicityCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }
        return this.subEthnicityCode;
    }

    @Override
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCode;
    }

    @Override
    public String getSubEthnicityCodeUnmasked() {
        return this.subEthnicityCode;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setEthnicityCode(String ethnicityCode) {
        this.ethnicityCode = ethnicityCode;
    }

    public void setSubEthnicityCode(String subEthnicityCode) {
        this.subEthnicityCode = subEthnicityCode;
    }

    public boolean getSuppressPersonal() {
        return suppressPersonal;
    }

    public void setSuppressPersonal(boolean suppressPersonal) {
        this.suppressPersonal = suppressPersonal;
    }
}
