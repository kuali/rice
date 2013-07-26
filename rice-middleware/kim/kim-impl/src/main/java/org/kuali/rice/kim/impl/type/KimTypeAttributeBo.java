/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.impl.type;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeAttributeContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KimTypeAttributeBo extends PersistableBusinessObjectBase implements KimTypeAttributeContract {

    private String id;
    private String sortCode;
    private String kimAttributeId;
    private KimAttributeBo kimAttribute;
    private String kimTypeId;
    private boolean active;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimTypeAttribute to(KimTypeAttributeBo bo) {
        if (bo == null) {
            return null;
        }

        return KimTypeAttribute.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KimTypeAttributeBo from(KimTypeAttribute im) {
        if (im == null) {
            return null;
        }

        KimTypeAttributeBo bo = new KimTypeAttributeBo();
        bo.setId(im.getId());
        bo.sortCode = im.getSortCode();
        final KimAttribute attribute = im.getKimAttribute();
        bo.kimAttributeId = (attribute == null ? null : attribute.getId());
        bo.kimAttribute = KimAttributeBo.from(im.getKimAttribute());
        bo.kimTypeId = im.getKimTypeId();
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        return bo;
    }

    @Override
    public KimAttributeBo getKimAttribute() {
        return kimAttribute;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getKimAttributeId() {
        return kimAttributeId;
    }

    public void setKimAttributeId(String kimAttributeId) {
        this.kimAttributeId = kimAttributeId;
    }

    public void setKimAttribute(KimAttributeBo kimAttribute) {
        this.kimAttribute = kimAttribute;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
