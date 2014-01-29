
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
package org.kuali.rice.kim.impl.identity.affiliation;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.framework.identity.affiliation.EntityAffiliationTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@AttributeOverrides({@AttributeOverride(name="code",column=@Column(name="AFLTN_TYP_CD"))})
@Entity
@Table(name = "KRIM_AFLTN_TYP_T")
public class EntityAffiliationTypeBo extends CodedAttributeBo implements EntityAffiliationTypeEbo {

    private static final long serialVersionUID = 4973602240626940004L;

    @Column(name = "EMP_AFLTN_TYP_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean employmentAffiliationType;

    public static EntityAffiliationTypeBo from(EntityAffiliationType immutable) {
        EntityAffiliationTypeBo bo = CodedAttributeBo.from(EntityAffiliationTypeBo.class, CodedAttribute.Builder.create(immutable).build());
        bo.setEmploymentAffiliationType(immutable.isEmploymentAffiliationType());
        return bo;
    }

    public static EntityAffiliationType to(EntityAffiliationTypeBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityAffiliationType.Builder.create(bo).build();
    }

    @Override
    public boolean isEmploymentAffiliationType() {
        return employmentAffiliationType;
    }

    public void setEmploymentAffiliationType(boolean employmentAffiliationType) {
        this.employmentAffiliationType = employmentAffiliationType;
    }
}
