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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.name.EntityNameTypeEbo;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "ENT_NM_TYP_CD")) })
@Entity
@Table(name = "KRIM_ENT_NM_TYP_T")
public class EntityNameTypeBo extends CodedAttributeBo implements EntityNameTypeEbo {

    private static final long serialVersionUID = -3667881155839568604L;

    public static EntityNameTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityNameTypeBo.class, immutable);
    }
}
