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
package org.kuali.rice.kim.bo.ui;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_PHONE_MT")
public class PersonDocumentPhone extends PersonDocumentBoDefaultBase {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_PHONE_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_PHONE_ID_S")
    @Id
    @Column(name = "ENTITY_PHONE_ID")
    protected String entityPhoneId;

    @Column(name = "ENT_TYP_CD")
    protected String entityTypeCode;

    @Column(name = "PHONE_TYP_CD")
    protected String phoneTypeCode;

    @Column(name = "PHONE_NBR")
    protected String phoneNumber;

    @Column(name = "PHONE_EXTN_NBR")
    protected String extensionNumber;

    @Column(name = "POSTAL_CNTRY_CD")
    protected String countryCode;

    @ManyToOne(targetEntity = EntityPhoneTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PHONE_TYP_CD", referencedColumnName = "PHONE_TYP_CD", insertable = false, updatable = false)
    protected EntityPhoneTypeBo phoneType;

    // Waiting until we pull in from KFS                       
    // protected Country country;                       
    public PersonDocumentPhone() {
        this.active = true;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCode()
	 */
    public String getCountryCode() {
        return countryCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getEntityPhoneId()
	 */
    public String getEntityPhoneId() {
        return entityPhoneId;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumber()
	 */
    public String getExtensionNumber() {
        return extensionNumber;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumber()
	 */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneTypeCode()
	 */
    public String getPhoneTypeCode() {
        return phoneTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setCountryCode(java.lang.String)
	 */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setExtensionNumber(java.lang.String)
	 */
    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setPhoneNumber(java.lang.String)
	 */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setPhoneTypeCode(java.lang.String)
	 */
    public void setPhoneTypeCode(String phoneTypeCode) {
        this.phoneTypeCode = phoneTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#getEntityTypeCode()
	 */
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#setEntityTypeCode(java.lang.String)
	 */
    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public EntityPhoneTypeBo getPhoneType() {
        return this.phoneType;
    }

    public void setPhoneType(EntityPhoneTypeBo phoneType) {
        this.phoneType = phoneType;
    }

    public void setEntityPhoneId(String entityPhoneId) {
        this.entityPhoneId = entityPhoneId;
    }

    public String getFormattedPhoneNumber() {
        StringBuilder sb = new StringBuilder(30);
        // TODO: get extension from country code table                       
        // TODO: append "+xxx" if country is not the default country                       
        sb.append(getPhoneNumber());
        if (StringUtils.isNotBlank(getExtensionNumber())) {
            sb.append(" x");
            sb.append(getExtensionNumber());
        }
        return sb.toString();
    }
}
