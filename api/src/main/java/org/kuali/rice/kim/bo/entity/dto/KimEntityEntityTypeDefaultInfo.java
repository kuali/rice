/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;

/**
 * entity type default info for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KimEntityEntityTypeDefaultInfo implements Serializable {
    private static final long serialVersionUID = -6585360231364528118L;
    
    protected String entityTypeCode;
    @XmlElement
    protected KimEntityAddressInfo defaultAddress;
    @XmlElement
    protected KimEntityPhoneInfo defaultPhoneNumber;
    @XmlElement
    protected KimEntityEmailInfo defaultEmailAddress;

    /**
     * Gets this {@link KimEntityEntityTypeDefaultInfo}'s entity type code.
     * @return the entity type code for this {@link KimEntityEntityTypeDefaultInfo}, or null if none has been assigned.
     */
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    /**
     * Gets this {@link KimEntityEntityTypeDefaultInfo}'s default address.
     * @return the default address for this {@link KimEntityEntityTypeDefaultInfo}, or null if none has been assigned.
     */
    public KimEntityAddressInfo getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(KimEntityAddress defaultAddress) {
        if (defaultAddress != null) {
            this.defaultAddress = new KimEntityAddressInfo(defaultAddress);
        }
    }

    /**
     * Gets this {@link KimEntityEntityTypeDefaultInfo}'s default phone number.
     * @return the default phone number for this {@link KimEntityEntityTypeDefaultInfo}, or null if none has been assigned.
     */
    public KimEntityPhoneInfo getDefaultPhoneNumber() {
        return defaultPhoneNumber;
    }

    public void setDefaultPhoneNumber(KimEntityPhone defaultPhoneNumber) {
        if (defaultPhoneNumber != null) {
            this.defaultPhoneNumber = new KimEntityPhoneInfo(defaultPhoneNumber);
        }
    }

    /**
     * Gets this {@link KimEntityEntityTypeDefaultInfo}'s default email address.
     * @return the default email address for this {@link KimEntityEntityTypeDefaultInfo}, or null if none has been assigned.
     */
    public KimEntityEmailInfo getDefaultEmailAddress() {
        return defaultEmailAddress;
    }

    public void setDefaultEmailAddress(KimEntityEmail defaultEmailAddress) {
        if (defaultEmailAddress != null) {
            this.defaultEmailAddress = new KimEntityEmailInfo(defaultEmailAddress);
        }
    }
}
