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
package org.kuali.rice.kim.impl.common.attribute;

import org.kuali.rice.kim.api.common.attribute.KimAttributeDataHistoryContract;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class KimAttributeDataHistoryBo extends KimAttributeDataBo implements KimAttributeDataHistoryContract {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimAttributeDataHistoryBo.class);
    private static final long serialVersionUID = 1L;
    //private Long historyId;

    public abstract void setAssignedToHistoryId(Long s);

   // @Override
   // public Long getHistoryId() {
   //     return historyId;
   // }

   // public void setHistoryId(Long historyId) {
   //     this.historyId = historyId;
   // }


    /** creates a list of KimAttributeDataBos from attributes, kimTypeId, and assignedToId. */
    /*public static <T extends KimAttributeDataHistoryBo> List<T> createFrom(Class<T> type, Map<String, String> attributes, String kimTypeId) {
        if (attributes == null) {
            //purposely not using Collections.emptyList() b/c we do not want to return an unmodifiable list.
            return new ArrayList<T>();
        }
        List<T> attrs = new ArrayList<T>();
        for (Map.Entry<String, String> it : attributes.entrySet()) {
            //return attributes.entrySet().collect {
            KimTypeAttribute attr = getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(it.getKey());
            KimType theType = getKimTypeInfoService().getKimType(kimTypeId);
            if (attr != null && StringUtils.isNotBlank(it.getValue())) {
                try {
                    T newDetail = type.newInstance();
                    newDetail.setKimAttributeId(attr.getKimAttribute().getId());
                    newDetail.setKimAttribute(KimAttributeBo.from(attr.getKimAttribute()));
                    newDetail.setKimTypeId(kimTypeId);
                    newDetail.setKimType(KimTypeBo.from(theType));
                    newDetail.setAttributeValue(it.getValue());
                    attrs.add(newDetail);
                } catch (InstantiationException e) {
                    LOG.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }

            }
        }
        return attrs;
    }*/
}
