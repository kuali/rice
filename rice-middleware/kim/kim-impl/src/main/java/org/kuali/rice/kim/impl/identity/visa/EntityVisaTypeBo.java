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
package org.kuali.rice.kim.impl.identity.visa;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.impl.identity.CodedAttributeBo;

//@Entity
//@AttributeOverrides({
//        @AttributeOverride(name="code",column=@Column(name="VISA_TYP_CD"))
//})
//@Table(name = "KRIM_VISA_TYP_T")
public class EntityVisaTypeBo extends CodedAttributeBo {
    private static final long serialVersionUID = -7999904356580992741L;

    public static EntityVisaTypeBo from(CodedAttribute immutable) {
        return CodedAttributeBo.from(EntityVisaTypeBo.class, immutable);
    }
}
