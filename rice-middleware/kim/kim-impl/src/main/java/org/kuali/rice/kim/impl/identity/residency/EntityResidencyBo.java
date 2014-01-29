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
package org.kuali.rice.kim.impl.identity.residency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.kuali.rice.kim.api.identity.residency.EntityResidency;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_RESIDENCY_T")
public class EntityResidencyBo extends DataObjectBase implements EntityResidencyContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_RESIDENCY_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_RESIDENCY_ID_S")
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "DETERMINATION_METHOD")
    private String determinationMethod;

    @Column(name = "IN_STATE")
    private String inState;

    public static EntityResidency to(EntityResidencyBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityResidency.Builder.create(bo).build();
    }

    /**
     * Creates a EntityResidencyBo business object from an immutable representation of a EntityResidency.
     *
     * @param immutable an immutable EntityResidency
     * @return a EntityResidencyBo
     */
    public static EntityResidencyBo from(EntityResidency immutable) {
        if (immutable == null) {
            return null;
        }
        EntityResidencyBo bo = new EntityResidencyBo();
        bo.entityId = immutable.getEntityId();
        bo.id = immutable.getId();
        bo.determinationMethod = immutable.getDeterminationMethod();
        bo.inState = immutable.getInState();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
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

    @Override
    public String getDeterminationMethod() {
        return determinationMethod;
    }

    public void setDeterminationMethod(String determinationMethod) {
        this.determinationMethod = determinationMethod;
    }

    @Override
    public String getInState() {
        return inState;
    }

    public void setInState(String inState) {
        this.inState = inState;
    }
}
