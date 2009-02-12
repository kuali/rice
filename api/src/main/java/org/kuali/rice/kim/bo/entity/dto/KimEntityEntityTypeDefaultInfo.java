/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.entity.dto;

import java.io.Serializable;

import org.kuali.rice.kim.bo.entity.EntityAddress;
import org.kuali.rice.kim.bo.entity.EntityEmail;
import org.kuali.rice.kim.bo.entity.EntityPhone;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityEntityTypeDefaultInfo extends KimInfoBase implements Serializable {
	protected String entityTypeCode;
	protected EntityAddress defaultAddress;
	protected EntityPhone defaultPhoneNumber;
	protected EntityEmail defaultEmailAddress;
	
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
	public EntityAddress getDefaultAddress() {
		return this.defaultAddress;
	}
	public void setDefaultAddress(EntityAddress defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
	public EntityPhone getDefaultPhoneNumber() {
		return this.defaultPhoneNumber;
	}
	public void setDefaultPhoneNumber(EntityPhone defaultPhoneNumber) {
		this.defaultPhoneNumber = defaultPhoneNumber;
	}
	public EntityEmail getDefaultEmailAddress() {
		return this.defaultEmailAddress;
	}
	public void setDefaultEmailAddress(EntityEmail defaultEmailAddress) {
		this.defaultEmailAddress = defaultEmailAddress;
	}
}
