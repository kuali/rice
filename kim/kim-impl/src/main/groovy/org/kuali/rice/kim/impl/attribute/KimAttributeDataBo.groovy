/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.attribute

import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

abstract class KimAttributeDataBo extends PersistableBusinessObjectBase implements KimAttributeDataContract {
    private static final long serialVersionUID = 1L;

    String id
    String attributeValue
    String kimAttributeId
    KimAttributeBo kimAttribute
    String kimTypeId

    @Override
    KimAttributeBo getKimAttribute() {
        return kimAttribute
    }

    @Override
    KimTypeBo getKimType() {
        return KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
    }
}
