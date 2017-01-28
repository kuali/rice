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
package org.kuali.rice.kim.bo.ui;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_EMAIL_MT")
public class PersonDocumentEmail extends PersonDocumentBoDefaultBase {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_EMAIL_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_EMAIL_ID_S")
    @Id
    @Column(name = "ENTITY_EMAIL_ID")
    protected String entityEmailId;

    @Column(name = "ENT_TYP_CD")
    protected String entityTypeCode;

    @Column(name = "EMAIL_TYP_CD")
    protected String emailTypeCode;

    @Column(name = "EMAIL_ADDR")
    protected String emailAddress;

    @JoinFetch(value= JoinFetchType.OUTER)
    @ManyToOne(targetEntity = EntityEmailBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "EMAIL_TYP_CD", referencedColumnName = "ENTITY_EMAIL_ID", insertable = false, updatable = false)
    protected EntityEmailTypeBo emailType;

    public PersonDocumentEmail() {
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.email.EntityEmailContract#getEmailAddress()
	 */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.email.EntityEmailContract#getEmailType()
	 */
    public String getEmailTypeCode() {
        return emailTypeCode;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.email.EntityEmailContract#getId()
	 */
    public String getEntityEmailId() {
        return entityEmailId;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.email.EntityEmailContract#setEmailAddress(java.lang.String)
	 */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
	 * @see org.kuali.rice.kim.api.identity.email.EntityEmailContract#setEmailType(java.lang.String)
	 */
    public void setEmailTypeCode(String emailTypeCode) {
        this.emailTypeCode = emailTypeCode;
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

    public void setEntityEmailId(String entityEmailId) {
        this.entityEmailId = entityEmailId;
    }

    public EntityEmailTypeBo getEmailType() {
        return this.emailType;
    }

    public void setEmailType(EntityEmailTypeBo emailType) {
        this.emailType = emailType;
    }
}
