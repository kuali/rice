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
package org.kuali.rice.kim.impl.identity.personal;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.personal.EntityDisability;
import org.kuali.rice.kim.api.identity.personal.EntityDisabilityContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_ENTITY_DSBTY_ID_S",property="id")
public class EntityDisabilityBo extends PersistableBusinessObjectBase implements EntityDisabilityContract {
    private static final long serialVersionUID = -575024049319370685L;
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    private String statusCode;
    private String determinationSourceTypeCode;
    private EntityDisabilityDeterminationSourceBo determinationSourceType;
    /* hook up with ojb indirection table */
    private List<EntityDisabilityAccomodationNeededBo> accommodationsNeeded;
    private String conditionTypeCode;
    private EntityDisabilityConditionTypeBo conditionType;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

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

    @Override
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public EntityDisabilityDeterminationSourceBo getDeterminationSourceType() {
        return determinationSourceType;
    }

    public void setDeterminationSourceType(EntityDisabilityDeterminationSourceBo determinationSourceType) {
        this.determinationSourceType = determinationSourceType;
    }

    @Override
    public List<EntityDisabilityAccomodationNeededBo> getAccommodationsNeeded() {
        return accommodationsNeeded;
    }

    public void setAccommodationsNeeded(List<EntityDisabilityAccomodationNeededBo> accommodationsNeeded) {
        this.accommodationsNeeded = accommodationsNeeded;
    }

    @Override
    public EntityDisabilityConditionTypeBo getConditionType() {
        return conditionType;
    }

    public String getDeterminationSourceTypeCode() {
        return determinationSourceTypeCode;
    }

    public void setDeterminationSourceTypeCode(String determinationSourceTypeCode) {
        this.determinationSourceTypeCode = determinationSourceTypeCode;
    }

    public String getConditionTypeCode() {
        return conditionTypeCode;
    }

    public void setConditionTypeCode(String conditionTypeCode) {
        this.conditionTypeCode = conditionTypeCode;
    }

    public void setConditionType(EntityDisabilityConditionTypeBo conditionType) {
        this.conditionType = conditionType;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static EntityDisability to(EntityDisabilityBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityDisability.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEthnicityBo business object from an immutable representation of a EntityEthnicity.
     *
     * @param immutable an immutable EntityEthnicity
     * @return a EntityEthnicityBo
     */
    public static EntityDisabilityBo from(EntityDisability immutable) {
        if (immutable == null) {
            return null;
        }

        EntityDisabilityBo bo = new EntityDisabilityBo();
        bo.entityId = immutable.getEntityId();
        bo.id = immutable.getId();
        bo.statusCode = immutable.getStatusCode();
        if (immutable.getDeterminationSourceType() != null) {
            bo.determinationSourceTypeCode = immutable.getDeterminationSourceType().getCode();
            bo.determinationSourceType = EntityDisabilityDeterminationSourceBo.from(immutable.getDeterminationSourceType());
        }
        if (immutable.getConditionType() != null) {
            bo.conditionTypeCode = immutable.getConditionType().getCode();
            bo.conditionType = EntityDisabilityConditionTypeBo.from(immutable.getDeterminationSourceType());
        }
    /* hook up with ojb indirection table */
        if (CollectionUtils.isNotEmpty(immutable.getAccommodationsNeeded())) {
            bo.accommodationsNeeded = new ArrayList<EntityDisabilityAccomodationNeededBo>();
            for (CodedAttribute an : immutable.getAccommodationsNeeded()) {
                bo.accommodationsNeeded.add(EntityDisabilityAccomodationNeededBo.from(an));
            }

        }

        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

}


