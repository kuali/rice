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

package org.kuali.rice.kim.impl.common.attribute

import javax.persistence.Transient
import org.apache.commons.lang.StringUtils
import org.kuali.rice.core.api.mo.common.Attributes
import org.kuali.rice.core.api.mo.common.ImmutableKeyValue
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.api.type.KimType
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

abstract class KimAttributeDataBo extends PersistableBusinessObjectBase implements KimAttributeDataContract {
    private static final long serialVersionUID = 1L;

    String id
    String attributeValue
    String kimAttributeId
    KimAttributeBo kimAttribute
    String kimTypeId
    @Transient
    KimTypeBo kimType

    @Override
    KimAttributeBo getKimAttribute() {
        return kimAttribute
    }

    @Override
    KimTypeBo getKimType() {
        if (kimType == null && StringUtils.isNotEmpty(id)) {
            kimType = KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
        }
        return kimType;
    }

    static Attributes toAttributes(KimAttributeDataBo... bos) {
        if (bos == null) {
            return Attributes.empty()
        }

        def types = [:]
        def attrs = bos.collect {
            //local cache of kim types
            KimType type = types[it.kimTypeId]
            if (type == null) {
                type = KimTypeBo.to(it.kimType)
                types[it.kimTypeId] = type
            }

            def attribute = null
            if ( type != null ) {
                attribute = type.getAttributeDefinitionById( it.getKimAttributeId() );
            }

            if ( attribute != null ) {
                ImmutableKeyValue.fromStrings(attribute.getKimAttribute().getAttributeName(), it.getAttributeValue() );
            } else {
                ImmutableKeyValue.fromStrings(it.getKimAttribute().getAttributeName(), it.getAttributeValue() );
            }
        }

		return Attributes.fromKeyValues(attrs);
    }
}
