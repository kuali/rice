/**
 * Copyright 2005-2012 The Kuali Foundation
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
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.api.type.KimTypeAttribute
import org.kuali.rice.kim.impl.type.KimTypeBo
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase
import org.kuali.rice.krad.util.ObjectUtils
import org.kuali.rice.krad.service.PersistenceStructureService

abstract class KimAttributeDataBo extends PersistableBusinessObjectBase implements KimAttributeDataContract {
    private static final long serialVersionUID = 1L;

    String id
    String attributeValue
    String kimAttributeId
    KimAttributeBo kimAttribute
    String kimTypeId
    @Transient
    KimTypeBo kimType

    abstract void setAssignedToId(String s);

   @Override
    KimAttributeBo getKimAttribute() {
        if(ObjectUtils.isNull(this.kimAttribute)
            && StringUtils.isNotBlank(kimAttributeId)) {
            this.refreshReferenceObject("kimAttribute");
        }
        return kimAttribute
    }

    @Override
    KimTypeBo getKimType() {
        if (kimType == null && StringUtils.isNotEmpty(id)) {
            kimType = KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
        }
        return kimType;
    }

    static <T extends KimAttributeDataBo> Map<String, String> toAttributes(Collection<T> bos) {
        def m = [:]
        if(bos != null) {
            bos.each {
                if (it != null) {
                    KimTypeAttribute attribute = null;
                    if ( it.kimType != null ) {
                        attribute = KimTypeBo.to(it.kimType).getAttributeDefinitionById( it.getKimAttributeId() );
                    }
                    if ( attribute != null ) {
                        m[attribute.getKimAttribute().getAttributeName()] = it.getAttributeValue();
                    } else {
                        m[it.getKimAttribute().getAttributeName()] = it.getAttributeValue();
                    }
                }
            }
        }
        return m;
    }

    /** creates a list of KimAttributeDataBos from attributes, kimTypeId, and assignedToId. */
    static <T extends KimAttributeDataBo> List<T> createFrom(Class<T> type, Map<String, String> attributes, String kimTypeId) {
       if (attributes == null) {
           //purposely not using Collections.emptyList() b/c we do not want to return an unmodifiable list.
           return new ArrayList<T>();
       }
       return attributes.entrySet().collect {
            KimTypeAttribute attr = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(it.getKey());
            if (attr != null && StringUtils.isNotBlank(it.getValue())) {
                T newDetail = type.newInstance();
                newDetail.setKimAttributeId(attr.getKimAttribute().getId());
                newDetail.setKimAttribute(KimAttributeBo.from(attr.getKimAttribute()));
                newDetail.setKimTypeId(kimTypeId);
                newDetail.setAttributeValue(it.getValue());
                return newDetail;
            }
        }
    }
}
