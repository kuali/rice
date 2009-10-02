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
import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
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
	
	public String getEntityTypeCode() {
		return unNullify( this.entityTypeCode);
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
	public KimEntityAddressInfo getDefaultAddress() {
		return unNullify( this.defaultAddress, KimEntityAddressInfo.class);
	}
	public void setDefaultAddress(KimEntityAddress defaultAddress) {
		this.defaultAddress = new KimEntityAddressInfo(defaultAddress);
	}
	public KimEntityPhoneInfo getDefaultPhoneNumber() {
		return unNullify( this.defaultPhoneNumber, KimEntityPhoneInfo.class);
	}
	public void setDefaultPhoneNumber(KimEntityPhone defaultPhoneNumber) {
		this.defaultPhoneNumber = new KimEntityPhoneInfo(defaultPhoneNumber);
	}
	public KimEntityEmailInfo getDefaultEmailAddress() {
		return unNullify( this.defaultEmailAddress, KimEntityEmailInfo.class);
	}
	public void setDefaultEmailAddress(KimEntityEmail defaultEmailAddress) {
		this.defaultEmailAddress = new KimEntityEmailInfo(defaultEmailAddress);
	}
}
