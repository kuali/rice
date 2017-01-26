/**
 * Copyright 2005-2017 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@MappedSuperclass
@PortableSequenceGenerator(name = "KRIM_ATTR_DATA_ID_S")
public abstract class KimAttributeDataBo extends DataObjectBase implements KimAttributeDataContract {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimAttributeDataBo.class);
    private static final long serialVersionUID = 1L;

    private static KimTypeInfoService kimTypeInfoService;

    @Column(name="ATTR_VAL")
    private String attributeValue;

    @Column(name="KIM_ATTR_DEFN_ID")
    private String kimAttributeId;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "KIM_ATTR_DEFN_ID", insertable = false, updatable = false)
    private KimAttributeBo kimAttribute;

    @Column(name="KIM_TYP_ID")
    private String kimTypeId;

    @Transient
    private KimTypeBo kimType;

    public abstract void setId(String id);

    public abstract void setAssignedToId(String assignedToId);

    @Override
    public KimAttributeBo getKimAttribute() {
        if(this.kimAttribute == null
                && StringUtils.isNotBlank(kimAttributeId)) {
            KradDataServiceLocator.getDataObjectService().wrap(this).fetchRelationship("kimAttribute");
        }
        return kimAttribute;
    }

    @Override
    public KimTypeBo getKimType() {
        if (kimType == null && StringUtils.isNotEmpty(getId())) {
            kimType = KimTypeBo.from(KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId));
        }
        return kimType;
    }

    public static <T extends KimAttributeDataBo> Map<String, String> toAttributes(Collection<T> bos) {
        Map<String, String> m = new HashMap<String, String>();
        if(CollectionUtils.isNotEmpty(bos)) {
            for (T it : bos) {
                if (it != null) {
                    KimTypeAttribute attribute = null;
                    if ( it.getKimType() != null ) {
                        attribute = KimTypeBo.to(it.getKimType()).getAttributeDefinitionById(it.getKimAttributeId());
                    }
                    if ( attribute != null ) {
                        m.put(attribute.getKimAttribute().getAttributeName(), it.getAttributeValue());
                    } else {
                        m.put(it.getKimAttribute().getAttributeName(), it.getAttributeValue());
                    }
                }
            }
        }
        return m;
    }

    /** creates a list of KimAttributeDataBos from attributes, kimTypeId, and assignedToId. */
    public static <T extends KimAttributeDataBo> List<T> createFrom(Class<T> type, Map<String, String> attributes, String kimTypeId) {
        if (attributes == null) {
            //purposely not using Collections.emptyList() b/c we do not want to return an unmodifiable list.
            return new ArrayList<T>();
        }
        List<T> attrs = new ArrayList<T>();
        for (Map.Entry<String, String> it : attributes.entrySet()) {
        //return attributes.entrySet().collect {
            KimTypeAttribute attr = getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(it.getKey());
            if (attr == null) {
                LOG.error("Attribute " + it.getKey() + " was not found for kimType " + getKimTypeInfoService().getKimType(kimTypeId).getName());
            }
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
    }

    @Override
    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getKimAttributeId() {
        return kimAttributeId;
    }

    public void setKimAttributeId(String kimAttributeId) {
        this.kimAttributeId = kimAttributeId;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public void setKimType(KimTypeBo kimType) {
        this.kimType = kimType;
    }
    
    public void setKimAttribute(KimAttributeBo kimAttribute) {
        this.kimAttribute = kimAttribute;
    }


    public static KimTypeInfoService getKimTypeInfoService() {
        if (kimTypeInfoService==null) {
            kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        }
        return kimTypeInfoService;
    }

    public static void setKimTypeInfoService(KimTypeInfoService kimTypeInfoService) {
        KimAttributeDataBo.kimTypeInfoService = kimTypeInfoService;
    }

}
