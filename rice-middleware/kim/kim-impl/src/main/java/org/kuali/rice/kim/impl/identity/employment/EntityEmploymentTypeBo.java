/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kim.impl.identity.employment;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.employment.EntityEmploymentTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "EMP_TYP_CD")) })
@Entity
@Table(name = "KRIM_EMP_TYP_T")
public class EntityEmploymentTypeBo extends CodedAttributeBo implements EntityEmploymentTypeEbo {

    private static final long serialVersionUID = -5365214631480350557L;

    public static EntityEmploymentTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityEmploymentTypeBo.class, immutable);
    }
}
