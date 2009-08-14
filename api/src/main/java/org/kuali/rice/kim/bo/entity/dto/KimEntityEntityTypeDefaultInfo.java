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

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimEntityEntityTypeDefaultInfo implements Serializable {
	protected String entityTypeCode;
	protected KimEntityAddress defaultAddress;
	protected KimEntityPhone defaultPhoneNumber;
	protected KimEntityEmail defaultEmailAddress;
	
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
	public KimEntityAddress getDefaultAddress() {
		return this.defaultAddress;
	}
	public void setDefaultAddress(KimEntityAddress defaultAddress) {
		this.defaultAddress = defaultAddress;
	}
	public KimEntityPhone getDefaultPhoneNumber() {
		return this.defaultPhoneNumber;
	}
	public void setDefaultPhoneNumber(KimEntityPhone defaultPhoneNumber) {
		this.defaultPhoneNumber = defaultPhoneNumber;
	}
	public KimEntityEmail getDefaultEmailAddress() {
		return this.defaultEmailAddress;
	}
	public void setDefaultEmailAddress(KimEntityEmail defaultEmailAddress) {
		this.defaultEmailAddress = defaultEmailAddress;
	}
}
