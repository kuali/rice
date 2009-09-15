/*
 * Copyright 2007-2009 The Kuali Foundation
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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityEmailInfo extends KimDefaultableInfo implements KimEntityEmail {

	private static final long serialVersionUID = 1L;

	protected String entityEmailId = "";
	protected String entityTypeCode = "";
	protected String emailTypeCode = "";
	protected String emailAddress = "";
	
	protected boolean suppressEmail = false;
	
	/**
	 * 
	 */
	public KimEntityEmailInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityEmailInfo( KimEntityEmail email ) {
		if ( email != null ) {
			this.entityEmailId = unNullify( email.getEntityEmailId() );
			this.entityTypeCode = unNullify( email.getEntityTypeCode() );
			this.emailTypeCode = unNullify( email.getEmailTypeCode() );
			this.emailAddress = unNullify( email.getEmailAddressUnmasked() );
			this.dflt = email.isDefault();
			this.active = email.isActive();
			this.suppressEmail = email.isSuppressEmail();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddress()
	 */
	public String getEmailAddress() {
	    if (isSuppressEmail()) {
	        return KimConstants.RESTRICTED_DATA_MASK;
	    }
		return emailAddress;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailTypeCode()
	 */
	public String getEmailTypeCode() {
		return emailTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEntityEmailId()
	 */
	public String getEntityEmailId() {
		return entityEmailId;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	public void setEntityEmailId(String entityEmailId) {
		this.entityEmailId = entityEmailId;
	}

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddressUnmasked()
     */
    public String getEmailAddressUnmasked() {
        return this.emailAddress;
    }

    public boolean isSuppressEmail() {
        return this.suppressEmail;
    }
}
